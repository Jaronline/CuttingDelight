package dev.jaronline.cuttingdelight.neoforge;

import dev.jaronline.cuttingdelight.common.config.IConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config implements IConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	@Deprecated(
		forRemoval = true,
		since = "1.1.0"
	)
	public static final ModConfigSpec.BooleanValue PROCESS_STACK = BUILDER
			.comment("Whether to allow processing the entire stack on the cutting board when using a tool.",
					"If false, only one item will be processed at a time.")
			.translation("cuttingdelight.configuration.process_stack")
			.define("processStack", true);

	static final ModConfigSpec SPEC = BUILDER.build();

	@Override
	@Deprecated(
		forRemoval = true,
		since = "1.1.0"
	)
	public boolean shouldProcessStack() {
		return PROCESS_STACK.getAsBoolean();
	}
}
