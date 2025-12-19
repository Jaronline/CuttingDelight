package dev.jaronline.cuttingdelight.common.config;

public class ConfigManager {
	private static IConfig instance = new DefaultConfig();

	public static IConfig getConfig() {
		return instance;
	}

	public static void setConfig(IConfig config) {
		instance = config;
	}

	private ConfigManager() {}
}
