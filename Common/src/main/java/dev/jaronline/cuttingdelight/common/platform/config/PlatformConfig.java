package dev.jaronline.cuttingdelight.common.platform.config;

public interface PlatformConfig {
	@Deprecated(
		forRemoval = true,
		since = "1.1.0"
	)
	boolean shouldProcessStack();
}
