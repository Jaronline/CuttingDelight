package dev.jaronline.cuttingdelight.common.provider;

import dev.jaronline.cuttingdelight.common.network.Packet;
import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;
import dev.jaronline.cuttingdelight.core.provider.SingletonProvider;

public interface ClientPacketListenerProvider extends SingletonProvider {
	void send(Packet<ServerPacketListener> packet);
}
