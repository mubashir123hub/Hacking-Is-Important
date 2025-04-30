package net.lavaclient.client.ui.hud.element;

import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * Base class for HUD elements
 */
public abstract class HUDElement {
    // Element position and size
    protected int x, y, width, height;
    
    // Visibility
    protected boolean visible = true;
    
    // Minecraft instance
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRenderer = mc.fontRenderer;
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     */
    public HUDElement(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 50;
    }
    
    /**
     * Renders the element
     * @param scaledResolution Scaled resolution
     */
    public abstract void render(ScaledResolution scaledResolution);
    
    /**
     * Renders the element in the editor
     * @param scaledResolution Scaled resolution
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public void renderInEditor(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        // Render normal element
        if (visible) {
            render(scaledResolution);
        }
        
        // Draw outline
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        Color outlineColor = hovered ? new Color(255, 50, 50, 180) : new Color(200, 200, 200, 120);
        
        RenderUtils.drawBorderedRect(x, y, x + width, y + height, 1, 
                new Color(0, 0, 0, 0).getRGB(), outlineColor.getRGB());
        
        // Draw name if invisible
        if (!visible) {
            String name = getClass().getSimpleName();
            int nameWidth = fontRenderer.getStringWidth(name);
            
            RenderUtils.drawRect(x, y, x + nameWidth + 4, y + fontRenderer.FONT_HEIGHT + 2, 
                    new Color(0, 0, 0, 150).getRGB());
            
            fontRenderer.drawStringWithShadow(name, x + 2, y + 1, Color.WHITE.getRGB());
        }
    }
    
    /**
     * Gets the X position
     * @return X position
     */
    public int getX() {
        return x;
    }
    
    /**
     * Sets the X position
     * @param x New X position
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Gets the Y position
     * @return Y position
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the Y position
     * @param y New Y position
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Gets the width
     * @return Width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Sets the width
     * @param width New width
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * Gets the height
     * @return Height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Sets the height
     * @param height New height
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Checks if the element is visible
     * @return Whether the element is visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Sets whether the element is visible
     * @param visible New visible state
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
