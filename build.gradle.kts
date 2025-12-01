plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.121"
    id("idea")
}

// gradle.properties
val parchmentMinecraftVersion: String by extra
val parchmentMappingsVersion: String by extra
val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val neoVersion: String by extra
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

val jeiVersion: String by extra
val farmersDelightVersion: String by extra
val farmersDelightVersionRange: String by extra

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

version = "${minecraftVersion}-${modVersion}"
group = modGroupId

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        // location of the maven that hosts JEI files since January 2023
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
    }
    maven {
        // location of a maven mirror for JEI files, as a fallback
        name = "ModMaven"
        url = uri("https://modmaven.dev")
    }
}

base {
    archivesName = "${modId}-neoforge"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(modJavaVersion)
    withSourcesJar()
}

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

//    accessTransformers {
//        file("src/main/resources/META-INF/accesstransformer.cfg")
//    }

    runs {
        create("client") {
            client()

            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            data()

            // gameDirectory = project.file("run-data")

            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

configurations {
    val localRuntime by creating {}
    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}

dependencies {
    val localRuntime by configurations

    compileOnly("mezz.jei:jei-${minecraftVersion}-neoforge-api:${jeiVersion}")
    localRuntime("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")

    implementation("maven.modrinth:farmers-delight:${minecraftVersion}-${farmersDelightVersion}")
    localRuntime("maven.modrinth:hearth-and-harvest:6rnNHSe5")
}

tasks.withType<ProcessResources>().configureEach {
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

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Specification-Title" to modName,
            "Specification-Vendor" to modAuthors,
            "Specification-Version" to modVersion,
            "Implementation-Title" to name,
            "Implementation-Version" to archiveVersion,
            "Implementation-Vendor" to modAuthors
        ))
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))
        }
    }
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/jaronline/cuttingdelight")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
