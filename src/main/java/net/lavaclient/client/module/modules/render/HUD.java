package net.lavaclient.client.module.modules.render;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.event.EventTarget;
import net.lavaclient.client.event.events.RenderEvent;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.module.modules.combat.KillAura;
import net.lavaclient.client.ui.hud.HUDManager;
import net.lavaclient.client.utils.RenderUtils;
import net.lavaclient.client.value.BoolValue;
import net.lavaclient.client.value.FloatValue;
import net.lavaclient.client.value.IntegerValue;
import net.lavaclient.client.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enhanced HUD module for rendering in-game UI
 */
public class HUD extends Module {
    // Primary settings
    private final BoolValue logoValue = addValue(new BoolValue("Logo", true));
    private final BoolValue arraylistValue = addValue(new BoolValue("ArrayList", true));
    private final BoolValue effectsValue = addValue(new BoolValue("Effects", true));
    private final BoolValue infoValue = addValue(new BoolValue("Info", true));
    private final BoolValue targetHUDValue = addValue(new BoolValue("TargetHUD", true));
    private final BoolValue armorValue = addValue(new BoolValue("Armor", true));
    private final BoolValue scoreboardValue = addValue(new BoolValue("Scoreboard", true));
    
    // Style settings
    private final ListValue colorModeValue = addValue(new ListValue("ColorMode", new String[] {"Custom", "Rainbow", "Fade", "Astolfo", "Gradient"}, "Gradient"));
    private final IntegerValue redValue = addValue(new IntegerValue("Red", 255, 0, 255));
    private final IntegerValue greenValue = addValue(new IntegerValue("Green", 50, 0, 255));
    private final IntegerValue blueValue = addValue(new IntegerValue("Blue", 100, 0, 255));
    private final IntegerValue alphaValue = addValue(new IntegerValue("Alpha", 255, 0, 255));
    private final FloatValue saturationValue = addValue(new FloatValue("Saturation", 0.9f, 0.0f, 1.0f));
    private final FloatValue brightnessValue = addValue(new FloatValue("Brightness", 1.0f, 0.0f, 1.0f));
    private final IntegerValue hueShiftValue = addValue(new IntegerValue("HueShift", 10, 0, 100));
    
    // Target HUD settings
    private final ListValue targetHUDStyleValue = addValue(new ListValue("TargetHUDStyle", new String[] {"Lava", "Astolfo", "Liquid", "Modern"}, "Lava"));
    private final ListValue targetHUDPositionValue = addValue(new ListValue("TargetHUDPosition", new String[] {"Left", "Center", "Right"}, "Center"));
    
    // Animation settings
    private final BoolValue animationValue = addValue(new BoolValue("Animation", true));
    private final FloatValue animationSpeedValue = addValue(new FloatValue("AnimationSpeed", 0.5f, 0.0f, 1.0f));
    
    // Font settings
    private final ListValue fontValue = addValue(new ListValue("Font", new String[] {"Minecraft", "Lava", "Roboto", "Comfortaa"}, "Lava"));
    
    // Watermark settings
    private final ListValue watermarkValue = addValue(new ListValue("Watermark", new String[] {"LavaClient", "LavaFork", "None"}, "LavaClient"));
    
    // Custom date format for info display
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    // Format for decimal numbers
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");
    
    // Logo resource
    private final ResourceLocation logo = new ResourceLocation("textures/lava_client_logo.svg");
    
    /**
     * Constructor
     */
    public HUD() {
        super("HUD", "Displays in-game user interface elements", Category.RENDER);
    }
    
    /**
     * Render event handler
     * @param event Render event
     */
    @EventTarget
    public void onRender(RenderEvent event) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }
        
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        FontRenderer fontRenderer = mc.fontRenderer;
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        
        GlStateManager.pushMatrix();
        
        // Render logo if enabled
        if (logoValue.get()) {
            renderLogo(scaledResolution);
        }
        
        // Render watermark if not None
        if (!watermarkValue.get().equals("None")) {
            renderWatermark(scaledResolution);
        }
        
        // Render module arraylist if enabled
        if (arraylistValue.get()) {
            renderArraylist(scaledResolution);
        }
        
        // Render potion effects if enabled
        if (effectsValue.get()) {
            renderEffects(scaledResolution);
        }
        
        // Render info if enabled
        if (infoValue.get()) {
            renderInfo(scaledResolution);
        }
        
        // Render target HUD if enabled and we have a target
        if (targetHUDValue.get()) {
            EntityLivingBase target = getTarget();
            if (target != null) {
                renderTargetHUD(scaledResolution, target);
            }
        }
        
        // Render armor and hand items if enabled
        if (armorValue.get()) {
            renderArmor(scaledResolution);
        }
        
        // Let the HUD manager render the elements
        LavaClient.getInstance().getHudManager().renderElements();
        
        GlStateManager.popMatrix();
    }
    
    /**
     * Renders the client logo
     * @param sr Scaled resolution
     */
    private void renderLogo(ScaledResolution sr) {
        // In an actual implementation, you would render the logo resource
        // For this example, we'll just render text
        FontRenderer fontRenderer = mc.fontRenderer;
        String clientName = "Lava Client";
        float scale = 2.0f;
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        
        int y = 5;
        int x = 5;
        
        // Render with gradient color
        int color1 = new Color(255, 50, 50).getRGB();
        int color2 = new Color(255, 150, 50).getRGB();
        
        RenderUtils.drawGradientString(clientName, x, y, color1, color2, false);
        
        GlStateManager.popMatrix();
    }
    
    /**
     * Renders the watermark
     * @param sr Scaled resolution
     */
    private void renderWatermark(ScaledResolution sr) {
        FontRenderer fontRenderer = mc.fontRenderer;
        String text;
        
        switch (watermarkValue.get()) {
            case "LavaClient":
                text = "Lava Client " + LavaClient.VERSION;
                break;
            case "LavaFork":
                text = "Lava Fork " + LavaClient.VERSION;
                break;
            default:
                return;
        }
        
        fontRenderer.drawStringWithShadow(text, 2, 2, getColor(0));
    }
    
    /**
     * Renders the module arraylist
     * @param sr Scaled resolution
     */
    private void renderArraylist(ScaledResolution sr) {
        FontRenderer fontRenderer = mc.fontRenderer;
        int y = 5;
        
        // Get enabled modules and sort by name length
        java.util.List<Module> modules = LavaClient.getInstance().getModuleManager().getEnabledModules();
        modules.sort((m1, m2) -> fontRenderer.getStringWidth(m2.getName()) - fontRenderer.getStringWidth(m1.getName()));
        
        // Render each module
        for (Module module : modules) {
            if (module == this) continue; // Don't render HUD module
            
            String name = module.getName();
            float xPos = sr.getScaledWidth() - fontRenderer.getStringWidth(name) - 3;
            
            // Animate if enabled
            if (animationValue.get()) {
                xPos -= (1 - module.getSlideAnimation()) * fontRenderer.getStringWidth(name);
            }
            
            // Get color based on index position
            int color = getColor(y);
            
            // Render background and text
            RenderUtils.drawRect(xPos - 2, y - 1, sr.getScaledWidth(), y + fontRenderer.FONT_HEIGHT, new Color(0, 0, 0, 120).getRGB());
            fontRenderer.drawStringWithShadow(name, xPos, y, color);
            
            y += fontRenderer.FONT_HEIGHT + 1;
        }
    }
    
    /**
     * Renders potion effects
     * @param sr Scaled resolution
     */
    private void renderEffects(ScaledResolution sr) {
        FontRenderer fontRenderer = mc.fontRenderer;
        int y = sr.getScaledHeight() - 25;
        
        // Get active potion effects
        if (mc.player.getActivePotionEffects().isEmpty()) {
            return;
        }
        
        // Render each potion effect
        for (net.minecraft.potion.PotionEffect effect : mc.player.getActivePotionEffects()) {
            net.minecraft.potion.Potion potion = effect.getPotion();
            String name = net.minecraft.client.resources.I18n.format(potion.getName());
            int amplifier = effect.getAmplifier() + 1;
            int duration = effect.getDuration() / 20;
            
            String text = name + " " + amplifier + " - " + duration + "s";
            int color = potion.getLiquidColor();
            
            fontRenderer.drawStringWithShadow(text, sr.getScaledWidth() - fontRenderer.getStringWidth(text) - 2, y, color);
            y -= fontRenderer.FONT_HEIGHT + 1;
        }
    }
    
    /**
     * Renders game and client info
     * @param sr Scaled resolution
     */
    private void renderInfo(ScaledResolution sr) {
        FontRenderer fontRenderer = mc.fontRenderer;
        int y = sr.getScaledHeight() - 10;
        
        // FPS and coordinates
        String fpsText = "FPS: " + Minecraft.getDebugFPS();
        String coordsText = "XYZ: " + decimalFormat.format(mc.player.posX) + " / " 
                + decimalFormat.format(mc.player.posY) + " / " 
                + decimalFormat.format(mc.player.posZ);
        String timeText = "Time: " + dateFormat.format(new Date());
        
        // Render
        fontRenderer.drawStringWithShadow(fpsText, 2, y - 30, getColor(0));
        fontRenderer.drawStringWithShadow(coordsText, 2, y - 20, getColor(1));
        fontRenderer.drawStringWithShadow(timeText, 2, y - 10, getColor(2));
    }
    
    /**
     * Renders target HUD
     * @param sr Scaled resolution
     * @param target Target entity
     */
    private void renderTargetHUD(ScaledResolution sr, EntityLivingBase target) {
        if (target == null) return;
        
        // Position based on setting
        int x;
        switch (targetHUDPositionValue.get()) {
            case "Left":
                x = 5;
                break;
            case "Right":
                x = sr.getScaledWidth() - 150;
                break;
            case "Center":
            default:
                x = sr.getScaledWidth() / 2 - 75;
        }
        
        int y = sr.getScaledHeight() / 2 + 30;
        
        // Decide which style to render
        switch (targetHUDStyleValue.get()) {
            case "Lava":
                renderLavaTargetHUD(x, y, target);
                break;
            case "Astolfo":
                renderAstolfoTargetHUD(x, y, target);
                break;
            case "Liquid":
                renderLiquidTargetHUD(x, y, target);
                break;
            case "Modern":
                renderModernTargetHUD(x, y, target);
                break;
        }
    }
    
    /**
     * Renders Lava style target HUD
     * @param x X position
     * @param y Y position
     * @param target Target entity
     */
    private void renderLavaTargetHUD(int x, int y, EntityLivingBase target) {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        // Target HUD dimensions
        int width = 140;
        int height = 40;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = health / maxHealth;
        
        // Background
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 160).getRGB());
        
        // Border
        RenderUtils.drawGradientRect(x, y, x + width, y + 1, getColor(0), getColor(width));
        
        // Health bar background
        RenderUtils.drawRect(x + 35, y + 23, x + width - 5, y + 33, new Color(40, 40, 40, 160).getRGB());
        
        // Health bar
        RenderUtils.drawGradientRect(
                x + 35, y + 23,
                x + 35 + (int)((width - 40) * healthPercentage), y + 33,
                new Color(255, 50, 50).getRGB(),
                new Color(255, 150, 50).getRGB()
        );
        
        // Target name and health
        String healthText = decimalFormat.format(health) + " HP";
        fontRenderer.drawStringWithShadow(target.getName(), x + 35, y + 5, -1);
        fontRenderer.drawStringWithShadow(healthText, x + 35, y + 15, getHealthColor(health, maxHealth));
        
        // Render target face
        // In an actual implementation, you would render player head or entity model here
        RenderUtils.drawRect(x + 5, y + 5, x + 30, y + 30, new Color(70, 70, 70, 160).getRGB());
    }
    
    /**
     * Renders Astolfo style target HUD
     * @param x X position
     * @param y Y position
     * @param target Target entity
     */
    private void renderAstolfoTargetHUD(int x, int y, EntityLivingBase target) {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        // Target HUD dimensions
        int width = 120;
        int height = 50;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = health / maxHealth;
        
        // Background
        RenderUtils.drawBorderedRect(x, y, x + width, y + height, 1, new Color(30, 30, 30, 200).getRGB(), new Color(60, 60, 60, 255).getRGB());
        
        // Health text
        String healthText = decimalFormat.format(health) + "/" + decimalFormat.format(maxHealth);
        
        // Target name and health
        fontRenderer.drawStringWithShadow(target.getName(), x + width / 2 - fontRenderer.getStringWidth(target.getName()) / 2, y + 5, -1);
        fontRenderer.drawStringWithShadow(healthText, x + width / 2 - fontRenderer.getStringWidth(healthText) / 2, y + 15, getHealthColor(health, maxHealth));
        
        // Health bar
        float barWidth = width - 10;
        RenderUtils.drawRect(x + 5, y + 28, x + 5 + barWidth, y + 33, new Color(40, 40, 40, 160).getRGB());
        RenderUtils.drawRect(x + 5, y + 28, x + 5 + barWidth * healthPercentage, y + 33, getHealthColor(health, maxHealth));
        
        // Distance
        String distanceText = "Distance: " + decimalFormat.format(mc.player.getDistance(target));
        fontRenderer.drawStringWithShadow(distanceText, x + width / 2 - fontRenderer.getStringWidth(distanceText) / 2, y + 38, -1);
    }
    
    /**
     * Renders Liquid style target HUD
     * @param x X position
     * @param y Y position
     * @param target Target entity
     */
    private void renderLiquidTargetHUD(int x, int y, EntityLivingBase target) {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        // Target HUD dimensions
        int width = 150;
        int height = 55;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = health / maxHealth;
        
        // Background with rounded corners
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, 3, new Color(0, 0, 0, 170).getRGB());
        
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
        
        // Face placeholder
        RenderUtils.drawRoundedRect(x + 5, y + 5, x + 25, y + 25, 3, new Color(70, 70, 70, 160).getRGB());
    }
    
    /**
     * Renders Modern style target HUD
     * @param x X position
     * @param y Y position
     * @param target Target entity
     */
    private void renderModernTargetHUD(int x, int y, EntityLivingBase target) {
        FontRenderer fontRenderer = mc.fontRenderer;
        
        // Target HUD dimensions
        int width = 160;
        int height = 60;
        
        // Calculate health percentage
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPercentage = health / maxHealth;
        
        // Background
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, 5, new Color(20, 20, 20, 190).getRGB());
        
        // Title
        RenderUtils.drawGradientRect(x, y, x + width, y + 18, new Color(30, 30, 30, 220).getRGB(), new Color(30, 30, 30, 180).getRGB());
        fontRenderer.drawStringWithShadow("TARGETING", x + width / 2 - fontRenderer.getStringWidth("TARGETING") / 2, y + 5, getColor(0));
        
        // Target name and health
        fontRenderer.drawStringWithShadow(target.getName(), x + 40, y + 22, -1);
        
        // Health text
        String healthText = decimalFormat.format(health) + " / " + decimalFormat.format(maxHealth);
        fontRenderer.drawStringWithShadow(healthText, x + width - fontRenderer.getStringWidth(healthText) - 5, y + 22, getHealthColor(health, maxHealth));
        
        // Health bar
        RenderUtils.drawRoundedRect(x + 40, y + 35, x + width - 5, y + 40, 2, new Color(40, 40, 40, 160).getRGB());
        RenderUtils.drawRoundedRect(x + 40, y + 35, x + 40 + (int)((width - 45) * healthPercentage), y + 40, 2, getHealthColor(health, maxHealth));
        
        // Distance
        String distanceText = decimalFormat.format(mc.player.getDistance(target)) + " blocks";
        fontRenderer.drawStringWithShadow(distanceText, x + 40, y + 45, -1);
        
        // Face placeholder
        RenderUtils.drawRoundedRect(x + 5, y + 22, x + 35, y + 52, 3, new Color(70, 70, 70, 160).getRGB());
    }
    
    /**
     * Renders player armor and held items
     * @param sr Scaled resolution
     */
    private void renderArmor(ScaledResolution sr) {
        GlStateManager.pushMatrix();
        
        int x = sr.getScaledWidth() / 2 - 90;
        int y = sr.getScaledHeight() - 55;
        
        // Reset GL state for item rendering
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        RenderUtils.glColor(new Color(255, 255, 255, 255));
        
        // Render armor items
        for (int i = 0; i < 4; i++) {
            ItemStack stack = mc.player.inventory.armorInventory.get(3 - i);
            if (stack != null && !stack.isEmpty()) {
                // In an actual implementation, this would render the armor item
                RenderUtils.drawRect(x + i * 20, y, x + i * 20 + 16, y + 16, new Color(50, 50, 50, 160).getRGB());
            }
        }
        
        // Render main hand item
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        if (mainHand != null && !mainHand.isEmpty()) {
            // In an actual implementation, this would render the held item
            RenderUtils.drawRect(x - 20, y, x - 4, y + 16, new Color(50, 50, 50, 160).getRGB());
        }
        
        // Render off hand item
        ItemStack offHand = mc.player.getHeldItemOffhand();
        if (offHand != null && !offHand.isEmpty()) {
            // In an actual implementation, this would render the held item
            RenderUtils.drawRect(x + 80, y, x + 96, y + 16, new Color(50, 50, 50, 160).getRGB());
        }
        
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    /**
     * Gets the current target from KillAura
     * @return The target entity
     */
    private EntityLivingBase getTarget() {
        KillAura killAura = (KillAura) LavaClient.getInstance().getModuleManager().getModule(KillAura.class);
        if (killAura != null && killAura.getState()) {
            return killAura.getCurrentTarget();
        }
        
        // If KillAura is not active, check for nearby players
        EntityPlayer nearestPlayer = null;
        float nearestDistance = 100f;
        
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player && !player.isDead && player.getHealth() > 0) {
                float distance = mc.player.getDistance(player);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPlayer = player;
                }
            }
        }
        
        if (nearestDistance <= 10f) {
            return nearestPlayer;
        }
        
        return null;
    }
    
    /**
     * Gets a color based on the current color settings
     * @param index Position index for rainbow/gradient
     * @return The color as an RGB int
     */
    private int getColor(int index) {
        switch (colorModeValue.get()) {
            case "Custom":
                return new Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()).getRGB();
                
            case "Rainbow":
                return Color.getHSBColor((System.currentTimeMillis() % 3000) / 3000f + index * 0.001f, 
                        saturationValue.get(), brightnessValue.get()).getRGB();
                
            case "Fade":
                return Color.getHSBColor((System.currentTimeMillis() % 2000) / 2000f, 
                        saturationValue.get(), brightnessValue.get()).getRGB();
                
            case "Astolfo":
                float speed = 30f;
                float hue = (System.currentTimeMillis() % (int)(speed * 2)) / speed * 2;
                if (hue > 1) {
                    hue = 2 - hue;
                }
                hue = 0.5f + 0.5f * hue;
                return Color.getHSBColor(hue, saturationValue.get(), brightnessValue.get()).getRGB();
                
            case "Gradient":
                float hueShift = hueShiftValue.get() / 100f;
                return Color.getHSBColor(
                        (System.currentTimeMillis() % 3000) / 3000f + index * hueShift, 
                        saturationValue.get(), 
                        brightnessValue.get()
                ).getRGB();
                
            default:
                return new Color(255, 50, 100).getRGB();
        }
    }
    
    /**
     * Gets a color based on health percentage
     * @param health Current health
     * @param maxHealth Maximum health
     * @return The color as an RGB int
     */
    private int getHealthColor(float health, float maxHealth) {
        float percentage = MathHelper.clamp(health / maxHealth, 0f, 1f);
        
        if (percentage > 0.75f) {
            return new Color(0, 200, 0).getRGB(); // Healthy (green)
        } else if (percentage > 0.5f) {
            return new Color(200, 200, 0).getRGB(); // Half health (yellow)
        } else if (percentage > 0.25f) {
            return new Color(200, 100, 0).getRGB(); // Low health (orange)
        } else {
            return new Color(200, 0, 0).getRGB(); // Critical health (red)
        }
    }
}
