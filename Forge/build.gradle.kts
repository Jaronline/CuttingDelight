import me.modmuss50.mpp.ReleaseType
import net.minecraftforge.gradle.common.tasks.DownloadMavenArtifact
import net.minecraftforge.gradle.common.tasks.JarExec
import net.minecraftforge.gradle.common.util.RunConfig
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("idea")
    id("maven-publish")
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
val parchmentMinecraftVersion: String by extra
val parchmentVersionForge: String by extra
val minecraftVersion: String by extra
val minecraftVersionRangeStart: String by extra
val spongeMixinVersion: String by extra
val forgeVersion: String by extra
val modId: String by extra
val modVersion: String by extra
val modJavaVersion: String by extra
val jeiVersion: String by extra
val farmersDelightVersion: String by extra
val hearthAndHarvestVersion: String by extra
val jUnitVersion: String by extra
val curseProjectId: String by extra
val modrinthId: String by extra

// set by ORG_GRADLE_PROJECT_modrinthToken
val modrinthToken: String? by project
// set by ORG_GRADLE_PROJECT_curseforgeApikey
val curseforgeApikey: String? by project

base {
    archivesName = "${modId}-forge"
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

java {
    toolchain.languageVersion = JavaLanguageVersion.of(modJavaVersion)
    withSourcesJar()
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
    "minecraft"(
        group = "net.minecraftforge",
        name = "forge",
        version = "${minecraftVersion}-${forgeVersion}"
    )

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor(
            group = "org.spongepowered",
            name = "mixin",
            version = spongeMixinVersion,
            classifier = "processor"
        )
    }

    dependencyProjects.forEach {
        implementation(it)
    }
    annotationProcessor(project(":Processor"))

    runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge:${jeiVersion}"))
    val farmersDelightDependency = "maven.modrinth:farmers-delight:${minecraftVersion}-${farmersDelightVersion}"
    implementation(fg.deobf(farmersDelightDependency))
    // Need runtimeOnly as well to ensure the mod is present in run configurations on IDEs
    runtimeOnly(fg.deobf(farmersDelightDependency))

    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter",
        version = jUnitVersion
    )
    testRuntimeOnly(
        group = "org.junit.platform",
        name = "junit-platform-launcher"
    )

    changelogHtml(project(":Changelog"))
    changelogMarkdown(project(":Changelog"))
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId-common.mixins.json")
}

minecraft {
    mappings("parchment", parchmentVersionForge)

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
                    modId,
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
                create(modId) {
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
    val publishType = System.getenv("PUBLISH_TYPE")

    if (publishType != null) {
        file.set(tasks.jar.get().archiveFile)
        type.set(ReleaseType.of(publishType.uppercase()))
        modLoaders.add("forge")
        displayName.set("$modVersion for Forge $minecraftVersion")
        version.set(project.version.toString())

        curseforge {
            projectId = curseProjectId
            accessToken.set(curseforgeApikey ?: "0")
            changelog.set(changelogHtml.singleFileContents())
            changelogType = "html"
            minecraftVersionRange {
                start = minecraftVersionRangeStart
                end = minecraftVersion
            }
            javaVersions.add(JavaVersion.toVersion(modJavaVersion))
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
    useJUnitPlatform()
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
    outputs.upToDateWhen { false }
    testLogging {
        events = setOf(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}

artifacts {
    archives(tasks.jar.get())
    archives(sourcesJarTask.get())
}

publishing {
    publications {
        register<MavenPublication>("neoforgeJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(sourcesJarTask.get())
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

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        for (fileName in listOf("build", "run", "run-data", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}

tasks.withType<DownloadMavenArtifact> {
    notCompatibleWithConfigurationCache("uses Task.project at execution time")
}

tasks.withType<JarExec> {
    notCompatibleWithConfigurationCache("uses external process at execution time")
}