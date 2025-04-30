package net.lavaclient.client.ui.clickgui;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.module.Module;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Click GUI for Lava Client modules
 */
public class ClickGUI extends GuiScreen {
    // List of category panels
    private final List<Panel> panels = new ArrayList<>();
    
    // Dragging state
    private boolean dragging;
    private int dragX, dragY;
    private Panel draggingPanel;
    
    // Colors
    private final Color backgroundColor = new Color(30, 30, 30, 200);
    private final Color accentColor = new Color(255, 50, 100);
    private final Color secondaryColor = new Color(255, 150, 50);
    
    // Logo
    private final ResourceLocation logo = new ResourceLocation("textures/lava_client_logo.svg");
    
    /**
     * Constructor initializes panels
     */
    public ClickGUI() {
        int panelWidth = 120;
        int startX = 10;
        
        // Create panel for each category
        for (Module.Category category : Module.Category.values()) {
            // Create panel
            Panel panel = new Panel(category, startX, 30, panelWidth, 18);
            
            // Add modules for this category
            List<Module> modules = LavaClient.getInstance().getModuleManager().getModulesByCategory(category);
            panel.setupModules(modules);
            
            // Add to panel list
            panels.add(panel);
            
            // Update startX for next panel
            startX += panelWidth + 10;
        }
    }
    
    /**
     * Draw the GUI
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw the background
        drawDefaultBackground();
        
        // Draw logo and title
        drawLogo();
        
        // Mouse wheel handling for scrolling
        handleScroll();
        
        // Update dragging
        if (dragging && draggingPanel != null) {
            draggingPanel.setX(mouseX - dragX);
            draggingPanel.setY(mouseY - dragY);
        }
        
        // Draw panels
        for (Panel panel : panels) {
            panel.drawPanel(mouseX, mouseY);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    /**
     * Draws the client logo
     */
    private void drawLogo() {
        // Draw the text logo since resource might not be available
        String clientName = "Lava Client";
        int textWidth = fontRenderer.getStringWidth(clientName);
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        
        // Draw gradient title
        RenderUtils.drawGradientString(clientName, 
                (width / 2 - textWidth) / 2, 2, 
                accentColor.getRGB(), secondaryColor.getRGB(), true);
        
        GlStateManager.popMatrix();
    }
    
    /**
     * Handle mouse wheel scrolling
     */
    private void handleScroll() {
        int scroll = Mouse.getDWheel();
        
        if (scroll != 0) {
            // Find hovered panel
            for (Panel panel : panels) {
                if (panel.isMouseOver(Mouse.getX(), Mouse.getY())) {
                    // Scroll the panel
                    panel.scroll(scroll > 0 ? -10 : 10);
                    break;
                }
            }
        }
    }
    
    /**
     * Handle mouse clicks
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Handle clicks on panels
        for (Panel panel : panels) {
            // Check if click was on panel
            if (panel.isMouseOver(mouseX, mouseY)) {
                // Handle click in panel
                if (mouseButton == 0) {
                    // If clicked on header, start dragging
                    if (mouseY <= panel.getY() + panel.getHeaderHeight()) {
                        dragging = true;
                        draggingPanel = panel;
                        dragX = mouseX - panel.getX();
                        dragY = mouseY - panel.getY();
                    } else {
                        // Click on panel content
                        panel.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                } else {
                    // Right click or middle click
                    panel.mouseClicked(mouseX, mouseY, mouseButton);
                }
                
                // Move this panel to the top of the render order
                panels.remove(panel);
                panels.add(panel);
                
                return;
            }
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    /**
     * Handle mouse release
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // Stop dragging
        dragging = false;
        draggingPanel = null;
        
        // Propagate to panels
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
        
        super.mouseReleased(mouseX, mouseY, state);
    }
    
    /**
     * Handle key presses
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Escape closes the GUI
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
            return;
        }
        
        // Propagate key press to panels
        for (Panel panel : panels) {
            panel.keyTyped(typedChar, keyCode);
        }
        
        super.keyTyped(typedChar, keyCode);
    }
    
    /**
     * Called when the GUI is opened
     */
    @Override
    public void initGui() {
        super.initGui();
    }
    
    /**
     * Called when the GUI is closed
     */
    @Override
    public void onGuiClosed() {
        // Save positions and states
        super.onGuiClosed();
    }
    
    /**
     * Returns whether the GUI pauses the game
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    /**
     * Gets the client accent color
     * @return Accent color
     */
    public Color getAccentColor() {
        return accentColor;
    }
    
    /**
     * Gets the client secondary color
     * @return Secondary color
     */
    public Color getSecondaryColor() {
        return secondaryColor;
    }
    
    /**
     * Gets the client background color
     * @return Background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
