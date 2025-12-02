package dev.jaronline.cuttingdelight.common.network;

import dev.jaronline.cuttingdelight.CuttingDelight;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CuttingBoardEmptiedPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CuttingBoardEmptiedPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "board_emptied"));

    public static final StreamCodec<ByteBuf, CuttingBoardEmptiedPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CuttingBoardEmptiedPayload::blockPos,
            CuttingBoardEmptiedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
