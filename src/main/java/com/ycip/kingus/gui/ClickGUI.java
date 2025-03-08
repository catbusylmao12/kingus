package com.ycip.kingus.gui;

import com.ycip.kingus.gui.components.CategoryPanel;
import com.ycip.kingus.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    private final ModuleManager moduleManager;
    private final List<CategoryPanel> categoryPanels = new ArrayList<>();
    private int offsetY = 0; // For scrolling

    public ClickGUI(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        initPanels();
    }

    private void initPanels() {
        int x = 20;
        for (String category : moduleManager.getCategories()) {
            categoryPanels.add(new CategoryPanel(category, x, 20, 100, 15, moduleManager.getModulesByCategory(category)));
            x += 110; // Spacing between panels
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        // Draw background
        drawRect(0, 0, screenWidth, screenHeight, new Color(30, 30, 30, 200).getRGB());

        // Draw category panels
        for (CategoryPanel panel : categoryPanels) {
            panel.drawComponent(mouseX, mouseY - offsetY);
        }

        // Handle scrolling
        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            offsetY += scroll > 0 ? 10 : -10;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel panel : categoryPanels) {
            panel.mouseClicked(mouseX, mouseY - offsetY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for (CategoryPanel panel : categoryPanels) {
            panel.mouseDrag(mouseX, mouseY - offsetY, clickedMouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // Close the GUI when ESC is pressed
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null); // Close the GUI
            return;
        }

        // Handle keybind input for panels
        for (CategoryPanel panel : categoryPanels) {
            panel.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
