package dev.jaronline.cuttingdelight.common.network;

import dev.jaronline.cuttingdelight.common.ModIds;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.Recipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public record CutPayload(Recipe<?> recipe) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CutPayload> TYPE = new CustomPacketPayload.Type<>(ModIds.cuttingDelightResource("cut"));

    public static final StreamCodec<ByteBuf, CutPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(CuttingBoardRecipe.CODEC),
            CutPayload::recipe,
            CutPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
