import me.modmuss50.mpp.ReleaseType
import net.minecraftforge.gradle.common.tasks.DownloadMavenArtifact
import net.minecraftforge.gradle.common.tasks.JarExec
import net.minecraftforge.gradle.common.util.RunConfig

plugins {
    id("cuttingdelight-convention")
    id("me.modmuss50.mod-publish-plugin")
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
    id("org.spongepowered.mixin")
}

repositories {
    mavenCentral()
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
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

// gradle.properties
val parchmentVersionForge = providers.gradleProperty("parchmentVersionForge")
val minecraftVersion = providers.gradleProperty("minecraftVersion")
val minecraftVersionRangeStart = providers.gradleProperty("minecraftVersionRangeStart")
    .orElse(minecraftVersion)
val spongeMixinVersion = providers.gradleProperty("spongeMixinVersion")
val forgeVersion = providers.gradleProperty("forgeVersion")
val modId = providers.gradleProperty("modId")
val modVersion = providers.gradleProperty("modVersion")
val javaVersion = providers.gradleProperty("javaVersion")
val jeiVersion = providers.gradleProperty("jeiVersion")
val farmersDelightVersion = providers.gradleProperty("farmersDelightVersion")
val jUnitVersion = providers.gradleProperty("jUnitVersion")
val curseProjectId = providers.gradleProperty("curseProjectId")
val modrinthId = providers.gradleProperty("modrinthId")

// set by ORG_GRADLE_PROJECT_modrinthToken
val modrinthToken = providers.gradleProperty("modrinthToken")
// set by ORG_GRADLE_PROJECT_curseforgeApikey
val curseforgeApikey = providers.gradleProperty("curseforgeApikey").orElse("0")

base {
    archivesName = "${modId.get()}-forge"
}

sourceSets {
    named("main") {
        resources {
            setSrcDirs(listOf("src/main/resources", "src/generated/resources"))
        }
    }
    named("test") {
        resources {
            //The test module has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

val dependencyProjects: List<Project> = listOf(
    project(":Core"),
    project(":Common")
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.path)
}

tasks.withType<ProcessResources> {
    dependencyProjects.forEach {
        if (it.sourceSets.findByName("dev") != null) {
            from(it.sourceSets.getByName("dev").resources)
        }
    }
}

val changelogHtml = configurations.create("changelogHtml")
changelogHtml.isCanBeConsumed = false
changelogHtml.isCanBeResolved = true
changelogHtml.attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogHtml"))
}

val changelogMarkdown = configurations.create("changelogMarkdown")
changelogMarkdown.isCanBeConsumed = false
changelogMarkdown.isCanBeResolved = true
changelogMarkdown.attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogMarkdown"))
}

fun Configuration.singleFileContents(): Provider<String> =
    incoming
        .files
        .elements
        .map { elements -> elements.single() }
        .map { it.asFile.readText() }

dependencies {
    "minecraft"("net.minecraftforge:forge:${minecraftVersion.get()}-${forgeVersion.get()}")

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:${spongeMixinVersion.get()}:processor")
    }

    dependencyProjects.forEach {
        implementation(it)
    }
    annotationProcessor(project(":Processor"))

    runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion.get()}-forge:${jeiVersion.get()}"))
    val farmersDelightDependency = "maven.modrinth:farmers-delight:${minecraftVersion.get()}-${farmersDelightVersion.get()}"
    implementation(fg.deobf(farmersDelightDependency))
    // Need runtimeOnly as well to ensure the mod is present in run configurations on IDEs
    runtimeOnly(fg.deobf(farmersDelightDependency))

    testImplementation("org.junit.jupiter:junit-jupiter:${jUnitVersion.get()}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    changelogHtml(project(":Changelog"))
    changelogMarkdown(project(":Changelog"))
}

mixin {
    add(sourceSets.main.get(), "${modId.get()}.refmap.json")
    config("${modId.get()}-common.mixins.json")
}

minecraft {
    mappings("parchment", parchmentVersionForge.get())

    copyIdeResources.set(true)

//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        val client = create("client", Action<RunConfig> {
            taskName("runClientDev")
            workingDirectory(file("run/client/Dev"))
        })

        create("client_1", Action<RunConfig> {
            taskName("runClientPlayer")
            parent(client)
            workingDirectory(file("run/client/Player1"))
            args("--username", "Player")
        })

        create("server", Action<RunConfig> {
            taskName("runServer")
            workingDirectory(file("run/server"))
            args("--nogui")
        })

        create("data", Action<RunConfig> {
            taskName("runData")
            workingDirectory(file("run-data"))
            args.addAll(
                listOf(
                    "--mod",
                    modId.get(),
                    "--all",
                    "--output",
                    file("src/generated/resources/").absolutePath,
                    "--existing",
                    file("src/main/resources/").absolutePath
                )
            )
            dependencyProjects.stream().flatMap { it.sourceSets.main.get().resources.srcDirs.stream() }.forEach {
                args.addAll(listOf("--existing", it.absolutePath))
            }
        })

        configureEach {
            property("forge.logging.console.level", "debug")
            ideaModule("${rootProject.name}.${project.name}.main")
            isSingleInstance = true
            mods {
                create(modId.get()) {
                    source(sourceSets.main.get())
                    for (p in dependencyProjects) {
                        source(p.sourceSets.main.get())
                    }
                }
            }
        }
    }
}

tasks.jar {
    from(sourceSets.main.get().output)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().output)
    }

    exclude("data/cuttingdelight-dev/**", "assets/cuttingdelight-dev/**")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    finalizedBy("reobfJar")
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().allJava)
    }

    exclude("data/cuttingdelight-dev/**", "assets/cuttingdelight-dev/**")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("sources")
}

publishMods {
    val publishType = providers.environmentVariable("PUBLISH_TYPE").orNull

    if (publishType != null) {
        file.set(tasks.jar.get().archiveFile)
        type.set(ReleaseType.of(publishType.uppercase()))
        modLoaders.add("forge")
        displayName.set("${modVersion.get()} for Forge ${minecraftVersion.get()}")
        version.set(project.version.toString())

        curseforge {
            projectId = curseProjectId
            accessToken = curseforgeApikey
            changelog.set(changelogHtml.singleFileContents())
            changelogType = "html"
            minecraftVersionRange {
                start = minecraftVersionRangeStart
                end = minecraftVersion
            }
            javaVersions.add(JavaVersion.toVersion(javaVersion.get()))
            requires("farmers-delight")
            client = true
            server = true
        }

        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken
            changelog.set(changelogMarkdown.singleFileContents())
            minecraftVersionRange {
                start = minecraftVersionRangeStart
                end = minecraftVersion
            }
            requires("farmers-delight")
        }
    }
}

tasks.test {
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}

artifacts {
    archives(tasks.jar)
    archives(sourcesJarTask)
}

publishing {
    publications {
        register<MavenPublication>("neoforgeJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(sourcesJarTask)
        }
    }
}

tasks.withType<DownloadMavenArtifact> {
    notCompatibleWithConfigurationCache("uses Task.project at execution time")
}

tasks.withType<JarExec> {
    notCompatibleWithConfigurationCache("uses external process at execution time")
}