package dev.jaronline.cuttingdelight.neoforge;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.config.ConfigManager;
import dev.jaronline.cuttingdelight.neoforge.adapter.NeoForgeRightClickBlockEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

@Mod(ModIds.CUTTING_DELIGHT_ID)
public class CuttingDelight {
	public static final Logger LOGGER = LogUtils.getLogger();

	public CuttingDelight(IEventBus modEventBus, ModContainer modContainer) {
		ConfigManager.setConfig(new Config());
		modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
		modEventBus.addListener(PlayerInteractEvent.RightClickBlock.class, event ->
				CustomCuttingBoardBlock.ToolCarvingEvent.onSneakPlaceTool(new NeoForgeRightClickBlockEvent(event)));
	}
}
