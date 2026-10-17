package dev.jaronline.cuttingdelight.fabric;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.platform.config.PlatformConfig;
import net.mezzdev.config.api.Configs;
import net.mezzdev.config.api.IConfigRegistration;
import net.mezzdev.config.api.schema.IConfigSchema;
import net.mezzdev.config.api.schema.builder.IConfigCategoryBuilder;
import net.mezzdev.config.api.schema.builder.IConfigSchemaBuilder;
import net.mezzdev.config.api.value.IConfigValue;

public class FabricConfig implements PlatformConfig {
	@Deprecated(
		forRemoval = true,
		since = "1.1.1"
	)
	public static final IConfigValue<Boolean> PROCESS_STACK;
	public static final IConfigSchema SERVER;

	static {
		IConfigRegistration registration = Configs.forMod(ModIds.CUTTING_DELIGHT_ID);
		IConfigSchemaBuilder schema = registration.createServerSchemaBuilder(
				"server.ini",
				ModIds.CUTTING_DELIGHT_ID + ".configuration"
		);
		IConfigCategoryBuilder general = schema.addCategory("general");

		PROCESS_STACK = general.addBoolean("process_stack", true).build();
		SERVER = schema.build();
	}

	@SuppressWarnings("UnusedReturnValue")
	static IConfigSchema bootstrap() {
		return SERVER;
	}

	@Override
	@Deprecated(
		forRemoval = true,
		since = "1.1.1"
	)
	public boolean shouldProcessStack() {
		return PROCESS_STACK.get();
	}
}
