package com.ycip.kingus;

import com.ycip.kingus.module.ModuleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "wallhack", name = "Wallhack Mod", version = "1.0")
public class WallhackMod {
    private final ModuleManager moduleManager = ModuleManager.getInstance(); // Use the singleton instance

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new BindManager(moduleManager));
        System.out.println("[DEBUG] WallhackMod initialized"); // Debug log
    }
}
