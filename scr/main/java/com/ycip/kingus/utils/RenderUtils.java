package com.ycip.kingus.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final RenderItem itemRenderer = mc.getRenderItem();

    public static void drawBorderedRect(float x, float y, float x2, float y2, float thickness, int borderColor, int fillColor) {
        // Cast float values to int for Gui.drawRect
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, fillColor); // Fill the rectangle
        Gui.drawRect((int) x, (int) y, (int) x2, (int) (y + thickness), borderColor); // Top border
        Gui.drawRect((int) x, (int) (y2 - thickness), (int) x2, (int) y2, borderColor); // Bottom border
        Gui.drawRect((int) x, (int) y, (int) (x + thickness), (int) y2, borderColor); // Left border
        Gui.drawRect((int) (x2 - thickness), (int) y, (int) x2, (int) y2, borderColor); // Right border
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        itemRenderer.zLevel = 0.0F;
        GlStateManager.disableDepth();
        GlStateManager.enableAlpha();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
