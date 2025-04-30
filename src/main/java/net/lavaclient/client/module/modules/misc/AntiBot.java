package net.lavaclient.client.module.modules.misc;

import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.PacketEvent;
import net.lavaclient.client.event.events.UpdateEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;

import java.util.*;

/**
 * AntiBot module to detect and filter server anti-cheat bots
 */
public class AntiBot extends Module {
    // Detection methods
    private final ListValue modeValue = addValue(new ListValue("Mode", new String[] {
            "Advanced", "Hypixel", "MinePlexRange", "LivingTime", "Ground", "Air", "MultiCheck"
    }, "Advanced"));
    
    // Advanced mode settings
    private final BoolValue tabListCheckValue = addValue(new BoolValue("TabListCheck", true));
    private final BoolValue duplicateNameCheckValue = addValue(new BoolValue("DuplicateNameCheck", true));
    private final BoolValue duplicateIdCheckValue = addValue(new BoolValue("DuplicateIdCheck", true));
    private final BoolValue namePatternCheckValue = addValue(new BoolValue("NamePatternCheck", true));
    private final BoolValue livingTimeCheckValue = addValue(new BoolValue("LivingTimeCheck", true));
    private final BoolValue groundCheckValue = addValue(new BoolValue("GroundCheck", false));
    private final BoolValue airCheckValue = addValue(new BoolValue("AirCheck", false));
    private final BoolValue invalidGoundCheckValue = addValue(new BoolValue("InvalidGroundCheck", true));
    private final BoolValue swingCheckValue = addValue(new BoolValue("SwingCheck", false));
    private final BoolValue healthCheckValue = addValue(new BoolValue("HealthCheck", false));
    private final BoolValue derp2CheckValue = addValue(new BoolValue("DerpCheck", true));
    private final BoolValue colorCheckValue = addValue(new BoolValue("ColorCheck", false));
    private final BoolValue gamemodeCheckValue = addValue(new BoolValue("GamemodeCheck", false));
    private final BoolValue pingCheckValue = addValue(new BoolValue("PingCheck", false));
    
    // Detection thresholds
    private final IntegerValue livingTimeValue = addValue(new IntegerValue("LivingTime", 0, 0, 1000));
    private final IntegerValue minPlexRangeValue = addValue(new IntegerValue("MinePlexRange", 2, 0, 10));
    
    // Storage for bot detection
    private final Map<UUID, Long> spawnTimeMap = new HashMap<>();
    private final Set<UUID> botSet = new HashSet<>();
    private final List<UUID> groundList = new ArrayList<>();
    private final List<UUID> airList = new ArrayList<>();
    private final List<UUID> invalidGroundList = new ArrayList<>();
    private final List<UUID> swingList = new ArrayList<>();
    private final List<EntityPlayer> detectedBots = new ArrayList<>();
    
    /**
     * Constructor
     */
    public AntiBot() {
        super("AntiBot", "Detects and filters server anti-cheat bots.", Category.MISC);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        // Clear all data
        clearAll();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
        // Clear all data
        clearAll();
    }
    
    /**
     * Clear all detection data
     */
    private void clearAll() {
        spawnTimeMap.clear();
        botSet.clear();
        groundList.clear();
        airList.clear();
        invalidGroundList.clear();
        swingList.clear();
        detectedBots.clear();
    }
    
    /**
     * Update event handler
     * @param event Update event
     */
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        // Clear lists periodically to prevent memory leaks
        if (mc.player.ticksExisted % 200 == 0) {
            List<UUID> toRemove = new ArrayList<>();
            
            for (UUID uuid : spawnTimeMap.keySet()) {
                if (System.currentTimeMillis() - spawnTimeMap.get(uuid) > 30000) { // 30 seconds
                    toRemove.add(uuid);
                }
            }
            
            for (UUID uuid : toRemove) {
                spawnTimeMap.remove(uuid);
                botSet.remove(uuid);
                groundList.remove(uuid);
                airList.remove(uuid);
                invalidGroundList.remove(uuid);
                swingList.remove(uuid);
            }
        }
        
        // Update based on detection mode
        updateDetectedBots();
    }
    
    /**
     * Packet event handler
     * @param event Packet event
     */
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!event.isIncoming()) {
            return;
        }
        
        // Track player spawn packets
        if (event.getPacket() instanceof SPacketSpawnPlayer) {
            SPacketSpawnPlayer packet = (SPacketSpawnPlayer) event.getPacket();
            spawnTimeMap.put(packet.getUniqueId(), System.currentTimeMillis());
            
            if (livingTimeCheckValue.get() || modeValue.get().equals("LivingTime") || modeValue.get().equals("MultiCheck")) {
                botSet.add(packet.getUniqueId());
            }
        }
    }
    
    /**
     * Update detected bots based on the selected mode
     */
    private void updateDetectedBots() {
        detectedBots.clear();
        
        // Detect bots based on mode
        switch (modeValue.get()) {
            case "Advanced":
                advancedCheck();
                break;
                
            case "Hypixel":
                hypixelCheck();
                break;
                
            case "MinePlexRange":
                minePlexRangeCheck();
                break;
                
            case "LivingTime":
                livingTimeCheck();
                break;
                
            case "Ground":
                groundCheck();
                break;
                
            case "Air":
                airCheck();
                break;
                
            case "MultiCheck":
                multiCheck();
                break;
        }
    }
    
    /**
     * Advanced bot detection
     */
    private void advancedCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            boolean isBot = false;
            
            // Tab list check
            if (tabListCheckValue.get()) {
                boolean inTabList = isInTabList(player);
                if (!inTabList) {
                    isBot = true;
                }
            }
            
            // Name pattern check (detects common bot naming patterns)
            if (namePatternCheckValue.get()) {
                String name = player.getName();
                if (name.matches("\\w{8,16}") || name.matches("\\w{1,3}\\d{1,5}")) {
                    isBot = true;
                }
            }
            
            // Living time check
            if (livingTimeCheckValue.get()) {
                if (botSet.contains(player.getUniqueID()) && 
                    System.currentTimeMillis() - spawnTimeMap.getOrDefault(player.getUniqueID(), 0L) < livingTimeValue.get()) {
                    isBot = true;
                }
            }
            
            // Ground check
            if (groundCheckValue.get()) {
                if (!groundList.contains(player.getUniqueID()) && player.onGround) {
                    groundList.add(player.getUniqueID());
                }
                
                if (!groundList.contains(player.getUniqueID())) {
                    isBot = true;
                }
            }
            
            // Invalid ground check
            if (invalidGoundCheckValue.get()) {
                if (player.onGround && player.posY % 0.5 != 0) {
                    if (!invalidGroundList.contains(player.getUniqueID())) {
                        invalidGroundList.add(player.getUniqueID());
                    }
                }
                
                if (invalidGroundList.contains(player.getUniqueID())) {
                    isBot = true;
                }
            }
            
            // Health check
            if (healthCheckValue.get() && player.getHealth() > 20) {
                isBot = true;
            }
            
            // Ping check
            if (pingCheckValue.get()) {
                NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUniqueID());
                if (playerInfo != null && playerInfo.getResponseTime() == 0) {
                    isBot = true;
                }
            }
            
            if (isBot) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Hypixel bot detection
     */
    private void hypixelCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            
            if (!isInTabList(player) || player.getName().contains("§") || 
                (player.getName().startsWith("[NPC]") && derp2CheckValue.get())) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * MinePlex range-based detection
     */
    private void minePlexRangeCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            
            if (mc.player.getDistance(player) <= minPlexRangeValue.get() && !isInTabList(player)) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Living time detection
     */
    private void livingTimeCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            
            if (botSet.contains(player.getUniqueID()) && 
                System.currentTimeMillis() - spawnTimeMap.getOrDefault(player.getUniqueID(), 0L) < livingTimeValue.get()) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Ground-based detection
     */
    private void groundCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            
            if (!groundList.contains(player.getUniqueID()) && player.onGround) {
                groundList.add(player.getUniqueID());
            }
            
            if (!groundList.contains(player.getUniqueID())) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Air-based detection
     */
    private void airCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            
            if (!airList.contains(player.getUniqueID()) && !player.onGround) {
                airList.add(player.getUniqueID());
            }
            
            if (!airList.contains(player.getUniqueID())) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Multiple check combination
     */
    private void multiCheck() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) {
                continue;
            }
            
            EntityPlayer player = (EntityPlayer) entity;
            int flags = 0;
            
            // Count flags
            if (!isInTabList(player)) flags++;
            if (botSet.contains(player.getUniqueID()) && 
                System.currentTimeMillis() - spawnTimeMap.getOrDefault(player.getUniqueID(), 0L) < livingTimeValue.get()) flags++;
            if (player.onGround && player.posY % 0.5 != 0) flags++;
            if (player.getHealth() > 20) flags++;
            
            // Flag threshold
            if (flags >= 2) {
                detectedBots.add(player);
            }
        }
    }
    
    /**
     * Checks if a player is in the tab list
     * @param player The player
     * @return Whether the player is in the tab list
     */
    private boolean isInTabList(EntityPlayer player) {
        return mc.getConnection().getPlayerInfo(player.getUniqueID()) != null;
    }
    
    /**
     * Checks if a player is a bot
     * @param player The player
     * @return Whether the player is a bot
     */
    public boolean isBot(EntityPlayer player) {
        return detectedBots.contains(player);
    }
}
