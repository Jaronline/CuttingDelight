package dev.jaronline.cuttingdelight.forge.event;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.ModMenuTypes;
import dev.jaronline.cuttingdelight.common.client.gui.screen.CuttingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;

@Mod.EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(ModMenuTypes.CUTTING_STATION_MENU, CuttingStationScreen::new);
		});
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CUTTING_STATION, CuttingBoardRenderer::new);
	}
}
