package dev.jaronline.cuttingdelight.neoforge.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Objects;

public class ClientHelper implements PlatformClientHelper {
	@Override
	public void send(CustomPacketPayload payload) {
		ClientPacketListener connection = Objects.requireNonNull(Minecraft.getInstance().getConnection());
		connection.send(payload);
	}
}
