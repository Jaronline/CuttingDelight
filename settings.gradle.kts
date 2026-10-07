@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
		fun exclusiveMaven(url: String, vararg groupPrefixes: String) =
			exclusiveContent {
				forRepository { maven(url) }
				filter {
					groupPrefixes.forEach(::includeGroupAndSubgroups)
				}
			}
		exclusiveMaven("https://maven.parchmentmc.org", "org.parchmentmc")
		exclusiveMaven("https://maven.neoforged.net/releases", "net.neoforged", "codechicken", "net.covers1624")
		maven("https://repo.spongepowered.org/repository/maven-public/") {
			content {
				includeGroupAndSubgroups("org.spongepowered")
				includeGroupAndSubgroups("net.minecraftforge")
			}
		}
        gradlePluginPortal()
    }
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "org.spongepowered.mixin") {
				useModule("org.spongepowered:mixingradle:${requested.version}")
			}
		}
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
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

rootProject.name = "cuttingdelight-1.21.1"

include(
    "Changelog",
    "Common",
    "NeoForge",
	"Debug"
)
