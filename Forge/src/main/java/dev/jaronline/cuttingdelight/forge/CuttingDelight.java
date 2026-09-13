package dev.jaronline.cuttingdelight.forge;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CuttingStationBlock;
import dev.jaronline.cuttingdelight.forge.adapter.ForgeRightClickBlockEvent;
import dev.jaronline.cuttingdelight.forge.network.PacketChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModIds.CUTTING_DELIGHT_ID)
public class CuttingDelight {
	public CuttingDelight(FMLJavaModLoadingContext context) {
		context.registerConfig(ModConfig.Type.SERVER, ForgeConfig.SPEC);
		PacketChannel.register();
		Bus.FORGE.bus().get().addListener(ForgeRightClickBlockEvent.withMethod(CuttingStationBlock
				.ToolCarvingEvent::onSneakPlaceTool));
	}
}
