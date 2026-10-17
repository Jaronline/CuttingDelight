package dev.jaronline.cuttingdelight.fabric.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformClientHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientHelper implements PlatformClientHelper {
	@Override
	public void send(CustomPacketPayload payload) {
		ClientPlayNetworking.send(payload);
	}
}
