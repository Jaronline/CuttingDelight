package dev.jaronline.cuttingdelight.common.config;

public class DefaultConfig implements IConfig {
	@Override
	@Deprecated(
			forRemoval = true,
			since = "1.1.0"
	)
	public boolean shouldProcessStack() {
		return true;
	}
}
