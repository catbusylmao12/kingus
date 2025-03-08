package com.ycip.kingus.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketBindCommand implements IMessage {
    private String command;

    // Default constructor required for Forge
    public PacketBindCommand() {}

    public PacketBindCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.command = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = command.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }
}
