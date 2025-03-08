package com.ycip.kingus.module.visual;

import com.ycip.kingus.module.Module;
import com.ycip.kingus.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModuleManager moduleManager = ModuleManager.getInstance();

    public HUD() {
        super("HUD", "Visual");
        this.setKey(Keyboard.KEY_H); // Default keybind to 'H'

        // Initialize settings
        this.addSetting("Watermark", true);
        this.addSetting("BPS", true);
        this.addSetting("FPS", true);
        this.addSetting("ArrayList", true);
    }

    // Helper methods to check if settings are enabled
    public boolean isWatermarkEnabled() {
        return (boolean) this.getSetting("Watermark");
    }

    public boolean isBPSEnabled() {
        return (boolean) this.getSetting("BPS");
    }

    public boolean isFPSEnabled() {
        return (boolean) this.getSetting("FPS");
    }

    public boolean isArrayListEnabled() {
        return (boolean) this.getSetting("ArrayList");
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled() || event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        // Ensure the world and font renderer are available
        if (mc.theWorld == null || mc.fontRendererObj == null) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fr = mc.fontRendererObj;

        // Render Watermark (Top-left corner)
        if (isWatermarkEnabled()) {
            renderWatermark(fr, 2, 2);
        }

        // Render BPS (Blocks Per Second) (Bottom-right corner)
        if (isBPSEnabled()) {
            renderBPS(fr, width, height);
        }

        // Render FPS (Above BPS in the bottom-right corner)
        if (isFPSEnabled()) {
            renderFPS(fr, width, height);
        }

        // Render ArrayList (Top-right corner)
        if (isArrayListEnabled()) {
            renderArrayList(fr, width);
        }
    }

    private void renderWatermark(FontRenderer fr, int x, int y) {
        String watermark = "Kingus Client";
        fr.drawStringWithShadow(watermark, x, y, 0xFFFFFF);
    }

    private void renderBPS(FontRenderer fr, int width, int height) {
        double bps = calculateBPS();
        String bpsText = String.format("BPS: %.1f", bps);
        int bpsX = width - fr.getStringWidth(bpsText) - 2; // Right-aligned
        int bpsY = height - 50; // 50 pixels from the bottom
        fr.drawStringWithShadow(bpsText, bpsX, bpsY, 0xFFFFFF);
    }

    private void renderFPS(FontRenderer fr, int width, int height) {
        int fps = Minecraft.getDebugFPS();
        String fpsText = "FPS: " + fps;
        int fpsX = width - fr.getStringWidth(fpsText) - 2; // Right-aligned
        int fpsY = height - 60; // 10 pixels above BPS
        fr.drawStringWithShadow(fpsText, fpsX, fpsY, 0xFFFFFF);
    }

    private void renderArrayList(FontRenderer fr, int width) {
        List<String> activeModules = getActiveModules();
        if (activeModules.isEmpty()) {
            System.out.println("No active modules to display in ArrayList.");
            return;
        }

        int yOffset = 2; // Start rendering at the top
        for (String moduleName : activeModules) {
            int moduleWidth = fr.getStringWidth(moduleName);
            int moduleX = width - moduleWidth - 2; // Right-aligned
            fr.drawStringWithShadow(moduleName, moduleX, yOffset, 0xFFFFFF);
            yOffset += 10; // Move down for the next module
        }
    }

    private double calculateBPS() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return 0.0;
        }
        return Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 20;
    }

    private List<String> getActiveModules() {
        try {
            if (moduleManager == null || moduleManager.getModules() == null) {
                System.out.println("ModuleManager or modules list is null.");
                return Collections.emptyList();
            }

            // Debugging: Print all modules and their enabled status
            System.out.println("--- Active Modules Debug ---");
            moduleManager.getModules().forEach(module -> {
                if (module != null) {
                    System.out.println("Module: " + module.getName() + ", Enabled: " + module.isEnabled());
                } else {
                    System.out.println("Found a null module in the list.");
                }
            });

            // Filter and return enabled modules (excluding HUD itself)
            List<String> activeModules = moduleManager.getModules().stream()
                    .filter(module -> module != null && module.isEnabled() && !module.getName().equalsIgnoreCase("HUD"))
                    .map(Module::getName)
                    .collect(Collectors.toList());

            System.out.println("Active Modules to Display: " + activeModules);
            return activeModules;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
