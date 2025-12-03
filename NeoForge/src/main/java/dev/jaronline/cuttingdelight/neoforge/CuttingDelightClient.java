package dev.jaronline.cuttingdelight.neoforge;

import dev.jaronline.cuttingdelight.common.ModIds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModIds.CUTTING_DELIGHT_ID, dist = Dist.CLIENT)
public class CuttingDelightClient {
    public CuttingDelightClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
