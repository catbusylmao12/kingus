package com.ycip.kingus.gui.components;

import com.ycip.kingus.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class CategoryPanel extends Component {
    private final String category;
    private final List<Module> modules;
    private boolean expanded = true;
    private final Minecraft mc;
    private Module selectedModule = null; // Track which module's settings are being shown
    private boolean listeningForKeybind = false; // Track if we're listening for a keybind
    private String keybindSettingKey = null; // Track which module's keybind is being set
    private boolean draggingSlider = false; // Track if a slider is being dragged
    private String draggedSettingKey = null; // Track which setting's slider is being dragged

    public CategoryPanel(String category, int x, int y, int width, int height, List<Module> modules) {
        super(x, y, width, height);
        this.category = category;
        this.modules = modules;
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        FontRenderer fr = mc.fontRendererObj;

        // Draw category header
        Gui.drawRect(x, y, x + width, y + height, new Color(50, 50, 50, 200).getRGB());
        fr.drawStringWithShadow(category, x + 5, y + 4, 0xFFFFFF);

        // Draw modules if expanded
        if (expanded) {
            int moduleY = y + height;
            for (Module module : modules) {
                // Draw module name
                Gui.drawRect(x, moduleY, x + width, moduleY + 15, new Color(70, 70, 70, 200).getRGB());
                fr.drawStringWithShadow(module.getName(), x + 5, moduleY + 4, module.isEnabled() ? 0x00FF00 : 0xFF0000);

                // Draw settings if this module is selected
                if (module == selectedModule) {
                    int settingY = moduleY + 15;
                    for (Map.Entry<String, Object> entry : module.getSettings().entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        // Draw setting name
                        Gui.drawRect(x, settingY, x + width, settingY + 15, new Color(90, 90, 90, 200).getRGB());

                        // Handle boolean settings (e.g., toggle buttons)
                        if (value instanceof Boolean) {
                            // Draw the setting name
                            fr.drawStringWithShadow(key, x + 5, settingY + 4, 0xFFFFFF);

                            // Draw the ON/OFF button
                            Gui.drawRect(x + width - 30, settingY + 2, x + width - 10, settingY + 12, new Color(100, 100, 100, 200).getRGB());
                            fr.drawStringWithShadow((Boolean) value ? "ON" : "OFF", x + width - 25, settingY + 4, (Boolean) value ? 0x00FF00 : 0xFF0000);
                        }

                        // Handle numeric settings (e.g., sliders)
                        else if ((value instanceof Float || value instanceof Integer) && !key.equals("Keybind")) {
                            // Draw the setting name and value
                            fr.drawStringWithShadow(key + ": " + value, x + 5, settingY + 4, 0xFFFFFF);

                            // Draw slider
                            int sliderWidth = width - 10;
                            int sliderX = x + 5;
                            int sliderY = settingY + 12;

                            // Draw slider background
                            Gui.drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, new Color(50, 50, 50, 200).getRGB());

                            // Calculate slider position based on value
                            float sliderValue = (value instanceof Float) ? (Float) value : (Integer) value / 100.0f;
                            int sliderPos = (int) (sliderX + (sliderWidth * sliderValue));

                            // Draw slider handle
                            Gui.drawRect(sliderPos - 2, sliderY - 2, sliderPos + 2, sliderY + 4, new Color(255, 255, 255, 200).getRGB());
                        }

                        // Handle keybind setting (as a button)
                        else if (key.equals("Keybind")) {
                            // Draw the keybind button
                            Gui.drawRect(x + width - 50, settingY + 2, x + width - 10, settingY + 12, new Color(100, 100, 100, 200).getRGB());
                            String keybindText = listeningForKeybind ? "Press a key..." : Keyboard.getKeyName((int) value);
                            fr.drawStringWithShadow("Keybind: " + keybindText, x + 5, settingY + 4, 0xFFFFFF);
                        }

                        settingY += 15;
                    }
                }

                moduleY += (module == selectedModule) ? 15 * (module.getSettings().size() + 1) : 15;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        System.out.println("[DEBUG] Mouse clicked at: (" + mouseX + ", " + mouseY + ")"); // Debug log

        // Check if the mouse is within the category header
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (button == 0) { // Left click to toggle expansion
                expanded = !expanded;
                System.out.println("[DEBUG] Toggled expansion: " + expanded); // Debug log
            }
        }

        // Handle module clicks
        if (expanded) {
            int moduleY = y + height; // Start drawing modules below the category header
            for (Module module : modules) {
                // Define the module's hitbox
                if (mouseX >= x && mouseX <= x + width && mouseY >= moduleY && mouseY <= moduleY + 15) {
                    System.out.println("[DEBUG] Clicked on module: " + module.getName()); // Debug log

                    if (button == 0) { // Left click to toggle module
                        module.toggle();
                        System.out.println("[DEBUG] Toggled module: " + module.getName() + ", Enabled: " + module.isEnabled()); // Debug log
                    } else if (button == 1) { // Right click to show/hide settings
                        if (selectedModule == module) {
                            selectedModule = null; // Hide settings if already shown
                            System.out.println("[DEBUG] Hid settings for module: " + module.getName()); // Debug log
                        } else {
                            selectedModule = module; // Show settings for this module
                            System.out.println("[DEBUG] Showed settings for module: " + module.getName()); // Debug log
                        }
                    }
                }

                // Handle clicks on settings
                if (module == selectedModule) {
                    int settingY = moduleY + 15; // Start drawing settings below the module
                    for (Map.Entry<String, Object> entry : module.getSettings().entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        // Define the setting's hitbox
                        if (mouseX >= x && mouseX <= x + width && mouseY >= settingY && mouseY <= settingY + 15) {
                            System.out.println("[DEBUG] Clicked on setting: " + key); // Debug log
                            System.out.println("[DEBUG] Setting hitbox: X=" + x + ", Y=" + settingY + ", Width=" + width + ", Height=15"); // Debug log

                            // Handle boolean settings (e.g., toggle buttons)
                            if (value instanceof Boolean) {
                                boolean newValue = !(Boolean) value; // Toggle the boolean value
                                module.updateSetting(key, newValue);
                                System.out.println("[DEBUG] Toggled setting: " + key + ", New value: " + newValue); // Debug log
                            }

                            // Handle slider dragging for numeric settings
                            else if ((value instanceof Float || value instanceof Integer) && !key.equals("Keybind")) {
                                // Debug log for slider hitbox
                                System.out.println("[DEBUG] Slider hitbox: X=" + (x + 5) + ", Y=" + (settingY + 10) + ", Width=" + (width - 10) + ", Height=4");

                                if (mouseX >= x + 5 && mouseX <= x + width - 5 && mouseY >= settingY + 10 && mouseY <= settingY + 14) {
                                    draggingSlider = true;
                                    draggedSettingKey = key;
                                    System.out.println("[DEBUG] Started dragging slider for setting: " + key); // Debug log
                                }
                            }

                            // Handle keybind setting (as a button)
                            else if (key.equals("Keybind")) {
                                listeningForKeybind = true;
                                keybindSettingKey = key;
                                System.out.println("[DEBUG] Listening for keybind for setting: " + key); // Debug log
                            }
                        }

                        settingY += 15; // Move to the next setting
                    }
                }

                moduleY += (module == selectedModule) ? 15 * (module.getSettings().size() + 1) : 15; // Adjust moduleY for the next module
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        draggingSlider = false; // Stop dragging when mouse is released
        draggedSettingKey = null;
        System.out.println("[DEBUG] Stopped dragging slider"); // Debug log
    }

    @Override
    public void mouseDrag(int mouseX, int mouseY, int button) {
        if (draggingSlider && selectedModule != null && draggedSettingKey != null) {
            int sliderWidth = width - 10; // Width of the slider
            int sliderX = x + 5; // X position of the slider

            // Debug log for mouse position during drag
            System.out.println("[DEBUG] Dragging slider. Mouse position: (" + mouseX + ", " + mouseY + ")");

            // Clamp mouseX to the slider's bounds
            int clampedMouseX = Math.max(sliderX, Math.min(mouseX, sliderX + sliderWidth));

            // Calculate new value based on clamped mouse position
            float sliderValue = (float) (clampedMouseX - sliderX) / sliderWidth;
            sliderValue = Math.max(0, Math.min(1, sliderValue)); // Clamp between 0 and 1

            // Debug log for slider value
            System.out.println("[DEBUG] Slider value: " + sliderValue);

            // Update the setting
            Object currentValue = selectedModule.getSetting(draggedSettingKey);
            if (currentValue instanceof Float) {
                selectedModule.updateSetting(draggedSettingKey, sliderValue);
                System.out.println("[DEBUG] Updated setting: " + draggedSettingKey + ", New value: " + sliderValue); // Debug log
            } else if (currentValue instanceof Integer) {
                selectedModule.updateSetting(draggedSettingKey, (int) (sliderValue * 100)); // Example for integer values
                System.out.println("[DEBUG] Updated setting: " + draggedSettingKey + ", New value: " + (int) (sliderValue * 100)); // Debug log
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (listeningForKeybind && selectedModule != null && keybindSettingKey != null) {
            if (keyCode != Keyboard.KEY_ESCAPE) { // Don't allow ESC to be a keybind
                selectedModule.updateSetting(keybindSettingKey, keyCode);
                selectedModule.setKey(keyCode);
                System.out.println("[DEBUG] Updated keybind for setting: " + keybindSettingKey + ", New key: " + keyCode); // Debug log
            }
            listeningForKeybind = false; // Stop listening after a key is pressed
            keybindSettingKey = null; // Reset the keybind setting key
        }
    }
}
