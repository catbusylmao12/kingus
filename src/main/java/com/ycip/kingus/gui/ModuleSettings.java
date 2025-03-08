package com.ycip.kingus.gui;

import com.ycip.kingus.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Map;

public class ModuleSettings extends GuiScreen {
    private final Module module;
    private int offsetY = 0; // For scrolling

    public ModuleSettings(Module module) {
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        // Draw background
        drawRect(0, 0, screenWidth, screenHeight, new Color(30, 30, 30, 200).getRGB());

        // Draw module name
        drawString(fontRendererObj, module.getName() + " Settings", 20, 20, 0xFFFFFF);

        // Draw settings
        int y = 40;
        for (Map.Entry<String, Object> entry : module.getSettings().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Draw setting name and value
            drawString(fontRendererObj, key + ": " + value, 20, y, 0xFFFFFF);

            // Highlight hovered setting
            if (mouseX >= 20 && mouseX <= 120 && mouseY >= y && mouseY <= y + 10) {
                drawRect(20, y, 120, y + 10, new Color(255, 255, 255, 50).getRGB());
            }

            y += 20;
        }

        // Handle scrolling
        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            offsetY += scroll > 0 ? 10 : -10;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        System.out.println("[DEBUG] Mouse clicked at: (" + mouseX + ", " + mouseY + ")"); // Debug log

        int y = 40;
        for (Map.Entry<String, Object> entry : module.getSettings().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check if the mouse is over this setting
            if (mouseX >= 20 && mouseX <= 120 && mouseY >= y && mouseY <= y + 10) {
                System.out.println("[DEBUG] Clicked on setting: " + key); // Debug log

                if (value instanceof Boolean) {
                    module.updateSetting(key, !(boolean) value); // Toggle boolean
                    System.out.println("[DEBUG] Toggled " + key + " to: " + module.getSetting(key)); // Debug log
                } else if (value instanceof Float) {
                    float newValue = (float) value + 0.1f;
                    module.updateSetting(key, newValue); // Increment float
                    System.out.println("[DEBUG] Updated " + key + " to: " + newValue); // Debug log
                }
            }

            y += 20;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
