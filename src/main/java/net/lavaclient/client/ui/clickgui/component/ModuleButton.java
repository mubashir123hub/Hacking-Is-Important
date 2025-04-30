package net.lavaclient.client.ui.clickgui.component;

import net.lavaclient.client.module.Module;
import net.lavaclient.client.ui.clickgui.Panel;
import net.lavaclient.client.utils.RenderUtils;
import net.lavaclient.client.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Button component for modules in the Click GUI
 */
public class ModuleButton implements Component {
    // Component data
    private int x, y, width, height;
    private final int originalY;
    private boolean visible = true;
    private boolean extended = false;
    private boolean listening = false;
    
    // Module
    private final Module module;
    private final Panel panel;
    
    // Value components
    private final List<ValueComponent> valueComponents = new ArrayList<>();
    
    // Minecraft instance
    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRenderer = mc.fontRenderer;
    
    // Animation
    private float animation = 0f;
    
    /**
     * Constructor
     * @param module The module
     * @param panel The parent panel
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     */
    public ModuleButton(Module module, Panel panel, int x, int y, int width, int height) {
        this.module = module;
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.width = width;
        this.height = height;
        
        // Setup value components
        setupValueComponents();
    }
    
    /**
     * Sets up the value components for this module
     */
    private void setupValueComponents() {
        int yOffset = height;
        
        // Create components for each value
        for (Value<?> value : module.getValues()) {
            ValueComponent component = null;
            
            if (value instanceof BoolValue) {
                component = new BooleanComponent((BoolValue) value, this, yOffset, 14);
            } else if (value instanceof FloatValue) {
                component = new SliderComponent<>((FloatValue) value, this, yOffset, 16);
            } else if (value instanceof IntegerValue) {
                component = new SliderComponent<>((IntegerValue) value, this, yOffset, 16);
            } else if (value instanceof ListValue) {
                component = new EnumComponent((ListValue) value, this, yOffset, 16);
            }
            
            if (component != null) {
                valueComponents.add(component);
                yOffset += component.getHeight();
            }
        }
        
        // Add bind component
        valueComponents.add(new BindComponent(this, yOffset, 16));
    }
    
    /**
     * Draws the module button
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if (!visible) {
            return;
        }
        
        // Calculate absolute position
        int absX = panel.getX() + x;
        int absY = panel.getY() + y;
        
        // Update animation
        if (module.getState()) {
            animation = Math.min(1.0f, animation + 0.1f);
        } else {
            animation = Math.max(0.0f, animation - 0.1f);
        }
        
        // Draw background
        int bgColor = module.getState() ? 
                        new Color(60, 60, 60, 200).getRGB() : 
                        new Color(40, 40, 40, 200).getRGB();
        
        RenderUtils.drawRect(absX, absY, absX + width, absY + height, bgColor);
        
        // Draw activation indicator
        if (animation > 0) {
            int indicatorWidth = (int) (width * animation);
            RenderUtils.drawGradientRect(absX, absY, absX + indicatorWidth, absY + height, 
                    new Color(255, 50, 50, 80).getRGB(), 
                    new Color(255, 150, 50, 80).getRGB());
        }
        
        // Draw module name
        String moduleName = module.getName();
        if (listening) {
            moduleName = "Press a key...";
        }
        
        fontRenderer.drawStringWithShadow(moduleName, 
                absX + 2, 
                absY + (height / 2f) - (fontRenderer.FONT_HEIGHT / 2f), 
                Color.WHITE.getRGB());
        
        // Draw extension indicator if module has values
        if (!valueComponents.isEmpty()) {
            String indicator = extended ? "-" : "+";
            fontRenderer.drawStringWithShadow(indicator, 
                    absX + width - 8, 
                    absY + (height / 2f) - (fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
        }
        
        // Draw value components if extended
        if (extended) {
            for (ValueComponent valueComponent : valueComponents) {
                valueComponent.drawComponent(mouseX, mouseY);
            }
        }
    }
    
    /**
     * Handles mouse clicks
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param mouseButton Mouse button
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) {
            return;
        }
        
        // Calculate absolute position
        int absX = panel.getX() + x;
        int absY = panel.getY() + y;
        
        // Check if click was on button
        if (mouseX >= absX && mouseX <= absX + width && mouseY >= absY && mouseY <= absY + height) {
            if (mouseButton == 0) {
                // Left click toggles module
                module.toggle();
            } else if (mouseButton == 1) {
                // Right click toggles extension
                extended = !extended;
                panel.updateHeight();
            }
        }
        
        // Propagate to value components if extended
        if (extended) {
            for (ValueComponent valueComponent : valueComponents) {
                valueComponent.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }
    
    /**
     * Handles mouse release
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param state Mouse button
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (!visible) {
            return;
        }
        
        // Propagate to value components if extended
        if (extended) {
            for (ValueComponent valueComponent : valueComponents) {
                valueComponent.mouseReleased(mouseX, mouseY, state);
            }
        }
    }
    
    /**
     * Handles key typing
     * @param typedChar Character typed
     * @param keyCode Key code
     */
    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!visible) {
            return;
        }
        
        // Handle key binding
        if (listening) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                // Clear bind on escape
                module.setKeyBind(0);
            } else {
                // Set new bind
                module.setKeyBind(keyCode);
            }
            
            listening = false;
            return;
        }
        
        // Propagate to value components if extended
        if (extended) {
            for (ValueComponent valueComponent : valueComponents) {
                valueComponent.keyTyped(typedChar, keyCode);
            }
        }
    }
    
    /**
     * Gets the X position
     * @return X position
     */
    @Override
    public int getX() {
        return x;
    }
    
    /**
     * Sets the X position
     * @param x New X position
     */
    @Override
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Gets the Y position
     * @return Y position
     */
    @Override
    public int getY() {
        return y;
    }
    
    /**
     * Sets the Y position
     * @param y New Y position
     */
    @Override
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Gets the original Y position (before scroll)
     * @return Original Y position
     */
    @Override
    public int getOriginalY() {
        return originalY;
    }
    
    /**
     * Gets the width
     * @return Width
     */
    @Override
    public int getWidth() {
        return width;
    }
    
    /**
     * Sets the width
     * @param width New width
     */
    @Override
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * Gets the height
     * @return Height
     */
    @Override
    public int getHeight() {
        // Calculate total height including value components
        int totalHeight = height;
        
        if (extended) {
            for (ValueComponent valueComponent : valueComponents) {
                totalHeight += valueComponent.getHeight();
            }
        }
        
        return totalHeight;
    }
    
    /**
     * Sets the height
     * @param height New height
     */
    @Override
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Checks if the button is visible
     * @return Whether the button is visible
     */
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Sets whether the button is visible
     * @param visible New visible state
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Gets the module
     * @return Module
     */
    public Module getModule() {
        return module;
    }
    
    /**
     * Gets the panel
     * @return Panel
     */
    public Panel getPanel() {
        return panel;
    }
    
    /**
     * Checks if the button is extended
     * @return Whether the button is extended
     */
    public boolean isExtended() {
        return extended;
    }
    
    /**
     * Sets whether the button is extended
     * @param extended New extended state
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
        panel.updateHeight();
    }
    
    /**
     * Checks if the button is listening for key input
     * @return Whether the button is listening
     */
    public boolean isListening() {
        return listening;
    }
    
    /**
     * Sets whether the button is listening for key input
     * @param listening New listening state
     */
    public void setListening(boolean listening) {
        this.listening = listening;
    }
    
    /**
     * Abstract base class for value components
     */
    private abstract static class ValueComponent {
        protected final ModuleButton button;
        protected final int yOffset;
        protected final int height;
        
        public ValueComponent(ModuleButton button, int yOffset, int height) {
            this.button = button;
            this.yOffset = yOffset;
            this.height = height;
        }
        
        public abstract void drawComponent(int mouseX, int mouseY);
        public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
        public abstract void mouseReleased(int mouseX, int mouseY, int state);
        public abstract void keyTyped(char typedChar, int keyCode);
        
        public int getHeight() {
            return height;
        }
    }
    
    /**
     * Boolean value component
     */
    private static class BooleanComponent extends ValueComponent {
        private final BoolValue value;
        
        public BooleanComponent(BoolValue value, ModuleButton button, int yOffset, int height) {
            super(button, yOffset, height);
            this.value = value;
        }
        
        @Override
        public void drawComponent(int mouseX, int mouseY) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Draw background
            RenderUtils.drawRect(absX, absY, absX + button.getWidth(), absY + height, 
                    new Color(30, 30, 30, 180).getRGB());
            
            // Draw checkbox
            int boxSize = 8;
            RenderUtils.drawRect(absX + 2, absY + (height / 2) - (boxSize / 2), 
                    absX + 2 + boxSize, absY + (height / 2) + (boxSize / 2), 
                    new Color(50, 50, 50, 255).getRGB());
            
            // Fill checkbox if enabled
            if (value.get()) {
                RenderUtils.drawRect(absX + 3, absY + (height / 2) - (boxSize / 2) + 1, 
                        absX + 2 + boxSize - 1, absY + (height / 2) + (boxSize / 2) - 1, 
                        new Color(255, 50, 50, 255).getRGB());
            }
            
            // Draw value name
            button.fontRenderer.drawStringWithShadow(value.getName(), 
                    absX + 2 + boxSize + 2, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
        }
        
        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Check if click was on component
            if (mouseX >= absX && mouseX <= absX + button.getWidth() && mouseY >= absY && mouseY <= absY + height) {
                if (mouseButton == 0) {
                    // Toggle the value
                    value.set(!value.get());
                }
            }
        }
        
        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            // Not needed for boolean
        }
        
        @Override
        public void keyTyped(char typedChar, int keyCode) {
            // Not needed for boolean
        }
    }
    
    /**
     * Slider component for numeric values
     */
    private static class SliderComponent<T extends Number> extends ValueComponent {
        private final Value<T> value;
        private boolean dragging = false;
        
        public SliderComponent(Value<T> value, ModuleButton button, int yOffset, int height) {
            super(button, yOffset, height);
            this.value = value;
        }
        
        @Override
        public void drawComponent(int mouseX, int mouseY) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Draw background
            RenderUtils.drawRect(absX, absY, absX + button.getWidth(), absY + height, 
                    new Color(30, 30, 30, 180).getRGB());
            
            // Handle dragging
            if (dragging) {
                if (value instanceof FloatValue) {
                    FloatValue floatValue = (FloatValue) value;
                    float min = floatValue.getMinimum();
                    float max = floatValue.getMaximum();
                    float range = max - min;
                    
                    float percentage = (mouseX - absX) / (float) button.getWidth();
                    percentage = Math.min(1, Math.max(0, percentage));
                    
                    float newValue = min + (range * percentage);
                    floatValue.set(newValue);
                } else if (value instanceof IntegerValue) {
                    IntegerValue intValue = (IntegerValue) value;
                    int min = intValue.getMinimum();
                    int max = intValue.getMaximum();
                    int range = max - min;
                    
                    float percentage = (mouseX - absX) / (float) button.getWidth();
                    percentage = Math.min(1, Math.max(0, percentage));
                    
                    int newValue = min + (int) (range * percentage);
                    intValue.set(newValue);
                }
            }
            
            // Calculate slider percentage
            float percentage = 0;
            if (value instanceof FloatValue) {
                FloatValue floatValue = (FloatValue) value;
                percentage = (floatValue.get() - floatValue.getMinimum()) / 
                            (floatValue.getMaximum() - floatValue.getMinimum());
            } else if (value instanceof IntegerValue) {
                IntegerValue intValue = (IntegerValue) value;
                percentage = (intValue.get() - intValue.getMinimum()) / 
                            (float) (intValue.getMaximum() - intValue.getMinimum());
            }
            
            // Draw slider
            int sliderWidth = (int) (button.getWidth() * percentage);
            RenderUtils.drawGradientRect(absX, absY, absX + sliderWidth, absY + height, 
                    new Color(255, 50, 50, 180).getRGB(), 
                    new Color(255, 150, 50, 180).getRGB());
            
            // Draw value name and current value
            String displayValue = value.getName() + ": " + value.get();
            button.fontRenderer.drawStringWithShadow(displayValue, 
                    absX + 2, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
        }
        
        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Check if click was on component
            if (mouseX >= absX && mouseX <= absX + button.getWidth() && mouseY >= absY && mouseY <= absY + height) {
                if (mouseButton == 0) {
                    // Start dragging
                    dragging = true;
                }
            }
        }
        
        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            // Stop dragging
            dragging = false;
        }
        
        @Override
        public void keyTyped(char typedChar, int keyCode) {
            // Not needed for slider
        }
    }
    
    /**
     * Enum/list value component
     */
    private static class EnumComponent extends ValueComponent {
        private final ListValue value;
        
        public EnumComponent(ListValue value, ModuleButton button, int yOffset, int height) {
            super(button, yOffset, height);
            this.value = value;
        }
        
        @Override
        public void drawComponent(int mouseX, int mouseY) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Draw background
            RenderUtils.drawRect(absX, absY, absX + button.getWidth(), absY + height, 
                    new Color(30, 30, 30, 180).getRGB());
            
            // Draw value name and current value
            String displayValue = value.getName() + ": " + value.get();
            button.fontRenderer.drawStringWithShadow(displayValue, 
                    absX + 2, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
            
            // Draw selector buttons
            button.fontRenderer.drawStringWithShadow("<", 
                    absX + button.getWidth() - 16, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
                    
            button.fontRenderer.drawStringWithShadow(">", 
                    absX + button.getWidth() - 8, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
        }
        
        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Check if click was on component
            if (mouseX >= absX && mouseX <= absX + button.getWidth() && mouseY >= absY && mouseY <= absY + height) {
                if (mouseButton == 0) {
                    // Check if click was on selector buttons
                    if (mouseX >= absX + button.getWidth() - 16 && mouseX <= absX + button.getWidth() - 8) {
                        // Left button - previous value
                        value.selectPrevious();
                    } else if (mouseX >= absX + button.getWidth() - 8 && mouseX <= absX + button.getWidth()) {
                        // Right button - next value
                        value.selectNext();
                    }
                }
            }
        }
        
        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            // Not needed for enum
        }
        
        @Override
        public void keyTyped(char typedChar, int keyCode) {
            // Not needed for enum
        }
    }
    
    /**
     * Key binding component
     */
    private static class BindComponent extends ValueComponent {
        public BindComponent(ModuleButton button, int yOffset, int height) {
            super(button, yOffset, height);
        }
        
        @Override
        public void drawComponent(int mouseX, int mouseY) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Draw background
            RenderUtils.drawRect(absX, absY, absX + button.getWidth(), absY + height, 
                    new Color(30, 30, 30, 180).getRGB());
            
            // Get key name
            String keyName = button.isListening() ? "Listening..." : 
                             (button.getModule().getKeyBind() == 0 ? "None" : 
                             Keyboard.getKeyName(button.getModule().getKeyBind()));
            
            // Draw bind text
            button.fontRenderer.drawStringWithShadow("Bind: " + keyName, 
                    absX + 2, 
                    absY + (height / 2f) - (button.fontRenderer.FONT_HEIGHT / 2f), 
                    Color.WHITE.getRGB());
        }
        
        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            // Calculate absolute position
            int absX = button.getPanel().getX() + button.getX();
            int absY = button.getPanel().getY() + button.getY() + yOffset;
            
            // Check if click was on component
            if (mouseX >= absX && mouseX <= absX + button.getWidth() && mouseY >= absY && mouseY <= absY + height) {
                if (mouseButton == 0) {
                    // Start listening for key input
                    button.setListening(true);
                } else if (mouseButton == 1) {
                    // Clear bind on right click
                    button.getModule().setKeyBind(0);
                }
            }
        }
        
        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            // Not needed for bind
        }
        
        @Override
        public void keyTyped(char typedChar, int keyCode) {
            // Handled by module button
        }
    }
}
