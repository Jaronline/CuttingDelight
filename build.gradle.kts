plugins {
    id("cuttingdelight-build")
    alias(libs.plugins.spotless)
    alias(libs.plugins.tasktree)
    alias(libs.plugins.vanillagradle) apply false
    alias(libs.plugins.mixin) apply false
    alias(libs.plugins.forgegradle) apply false
    alias(libs.plugins.librarian.forgegradle) apply false
    alias(libs.plugins.modpublish) apply false
}

val cuttingDelight = extensions.getByType<CuttingDelightBuildPlugin>()

repositories {
    mavenCentral()
}

spotless {
    java {
        target("*/src/*/java/dev/jaronline/cuttingdelight/**/*.java")

        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
        leadingSpacesToTabs(4)
        replaceRegex("class-level javadoc indentation fix", "^\\*", " *")
        replaceRegex("method-level javadoc indentation fix", "\t\\*", "\t *")
    }
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.14.3"
}

subprojects {
    version = "${cuttingDelight.mcVersion.version}-${cuttingDelight.modVersion.version}"
    group = cuttingDelight.modGroupId.get()

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = JavaLanguageVersion.of(cuttingDelight.javaVersion.version).asInt()
        options.isDeprecation = true
        options.compilerArgs.add("-Xlint:unchecked")
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to cuttingDelight.modName.get(),
                    "Specification-Vendor" to cuttingDelight.modAuthors.get(),
                    "Specification-Version" to cuttingDelight.modVersion.version,
                    "Implementation-Title" to name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to cuttingDelight.modAuthors.get()
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
        var replaceProperties = mapOf(
            "minecraft_version" to cuttingDelight.mcVersion.version, "minecraft_version_range" to cuttingDelight.mcVersion.range,
            "forge_version_range" to cuttingDelight.forgeVersion.range, "forge_loader_version_range" to cuttingDelight.fmlVersion.range,
            "mod_id" to cuttingDelight.modId.get(), "mod_name" to cuttingDelight.modName.get(), "mod_license" to cuttingDelight.modLicense.get(),
            "mod_version" to cuttingDelight.modVersion.version, "mod_authors" to cuttingDelight.modAuthors.get(),
            "mod_credits" to cuttingDelight.modCredits.get(), "mod_description" to cuttingDelight.modDescription.get(),
            "farmers_delight_version_range" to cuttingDelight.farmersDelightVersion.range
        )
        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
            expand(replaceProperties)
        }
    }

    // Activate reproducible builds
    // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
