package dev.jaronline.cuttingdelight.common.config;

public interface IConfig {
	@Deprecated(
			forRemoval = true,
			since = "1.1.0"
	)
	boolean shouldProcessStack();
}
