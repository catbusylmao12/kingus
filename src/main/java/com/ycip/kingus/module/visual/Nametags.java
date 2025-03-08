package com.ycip.kingus.module.visual;

import com.ycip.kingus.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Nametags extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US)); // Use decimal point

    public Nametags() {
        super("Nametags", "Visual"); // Pass both name and category
        this.setKey(Keyboard.KEY_N); // Set default keybind to 'N'

        // Add settings dynamically
        this.addSetting("Scale", 1.0f); // Float value for slider (0.5 to 1.5)
        this.addSetting("Self Display", false); // Show nametag for the player
        this.addSetting("Background", true); // Show background behind nametags
        this.addSetting("Show Distance", true); // Show distance to the entity
        this.addSetting("Show Health", true); // Show health of the entity
        this.addSetting("Show Armor", false); // Show armor of the player
        this.addSetting("Show Players", true); // Show nametags for players
        this.addSetting("Show Bosses", true); // Show nametags for bosses
        this.addSetting("Show Mobs", false); // Show nametags for mobs
        this.addSetting("Show Animals", false); // Show nametags for animals

        // Add keybind setting
        this.addSetting("Keybind", this.getKey());
    }

    // Helper methods to get settings
    public float getScale() {
        return (float) this.getSetting("Scale");
    }

    public boolean isSelfDisplay() {
        return (boolean) this.getSetting("Self Display");
    }

    public boolean isBackground() {
        return (boolean) this.getSetting("Background");
    }

    public boolean isShowDistance() {
        return (boolean) this.getSetting("Show Distance");
    }

    public boolean isShowHealth() {
        return (boolean) this.getSetting("Show Health");
    }

    public boolean isShowArmor() {
        return (boolean) this.getSetting("Show Armor");
    }

    public boolean isShowPlayers() {
        return (boolean) this.getSetting("Show Players");
    }

    public boolean isShowBosses() {
        return (boolean) this.getSetting("Show Bosses");
    }

    public boolean isShowMobs() {
        return (boolean) this.getSetting("Show Mobs");
    }

    public boolean isShowAnimals() {
        return (boolean) this.getSetting("Show Animals");
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (isEnabled()) {
            EntityPlayer player = mc.thePlayer;

            if (player != null) {
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityLivingBase && isValid((EntityLivingBase) entity)) {
                        renderNametag((EntityLivingBase) entity, event.partialTicks);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderNametag(RenderLivingEvent.Specials.Pre<?> event) {
        if (isEnabled() && event.entity instanceof EntityPlayer) {
            event.setCanceled(true); // Cancel the original nametag rendering
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        if (entity.isInvisible()) {
            return false;
        }
        if (mc.thePlayer.getDistanceToEntity(entity) > 512.0f) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return isSelfDisplay(); // Only show self if Self Display is enabled
            } else {
                return isShowPlayers(); // Show other players if Show Players is enabled
            }
        } else if (entity instanceof EntityDragon || entity instanceof EntityWither) {
            return isShowBosses();
        } else if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            return isShowMobs();
        } else if (entity instanceof EntityAnimal || entity instanceof EntityBat || entity instanceof EntitySquid || entity instanceof EntityVillager) {
            return isShowAnimals();
        }
        return false;
    }

    private void renderNametag(EntityLivingBase entity, float partialTicks) {
        // Interpolate entity position to prevent jitter
        double x = interpolate(entity.posX, entity.lastTickPosX, partialTicks) - mc.getRenderManager().viewerPosX;
        double y = interpolate(entity.posY, entity.lastTickPosY, partialTicks) - mc.getRenderManager().viewerPosY + entity.height + 0.5;
        double z = interpolate(entity.posZ, entity.lastTickPosZ, partialTicks) - mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix(); // Save the current transformation matrix
        GL11.glTranslated(x, y, z); // Translate to the entity's position
        GL11.glNormal3f(0.0F, 1.0F, 0.0F); // Set the normal vector
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F); // Rotate to face the camera

        // Calculate scaling based on distance
        double distance = mc.thePlayer.getDistanceToEntity(entity);
        double factor = Math.pow(Math.min(Math.max(distance, 6.0), 100.0), 0.7) * 0.0077;
        GL11.glScaled(-factor * getScale(), -factor * getScale(), 1.0); // Apply scaling

        // Disable depth testing so the nametag is visible through blocks
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Build the nametag text
        String name = entity.getDisplayName().getFormattedText();
        String distanceText = isShowDistance() ? String.format("%d", (int) distance) : "";

        // Calculate health value
        float hp = (float) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
        String healthText = isShowHealth() ? df.format(hp / 2.0) : ""; // Display health divided by 2 (like in your example)

        // Render the nametag background (if enabled)
        if (isBackground()) {
            FontRenderer fontRenderer = mc.fontRendererObj;
            int width = fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText);
            Gui.drawRect(-width / 2 - 2, -fontRenderer.FONT_HEIGHT - 2, width / 2 + 2, 2, 0x80000000); // Semi-transparent black background
        }

        // Render the nametag text
        FontRenderer fontRenderer = mc.fontRendererObj;

        // Render distance (left, with colored brackets)
        if (isShowDistance()) {
            // Render the left bracket "["
            fontRenderer.drawStringWithShadow("[", -fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText) / 2, -fontRenderer.FONT_HEIGHT, 0x55FF55); // Green color for brackets

            // Render the distance number (white)
            fontRenderer.drawStringWithShadow(distanceText, -fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText) / 2 + fontRenderer.getStringWidth("["), -fontRenderer.FONT_HEIGHT, 0xFFFFFF);

            // Render the right bracket "]"
            fontRenderer.drawStringWithShadow("]", -fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText) / 2 + fontRenderer.getStringWidth("[" + distanceText), -fontRenderer.FONT_HEIGHT, 0x55FF55); // Green color for brackets
        }

        // Render name (middle, white)
        int nameOffset = isShowDistance() ? fontRenderer.getStringWidth("[" + distanceText + "] ") : 0;
        fontRenderer.drawStringWithShadow(name, -fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText) / 2 + nameOffset, -fontRenderer.FONT_HEIGHT, 0xFFFFFF);

        // Render health (right, colored based on health)
        if (isShowHealth()) {
            int healthOffset = fontRenderer.getStringWidth("[" + distanceText + "] " + name + " ");
            fontRenderer.drawStringWithShadow(healthText, -fontRenderer.getStringWidth("[" + distanceText + "] " + name + " " + healthText) / 2 + healthOffset, -fontRenderer.FONT_HEIGHT, getHealthColor(hp / entity.getMaxHealth()));
        }

        // Re-enable depth testing
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix(); // Restore the previous transformation matrix
    }

    private double interpolate(double current, double previous, float partialTicks) {
        return previous + (current - previous) * partialTicks;
    }

    private int getHealthColor(float healthRatio) {
        if (healthRatio >= 0.75f) return 0x00FF00; // Green
        else if (healthRatio >= 0.25f) return 0xFFFF00; // Yellow
        else return 0xFF0000; // Red
    }
}
