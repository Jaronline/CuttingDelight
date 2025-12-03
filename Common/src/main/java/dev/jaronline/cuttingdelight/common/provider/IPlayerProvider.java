package dev.jaronline.cuttingdelight.common.provider;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface IPlayerProvider {
    void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BlockPos blockPos);
}
