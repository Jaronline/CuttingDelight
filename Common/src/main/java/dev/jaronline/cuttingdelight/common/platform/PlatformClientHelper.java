package dev.jaronline.cuttingdelight.common.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface PlatformClientHelper {
	void send(CustomPacketPayload payload);
}
