package dev.jaronline.cuttingdelight;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static Config instance;

    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private final ModConfigSpec spec = builder.build();

    private Config() {}

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    public static void register(ModContainer container, ModConfig.Type type) {
        container.registerConfig(type, getInstance().spec);
    }
}
