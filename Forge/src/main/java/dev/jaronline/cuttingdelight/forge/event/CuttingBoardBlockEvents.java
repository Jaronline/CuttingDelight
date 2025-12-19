package dev.jaronline.cuttingdelight.forge.event;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
