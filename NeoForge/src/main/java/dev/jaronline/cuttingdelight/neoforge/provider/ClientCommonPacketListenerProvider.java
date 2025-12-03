package dev.jaronline.cuttingdelight.neoforge.provider;

import dev.jaronline.cuttingdelight.common.provider.IClientCommonPacketListenerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Objects;

public class ClientCommonPacketListenerProvider implements IClientCommonPacketListenerProvider {
	@Override
	public void send(CustomPacketPayload payload) {
		ClientPacketListener connection = Objects.requireNonNull(Minecraft.getInstance().getConnection());
		connection.send(payload);
	}
}
