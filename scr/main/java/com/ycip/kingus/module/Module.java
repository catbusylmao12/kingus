package com.ycip.kingus.module;

import java.util.HashMap;
import java.util.Map;

public class Module {
    private final String name;
    private final String category;
    private int key;
    private boolean enabled;
    private final Map<String, Object> settings = new HashMap<>();

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
        this.key = -1; // Default to no keybind
        this.enabled = false; // Default to disabled
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        System.out.println("[DEBUG] " + name + " toggled. Enabled: " + enabled); // Debug log
    }

    public void addSetting(String key, Object value) {
        settings.put(key, value);
    }

    public Object getSetting(String key) {
        return settings.get(key);
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void updateSetting(String key, Object value) {
        if (settings.containsKey(key)) {
            settings.put(key, value);
        }
    }
