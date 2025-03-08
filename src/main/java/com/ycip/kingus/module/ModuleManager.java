package com.ycip.kingus.module;

import com.ycip.kingus.module.visual.Nametags;
import com.ycip.kingus.module.visual.HUD;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager(); // Singleton instance
    private final Map<String, Module> modules = new HashMap<>();

    private ModuleManager() {
        // Register modules here
        registerModule(new Nametags()); // Register the Nametags module
        registerModule(new HUD());      // Register the HUD module
    }

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public void registerModule(Module module) {
        modules.put(module.getName().toLowerCase(), module);
        MinecraftForge.EVENT_BUS.register(module); // Register the module with the event bus
        System.out.println("[DEBUG] Registered module: " + module.getName()); // Debug log
    }

    public Module getModuleByName(String name) {
        return modules.get(name.toLowerCase());
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }

    public List<Module> getModulesByCategory(String category) {
        return modules.values().stream()
                .filter(module -> module.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return modules.values().stream()
                .map(Module::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
}
