package dev.jaronline.cuttingdelight.neoforge.event;

import dev.jaronline.cuttingdelight.common.*;
import dev.jaronline.cuttingdelight.common.network.CutPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardFilledPayload;
import dev.jaronline.cuttingdelight.common.server.ServerPayloadHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;

@EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID)
public class SetupEvents {
    @SubscribeEvent
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                CutPayload.TYPE,
                CutPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(playerPayloadHandlerWrapper(ServerPayloadHandler::handleCut))
        );
        registrar.playToServer(
                CuttingBoardFilledPayload.TYPE,
                CuttingBoardFilledPayload.STREAM_CODEC,
                new MainThreadPayloadHandler<>(playerPayloadHandlerWrapper(ServerPayloadHandler::handleCuttingBoardFilled))
        );
    }

    @SubscribeEvent
    public static void registerEntries(RegisterEvent event) {
        Bootstrapper.boostrapByRegistryKey(event.getRegistryKey());
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> playerPayloadHandlerWrapper(BiConsumer<T, Player> handler) {
        return (payload, context) -> handler.accept(payload, context.player());
    }
}
