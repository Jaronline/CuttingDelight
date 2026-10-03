plugins {
    id("cuttingdelight-build")
    alias(libs.plugins.spotless)
    alias(libs.plugins.tasktree)
    alias(libs.plugins.modpublish) apply false
    alias(libs.plugins.moddevgradle.legacyforge) apply false
}

repositories {
    mavenCentral()
}

spotless {
    java {
        val customTargets = providers.environmentVariable("SPOTLESS_TARGETS")
            .map { it ->
                it.split('\n')
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

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.14.3"
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
        var replaceProperties = mapOf(
            "minecraft_version" to cuttingdelight.mcVersion.version, "minecraft_version_range" to cuttingdelight.mcVersion.range,
            "forge_version_range" to cuttingdelight.forgeVersion.range, "forge_loader_version_range" to cuttingdelight.fmlVersion.range,
            "mod_id" to cuttingdelight.modId.get(), "mod_name" to cuttingdelight.modName.get(), "mod_license" to cuttingdelight.modLicense.get(),
            "mod_version" to cuttingdelight.modVersion.version, "mod_authors" to cuttingdelight.modAuthors.get(),
            "mod_credits" to cuttingdelight.modCredits.get(), "mod_description" to cuttingdelight.modDescription.get(),
            "farmers_delight_version_range" to cuttingdelight.farmersDelightVersion.range
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
