package dev.jaronline.cuttingdelight.forge.event;

import dev.jaronline.cuttingdelight.common.Bootstrapper;
import dev.jaronline.cuttingdelight.common.ModCreativeTabs;
import dev.jaronline.cuttingdelight.common.ModIds;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = ModIds.CUTTING_DELIGHT_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {
	@SubscribeEvent
	public static void registerEntries(RegisterEvent event) {
		Bootstrapper.boostrapByRegistryKey(event.getRegistryKey());
	}

	@SubscribeEvent
	public static void addCreative(BuildCreativeModeTabContentsEvent event) {
		ModCreativeTabs.registerItems(event.getTabKey(), event::accept);
	}
}
