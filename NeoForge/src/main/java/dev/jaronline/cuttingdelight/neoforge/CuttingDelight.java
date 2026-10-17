package dev.jaronline.cuttingdelight.neoforge;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModIds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ModIds.CUTTING_DELIGHT_ID)
public class CuttingDelight {
	public static final Logger LOGGER = LogUtils.getLogger();

	public CuttingDelight(IEventBus modEventBus, ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.SERVER, NeoForgeConfig.SPEC);
	}
}
