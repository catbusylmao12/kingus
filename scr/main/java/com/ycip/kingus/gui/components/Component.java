package com.ycip.kingus.gui.components;

import java.awt.*;

public abstract class Component {
    protected int x, y, width, height;
    protected boolean hovered;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void drawComponent(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, int button);

    public void mouseReleased(int mouseX, int mouseY, int button) {
        // Default implementation (can be overridden)
    }

    public void mouseDrag(int mouseX, int mouseY, int button) {
        // Default implementation (can be overridden)
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    // Add this method to allow keyTyped to be overridden
    public void keyTyped(char typedChar, int keyCode) {
        // Default implementation (can be overridden)
    }
}
