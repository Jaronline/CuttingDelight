package dev.jaronline.cuttingdelight.forge.provider;

import dev.jaronline.cuttingdelight.common.network.Packet;
import dev.jaronline.cuttingdelight.common.provider.ClientPacketListenerProvider;
import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;
import dev.jaronline.cuttingdelight.core.provider.AutoProvider;
import dev.jaronline.cuttingdelight.forge.network.PacketChannel;

@AutoProvider
public class ForgeClientPacketListenerProvider implements ClientPacketListenerProvider {
	@Override
	public void send(Packet<ServerPacketListener> packet) {
		PacketChannel.sendToServer(packet);
	}
}
