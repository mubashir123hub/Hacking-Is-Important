package net.lavaclient.client.ui.hud.element.elements;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.ui.hud.element.HUDElement;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Watermark HUD element showing client name and version
 */
public class Watermark extends HUDElement {
    // Logo resource
    private final ResourceLocation logo = new ResourceLocation("textures/lava_client_logo.svg");
    
    // Watermark mode
    private enum Mode {
        TEXT,
        LOGO,
        BOTH
    }
    
    // Current mode
    private Mode mode = Mode.BOTH;
    
    // Text
    private String clientName = LavaClient.NAME;
    private String clientVersion = LavaClient.VERSION;
    
    // Colors
    private int color1 = new Color(255, 50, 50).getRGB();
    private int color2 = new Color(255, 150, 50).getRGB();
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     */
    public Watermark(int x, int y) {
        super(x, y);
        this.width = 100;
        this.height = 20;
    }
    
    /**
     * Renders the watermark
     * @param scaledResolution Scaled resolution
     */
    @Override
    public void render(ScaledResolution scaledResolution) {
        switch (mode) {
            case TEXT:
                renderText();
                break;
            case LOGO:
                renderLogo();
                break;
            case BOTH:
                renderBoth();
                break;
        }
    }
    
    /**
     * Renders text watermark
     */
    private void renderText() {
        // Calculate width based on text
        int textWidth = fontRenderer.getStringWidth(clientName + " " + clientVersion);
        this.width = textWidth + 4;
        this.height = fontRenderer.FONT_HEIGHT + 2;
        
        // Draw background
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 120).getRGB());
        
        // Draw text with gradient
        RenderUtils.drawGradientString(clientName + " " + clientVersion, 
                x + 2, y + 1, color1, color2, false);
    }
    
    /**
     * Renders logo watermark
     */
    private void renderLogo() {
        // Set dimensions
        this.width = 40;
        this.height = 40;
        
        // Draw logo background
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 120).getRGB());
        
        // Draw logo
        GlStateManager.pushMatrix();
        
        // In a real implementation, this would render the logo texture
        // For this example, draw a simple shape
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float size = Math.min(width, height) * 0.8f;
        
        // Draw a "L" shape for Lava Client
        RenderUtils.drawGradientRect(
                (int) (centerX - size / 2), 
                (int) (centerY - size / 2), 
                (int) (centerX - size / 4), 
                (int) (centerY + size / 2), 
                color1, color2);
        
        RenderUtils.drawGradientRect(
                (int) (centerX - size / 2), 
                (int) (centerY + size / 4), 
                (int) (centerX + size / 2), 
                (int) (centerY + size / 2), 
                color2, color1);
        
        GlStateManager.popMatrix();
    }
    
    /**
     * Renders both logo and text
     */
    private void renderBoth() {
        // Calculate width based on text and logo
        int textWidth = fontRenderer.getStringWidth(clientName + " " + clientVersion);
        this.width = textWidth + 44;
        this.height = 40;
        
        // Draw background
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 120).getRGB());
        
        // Draw logo (similar to renderLogo but positioned to the left)
        GlStateManager.pushMatrix();
        
        float logoX = x + 20;
        float logoY = y + height / 2f;
        float size = 30;
        
        // Draw a "L" shape for Lava Client
        RenderUtils.drawGradientRect(
                (int) (logoX - size / 2), 
                (int) (logoY - size / 2), 
                (int) (logoX - size / 4), 
                (int) (logoY + size / 2), 
                color1, color2);
        
        RenderUtils.drawGradientRect(
                (int) (logoX - size / 2), 
                (int) (logoY + size / 4), 
                (int) (logoX + size / 2), 
                (int) (logoY + size / 2), 
                color2, color1);
        
        GlStateManager.popMatrix();
        
        // Draw text with gradient
        RenderUtils.drawGradientString(clientName, 
                x + 40, y + 10, color1, color2, false);
                
        RenderUtils.drawGradientString(clientVersion, 
                x + 40, y + 22, color2, color1, false);
    }
    
    /**
     * Sets the watermark mode
     * @param mode New mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    /**
     * Sets the client name
     * @param clientName New client name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    /**
     * Sets the client version
     * @param clientVersion New client version
     */
    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
    
    /**
     * Sets the gradient colors
     * @param color1 First color
     * @param color2 Second color
     */
    public void setColors(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }
}
