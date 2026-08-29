plugins {
    // https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless
    id("com.diffplug.spotless") version ("8.10.1")
    // https://plugins.gradle.org/plugin/com.dorongold.task-tree
    id("com.dorongold.task-tree") version ("4.0.2")
    // https://projects.neoforged.net/neoforged/moddevgradle
    id("net.neoforged.moddev") version ("2.0.144") apply (false)
    // https://plugins.gradle.org/plugin/me.modmuss50.mod-publish-plugin
    id("me.modmuss50.mod-publish-plugin") version ("2.2.0") apply (false)
}

repositories {
    mavenCentral()
}

// gradle.properties
val minecraftVersion = providers.gradleProperty("minecraftVersion")
val minecraftVersionRange = providers.gradleProperty("minecraftVersionRange")
val neoVersionRange = providers.gradleProperty("neoVersionRange")
val loaderVersionRange = providers.gradleProperty("loaderVersionRange")

val modId = providers.gradleProperty("modId")
val modName = providers.gradleProperty("modName")
val modLicense = providers.gradleProperty("modLicense")
val modVersion = providers.gradleProperty("modVersion")
val modGroupId = providers.gradleProperty("modGroupId")
val modAuthors = providers.gradleProperty("modAuthors")
val modCredits = providers.gradleProperty("modCredits")
val modDescription = providers.gradleProperty("modDescription")
val modJavaVersion = providers.gradleProperty("modJavaVersion")

val farmersDelightVersionRange = providers.gradleProperty("farmersDelightVersionRange")

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

subprojects {
    version = minecraftVersion.zip(modVersion) { minecraftVersion, modVersion -> "$minecraftVersion-$modVersion" }
        .get()
    group = modGroupId.get()

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(JavaLanguageVersion.of(modJavaVersion.get()).asInt())
        options.isDeprecation = true
        options.compilerArgs.add("-Xlint:unchecked")
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to modName.get(),
                    "Specification-Vendor" to modAuthors.get(),
                    "Specification-Version" to modVersion.get(),
                    "Implementation-Title" to name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to modAuthors.get()
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
            "minecraft_version" to minecraftVersion.get(), "minecraft_version_range" to minecraftVersionRange.get(),
            "neo_version_range" to neoVersionRange.get(), "loader_version_range" to loaderVersionRange.get(),
            "mod_id" to modId.get(), "mod_name" to modName.get(), "mod_license" to modLicense.get(), "mod_version" to modVersion.get(),
            "mod_authors" to modAuthors.get(), "mod_credits" to modCredits.get(), "mod_description" to modDescription.get(),
            "farmers_delight_version_range" to farmersDelightVersionRange.get()
        )
        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/neoforge.mods.toml")) {
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