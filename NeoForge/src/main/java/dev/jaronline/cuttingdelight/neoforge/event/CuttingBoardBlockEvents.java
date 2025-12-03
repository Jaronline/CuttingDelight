package dev.jaronline.cuttingdelight.neoforge.event;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID)
public class CuttingBoardBlockEvents {
    @SubscribeEvent
    public static void onSneakPlaceTool(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();

        InteractionResult result = CustomCuttingBoardBlock.ToolCarvingEvent.onSneakPlaceTool(level, pos, player);

        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}
