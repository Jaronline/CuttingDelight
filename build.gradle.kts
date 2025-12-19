plugins {
    // https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless
	id("com.diffplug.spotless") version ("8.1.0")
    // https://plugins.gradle.org/plugin/com.dorongold.task-tree
    id("com.dorongold.task-tree") version ("4.0.1")
    // https://repo.spongepowered.org/service/rest/repository/browse/maven-public/org/spongepowered/vanillagradle/
    id("org.spongepowered.gradle.vanilla") version("0.2.2-SNAPSHOT") apply(false)
    // https://repo.spongepowered.org/service/rest/repository/browse/maven-public/org/spongepowered/mixingradle/
    id("org.spongepowered.mixin") version("0.7.38") apply(false)
    // https://files.minecraftforge.net/net/minecraftforge/gradle/ForgeGradle/index.html
    id("net.minecraftforge.gradle") version("6.0.46") apply(false)
    // https://mvnrepository.com/artifact/org.parchmentmc.librarian.forgegradle/org.parchmentmc.librarian.forgegradle.gradle.plugin
    id("org.parchmentmc.librarian.forgegradle") version("1.2.0") apply(false)
    // https://plugins.gradle.org/plugin/me.modmuss50.mod-publish-plugin
    id("me.modmuss50.mod-publish-plugin") version ("1.1.0") apply (false)
}

// gradle.properties
val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val forgeVersionRange: String by extra
val forgeLoaderVersionRange: String by extra
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
    version = "${minecraftVersion}-${modVersion}"
    group = modGroupId

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(JavaLanguageVersion.of(modJavaVersion).asInt())
        options.isDeprecation = true
        options.compilerArgs.add("-Xlint:unchecked")
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
            "forge_version_range" to forgeVersionRange, "forge_loader_version_range" to forgeLoaderVersionRange,
            "mod_id" to modId, "mod_name" to modName, "mod_license" to modLicense, "mod_version" to modVersion,
            "mod_authors" to modAuthors, "mod_credits" to modCredits, "mod_description" to modDescription,
            "farmers_delight_version_range" to farmersDelightVersionRange
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