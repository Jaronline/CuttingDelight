import me.modmuss50.mpp.ReleaseType
import net.minecraftforge.gradle.common.tasks.DownloadMavenArtifact
import net.minecraftforge.gradle.common.tasks.JarExec
import net.minecraftforge.gradle.common.util.RunConfig

plugins {
    id("cuttingdelight-convention")
    alias(libs.plugins.modpublish)
    alias(libs.plugins.forgegradle)
    alias(libs.plugins.librarian.forgegradle)
    alias(libs.plugins.mixin)
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

val cuttingDelight = extensions.getByType<CuttingDelightBuildPlugin>()

base {
    archivesName = "${cuttingDelight.modId.get()}-forge"
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
    "minecraft"(libs.forge)

    if (System.getProperty("idea.sync.active") != "true") {
         annotationProcessor(variantOf(libs.mixin) {
            classifier("processor")
        })
    }

    dependencyProjects.forEach {
        implementation(it)
    }
    annotationProcessor(project(":Processor"))

    runtimeOnly(fg.deobf(libs.jei.forge))
    implementation(fg.deobf(libs.farmersdelight))
    // Need runtimeOnly as well to ensure the mod is present in run configurations on IDEs
    runtimeOnly(fg.deobf(libs.farmersdelight))

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    changelogHtml(project(":Changelog"))
    changelogMarkdown(project(":Changelog"))
}

mixin {
    add(sourceSets.main.get(), "${cuttingDelight.modId.get()}.refmap.json")
    config("${cuttingDelight.modId.get()}-common.mixins.json")
}

minecraft {
    mappings("parchment", cuttingDelight.parchmentVersionForge)

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
                    cuttingDelight.modId.get(),
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
                create(cuttingDelight.modId.get()) {
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
        displayName.set("${cuttingDelight.modVersion.version} for Forge ${cuttingDelight.mcVersion.version}")
        version.set(project.version.toString())

        curseforge {
            projectId = cuttingDelight.curseProjectId
            accessToken = cuttingDelight.curseforgeApikey
            changelog.set(changelogHtml.singleFileContents())
            changelogType = "html"
            minecraftVersionRange {
                start = cuttingDelight.mcVersion.version
                end = cuttingDelight.mcVersion.version
            }
            javaVersions.add(JavaVersion.toVersion(cuttingDelight.javaVersion.version))
            requires("farmers-delight")
            client = true
            server = true
        }

        modrinth {
            projectId = cuttingDelight.modrinthId
            accessToken = cuttingDelight.modrinthToken
            changelog.set(changelogMarkdown.singleFileContents())
            minecraftVersionRange {
                start = cuttingDelight.mcVersion.version
                end = cuttingDelight.mcVersion.version
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