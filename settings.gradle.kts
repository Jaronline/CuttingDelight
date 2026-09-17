pluginManagement {
    repositories {
        fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) =
            exclusiveContent {
                forRepository { maven(url) }
                filter(filter)
            }
        maven("https://maven.minecraftforge.net") {
            content {
                includeGroupByRegex("net\\.minecraftforge.*")
            }
        }
        exclusiveMaven("https://maven.parchmentmc.org") {
            includeGroupByRegex("org\\.parchmentmc.*")
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            content {
                includeGroupByRegex("org\\.spongepowered.*")
                includeGroupByRegex("net\\.minecraftforge.*")
            }
        }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

runCatching {
	logger.info("Configuring git hooks")

	providers.exec {
		commandLine("git", "config", "extensions.worktreeConfig", "true")
	}.result.get()

	providers.exec {
		commandLine("git", "config", "--worktree", "core.bare", "false")
	}.result.get()

	providers.exec {
		commandLine("git", "config", "--worktree", "core.hooksPath", ".githooks")
	}.result.get()

	logger.info("Git hooks configured")
}.onFailure {
	logger.error("Could not auto-configure Git hooks: ${it.message}")
}

rootProject.name = "cuttingdelight-1.20.1"

include(
    "Core",
    "Changelog",
    "Common",
    "Forge"
)
