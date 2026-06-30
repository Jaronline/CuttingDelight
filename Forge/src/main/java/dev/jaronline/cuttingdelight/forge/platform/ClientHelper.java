package dev.jaronline.cuttingdelight.forge.platform;

import dev.jaronline.cuttingdelight.common.network.Packet;
import dev.jaronline.cuttingdelight.common.platform.PlatformClientHelper;
import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;
import dev.jaronline.cuttingdelight.forge.network.PacketChannel;

public final class ClientHelper implements PlatformClientHelper {
    @Override
    public void send(Packet<ServerPacketListener> packet) {
        PacketChannel.sendToServer(packet);
    }
}
