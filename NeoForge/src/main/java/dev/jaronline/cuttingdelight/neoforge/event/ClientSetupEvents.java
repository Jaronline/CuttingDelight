package dev.jaronline.cuttingdelight.neoforge.event;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.ModMenuTypes;
import dev.jaronline.cuttingdelight.common.client.gui.screen.CuttingBoardScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;

@EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID, value = Dist.CLIENT)
public class ClientSetupEvents {
	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.CUTTING_BOARD_MENU, CuttingBoardScreen::new);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CUTTING_BOARD, CuttingBoardRenderer::new);
	}
}
