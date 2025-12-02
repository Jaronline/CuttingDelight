package dev.jaronline.cuttingdelight.common.event;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.network.CutPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardEmptiedPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardFilledPayload;
import dev.jaronline.cuttingdelight.server.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CuttingDelight.MOD_ID)
public class SetupEvents {
    @SubscribeEvent
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                CutPayload.TYPE,
                CutPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(ServerPayloadHandler::handleCut)
        );
        registrar.playToServer(
                CuttingBoardEmptiedPayload.TYPE,
                CuttingBoardEmptiedPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(ServerPayloadHandler::handleCuttingBoardEmptied)
        );
        registrar.playToServer(
                CuttingBoardFilledPayload.TYPE,
                CuttingBoardFilledPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(ServerPayloadHandler::handleCuttingBoardFilled)
        );
    }
}
