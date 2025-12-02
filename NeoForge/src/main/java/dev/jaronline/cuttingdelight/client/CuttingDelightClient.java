package dev.jaronline.cuttingdelight.client;

import dev.jaronline.cuttingdelight.CuttingDelight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CuttingDelight.MOD_ID, dist = Dist.CLIENT)
public class CuttingDelightClient {
    public CuttingDelightClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
