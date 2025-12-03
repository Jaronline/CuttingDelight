package dev.jaronline.cuttingdelight.common.network;

import dev.jaronline.cuttingdelight.common.ModIds;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record CuttingBoardFilledPayload(ItemStack itemStack) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CuttingBoardFilledPayload> TYPE = new CustomPacketPayload.Type<>(ModIds.cuttingDelightResource("board_filled"));

	public static final StreamCodec<ByteBuf, CuttingBoardFilledPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodec(ItemStack.OPTIONAL_CODEC),
			CuttingBoardFilledPayload::itemStack,
			CuttingBoardFilledPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
