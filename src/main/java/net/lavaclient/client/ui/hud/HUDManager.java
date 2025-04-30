package net.lavaclient.client.ui.hud;

import net.lavaclient.client.ui.hud.element.HUDElement;
import net.lavaclient.client.ui.hud.element.elements.Arraylist;
import net.lavaclient.client.ui.hud.element.elements.Scoreboard;
import net.lavaclient.client.ui.hud.element.elements.TargetHUD;
import net.lavaclient.client.ui.hud.element.elements.Watermark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD elements
 */
public class HUDManager {
    // Minecraft instance
    private final Minecraft mc = Minecraft.getMinecraft();
    
    // List of HUD elements
    private final List<HUDElement> elements = new ArrayList<>();
    
    // Currently dragged element
    private HUDElement draggingElement = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    
    /**
     * Constructor initializes elements
     */
    public HUDManager() {
        // Register default HUD elements
        registerElement(new Watermark(5, 5));
        registerElement(new Arraylist(5, 20));
        registerElement(new TargetHUD(5, 100));
        registerElement(new Scoreboard(0, 5));
        
        // Add other elements here
    }
    
    /**
     * Registers a HUD element
     * @param element Element to register
     */
    public void registerElement(HUDElement element) {
        elements.add(element);
    }
    
    /**
     * Renders all HUD elements
     */
    public void renderElements() {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }
        
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        
        // Render each element
        for (HUDElement element : elements) {
            if (element.isVisible()) {
                element.render(scaledResolution);
            }
        }
    }
    
    /**
     * Renders all HUD elements for the editor
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public void renderEditor(int mouseX, int mouseY) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        
        // Update dragging
        if (draggingElement != null) {
            draggingElement.setX(mouseX - dragOffsetX);
            draggingElement.setY(mouseY - dragOffsetY);
        }
        
        // Render each element
        for (HUDElement element : elements) {
            element.renderInEditor(scaledResolution, mouseX, mouseY);
        }
    }
    
    /**
     * Handles mouse clicks for the editor
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param mouseButton Mouse button
     * @return Whether a click was handled
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Check each element in reverse order (top to bottom in z-order)
        for (int i = elements.size() - 1; i >= 0; i--) {
            HUDElement element = elements.get(i);
            
            // Check if click was on element
            if (mouseX >= element.getX() && mouseX <= element.getX() + element.getWidth() &&
                mouseY >= element.getY() && mouseY <= element.getY() + element.getHeight()) {
                
                if (mouseButton == 0) {
                    // Left click starts dragging
                    draggingElement = element;
                    dragOffsetX = mouseX - element.getX();
                    dragOffsetY = mouseY - element.getY();
                    
                    // Move element to top
                    elements.remove(element);
                    elements.add(element);
                    
                    return true;
                } else if (mouseButton == 1) {
                    // Right click toggles visibility
                    element.setVisible(!element.isVisible());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Handles mouse release for the editor
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param state Mouse button
     */
    public void mouseReleased(int mouseX, int mouseY, int state) {
        // Stop dragging
        draggingElement = null;
    }
    
    /**
     * Gets all HUD elements
     * @return List of HUD elements
     */
    public List<HUDElement> getElements() {
        return elements;
    }
    
    /**
     * Gets a HUD element by class
     * @param clazz The element class
     * @param <T> The element type
     * @return The element instance
     */
    @SuppressWarnings("unchecked")
    public <T extends HUDElement> T getElement(Class<T> clazz) {
        for (HUDElement element : elements) {
            if (element.getClass() == clazz) {
                return (T) element;
            }
        }
        
        return null;
    }
}
