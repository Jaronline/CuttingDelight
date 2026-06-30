package dev.jaronline.cuttingdelight.common.platform;

import dev.jaronline.cuttingdelight.common.network.Packet;
import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;

public interface PlatformClientHelper {
	void send(Packet<ServerPacketListener> packet);
}
