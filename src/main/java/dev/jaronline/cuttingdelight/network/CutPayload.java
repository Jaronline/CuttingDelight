package dev.jaronline.cuttingdelight.network;

import dev.jaronline.cuttingdelight.CuttingDelight;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public record CutPayload(BlockPos blockPos, Recipe<?> recipe) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CutPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "cut"));

    public static final StreamCodec<ByteBuf, CutPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CutPayload::blockPos,
            ByteBufCodecs.fromCodec(CuttingBoardRecipe.CODEC),
            CutPayload::recipe,
            CutPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
