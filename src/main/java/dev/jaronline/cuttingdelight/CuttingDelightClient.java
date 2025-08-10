package dev.jaronline.cuttingdelight;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CuttingDelight.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CuttingDelight.MOD_ID, value = Dist.CLIENT)
public class CuttingDelightClient {
    public CuttingDelightClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CuttingDelight.LOGGER.info("HELLO FROM CLIENT SETUP");
    }
}
