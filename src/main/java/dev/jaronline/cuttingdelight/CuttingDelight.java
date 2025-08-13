package dev.jaronline.cuttingdelight;

import dev.jaronline.cuttingdelight.registry.BlockEntityTypeRegistry;
import dev.jaronline.cuttingdelight.registry.BlockRegistry;
import dev.jaronline.cuttingdelight.registry.CreativeTabRegistry;
import dev.jaronline.cuttingdelight.registry.ItemRegistry;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CuttingDelight.MOD_ID)
public class CuttingDelight {
    public static final String MOD_ID = "cuttingdelight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CuttingDelight(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BlockRegistry.register(modEventBus);
        BlockEntityTypeRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
