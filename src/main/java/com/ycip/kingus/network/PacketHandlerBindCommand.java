package com.ycip.kingus.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerBindCommand implements IMessageHandler<PacketBindCommand, IMessage> {
    @Override
    public IMessage onMessage(PacketBindCommand message, MessageContext ctx) {
        // This code runs on the server side
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        String command = message.getCommand();

        System.out.println("[DEBUG] Received packet with command: " + command); // Debug log

        // Process the .bind command
        if (command.startsWith(".bind")) {
            System.out.println("[DEBUG] Processing .bind command"); // Debug log
            String[] parts = command.split(" ");
            if (parts.length == 3) {
                String keyName = parts[1].toUpperCase();
                String moduleName = parts[2].toLowerCase();

                // Handle the bind command (e.g., update key bindings)
                // You can add your logic here
                player.addChatMessage(new ChatComponentText("[Catutils] bind successful [Module name] < " + moduleName));
                System.out.println("[DEBUG] Bound " + moduleName + " to key: " + keyName); // Debug log
            } else {
                player.addChatMessage(new ChatComponentText("[CATUTILS] Usage: .bind [key] [module]"));
                System.out.println("[DEBUG] Invalid .bind command format: " + command); // Debug log
            }
        }

        return null; // No response packet
    }
}
