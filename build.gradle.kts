plugins {
    // https://plugins.gradle.org/plugin/com.dorongold.task-tree
    id("com.dorongold.task-tree") version ("4.0.1")
    // https://projects.neoforged.net/neoforged/moddevgradle
    id("net.neoforged.moddev") version ("2.0.121") apply (false)
    // https://plugins.gradle.org/plugin/me.modmuss50.mod-publish-plugin
    id("me.modmuss50.mod-publish-plugin") version ("1.1.0") apply (false)
}

repositories {
    mavenCentral()
}

// gradle.properties
val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val neoVersionRange: String by extra
val loaderVersionRange: String by extra

val modId: String by extra
val modName: String by extra
val modLicense: String by extra
val modVersion: String by extra
val modGroupId: String by extra
val modAuthors: String by extra
val modCredits: String by extra
val modDescription: String by extra
val modJavaVersion: String by extra

val farmersDelightVersionRange: String by extra

subprojects {
    version = "${minecraftVersion}-${modVersion}"
    group = modGroupId

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(JavaLanguageVersion.of(modJavaVersion).asInt())
        options.isDeprecation = true
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to modName,
                    "Specification-Vendor" to modAuthors,
                    "Specification-Version" to modVersion,
                    "Implementation-Title" to name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to modAuthors
                )
            )
        }
    }

    tasks.withType<ProcessResources> {
        var replaceProperties = mapOf(
            "minecraft_version" to minecraftVersion, "minecraft_version_range" to minecraftVersionRange,
            "neo_version_range" to neoVersionRange, "loader_version_range" to loaderVersionRange,
            "mod_id" to modId, "mod_name" to modName, "mod_license" to modLicense, "mod_version" to modVersion,
            "mod_authors" to modAuthors, "mod_credits" to modCredits, "mod_description" to modDescription,
            "farmers_delight_version_range" to farmersDelightVersionRange
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