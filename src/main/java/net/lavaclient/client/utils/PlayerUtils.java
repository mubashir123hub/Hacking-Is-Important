package net.lavaclient.client.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for player related operations
 */
public class PlayerUtils {
    // Minecraft instance
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * Gets the player's health
     * @return Health including absorption
     */
    public static float getHealth() {
        if (mc.player == null) {
            return 0.0f;
        }
        
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }
    
    /**
     * Gets the ground position below the player
     * @return Ground position
     */
    public static BlockPos getGroundPos() {
        if (mc.player == null) {
            return BlockPos.ORIGIN;
        }
        
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        
        // Check if the player is already on ground
        if (isOnGround()) {
            return pos.down();
        }
        
        // Find the ground
        BlockPos groundPos = pos;
        
        for (int i = 0; i < 256; i++) {
            groundPos = groundPos.down();
            
            if (Block.getIdFromBlock(mc.world.getBlockState(groundPos).getBlock()) != 0) {
                return groundPos;
            }
            
            // Safety check
            if (groundPos.getY() <= 0) {
                break;
            }
        }
        
        return pos.down();
    }
    
    /**
     * Checks if the player is on ground
     * @return Whether the player is on ground
     */
    public static boolean isOnGround() {
        if (mc.player == null) {
            return false;
        }
        
        return mc.player.onGround;
    }
    
    /**
     * Gets the active potion effect level (amplifier + 1)
     * @param potion The potion
     * @return Potion effect level or 0 if not active
     */
    public static int getPotionLevel(Potion potion) {
        if (mc.player == null) {
            return 0;
        }
        
        PotionEffect effect = mc.player.getActivePotionEffect(potion);
        
        if (effect != null) {
            return effect.getAmplifier() + 1;
        }
        
        return 0;
    }
    
    /**
     * Gets whether the player has a specific potion effect
     * @param potion The potion
     * @return Whether the potion is active
     */
    public static boolean hasPotionEffect(Potion potion) {
        if (mc.player == null) {
            return false;
        }
        
        return mc.player.isPotionActive(potion);
    }
    
    /**
     * Gets the item that the player is holding
     * @param hand The hand
     * @return The item
     */
    public static Item getHeldItem(EnumHand hand) {
        if (mc.player == null) {
            return null;
        }
        
        ItemStack stack = mc.player.getHeldItem(hand);
        
        if (stack.isEmpty()) {
            return null;
        }
        
        return stack.getItem();
    }
    
    /**
     * Gets the player's ping to the server
     * @return Ping in milliseconds
     */
    public static int getPing() {
        if (mc.player == null || mc.getConnection() == null) {
            return 0;
        }
        
        try {
            return mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Finds the closest player within a specified range
     * @param range Range to search
     * @return The closest player or null if none found
     */
    public static EntityPlayer getClosestPlayer(float range) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        
        EntityPlayer closestPlayer = null;
        float closestDistance = range;
        
        for (EntityPlayer player : mc.world.playerEntities) {
            // Don't target ourselves
            if (player == mc.player) {
                continue;
            }
            
            // Don't target dead players
            if (player.isDead || player.getHealth() <= 0) {
                continue;
            }
            
            float distance = mc.player.getDistance(player);
            
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }
        
        return closestPlayer;
    }
    
    /**
     * Gets a list of players sorted by distance
     * @param range Range to search
     * @return Sorted list of players
     */
    public static List<EntityPlayer> getSortedPlayers(float range) {
        if (mc.player == null || mc.world == null) {
            return Collections.emptyList();
        }
        
        List<EntityPlayer> players = new ArrayList<>();
        
        for (EntityPlayer player : mc.world.playerEntities) {
            // Don't include ourselves
            if (player == mc.player) {
                continue;
            }
            
            // Don't include dead players
            if (player.isDead || player.getHealth() <= 0) {
                continue;
            }
            
            float distance = mc.player.getDistance(player);
            
            if (distance <= range) {
                players.add(player);
            }
        }
        
        // Sort by distance
        players.sort((p1, p2) -> Float.compare(mc.player.getDistance(p1), mc.player.getDistance(p2)));
        
        return players;
    }
    
    /**
     * Gets the player's eye position
     * @return Eye position vector
     */
    public static Vec3d getEyePos() {
        if (mc.player == null) {
            return Vec3d.ZERO;
        }
        
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }
    
    /**
     * Gets an entity's eye position
     * @param entity The entity
     * @return Eye position vector
     */
    public static Vec3d getEyePos(Entity entity) {
        if (entity == null) {
            return Vec3d.ZERO;
        }
        
        return new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }
    
    /**
     * Gets the block position that the player is looking at
     * @param range Range to check
     * @return The block position or null if none found
     */
    public static BlockPos getLookingBlock(float range) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        
        Vec3d eyePos = getEyePos();
        Vec3d lookVec = mc.player.getLook(1.0f);
        Vec3d endPos = eyePos.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        
        net.minecraft.util.math.RayTraceResult result = mc.world.rayTraceBlocks(eyePos, endPos, false, false, true);
        
        if (result == null || result.typeOfHit != net.minecraft.util.math.RayTraceResult.Type.BLOCK) {
            return null;
        }
        
        return result.getBlockPos();
    }
    
    /**
     * Gets the block position that the player is looking at and its face
     * @param range Range to check
     * @return The block position and face or null if none found
     */
    public static BlockPosWithFacing getLookingBlockWithFace(float range) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        
        Vec3d eyePos = getEyePos();
        Vec3d lookVec = mc.player.getLook(1.0f);
        Vec3d endPos = eyePos.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        
        net.minecraft.util.math.RayTraceResult result = mc.world.rayTraceBlocks(eyePos, endPos, false, false, true);
        
        if (result == null || result.typeOfHit != net.minecraft.util.math.RayTraceResult.Type.BLOCK) {
            return null;
        }
        
        return new BlockPosWithFacing(result.getBlockPos(), result.sideHit);
    }
    
    /**
     * Class to store a block position and its face
     */
    public static class BlockPosWithFacing {
        private final BlockPos pos;
        private final EnumFacing facing;
        
        public BlockPosWithFacing(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }
        
        public BlockPos getPos() {
            return pos;
        }
        
        public EnumFacing getFacing() {
            return facing;
        }
    }
    
    /**
     * Checks if the player can place a block at the specified position
     * @param pos Block position
     * @return Whether the player can place a block
     */
    public static boolean canPlaceBlock(BlockPos pos) {
        if (mc.player == null || mc.world == null) {
            return false;
        }
        
        // Check if the block is replaceable
        if (!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)) {
            return false;
        }
        
        // Check if the position is outside world height limits
        if (pos.getY() < 0 || pos.getY() >= 256) {
            return false;
        }
        
        // Check if the position collides with any entity
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, Block.FULL_BLOCK_AABB.offset(pos))) {
            if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the slot index of an item in the player's inventory
     * @param item The item
     * @return Slot index or -1 if not found
     */
    public static int getItemSlot(Item item) {
        if (mc.player == null) {
            return -1;
        }
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Checks if the player is in a liquid
     * @return Whether the player is in a liquid
     */
    public static boolean isInLiquid() {
        if (mc.player == null) {
            return false;
        }
        
        return mc.player.isInWater() || mc.player.isInLava();
    }
}
