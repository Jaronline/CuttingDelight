package dev.jaronline.cuttingdelight.neoforge;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.neoforge.common.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

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
        MenuTypeRegistry.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
