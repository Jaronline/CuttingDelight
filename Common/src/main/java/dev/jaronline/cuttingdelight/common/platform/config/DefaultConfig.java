package dev.jaronline.cuttingdelight.common.platform.config;

public class DefaultConfig implements PlatformConfig {
	@Override
	@Deprecated(
			forRemoval = true,
			since = "1.1.0"
	)
	public boolean shouldProcessStack() {
		return true;
	}
}
