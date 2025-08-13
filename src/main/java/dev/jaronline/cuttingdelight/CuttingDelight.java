package dev.jaronline.cuttingdelight;

import dev.jaronline.cuttingdelight.common.registry.BlockEntityTypeRegistry;
import dev.jaronline.cuttingdelight.common.registry.BlockRegistry;
import dev.jaronline.cuttingdelight.common.registry.CreativeTabRegistry;
import dev.jaronline.cuttingdelight.common.registry.ItemRegistry;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
//import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

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

        NeoForge.EVENT_BUS.register(this);

//        Config.register(modContainer, ModConfig.Type.COMMON);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
