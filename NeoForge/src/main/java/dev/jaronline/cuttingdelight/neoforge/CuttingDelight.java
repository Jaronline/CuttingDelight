package dev.jaronline.cuttingdelight.neoforge;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.config.ConfigManager;
import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import dev.jaronline.cuttingdelight.neoforge.provider.ClientCommonPacketListenerProvider;
import dev.jaronline.cuttingdelight.neoforge.provider.InventoryProvider;
import dev.jaronline.cuttingdelight.neoforge.provider.PlayerProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ModIds.CUTTING_DELIGHT_ID)
public class CuttingDelight {
    public static final Logger LOGGER = LogUtils.getLogger();

    public CuttingDelight(IEventBus modEventBus, ModContainer modContainer) {
        ConfigManager.setConfig(new Config());
        ProviderManager.setClientCommonPacketListenerProvider(new ClientCommonPacketListenerProvider());
        ProviderManager.setPlayerProvider(new PlayerProvider());
        ProviderManager.setInventoryProvider(new InventoryProvider());
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}
