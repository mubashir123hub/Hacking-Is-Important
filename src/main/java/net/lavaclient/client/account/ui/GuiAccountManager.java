package net.lavaclient.client.account.ui;

import net.lavaclient.client.LavaClient;
import net.lavaclient.client.account.Account;
import net.lavaclient.client.account.AccountManager;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Account manager GUI
 */
public class GuiAccountManager extends GuiScreen {
    // Background image
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/options_background.png");
    
    // Header text
    private static final String HEADER = "Account Manager";
    
    // Account manager
    private final AccountManager accountManager;
    
    // UI elements
    private GuiButton loginButton;
    private GuiButton addButton;
    private GuiButton editButton;
    private GuiButton deleteButton;
    private GuiButton directLoginButton;
    private GuiButton importButton;
    private GuiButton exportButton;
    private GuiButton backButton;
    
    // Account list
    private GuiAccountList accountList;
    
    // Status message
    private String status = "";
    private long statusTime = 0;
    
    // Date formatter
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructor
     */
    public GuiAccountManager() {
        this.accountManager = LavaClient.getInstance().getAccountManager();
    }
    
    /**
     * Initialize the GUI
     */
    @Override
    public void initGui() {
        // Initialize the account list
        accountList = new GuiAccountList(mc, width / 2 + 10, height - 60, 30, height - 30, width / 2 - 20);
        
        // Add buttons
        int buttonWidth = 98;
        int leftColumn = width / 2 - buttonWidth - 10;
        int rightColumn = width / 2 + 10;
        
        // Left column buttons
        loginButton = new GuiButton(1, leftColumn, height - 52, buttonWidth, 20, "Login");
        directLoginButton = new GuiButton(2, leftColumn, height - 30, buttonWidth, 20, "Direct Login");
        
        // Right column buttons
        addButton = new GuiButton(3, rightColumn, height - 52, buttonWidth, 20, "Add");
        deleteButton = new GuiButton(4, rightColumn, height - 30, buttonWidth, 20, "Delete");
        
        // Bottom buttons
        backButton = new GuiButton(5, width / 2 - 100, height - 52, 200, 20, "Back");
        
        // Add all buttons
        buttonList.add(loginButton);
        buttonList.add(directLoginButton);
        buttonList.add(addButton);
        buttonList.add(deleteButton);
        buttonList.add(backButton);
        
        // Set initial button states
        updateButtons();
    }
    
    /**
     * Updates button enabled states
     */
    private void updateButtons() {
        Account selectedAccount = accountList.getSelectedAccount();
        
        loginButton.enabled = selectedAccount != null;
        deleteButton.enabled = selectedAccount != null;
    }
    
    /**
     * Draws the screen
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw background
        drawBackground();
        
        // Draw account list
        accountList.drawScreen(mouseX, mouseY, partialTicks);
        
        // Draw header
        drawCenteredString(fontRenderer, HEADER, width / 2, 10, 0xFFFFFF);
        
        // Draw current account info
        Account currentAccount = accountManager.getCurrentAccount();
        if (currentAccount != null) {
            drawString(fontRenderer, "Current: " + currentAccount.getUsername(), 10, 10, 0xFFFFFF);
        }
        
        // Draw selected account info
        Account selectedAccount = accountList.getSelectedAccount();
        if (selectedAccount != null) {
            int infoY = 50;
            
            drawString(fontRenderer, "Username: " + selectedAccount.getUsername(), 10, infoY, 0xFFFFFF);
            infoY += 12;
            
            drawString(fontRenderer, "Type: " + selectedAccount.getType(), 10, infoY, 0xFFFFFF);
            infoY += 12;
            
            drawString(fontRenderer, "Last Login: " + dateFormat.format(new Date(selectedAccount.getLastLogin())), 10, infoY, 0xFFFFFF);
            infoY += 12;
        }
        
        // Draw status message if active
        if (System.currentTimeMillis() - statusTime < 3000) {
            drawCenteredString(fontRenderer, status, width / 2, 30, 0xFFFFFF);
        }
        
        // Draw buttons
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    /**
     * Draws the background
     */
    private void drawBackground() {
        // Draw dirt background
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        mc.getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Tile the background
        float scale = 32.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        
        // Draw the tiled background
        for (int x = 0; x <= width / scale; x++) {
            for (int y = 0; y <= height / scale; y++) {
                bufferbuilder.pos(x * scale, y * scale + scale, 0.0D).tex(0, 1).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(x * scale + scale, y * scale + scale, 0.0D).tex(1, 1).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(x * scale + scale, y * scale, 0.0D).tex(1, 0).color(64, 64, 64, 255).endVertex();
                bufferbuilder.pos(x * scale, y * scale, 0.0D).tex(0, 0).color(64, 64, 64, 255).endVertex();
            }
        }
        
        tessellator.draw();
        
        // Draw overlay
        RenderUtils.drawRect(0, 0, width, height, new Color(0, 0, 0, 100).getRGB());
    }
    
    /**
     * Handle button clicks
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1: // Login
                Account selectedAccount = accountList.getSelectedAccount();
                if (selectedAccount != null) {
                    // Attempt to log in
                    boolean success = accountManager.login(selectedAccount);
                    
                    if (success) {
                        // Update last login time
                        selectedAccount.updateLastLogin();
                        accountManager.saveAccounts();
                        
                        // Show success message
                        setStatus("Logged in as " + selectedAccount.getUsername());
                    } else {
                        // Show error message
                        setStatus("Login failed!");
                    }
                }
                break;
                
            case 2: // Direct Login
                mc.displayGuiScreen(new GuiDirectLogin(this));
                break;
                
            case 3: // Add
                mc.displayGuiScreen(new GuiAddAccount(this));
                break;
                
            case 4: // Delete
                selectedAccount = accountList.getSelectedAccount();
                if (selectedAccount != null) {
                    // Confirm deletion
                    mc.displayGuiScreen(new GuiYesNo(
                        this,
                        "Are you sure you want to delete this account?",
                        selectedAccount.getUsername(),
                        "Delete",
                        "Cancel",
                        0
                    ));
                }
                break;
                
            case 5: // Back
                mc.displayGuiScreen(null);
                break;
        }
    }
    
    /**
     * Handle confirmation result
     */
    @Override
    public void confirmClicked(boolean result, int id) {
        if (result && id == 0) {
            // Delete the selected account
            Account selectedAccount = accountList.getSelectedAccount();
            if (selectedAccount != null) {
                accountManager.removeAccount(selectedAccount);
                accountList.setSelectedIndex(-1);
                updateButtons();
                setStatus("Account deleted");
            }
        }
        
        // Return to the account manager
        mc.displayGuiScreen(this);
    }
    
    /**
     * Handle mouse clicks
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Handle clicks in the account list
        accountList.mouseClicked(mouseX, mouseY, mouseButton);
        
        // Update button states
        updateButtons();
        
        // Handle button clicks
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    /**
     * Handle mouse releases
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // Handle releases in the account list
        accountList.mouseReleased(mouseX, mouseY, state);
        
        // Handle button releases
        super.mouseReleased(mouseX, mouseY, state);
    }
    
    /**
     * Handle key presses
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Handle escape to return to the game
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        
        // Handle delete key
        if (keyCode == Keyboard.KEY_DELETE) {
            actionPerformed(deleteButton);
            return;
        }
        
        // Handle enter key to log in
        if (keyCode == Keyboard.KEY_RETURN) {
            actionPerformed(loginButton);
            return;
        }
        
        super.keyTyped(typedChar, keyCode);
    }
    
    /**
     * Handle mouse input
     */
    @Override
    public void handleMouseInput() throws IOException {
        // Handle mouse input
        super.handleMouseInput();
        
        // Handle scrolling
        accountList.handleMouseInput();
    }
    
    /**
     * Updates the screen
     */
    @Override
    public void updateScreen() {
        // Update the account list
        accountList.updateScreen();
    }
    
    /**
     * Sets the status message
     * @param status Status message
     */
    public void setStatus(String status) {
        this.status = status;
        this.statusTime = System.currentTimeMillis();
    }
    
    /**
     * Gets the account manager
     * @return Account manager
     */
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    /**
     * Inner class for the account list
     */
    private class GuiAccountList extends GuiSlot {
        private final List<Account> accounts;
        private int selectedIndex = -1;
        
        /**
         * Constructor
         * @param mc Minecraft instance
         * @param width List width
         * @param height List height
         * @param top Top edge
         * @param bottom Bottom edge
         * @param slotWidth Slot width
         */
        public GuiAccountList(Minecraft mc, int width, int height, int top, int bottom, int slotWidth) {
            super(mc, width, height, top, bottom, 12);
            this.accounts = accountManager.getAccounts();
            this.width = slotWidth;
        }
        
        /**
         * Gets the number of slots
         * @return Number of slots
         */
        @Override
        protected int getSize() {
            return accounts.size();
        }
        
        /**
         * Gets the selected account
         * @return Selected account
         */
        public Account getSelectedAccount() {
            if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                return accounts.get(selectedIndex);
            }
            
            return null;
        }
        
        /**
         * Sets the selected index
         * @param index New index
         */
        public void setSelectedIndex(int index) {
            this.selectedIndex = index;
        }
        
        /**
         * Checks if a slot is selected
         * @param slotIndex Slot index
         * @param mouseX Mouse X position
         * @param mouseY Mouse Y position
         * @return Whether the slot is selected
         */
        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == selectedIndex;
        }
        
        /**
         * Handles a slot click
         * @param slotIndex Slot index
         * @param mouseX Mouse X position
         * @param mouseY Mouse Y position
         * @param mouseEvent Mouse event
         */
        @Override
        protected void elementClicked(int slotIndex, boolean doubleClick, int mouseX, int mouseY) {
            selectedIndex = slotIndex;
            
            if (doubleClick) {
                // Double click to log in
                try {
                    actionPerformed(loginButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            // Update button states
            updateButtons();
        }
        
        /**
         * Draws the background
         */
        @Override
        protected void drawBackground() {
            // Draw nothing
        }
        
        /**
         * Draws a slot
         * @param slotIndex Slot index
         * @param xPos X position
         * @param yPos Y position
         * @param slotHeight Slot height
         * @param mouseX Mouse X position
         * @param mouseY Mouse Y position
         * @param partialTicks Partial ticks
         */
        @Override
        protected void drawSlot(int slotIndex, int xPos, int yPos, int slotHeight, int mouseX, int mouseY, float partialTicks) {
            // Get the account
            Account account = accounts.get(slotIndex);
            
            // Determine the color
            int color = 0xFFFFFF;
            
            // If this is the current account, highlight it
            if (accountManager.getCurrentAccount() != null && 
                accountManager.getCurrentAccount().getUsername().equalsIgnoreCase(account.getUsername())) {
                color = 0x00FF00;
            }
            
            // Draw the account name
            fontRenderer.drawString(account.getUsername(), xPos + 2, yPos + 2, color);
            
            // Draw the account type
            fontRenderer.drawString(account.getType().toString(), xPos + 200, yPos + 2, 0xAAAAAA);
        }
    }
    
    /**
     * Inner class for the direct login screen
     */
    private class GuiDirectLogin extends GuiScreen {
        private final GuiAccountManager parent;
        private GuiTextField usernameField;
        private GuiPasswordField passwordField;
        private GuiButton loginButton;
        private GuiButton cancelButton;
        private String status = "";
        
        /**
         * Constructor
         * @param parent Parent screen
         */
        public GuiDirectLogin(GuiAccountManager parent) {
            this.parent = parent;
        }
        
        /**
         * Initialize the GUI
         */
        @Override
        public void initGui() {
            // Set up text fields
            usernameField = new GuiTextField(1, fontRenderer, width / 2 - 100, height / 2 - 50, 200, 20);
            usernameField.setFocused(true);
            usernameField.setMaxStringLength(100);
            
            passwordField = new GuiPasswordField(2, fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20);
            passwordField.setMaxStringLength(100);
            
            // Set up buttons
            loginButton = new GuiButton(3, width / 2 - 100, height / 2 + 10, 200, 20, "Login");
            cancelButton = new GuiButton(4, width / 2 - 100, height / 2 + 40, 200, 20, "Cancel");
            
            // Add buttons
            buttonList.add(loginButton);
            buttonList.add(cancelButton);
            
            // Set up keyboard navigation
            Keyboard.enableRepeatEvents(true);
        }
        
        /**
         * Draws the screen
         */
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            // Draw background
            drawDefaultBackground();
            
            // Draw title
            drawCenteredString(fontRenderer, "Direct Login", width / 2, 20, 0xFFFFFF);
            
            // Draw field labels
            drawString(fontRenderer, "Username:", width / 2 - 100, height / 2 - 65, 0xFFFFFF);
            drawString(fontRenderer, "Password:", width / 2 - 100, height / 2 - 35, 0xFFFFFF);
            
            // Draw text fields
            usernameField.drawTextBox();
            passwordField.drawTextBox();
            
            // Draw status
            drawCenteredString(fontRenderer, status, width / 2, height / 2 - 80, 0xFFFFFF);
            
            // Draw buttons
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
        
        /**
         * Handle key presses
         */
        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            // Handle escape to cancel
            if (keyCode == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(parent);
                return;
            }
            
            // Handle tab to switch fields
            if (keyCode == Keyboard.KEY_TAB) {
                if (usernameField.isFocused()) {
                    usernameField.setFocused(false);
                    passwordField.setFocused(true);
                } else {
                    passwordField.setFocused(false);
                    usernameField.setFocused(true);
                }
                return;
            }
            
            // Handle enter to log in
            if (keyCode == Keyboard.KEY_RETURN) {
                actionPerformed(loginButton);
                return;
            }
            
            // Handle field input
            if (usernameField.isFocused()) {
                usernameField.textboxKeyTyped(typedChar, keyCode);
            }
            
            if (passwordField.isFocused()) {
                passwordField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        
        /**
         * Handle mouse clicks
         */
        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            // Handle field clicks
            usernameField.mouseClicked(mouseX, mouseY, mouseButton);
            passwordField.mouseClicked(mouseX, mouseY, mouseButton);
            
            // Handle button clicks
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        
        /**
         * Handle button clicks
         */
        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            switch (button.id) {
                case 3: // Login
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    
                    if (username.isEmpty()) {
                        status = "Username cannot be empty!";
                        return;
                    }
                    
                    // Attempt to log in
                    Account.AccountType type = password.isEmpty() ? Account.AccountType.OFFLINE : Account.AccountType.MOJANG;
                    boolean success = accountManager.login(username, password, type);
                    
                    if (success) {
                        // Return to the account manager
                        parent.setStatus("Logged in as " + username);
                        mc.displayGuiScreen(parent);
                    } else {
                        status = "Login failed!";
                    }
                    break;
                    
                case 4: // Cancel
                    mc.displayGuiScreen(parent);
                    break;
            }
        }
        
        /**
         * Called when the GUI is closed
         */
        @Override
        public void onGuiClosed() {
            Keyboard.enableRepeatEvents(false);
        }
    }
    
    /**
     * Inner class for the add account screen
     */
    private class GuiAddAccount extends GuiScreen {
        private final GuiAccountManager parent;
        private GuiTextField usernameField;
        private GuiPasswordField passwordField;
        private GuiButton addButton;
        private GuiButton cancelButton;
        private String status = "";
        
        /**
         * Constructor
         * @param parent Parent screen
         */
        public GuiAddAccount(GuiAccountManager parent) {
            this.parent = parent;
        }
        
        /**
         * Initialize the GUI
         */
        @Override
        public void initGui() {
            // Set up text fields
            usernameField = new GuiTextField(1, fontRenderer, width / 2 - 100, height / 2 - 50, 200, 20);
            usernameField.setFocused(true);
            usernameField.setMaxStringLength(100);
            
            passwordField = new GuiPasswordField(2, fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20);
            passwordField.setMaxStringLength(100);
            
            // Set up buttons
            addButton = new GuiButton(3, width / 2 - 100, height / 2 + 10, 200, 20, "Add Account");
            cancelButton = new GuiButton(4, width / 2 - 100, height / 2 + 40, 200, 20, "Cancel");
            
            // Add buttons
            buttonList.add(addButton);
            buttonList.add(cancelButton);
            
            // Set up keyboard navigation
            Keyboard.enableRepeatEvents(true);
        }
        
        /**
         * Draws the screen
         */
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            // Draw background
            drawDefaultBackground();
            
            // Draw title
            drawCenteredString(fontRenderer, "Add Account", width / 2, 20, 0xFFFFFF);
            
            // Draw field labels
            drawString(fontRenderer, "Username:", width / 2 - 100, height / 2 - 65, 0xFFFFFF);
            drawString(fontRenderer, "Password:", width / 2 - 100, height / 2 - 35, 0xFFFFFF);
            
            // Draw text fields
            usernameField.drawTextBox();
            passwordField.drawTextBox();
            
            // Draw status
            drawCenteredString(fontRenderer, status, width / 2, height / 2 - 80, 0xFFFFFF);
            
            // Draw buttons
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
        
        /**
         * Handle key presses
         */
        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            // Handle escape to cancel
            if (keyCode == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(parent);
                return;
            }
            
            // Handle tab to switch fields
            if (keyCode == Keyboard.KEY_TAB) {
                if (usernameField.isFocused()) {
                    usernameField.setFocused(false);
                    passwordField.setFocused(true);
                } else {
                    passwordField.setFocused(false);
                    usernameField.setFocused(true);
                }
                return;
            }
            
            // Handle enter to add account
            if (keyCode == Keyboard.KEY_RETURN) {
                actionPerformed(addButton);
                return;
            }
            
            // Handle field input
            if (usernameField.isFocused()) {
                usernameField.textboxKeyTyped(typedChar, keyCode);
            }
            
            if (passwordField.isFocused()) {
                passwordField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        
        /**
         * Handle mouse clicks
         */
        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            // Handle field clicks
            usernameField.mouseClicked(mouseX, mouseY, mouseButton);
            passwordField.mouseClicked(mouseX, mouseY, mouseButton);
            
            // Handle button clicks
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        
        /**
         * Handle button clicks
         */
        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            switch (button.id) {
                case 3: // Add
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    
                    if (username.isEmpty()) {
                        status = "Username cannot be empty!";
                        return;
                    }
                    
                    // Create a new account
                    Account.AccountType type = password.isEmpty() ? Account.AccountType.OFFLINE : Account.AccountType.MOJANG;
                    Account account = new Account(username, password, "", "", type);
                    
                    // Add the account
                    accountManager.addAccount(account);
                    
                    // Return to the account manager
                    parent.setStatus("Account added: " + username);
                    mc.displayGuiScreen(parent);
                    break;
                    
                case 4: // Cancel
                    mc.displayGuiScreen(parent);
                    break;
            }
        }
        
        /**
         * Called when the GUI is closed
         */
        @Override
        public void onGuiClosed() {
            Keyboard.enableRepeatEvents(false);
        }
    }
}
