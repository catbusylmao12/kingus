package com.ycip.kingus.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtils {
    public static void chat(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
