package net.lavaclient.client.module.modules.combat;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.MotionEvent;
import net.lavaclient.client.event.events.RenderEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.utils.RotationUtils;
import net.lavaclient.client.utils.TimerUtils;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.FloatValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced KillAura module with improved target selection and rotation
 */
public class KillAura extends Module {
    // Target settings
    private final FloatValue rangeValue = addValue(new FloatValue("Range", 4.2f, 1.0f, 8.0f));
    private final BoolValue autoBlockValue = addValue(new BoolValue("AutoBlock", true));
    private final BoolValue throughWallsValue = addValue(new BoolValue("ThroughWalls", false));
    private final IntegerValue maxTargetsValue = addValue(new IntegerValue("MaxTargets", 1, 1, 5));
    private final ListValue priorityValue = addValue(new ListValue("Priority", new String[] {"Distance", "Health", "Direction"}, "Distance"));
    
    // Timing settings
    private final IntegerValue cpsValue = addValue(new IntegerValue("CPS", 12, 1, 20));
    private final BoolValue smartCPSValue = addValue(new BoolValue("SmartCPS", true));
    
    // Rotation settings
    private final BoolValue rotationsValue = addValue(new BoolValue("Rotations", true));
    private final ListValue rotationModeValue = addValue(new ListValue("RotationMode", new String[] {"Smooth", "Instant", "None"}, "Smooth"));
    private final FloatValue rotationSmoothingValue = addValue(new FloatValue("RotationSmoothing", 2.0f, 1.0f, 10.0f));
    
    // Visuals
    private final BoolValue silentRotationsValue = addValue(new BoolValue("SilentRotations", true));
    private final BoolValue swingValue = addValue(new BoolValue("Swing", true));
    
    // Target filters
    private final BoolValue targetsPlayersValue = addValue(new BoolValue("TargetPlayers", true));
    private final BoolValue targetsMobsValue = addValue(new BoolValue("TargetMobs", false));
    private final BoolValue targetsAnimalsValue = addValue(new BoolValue("TargetAnimals", false));
    private final BoolValue targetsInvisibleValue = addValue(new BoolValue("TargetInvisible", false));
    
    // Attack timing
    private final TimerUtils attackTimer = new TimerUtils();
    
    // Current targets
    private List<EntityLivingBase> targets = new ArrayList<>();
    private EntityLivingBase currentTarget = null;
    
    // Rotation data
    private float currentYaw = 0f;
    private float currentPitch = 0f;
    private boolean rotating = false;
    
    /**
     * Constructor
     */
    public KillAura() {
        super("KillAura", "Automatically attacks entities around you.", Category.COMBAT);
    }
    
    /**
     * Motion event handler
     * @param event Motion event
     */
    @EventTarget
    public void onMotion(MotionEvent event) {
        // Update targets
        updateTargets();
        
        // Check if we have targets
        if (targets.isEmpty() || currentTarget == null) {
            return;
        }
        
        // Handle rotations
        if (rotationsValue.get()) {
            updateRotations();
            
            // Apply rotations if not silent
            if (!silentRotationsValue.get()) {
                mc.player.rotationYaw = currentYaw;
                mc.player.rotationPitch = currentPitch;
            }
        }
        
        // Attack target
        attackTarget();
    }
    
    /**
     * Render event handler
     * @param event Render event
     */
    @EventTarget
    public void onRender(RenderEvent event) {
        // Render target ESP or other visual indicators here
    }
    
    /**
     * Updates the list of potential targets
     */
    private void updateTargets() {
        // Get range
        float range = rangeValue.get();
        
        // Get all possible targets
        List<EntityLivingBase> potentialTargets = new ArrayList<>();
        
        for (Entity entity : mc.world.loadedEntityList) {
            // Check if entity is valid
            if (!(entity instanceof EntityLivingBase)) {
                continue;
            }
            
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            
            // Check if entity is alive and not us
            if (livingBase.isDead || livingBase.getHealth() <= 0 || livingBase == mc.player) {
                continue;
            }
            
            // Check if entity is in range
            if (mc.player.getDistance(livingBase) > range) {
                continue;
            }
            
            // Check if we can see the entity
            if (!throughWallsValue.get() && !mc.player.canEntityBeSeen(livingBase)) {
                continue;
            }
            
            // Check entity type filters
            if (livingBase instanceof EntityPlayer && targetsPlayersValue.get()) {
                potentialTargets.add(livingBase);
            } else if (livingBase instanceof EntityMob && targetsMobsValue.get()) {
                potentialTargets.add(livingBase);
            } else if (livingBase instanceof EntityAnimal && targetsAnimalsValue.get()) {
                potentialTargets.add(livingBase);
            }
            
            // Check invisibility
            if (!targetsInvisibleValue.get() && livingBase.isInvisible()) {
                potentialTargets.remove(livingBase);
            }
        }
        
        // Sort targets based on priority
        switch (priorityValue.get()) {
            case "Distance":
                potentialTargets.sort(Comparator.comparingDouble(entity -> mc.player.getDistance(entity)));
                break;
            case "Health":
                potentialTargets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Direction":
                potentialTargets.sort(Comparator.comparingDouble(this::getDistanceToEntityLook));
                break;
        }
        
        // Limit targets
        targets = potentialTargets.stream()
                .limit(maxTargetsValue.get())
                .collect(Collectors.toList());
        
        // Update current target
        currentTarget = targets.isEmpty() ? null : targets.get(0);
    }
    
    /**
     * Calculates the distance to an entity based on where the player is looking
     * @param entity The entity
     * @return The distance
     */
    private double getDistanceToEntityLook(EntityLivingBase entity) {
        EntityPlayerSP player = mc.player;
        
        // Yaw and pitch to target
        float[] rotations = RotationUtils.getRotations(entity);
        
        // Current player rotations
        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;
        
        // Calculate angle difference
        float yawDiff = MathHelper.wrapDegrees(rotations[0] - yaw);
        float pitchDiff = MathHelper.wrapDegrees(rotations[1] - pitch);
        
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }
    
    /**
     * Updates rotation values based on current target
     */
    private void updateRotations() {
        if (currentTarget == null) {
            return;
        }
        
        // Get rotations to target
        float[] rotations = RotationUtils.getRotations(currentTarget);
        
        // Apply smoothing if enabled
        if (rotationModeValue.get().equals("Smooth")) {
            float yawDiff = MathHelper.wrapDegrees(rotations[0] - currentYaw);
            float pitchDiff = MathHelper.wrapDegrees(rotations[1] - currentPitch);
            
            float smoothing = rotationSmoothingValue.get();
            
            currentYaw += yawDiff / smoothing;
            currentPitch += pitchDiff / smoothing;
        } else if (rotationModeValue.get().equals("Instant")) {
            currentYaw = rotations[0];
            currentPitch = rotations[1];
        }
        
        // Fix rotations
        currentYaw = MathHelper.wrapDegrees(currentYaw);
        currentPitch = MathHelper.clamp(currentPitch, -90f, 90f);
        
        rotating = true;
    }
    
    /**
     * Attacks the current target
     */
    private void attackTarget() {
        if (currentTarget == null) {
            return;
        }
        
        // Calculate CPS delay
        int cps = cpsValue.get();
        
        // Smart CPS adjusts CPS based on ping
        if (smartCPSValue.get()) {
            // Get ping and adjust CPS
            int ping = mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
            
            // Lower CPS if high ping
            if (ping > 100) {
                cps = Math.max(5, cps - (ping / 50));
            }
        }
        
        // Attack delay in milliseconds
        long attackDelay = 1000 / cps;
        
        // Check if we can attack
        if (attackTimer.hasTimePassed(attackDelay)) {
            // Reset timer
            attackTimer.reset();
            
            // Attack the entity
            if (rotationsValue.get() && !rotating) {
                return;
            }
            
            // Attack packet
            mc.getConnection().sendPacket(new CPacketUseEntity(currentTarget, CPacketUseEntity.Action.ATTACK));
            
            // Swing arm
            if (swingValue.get()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else {
                mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            
            // Auto block if enabled
            if (autoBlockValue.get()) {
                // Would implement blocking here, but mechanics changed in newer versions
            }
        }
    }
    
    /**
     * Gets the current target
     * @return The current target
     */
    public EntityLivingBase getCurrentTarget() {
        return currentTarget;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
        // Reset targets
        targets.clear();
        currentTarget = null;
        rotating = false;
    }
}
