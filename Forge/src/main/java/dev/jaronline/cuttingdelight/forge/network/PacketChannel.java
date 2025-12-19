package dev.jaronline.cuttingdelight.forge.network;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.network.CutPacket;
import dev.jaronline.cuttingdelight.common.network.Packet;
import dev.jaronline.cuttingdelight.common.server.ServerPacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketChannel {
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			ModIds.cuttingDelightResource("main"),
			() -> PROTOCOL_VERSION,
			PacketChannel::clientCompatible,
			PacketChannel::serverCompatible
	);

	public static void register() {
		int id = -1;
		INSTANCE.registerMessage(++id, CutPacket.class,
				CutPacket::write, CutPacket::new, PacketChannel::serverHandler);
	}

	public static void sendToServer(Packet<ServerPacketListener> packet) {
		INSTANCE.sendToServer(packet);
	}

	private static <P extends Packet<ServerPacketListener>> void serverHandler(P packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null) return;

			packet.handle(ServerPacketListener.getListenerFor(sender));
		});
		ctx.get().setPacketHandled(true);
	}

	private static boolean clientCompatible(String version) {
		return PROTOCOL_VERSION.equals(version);
	}

	private static boolean serverCompatible(String version) {
		return PROTOCOL_VERSION.equals(version);
	}
}
