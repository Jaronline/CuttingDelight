package dev.jaronline.cuttingdelight.common.network;

import net.minecraft.network.FriendlyByteBuf;

public sealed interface Packet<H extends PacketListener> permits CutPacket {
	void write(FriendlyByteBuf friendlyByteBuf);
	void handle(H handler);
}
