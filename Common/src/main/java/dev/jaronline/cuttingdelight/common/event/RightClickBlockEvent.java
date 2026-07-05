package dev.jaronline.cuttingdelight.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface RightClickBlockEvent {
    Level getLevel();
    BlockPos getPos();
    Player getEntity();
    void setCanceled(boolean canceled);
    void setCancellationResult(InteractionResult result);
}
