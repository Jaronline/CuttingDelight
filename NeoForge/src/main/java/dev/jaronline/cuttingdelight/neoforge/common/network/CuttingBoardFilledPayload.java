package dev.jaronline.cuttingdelight.neoforge.common.network;

import dev.jaronline.cuttingdelight.neoforge.CuttingDelight;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CuttingBoardFilledPayload(BlockPos blockPos, ItemStack itemStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CuttingBoardFilledPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "board_filled"));

    public static final StreamCodec<ByteBuf, CuttingBoardFilledPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CuttingBoardFilledPayload::blockPos,
            ByteBufCodecs.fromCodec(ItemStack.CODEC),
            CuttingBoardFilledPayload::itemStack,
            CuttingBoardFilledPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
