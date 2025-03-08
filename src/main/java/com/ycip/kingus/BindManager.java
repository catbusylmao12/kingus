package com.ycip.kingus;

import com.ycip.kingus.gui.ClickGUI;
import com.ycip.kingus.module.Module;
import com.ycip.kingus.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BindManager {
    private final ModuleManager moduleManager;
    private final ClickGUI clickGUI;

    public BindManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.clickGUI = new ClickGUI(moduleManager);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        // Open ClickGUI when RIGHT_SHIFT is pressed
        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            Minecraft.getMinecraft().displayGuiScreen(clickGUI);
        }

        // Toggle modules based on their bound keys
        List<Module> modules = moduleManager.getModules();
        for (Module module : modules) {
            if (module.getKey() != Keyboard.KEY_NONE && Keyboard.isKeyDown(module.getKey())) {
                module.toggle();
            }
        }
    }
}
