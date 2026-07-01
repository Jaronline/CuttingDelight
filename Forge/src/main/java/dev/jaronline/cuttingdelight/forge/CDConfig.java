package dev.jaronline.cuttingdelight.forge;

import dev.jaronline.cuttingdelight.common.config.IConfig;
import dev.jaronline.cuttingdelight.core.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

@Config
public class CDConfig implements IConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	@Deprecated(
			forRemoval = true,
			since = "1.1.0"
	)
	private static final ForgeConfigSpec.BooleanValue PROCESS_STACK = BUILDER
			.comment("Whether to allow processing the entire stack on the cutting board when using a tool.",
					"If false, only one item will be processed at a time.")
			.translation("cuttingdelight.configuration.process_stack")
			.define("processStack", true);

	static final ForgeConfigSpec SPEC = BUILDER.build();

	@Override
	@Deprecated(
			forRemoval = true,
			since = "1.1.0"
	)
	public boolean shouldProcessStack() {
		return PROCESS_STACK.get();
	}
}
