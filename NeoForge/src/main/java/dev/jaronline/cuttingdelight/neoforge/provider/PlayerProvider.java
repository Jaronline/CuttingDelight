package dev.jaronline.cuttingdelight.neoforge.provider;

import dev.jaronline.cuttingdelight.common.provider.IPlayerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class PlayerProvider implements IPlayerProvider {
    @Override
    public void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, BlockPos blockPos) {
        serverPlayer.openMenu(menuProvider, blockPos);
    }
}
