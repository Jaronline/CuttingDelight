package dev.jaronline.cuttingdelight.client.event;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.registry.BlockEntityTypeRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;

@EventBusSubscriber(modid = CuttingDelight.MOD_ID, value = Dist.CLIENT)
public class ClientSetupEvents {
    private static final BlockEntityTypeRegistry blockEntityTypes = BlockEntityTypeRegistry.getInstance();

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(blockEntityTypes.cuttingBoard.get(), CuttingBoardRenderer::new);
    }
}
