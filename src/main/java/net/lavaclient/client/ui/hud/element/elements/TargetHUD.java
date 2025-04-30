package net.lavaclient.client.ui.hud.element.elements;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.module.modules.combat.KillAura;
import net.lavaclient.client.ui.hud.element.HUDElement;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Target HUD element showing information about the current target
 */
public class TargetHUD extends HUDElement {
    // Target HUD styles
    private enum Style {
        LAVA,
        ASTOLFO,
        LIQUID,
        MODERN
    }
    
    // Format for decimals
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");
    
    // Current style
    private Style style = Style.LAVA;
    
    // Colors
    private int backgroundColor = new Color(0, 0, 0, 160).getRGB();
    private int healthColor1 = new Color(255, 50, 50).getRGB();
    private int healthColor2 = new Color(255, 150, 50).getRGB();
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     */
    public TargetHUD(int x, int y) {
        super(x, y);
        this.width = 140;
        this.height = 40;
    }
    
    /**
     * Renders the target HUD
     * @param scaledResolution Scaled resolution
     */
    @Override
    public void render(ScaledResolution scaledResolution) {
        // Get target
        EntityLivingBase target = getTarget();
        
        // If no target, don't render
        if (target == null) {
            return;
        }
        
        // Render based on style
        switch (style) {
            case LAVA:
                renderLavaStyle(target);
                break;
            case ASTOLFO:
                renderAstolfoStyle(target);
                break;
            case LIQUID:
                renderLiquidStyle(target);
                break;
            case MODERN:
                renderModernStyle(target);
                break;
        }
    }
    
    /**
     * Renders the Lava style target HUD
     * @param target The target entity
     */
    private void renderLavaStyle(EntityLivingBase target) {
        // Set dimensions
        this.width = 140;
        this.height = 40;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = Math.min(1, health / maxHealth);
        
        // Background
        RenderUtils.drawRect(x, y, x + width, y + height, backgroundColor);
        
        // Border
        RenderUtils.drawGradientRect(x, y, x + width, y + 1, healthColor1, healthColor2);
        
        // Health bar background
        RenderUtils.drawRect(x + 35, y + 23, x + width - 5, y + 33, new Color(40, 40, 40, 160).getRGB());
        
        // Health bar
        RenderUtils.drawGradientRect(
                x + 35, y + 23,
                x + 35 + (int)((width - 40) * healthPercentage), y + 33,
                healthColor1, healthColor2
        );
        
        // Target name and health
        String healthText = decimalFormat.format(health) + " HP";
        fontRenderer.drawStringWithShadow(target.getName(), x + 35, y + 5, -1);
        fontRenderer.drawStringWithShadow(healthText, x + 35, y + 15, getHealthColor(health, maxHealth));
        
        // Draw player face
        drawPlayerFace(x + 5, y + 5, 30, target);
    }
    
    /**
     * Renders the Astolfo style target HUD
     * @param target The target entity
     */
    private void renderAstolfoStyle(EntityLivingBase target) {
        // Set dimensions
        this.width = 120;
        this.height = 50;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = Math.min(1, health / maxHealth);
        
        // Background
        RenderUtils.drawBorderedRect(x, y, x + width, y + height, 1, 
                new Color(30, 30, 30, 200).getRGB(), new Color(60, 60, 60, 255).getRGB());
        
        // Health text
        String healthText = decimalFormat.format(health) + "/" + decimalFormat.format(maxHealth);
        
        // Target name and health
        fontRenderer.drawStringWithShadow(target.getName(), 
                x + width / 2 - fontRenderer.getStringWidth(target.getName()) / 2, y + 5, -1);
                
        fontRenderer.drawStringWithShadow(healthText, 
                x + width / 2 - fontRenderer.getStringWidth(healthText) / 2, y + 15, getHealthColor(health, maxHealth));
        
        // Health bar
        float barWidth = width - 10;
        RenderUtils.drawRect(x + 5, y + 28, x + 5 + barWidth, y + 33, new Color(40, 40, 40, 160).getRGB());
        RenderUtils.drawRect(x + 5, y + 28, x + 5 + barWidth * healthPercentage, y + 33, getHealthColor(health, maxHealth));
        
        // Distance
        String distanceText = "Distance: " + decimalFormat.format(mc.player.getDistance(target));
        fontRenderer.drawStringWithShadow(distanceText, 
                x + width / 2 - fontRenderer.getStringWidth(distanceText) / 2, y + 38, -1);
    }
    
    /**
     * Renders the Liquid style target HUD
     * @param target The target entity
     */
    private void renderLiquidStyle(EntityLivingBase target) {
        // Set dimensions
        this.width = 150;
        this.height = 55;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = Math.min(1, health / maxHealth);
        
        // Background with rounded corners
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, 3, backgroundColor);
        
        // Target name and health
        fontRenderer.drawStringWithShadow(target.getName(), x + 30, y + 5, -1);
        
        // Health bar
        RenderUtils.drawRoundedRect(x + 30, y + 15, x + width - 5, y + 25, 3, new Color(40, 40, 40, 160).getRGB());
        RenderUtils.drawRoundedRect(x + 30, y + 15, x + 30 + (int)((width - 35) * healthPercentage), y + 25, 3, getHealthColor(health, maxHealth));
        
        // Health text
        String healthText = decimalFormat.format(health) + " HP";
        fontRenderer.drawStringWithShadow(healthText, x + 30, y + 30, getHealthColor(health, maxHealth));
        
        // Distance
        String distanceText = decimalFormat.format(mc.player.getDistance(target)) + " blocks away";
        fontRenderer.drawStringWithShadow(distanceText, x + 30, y + 40, -1);
        
        // Draw player face
        drawPlayerFace(x + 5, y + 5, 20, target);
        
        // Draw armor if target is a player
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            drawPlayerArmor(x + 30, y + 40, player);
        }
    }
    
    /**
     * Renders the Modern style target HUD
     * @param target The target entity
     */
    private void renderModernStyle(EntityLivingBase target) {
        // Set dimensions
        this.width = 160;
        this.height = 60;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = Math.min(1, health / maxHealth);
        
        // Background
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, 5, new Color(20, 20, 20, 190).getRGB());
        
        // Title
        RenderUtils.drawGradientRect(x, y, x + width, y + 18, 
                new Color(30, 30, 30, 220).getRGB(), new Color(30, 30, 30, 180).getRGB());
                
        fontRenderer.drawStringWithShadow("TARGETING", 
                x + width / 2 - fontRenderer.getStringWidth("TARGETING") / 2, y + 5, healthColor1);
        
        // Target name and health
        fontRenderer.drawStringWithShadow(target.getName(), x + 40, y + 22, -1);
        
        // Health text
        String healthText = decimalFormat.format(health) + " / " + decimalFormat.format(maxHealth);
        fontRenderer.drawStringWithShadow(healthText, 
                x + width - fontRenderer.getStringWidth(healthText) - 5, y + 22, getHealthColor(health, maxHealth));
        
        // Health bar
        RenderUtils.drawRoundedRect(x + 40, y + 35, x + width - 5, y + 40, 2, new Color(40, 40, 40, 160).getRGB());
        RenderUtils.drawRoundedRect(x + 40, y + 35, x + 40 + (int)((width - 45) * healthPercentage), y + 40, 2, getHealthColor(health, maxHealth));
        
        // Distance
        String distanceText = decimalFormat.format(mc.player.getDistance(target)) + " blocks";
        fontRenderer.drawStringWithShadow(distanceText, x + 40, y + 45, -1);
        
        // Draw player face
        drawPlayerFace(x + 5, y + 22, 30, target);
    }
    
    /**
     * Draws a player's face
     * @param x X position
     * @param y Y position
     * @param size Size
     * @param entity Entity
     */
    private void drawPlayerFace(int x, int y, int size, EntityLivingBase entity) {
        // In a real implementation, this would render the player's head texture
        // For this example, draw a placeholder
        RenderUtils.drawRect(x, y, x + size, y + size, new Color(70, 70, 70, 160).getRGB());
        
        if (entity instanceof EntityPlayer) {
            // If this was a real implementation, we would draw:
            // 1. Bind the player's skin texture
            // 2. Draw the head portion of the skin
            // For this example, draw a simple face
            int margin = size / 8;
            
            // Face background
            RenderUtils.drawRect(x + margin, y + margin, x + size - margin, y + size - margin, 
                    new Color(200, 170, 140).getRGB());
            
            // Eyes
            int eyeSize = size / 8;
            int eyeY = y + size / 3;
            RenderUtils.drawRect(x + size / 3 - eyeSize / 2, eyeY, x + size / 3 + eyeSize / 2, eyeY + eyeSize, 
                    new Color(40, 40, 40).getRGB());
            RenderUtils.drawRect(x + 2 * size / 3 - eyeSize / 2, eyeY, x + 2 * size / 3 + eyeSize / 2, eyeY + eyeSize, 
                    new Color(40, 40, 40).getRGB());
            
            // Mouth
            int mouthY = y + 2 * size / 3;
            RenderUtils.drawRect(x + size / 3, mouthY, x + 2 * size / 3, mouthY + eyeSize / 2, 
                    new Color(40, 40, 40).getRGB());
        }
    }
    
    /**
     * Draws a player's armor
     * @param x X position
     * @param y Y position
     * @param player Player
     */
    private void drawPlayerArmor(int x, int y, EntityPlayer player) {
        // This function would render the player's armor items
        // For this example, just draw placeholders
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory.get(3 - i);
            if (stack != null && !stack.isEmpty()) {
                // In a real implementation, this would render the armor item
                RenderUtils.drawRect(x + i * 16, y, x + i * 16 + 14, y + 14, 
                        new Color(50, 50, 50, 160).getRGB());
            }
        }
        
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
    
    /**
     * Gets a color based on health percentage
     * @param health Current health
     * @param maxHealth Maximum health
     * @return Color as RGB int
     */
    private int getHealthColor(float health, float maxHealth) {
        float percentage = Math.min(1, health / maxHealth);
        
        if (percentage > 0.75f) {
            return new Color(0, 200, 0).getRGB(); // Green for high health
        } else if (percentage > 0.5f) {
            return new Color(200, 200, 0).getRGB(); // Yellow for medium health
        } else if (percentage > 0.25f) {
            return new Color(200, 100, 0).getRGB(); // Orange for low health
        } else {
            return new Color(200, 0, 0).getRGB(); // Red for critical health
        }
    }
    
    /**
     * Gets the current target
     * @return Target entity
     */
    private EntityLivingBase getTarget() {
        // First check if KillAura has a target
        KillAura killAura = (KillAura) LavaClient.getInstance().getModuleManager().getModule(KillAura.class);
        if (killAura != null && killAura.getState()) {
            return killAura.getCurrentTarget();
        }
        
        // If no KillAura target, check for nearby players
        EntityLivingBase nearestEntity = null;
        float nearestDistance = 100f;
        
        for (EntityLivingBase entity : mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .collect(java.util.stream.Collectors.toList())) {
            
            if (entity != mc.player && !entity.isDead && entity.getHealth() > 0) {
                float distance = mc.player.getDistance(entity);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestEntity = entity;
                }
            }
        }
        
        if (nearestDistance <= 10f) {
            return nearestEntity;
        }
        
        return null;
    }
    
    /**
     * Sets the target HUD style
     * @param style New style
     */
    public void setStyle(Style style) {
        this.style = style;
    }
    
    /**
     * Sets the background color
     * @param color New color
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }
    
    /**
     * Sets the health bar colors
     * @param color1 First color
     * @param color2 Second color
     */
    public void setHealthColors(int color1, int color2) {
        this.healthColor1 = color1;
        this.healthColor2 = color2;
    }
}
