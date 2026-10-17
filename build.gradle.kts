plugins {
    id("cuttingdelight-build")
    alias(libs.plugins.spotless)
    alias(libs.plugins.tasktree)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.loom.companion)
    alias(libs.plugins.moddevgradle) apply false
    alias(libs.plugins.modpublish) apply false
}

repositories {
    mavenCentral()
}

spotless {
    java {
        val customTargets = providers.environmentVariable("SPOTLESS_TARGETS")
            .map { it ->
                it.split("\n")
                    .filter { it.endsWith(".java") }
            }
            .orNull

        if (customTargets == null)
            target("*/src/*/java/dev/jaronline/cuttingdelight/**/*.java")
        else
            target(customTargets)

        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
        leadingSpacesToTabs(4)
        replaceRegex("class-level javadoc indentation fix", "^\\*", " *")
        replaceRegex("method-level javadoc indentation fix", "\t\\*", "\t *")
    }
}

subprojects {
    val cuttingdelight = rootProject.cuttingdelight

    version = "${cuttingdelight.mcVersion.version}-${cuttingdelight.modVersion.version}"
    group = cuttingdelight.modGroupId.get()

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = JavaLanguageVersion.of(cuttingdelight.javaVersion.version).asInt()
        options.isDeprecation = true
        options.compilerArgs.add("-Xlint:unchecked")
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to cuttingdelight.modName.get(),
                    "Specification-Vendor" to cuttingdelight.modAuthors.get(),
                    "Specification-Version" to cuttingdelight.modVersion.version,
                    "Implementation-Title" to name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to cuttingdelight.modAuthors.get()
                )
            )
        }

        // usage: -PjarName=<value>. should only be used in CI.
        val customName = providers.gradleProperty("jarName")

        if (customName.isPresent)
            archiveFileName.set(
                customName.zip(archiveClassifier) { name, classifier ->
                    if (classifier.isEmpty()) name else "$name-$classifier"
                }.zip(archiveExtension) { name, extension ->
                    "$name.$extension"
                }
            )
    }

    tasks.withType<ProcessResources> {
        val replaceProperties = mapOf(
            "minecraft_version" to cuttingdelight.mcVersion.version, "minecraft_version_range" to cuttingdelight.mcVersion.range,
            "mod_id" to cuttingdelight.modId.get(), "mod_name" to cuttingdelight.modName.get(), "mod_license" to cuttingdelight.modLicense.get(),
            "mod_version" to cuttingdelight.modVersion.version, "mod_authors" to cuttingdelight.modAuthors.get(),
            "mod_credits" to cuttingdelight.modCredits.get(), "mod_description" to cuttingdelight.modDescription.get(),
            "mod_java_version" to cuttingdelight.javaVersion.version,
        )
        val forgeReplaceProperties = mapOf(
            "neo_version_range" to cuttingdelight.neoforgeVersion.range,
            "neo_loader_version_range" to cuttingdelight.neoforgeLoaderVersion.range,
            "farmers_delight_version_range" to cuttingdelight.farmersDelightVersion.range,
        )
        val fabricReplaceProperties = mapOf(
            "fabric_api_version_range" to cuttingdelight.fabricApiVersionRange.version,
            "fabric_loader_version_range" to cuttingdelight.fabricVersionRange.version,
            "mezz_config_version_range" to cuttingdelight.mezzConfigVersionRange.version,
            "mezz_config_gui_suggests_range" to ">=${cuttingdelight.mezzConfigGUIVersionMin.version}",
            "mezz_config_gui_breaks_range" to "<${cuttingdelight.mezzConfigGUIVersionMin.version}",
            "farmers_delight_version_range" to cuttingdelight.farmersDelightFabricVersion.range,
        )

        inputs.properties(replaceProperties + forgeReplaceProperties + fabricReplaceProperties)
        filesMatching("pack.mcmeta") {
            expand(replaceProperties)
        }
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(replaceProperties + forgeReplaceProperties)
        }
        filesMatching("fabric.mod.json") {
            expand(replaceProperties + fabricReplaceProperties)
        }
    }

    // Activate reproducible builds
    // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}