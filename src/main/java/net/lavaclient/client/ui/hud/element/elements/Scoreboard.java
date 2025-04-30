package net.lavaclient.client.ui.hud.element.elements;

import net.lavaclient.client.ui.hud.element.HUDElement;
import net.lavaclient.client.utils.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scoreboard HUD element showing the Minecraft scoreboard
 */
public class Scoreboard extends HUDElement {
    // Whether to show numbers
    private boolean showNumbers = false;
    
    // Whether to remove duplicated lines
    private boolean removeDuplicates = true;
    
    // Whether to remove color codes
    private boolean removeColorCodes = false;
    
    // Background color
    private int backgroundColor = new Color(0, 0, 0, 100).getRGB();
    
    // Border colors
    private int borderColor1 = new Color(255, 50, 50, 200).getRGB();
    private int borderColor2 = new Color(255, 150, 50, 200).getRGB();
    
    /**
     * Constructor
     * @param x X position
     * @param y Y position
     */
    public Scoreboard(int x, int y) {
        super(x, y);
        this.width = 120;
        this.height = 150;
    }
    
    /**
     * Renders the scoreboard
     * @param scaledResolution Scaled resolution
     */
    @Override
    public void render(ScaledResolution scaledResolution) {
        // Check if there's a scoreboard
        net.minecraft.scoreboard.Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = null;
        
        if (scoreboard != null) {
            objective = scoreboard.getObjectiveInDisplaySlot(1);
        }
        
        // If no objective, don't render
        if (objective == null) {
            return;
        }
        
        // Get scores
        List<Score> scores = new ArrayList<>();
        if (scoreboard != null) {
            scores = scoreboard.getSortedScores(objective).stream()
                    .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                    .collect(Collectors.toList());
            
            // Limit to 15 scores
            if (scores.size() > 15) {
                scores = scores.subList(0, 15);
            }
            
            // Sort scores
            Collections.reverse(scores);
        }
        
        // Get title
        String title = objective.getDisplayName();
        
        // Calculate dimensions
        int maxWidth = Math.max(fontRenderer.getStringWidth(title), scores.stream()
                .mapToInt(score -> fontRenderer.getStringWidth(getScoreText(score, scoreboard)))
                .max()
                .orElse(0)) + 4;
        
        int height = 10 + scores.size() * (fontRenderer.FONT_HEIGHT + 1);
        
        // Update dimensions for editor
        this.width = maxWidth;
        this.height = height;
        
        // Adjust position if necessary
        int actualX = x;
        int actualY = y;
        
        // If x is 0, position on right side
        if (x == 0) {
            actualX = scaledResolution.getScaledWidth() - maxWidth - 5;
        }
        
        // Draw background
        RenderUtils.drawRect(actualX, actualY, actualX + maxWidth, actualY + height, backgroundColor);
        
        // Draw border
        RenderUtils.drawGradientRect(actualX, actualY, actualX + maxWidth, actualY + 1, borderColor1, borderColor2);
        RenderUtils.drawGradientRect(actualX, actualY + height - 1, actualX + maxWidth, actualY + height, borderColor1, borderColor2);
        RenderUtils.drawGradientRect(actualX, actualY, actualX + 1, actualY + height, borderColor1, borderColor2);
        RenderUtils.drawGradientRect(actualX + maxWidth - 1, actualY, actualX + maxWidth, actualY + height, borderColor1, borderColor2);
        
        // Draw title
        fontRenderer.drawStringWithShadow(title, actualX + maxWidth / 2 - fontRenderer.getStringWidth(title) / 2, actualY + 1, -1);
        
        // Draw scores
        int yOffset = 10;
        for (Score score : scores) {
            String text = getScoreText(score, scoreboard);
            
            // Check for duplicates
            if (removeDuplicates) {
                boolean isDuplicate = false;
                for (Score otherScore : scores) {
                    if (otherScore != score && getScoreText(otherScore, scoreboard).equals(text)) {
                        isDuplicate = true;
                        break;
                    }
                }
                
                if (isDuplicate) {
                    continue;
                }
            }
            
            // Draw score
            String scoreText = showNumbers ? score.getScorePoints() + " " + text : text;
            fontRenderer.drawStringWithShadow(scoreText, actualX + 2, actualY + yOffset, -1);
            yOffset += fontRenderer.FONT_HEIGHT + 1;
        }
    }
    
    /**
     * Gets the text for a score
     * @param score The score
     * @param scoreboard The scoreboard
     * @return The score text
     */
    private String getScoreText(Score score, net.minecraft.scoreboard.Scoreboard scoreboard) {
        String text = ScorePlayerTeam.formatPlayerName(
                scoreboard.getPlayersTeam(score.getPlayerName()), score.getPlayerName());
        
        // Remove color codes if enabled
        if (removeColorCodes) {
            text = TextFormatting.getTextWithoutFormattingCodes(text);
        }
        
        return text;
    }
    
    /**
     * Sets whether to show numbers
     * @param showNumbers Whether to show numbers
     */
    public void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }
    
    /**
     * Sets whether to remove duplicates
     * @param removeDuplicates Whether to remove duplicates
     */
    public void setRemoveDuplicates(boolean removeDuplicates) {
        this.removeDuplicates = removeDuplicates;
    }
    
    /**
     * Sets whether to remove color codes
     * @param removeColorCodes Whether to remove color codes
     */
    public void setRemoveColorCodes(boolean removeColorCodes) {
        this.removeColorCodes = removeColorCodes;
    }
    
    /**
     * Sets the background color
     * @param backgroundColor New background color
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    /**
     * Sets the border colors
     * @param borderColor1 First border color
     * @param borderColor2 Second border color
     */
    public void setBorderColors(int borderColor1, int borderColor2) {
        this.borderColor1 = borderColor1;
        this.borderColor2 = borderColor2;
    }
}
