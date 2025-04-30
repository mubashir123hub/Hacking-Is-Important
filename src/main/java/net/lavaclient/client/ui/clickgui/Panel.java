package net.lavaclient.client.ui.clickgui;

import net.lavaclient.client.module.Module;
import net.lavaclient.client.ui.clickgui.component.Component;
import net.lavaclient.client.ui.clickgui.component.ModuleButton;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for a module category in the Click GUI
 */
public class Panel {
    // Panel data
    private int x, y, width, height;
    private final int headerHeight;
    private final Module.Category category;
    private boolean extended = true;
    
    // Scroll position and limits
    private int scrollPosition = 0;
    private int maxScroll = 0;
    
    // Components
    private final List<Component> components = new ArrayList<>();
    
    // Colors
    private final Color backgroundColor = new Color(30, 30, 30, 200);
    private final Color headerColor = new Color(40, 40, 40, 255);
    private final Color accentColor = new Color(255, 50, 100);
    private final Color secondaryColor = new Color(255, 150, 50);
    
    // Minecraft instance
    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRenderer = mc.fontRenderer;
    
    /**
     * Constructor
     * @param category The module category
     * @param x Panel X position
     * @param y Panel Y position
     * @param width Panel width
     * @param headerHeight Panel header height
     */
    public Panel(Module.Category category, int x, int y, int width, int headerHeight) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.headerHeight = headerHeight;
        this.height = headerHeight;
    }
    
    /**
     * Sets up the modules for this panel
     * @param modules List of modules to add
     */
    public void setupModules(List<Module> modules) {
        int yOffset = headerHeight;
        
        // Create module buttons
        for (Module module : modules) {
            ModuleButton button = new ModuleButton(module, this, 0, yOffset, width, 14);
            components.add(button);
            yOffset += 14;
        }
        
        // Update panel height
        updateHeight();
    }
    
    /**
     * Draws the panel
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public void drawPanel(int mouseX, int mouseY) {
        // Draw header
        RenderUtils.drawRect(x, y, x + width, y + headerHeight, headerColor.getRGB());
        
        // Draw header text with gradient
        String categoryName = category.getDisplayName();
        float textX = x + (width / 2f) - (fontRenderer.getStringWidth(categoryName) / 2f);
        float textY = y + (headerHeight / 2f) - (fontRenderer.FONT_HEIGHT / 2f);
        
        RenderUtils.drawGradientString(categoryName, (int) textX, (int) textY, 
                accentColor.getRGB(), secondaryColor.getRGB(), false);
        
        // Draw extend indicator
        String indicator = extended ? "-" : "+";
        fontRenderer.drawStringWithShadow(indicator, x + width - 10, y + (headerHeight / 2f) - (fontRenderer.FONT_HEIGHT / 2f), Color.WHITE.getRGB());
        
        // If not extended, only draw header
        if (!extended) {
            return;
        }
        
        // Calculate visible area
        int contentY = y + headerHeight;
        int contentHeight = height - headerHeight;
        
        // Draw panel background
        RenderUtils.drawRect(x, contentY, x + width, y + height, backgroundColor.getRGB());
        
        // Enable scissor to clip content
        RenderUtils.scissor(x, contentY, width, contentHeight);
        GlStateManager.pushMatrix();
        
        // Draw components with scroll offset
        for (Component component : components) {
            component.setY(component.getOriginalY() - scrollPosition);
            
            // Only draw if in visible area
            if (component.getY() >= headerHeight - component.getHeight() && 
                component.getY() <= headerHeight + contentHeight) {
                component.drawComponent(mouseX, mouseY);
            }
        }
        
        // Reset scissor
        GlStateManager.popMatrix();
        RenderUtils.resetScissor();
        
        // Draw scrollbar if needed
        if (maxScroll > 0) {
            // Calculate scrollbar size and position
            int scrollHeight = Math.max(20, contentHeight * contentHeight / (contentHeight + maxScroll));
            int scrollY = contentY + (scrollPosition * (contentHeight - scrollHeight) / maxScroll);
            
            // Draw scrollbar
            RenderUtils.drawRect(x + width - 2, scrollY, x + width, scrollY + scrollHeight, 
                    new Color(150, 150, 150, 180).getRGB());
        }
    }
    
    /**
     * Updates the panel height based on components
     */
    public void updateHeight() {
        // Start with header height
        int newHeight = headerHeight;
        
        if (extended) {
            // Add up component heights
            for (Component component : components) {
                newHeight += component.getHeight();
            }
        }
        
        // Update height
        height = newHeight;
        
        // Update max scroll
        int totalComponentHeight = 0;
        for (Component component : components) {
            totalComponentHeight += component.getHeight();
        }
        
        // Calculate max scroll value
        maxScroll = Math.max(0, totalComponentHeight - (height - headerHeight));
        
        // Ensure scroll is within bounds
        if (scrollPosition > maxScroll) {
            scrollPosition = maxScroll;
        }
        if (scrollPosition < 0) {
            scrollPosition = 0;
        }
    }
    
    /**
     * Checks if the mouse is over the panel
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return Whether the mouse is over the panel
     */
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    /**
     * Handles mouse clicks
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param mouseButton Mouse button
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Check if click was on header
        if (mouseY <= y + headerHeight) {
            if (mouseButton == 1) {
                // Right click toggles extension
                extended = !extended;
                updateHeight();
            }
            return;
        }
        
        // If extended, propagate to components
        if (extended) {
            for (Component component : components) {
                if (component.getY() >= headerHeight && 
                    component.getY() + component.getHeight() <= height) {
                    
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }
    
    /**
     * Handles mouse release
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param state Mouse button
     */
    public void mouseReleased(int mouseX, int mouseY, int state) {
        // Propagate to components
        if (extended) {
            for (Component component : components) {
                component.mouseReleased(mouseX, mouseY, state);
            }
        }
    }
    
    /**
     * Handles key typing
     * @param typedChar Character typed
     * @param keyCode Key code
     */
    public void keyTyped(char typedChar, int keyCode) {
        // Propagate to components
        if (extended) {
            for (Component component : components) {
                component.keyTyped(typedChar, keyCode);
            }
        }
    }
    
    /**
     * Scrolls the panel
     * @param amount Scroll amount
     */
    public void scroll(int amount) {
        // Update scroll position
        scrollPosition += amount;
        
        // Enforce bounds
        if (scrollPosition < 0) {
            scrollPosition = 0;
        }
        if (scrollPosition > maxScroll) {
            scrollPosition = maxScroll;
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
     * Gets the height
     * @return Height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the header height
     * @return Header height
     */
    public int getHeaderHeight() {
        return headerHeight;
    }
    
    /**
     * Gets the category
     * @return Category
     */
    public Module.Category getCategory() {
        return category;
    }
    
    /**
     * Checks if the panel is extended
     * @return Whether the panel is extended
     */
    public boolean isExtended() {
        return extended;
    }
    
    /**
     * Sets whether the panel is extended
     * @param extended New extended state
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
        updateHeight();
    }
}
