package net.lavaclient.client.ui.clickgui.component;

/**
 * Base interface for ClickGUI components
 */
public interface Component {
    /**
     * Draws the component
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    void drawComponent(int mouseX, int mouseY);
    
    /**
     * Handles mouse clicks
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param mouseButton Mouse button
     */
    void mouseClicked(int mouseX, int mouseY, int mouseButton);
    
    /**
     * Handles mouse release
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param state Mouse button
     */
    void mouseReleased(int mouseX, int mouseY, int state);
    
    /**
     * Handles key typing
     * @param typedChar Character typed
     * @param keyCode Key code
     */
    void keyTyped(char typedChar, int keyCode);
    
    /**
     * Gets the component X position
     * @return X position
     */
    int getX();
    
    /**
     * Sets the component X position
     * @param x New X position
     */
    void setX(int x);
    
    /**
     * Gets the component Y position
     * @return Y position
     */
    int getY();
    
    /**
     * Sets the component Y position
     * @param y New Y position
     */
    void setY(int y);
    
    /**
     * Gets the original Y position (before scroll)
     * @return Original Y position
     */
    int getOriginalY();
    
    /**
     * Gets the component width
     * @return Width
     */
    int getWidth();
    
    /**
     * Sets the component width
     * @param width New width
     */
    void setWidth(int width);
    
    /**
     * Gets the component height
     * @return Height
     */
    int getHeight();
    
    /**
     * Sets the component height
     * @param height New height
     */
    void setHeight(int height);
    
    /**
     * Checks if the component is visible
     * @return Whether the component is visible
     */
    boolean isVisible();
    
    /**
     * Sets whether the component is visible
     * @param visible New visible state
     */
    void setVisible(boolean visible);
}
