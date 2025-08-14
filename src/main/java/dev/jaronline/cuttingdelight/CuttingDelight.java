package dev.jaronline.cuttingdelight;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
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
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
