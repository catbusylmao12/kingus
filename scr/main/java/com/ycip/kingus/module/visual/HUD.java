package com.ycip.kingus.module.visual;

import com.ycip.kingus.module.Module;
import com.ycip.kingus.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModuleManager moduleManager = ModuleManager.getInstance(); // Access the singleton
    private final List<String> arrayList = new ArrayList<>();

    public HUD() {
        super("HUD", "Visual"); // Pass both name and category
        this.setKey(Keyboard.KEY_H); // Set default keybind to 'H'

        // Add settings dynamically
        this.addSetting("Watermark", true); // Show watermark
        this.addSetting("BPS", true); // Show blocks per second
        this.addSetting("FPS", true); // Show frames per second
        this.addSetting("ArrayList", true); // Show ArrayList
    }

    // Helper methods to get settings
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
        if (isEnabled() && event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            ScaledResolution sr = new ScaledResolution(mc);
            int width = sr.getScaledWidth();
            int height = sr.getScaledHeight();
            FontRenderer fr = mc.fontRendererObj;

            // Render Watermark
            if (isWatermarkEnabled()) {
                String watermark = "Kingus Client";
                fr.drawStringWithShadow(watermark, 2, 2, 0xFFFFFF);
            }

            // Render BPS (Blocks Per Second)
            if (isBPSEnabled()) {
                double bps = calculateBPS();
                String bpsText = String.format("BPS: %.1f", bps);
                fr.drawStringWithShadow(bpsText, width - fr.getStringWidth(bpsText) - 2, 2, 0xFFFFFF);
            }

            // Render FPS
            if (isFPSEnabled()) {
                int fps = Minecraft.getDebugFPS();
                String fpsText = "FPS: " + fps;
                fr.drawStringWithShadow(fpsText, width - fr.getStringWidth(fpsText) - 2, 12, 0xFFFFFF);
            }

            // Render ArrayList
            if (isArrayListEnabled()) {
                int yOffset = 20; // Start rendering below the watermark, BPS, and FPS
                for (String moduleName : getActiveModules()) {
                    fr.drawStringWithShadow(moduleName, width - fr.getStringWidth(moduleName) - 5, yOffset, 0xFFFFFF); // Render module name
                    yOffset += 10; // Move down for the next module
                }
            }
        }
    }

    private double calculateBPS() {
        // Calculate blocks per second based on player movement
        if (mc.thePlayer == null || mc.theWorld == null) {
            return 0.0; // Avoid NullPointerException
        }
        return Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 20;
    }

    private List<String> getActiveModules() {
        arrayList.clear(); // Clear the list before populating it
        if (moduleManager == null) {
            System.out.println("[DEBUG] ModuleManager is null!"); // Debug log
            return arrayList; // Avoid NullPointerException
        }

        // Safely iterate over modules
        List<Module> modules = new ArrayList<>(moduleManager.getModules()); // Create a copy to avoid concurrent modification
        System.out.println("[DEBUG] Total modules: " + modules.size()); // Debug log

        for (Module module : modules) {
            System.out.println("[DEBUG] Checking module: " + module.getName() + " (Enabled: " + module.isEnabled() + ")"); // Debug log
            if (module.isEnabled() && !module.getName().equals("HUD")) { // Exclude the HUD module itself
                arrayList.add(module.getName()); // Add the name of the enabled module
                System.out.println("[DEBUG] Added active module: " + module.getName()); // Debug log
            }
        }

        // Debug log to verify active modules
        System.out.println("[DEBUG] Active Modules: " + arrayList);

        return arrayList;
    }
}
