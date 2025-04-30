package net.lavaclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Utility class for rendering operations
 */
public class RenderUtils {
    // Minecraft instance
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * Draws a colored rectangle
     * @param left Left edge
     * @param top Top edge
     * @param right Right edge
     * @param bottom Bottom edge
     * @param color RGBA color
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }
        
        if (top < bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }
        
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    /**
     * Draws a rectangle with a gradient
     * @param left Left edge
     * @param top Top edge
     * @param right Right edge
     * @param bottom Bottom edge
     * @param startColor Start color
     * @param endColor End color
     */
    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (startColor >> 24 & 0xFF) / 255.0F;
        float startRed = (startColor >> 16 & 0xFF) / 255.0F;
        float startGreen = (startColor >> 8 & 0xFF) / 255.0F;
        float startBlue = (startColor & 0xFF) / 255.0F;
        
        float endAlpha = (endColor >> 24 & 0xFF) / 255.0F;
        float endRed = (endColor >> 16 & 0xFF) / 255.0F;
        float endGreen = (endColor >> 8 & 0xFF) / 255.0F;
        float endBlue = (endColor & 0xFF) / 255.0F;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0D).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.pos(left, top, 0.0D).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.pos(left, bottom, 0.0D).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();
        
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    /**
     * Draws a bordered rectangle
     * @param left Left edge
     * @param top Top edge
     * @param right Right edge
     * @param bottom Bottom edge
     * @param borderWidth Border width
     * @param insideColor Inside color
     * @param borderColor Border color
     */
    public static void drawBorderedRect(int left, int top, int right, int bottom, int borderWidth, int insideColor, int borderColor) {
        // Draw inside
        drawRect(left + borderWidth, top + borderWidth, right - borderWidth, bottom - borderWidth, insideColor);
        
        // Draw border
        drawRect(left, top, right, top + borderWidth, borderColor); // Top
        drawRect(left, bottom - borderWidth, right, bottom, borderColor); // Bottom
        drawRect(left, top + borderWidth, left + borderWidth, bottom - borderWidth, borderColor); // Left
        drawRect(right - borderWidth, top + borderWidth, right, bottom - borderWidth, borderColor); // Right
    }
    
    /**
     * Draws a rectangle with rounded corners
     * @param left Left edge
     * @param top Top edge
     * @param right Right edge
     * @param bottom Bottom edge
     * @param radius Corner radius
     * @param color RGBA color
     */
    public static void drawRoundedRect(int left, int top, int right, int bottom, int radius, int color) {
        // Draw the main rectangle without corners
        drawRect(left + radius, top, right - radius, bottom, color); // Center
        drawRect(left, top + radius, left + radius, bottom - radius, color); // Left edge
        drawRect(right - radius, top + radius, right, bottom - radius, color); // Right edge
        
        // Draw the four corner quadrants
        drawQuadrant(right - radius, bottom - radius, radius, 0, color); // Bottom right
        drawQuadrant(left + radius, bottom - radius, radius, 1, color); // Bottom left
        drawQuadrant(left + radius, top + radius, radius, 2, color); // Top left
        drawQuadrant(right - radius, top + radius, radius, 3, color); // Top right
    }
    
    /**
     * Draws a quadrant for rounded rectangles
     * @param centerX Center X
     * @param centerY Center Y
     * @param radius Radius
     * @param quadrant Quadrant (0=bottom right, 1=bottom left, 2=top left, 3=top right)
     * @param color RGBA color
     */
    private static void drawQuadrant(int centerX, int centerY, int radius, int quadrant, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        
        // Draw the quadrant
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(centerX, centerY);
        
        // Determine angle range based on quadrant
        float startAngle = 0;
        float endAngle = 0;
        switch (quadrant) {
            case 0: // Bottom right
                startAngle = 0;
                endAngle = 90;
                break;
            case 1: // Bottom left
                startAngle = 90;
                endAngle = 180;
                break;
            case 2: // Top left
                startAngle = 180;
                endAngle = 270;
                break;
            case 3: // Top right
                startAngle = 270;
                endAngle = 360;
                break;
        }
        
        // Draw quadrant
        for (float angle = startAngle; angle <= endAngle; angle += 5) {
            double x = centerX + Math.sin(Math.toRadians(angle)) * radius;
            double y = centerY + Math.cos(Math.toRadians(angle)) * radius;
            GL11.glVertex2d(x, y);
        }
        
        GL11.glEnd();
        
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    /**
     * Draws a string with a gradient color
     * @param text Text to draw
     * @param x X position
     * @param y Y position
     * @param color1 Start color
     * @param color2 End color
     * @param shadow Whether to draw shadow
     */
    public static void drawGradientString(String text, int x, int y, int color1, int color2, boolean shadow) {
        FontRenderer fontRenderer = mc.fontRenderer;
        int textWidth = fontRenderer.getStringWidth(text);
        
        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / text.length();
            int color = interpolateColor(color1, color2, ratio);
            
            String character = String.valueOf(text.charAt(i));
            fontRenderer.drawString(character, x, y, color, shadow);
            
            x += fontRenderer.getStringWidth(character);
        }
    }
    
    /**
     * Interpolates between two colors
     * @param color1 First color
     * @param color2 Second color
     * @param ratio Ratio (0.0-1.0)
     * @return Interpolated color
     */
    public static int interpolateColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        int a = (int) (a1 + (a2 - a1) * ratio);
        
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    
    /**
     * Enables scissor testing
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     */
    public static void scissor(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        
        GL11.glScissor(x * scale, mc.displayHeight - (y + height) * scale, width * scale, height * scale);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }
    
    /**
     * Disables scissor testing
     */
    public static void resetScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
    
    /**
     * Draws a texture
     * @param texture Texture resource location
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param color RGBA color
     */
    public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        
        GlStateManager.enableBlend();
        GlStateManager.color(red, green, blue, alpha);
        mc.getTextureManager().bindTexture(texture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.disableBlend();
    }
    
    /**
     * Sets the color for GL rendering
     * @param color Color
     */
    public static void glColor(Color color) {
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
}
