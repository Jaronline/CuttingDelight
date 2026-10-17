package dev.jaronline.cuttingdelight.fabric;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.Bootstrapper;
import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.network.CutPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardFilledPayload;
import dev.jaronline.cuttingdelight.common.server.ServerPayloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import org.slf4j.Logger;

public class CuttingDelight implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Bootstrapper.boostrapAll();
		//noinspection ResultOfMethodCallIgnored
		FabricConfig.bootstrap();
		FuelRegistry.INSTANCE.add(ModBlocks.CUTTING_BOARD, 200);
		PayloadTypeRegistry.playC2S().register(CutPayload.TYPE, CutPayload.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(CuttingBoardFilledPayload.TYPE, CuttingBoardFilledPayload.STREAM_CODEC);
		UseBlockCallback.EVENT.register(((player, world, hand, hitResult) ->
				CustomCuttingBoardBlock.ToolCarvingEvent.onSneakPlaceTool(world, hitResult.getBlockPos(), player)));
		ServerPlayNetworking.registerGlobalReceiver(CutPayload.TYPE, ((payload, context) ->
				ServerPayloadHandler.handleCut(payload, context.player())));
		ServerPlayNetworking.registerGlobalReceiver(CuttingBoardFilledPayload.TYPE, ((payload, context) ->
				ServerPayloadHandler.handleCuttingBoardFilled(payload, context.player())));
	}
}
