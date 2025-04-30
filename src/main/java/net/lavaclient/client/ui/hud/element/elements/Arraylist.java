package net.lavaclient.client.ui.hud.element.elements;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.ui.hud.element.HUDElement;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * Arraylist HUD element showing enabled modules
 */
public class Arraylist extends HUDElement {
    // Render mode
    private enum Mode {
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT
    }
    
    // Current mode
    private Mode mode = Mode.TOP_RIGHT;
    
    // Colors
    private int startColor = new Color(255, 50, 50).getRGB();
    private int endColor = new Color(255, 150, 50).getRGB();
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     */
    public Arraylist(int x, int y) {
        super(x, y);
        this.width = 100;
        this.height = 150;
    }
    
    /**
     * Renders the arraylist
     * @param scaledResolution Scaled resolution
     */
    @Override
    public void render(ScaledResolution scaledResolution) {
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        
        // Get enabled modules
        List<Module> enabledModules = LavaClient.getInstance().getModuleManager().getEnabledModules();
        
        // Don't show HUD module
        enabledModules.removeIf(module -> module.getName().equals("HUD"));
        
        // If no modules, return
        if (enabledModules.isEmpty()) {
            return;
        }
        
        // Sort modules by name length
        switch (mode) {
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                enabledModules.sort((m1, m2) -> fontRenderer.getStringWidth(m2.getName()) - fontRenderer.getStringWidth(m1.getName()));
                break;
            case TOP_LEFT:
            case BOTTOM_LEFT:
                enabledModules.sort(Comparator.comparingInt(m -> fontRenderer.getStringWidth(m.getName())));
                break;
        }
        
        // Render modules
        int offset = 0;
        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            String name = module.getName();
            int nameWidth = fontRenderer.getStringWidth(name);
            
            // Calculate position based on mode
            int posX = x;
            int posY = y;
            
            switch (mode) {
                case TOP_RIGHT:
                    posX = x + width - nameWidth - 2;
                    posY = y + offset;
                    break;
                case TOP_LEFT:
                    posX = x + 2;
                    posY = y + offset;
                    break;
                case BOTTOM_RIGHT:
                    posX = x + width - nameWidth - 2;
                    posY = y + height - offset - fontRenderer.FONT_HEIGHT;
                    break;
                case BOTTOM_LEFT:
                    posX = x + 2;
                    posY = y + height - offset - fontRenderer.FONT_HEIGHT;
                    break;
            }
            
            // Calculate color based on position
            float ratio = (float) i / enabledModules.size();
            int color = RenderUtils.interpolateColor(startColor, endColor, ratio);
            
            // Draw background
            RenderUtils.drawRect(posX - 2, posY - 1, posX + nameWidth + 2, posY + fontRenderer.FONT_HEIGHT, 
                    new Color(0, 0, 0, 120).getRGB());
            
            // Draw text
            fontRenderer.drawStringWithShadow(name, posX, posY, color);
            
            // Update offset
            offset += fontRenderer.FONT_HEIGHT + 1;
        }
        
        // Update height for editor
        this.height = offset;
    }
    
    /**
     * Sets the render mode
     * @param mode New mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    /**
     * Sets the start color
     * @param color New color
     */
    public void setStartColor(int color) {
        this.startColor = color;
    }
    
    /**
     * Sets the end color
     * @param color New color
     */
    public void setEndColor(int color) {
        this.endColor = color;
    }
}
