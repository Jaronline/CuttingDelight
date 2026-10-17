package dev.jaronline.cuttingdelight.fabric;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.ModMenuTypes;
import dev.jaronline.cuttingdelight.common.client.gui.screen.CuttingBoardScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;

public class CuttingDelightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ModBlockEntityTypes.CUTTING_BOARD, CuttingBoardRenderer::new);
		MenuScreens.register(ModMenuTypes.CUTTING_BOARD_MENU, CuttingBoardScreen::new);
	}
}
