import me.modmuss50.mpp.ReleaseType
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("me.modmuss50.mod-publish-plugin")
    id("net.neoforged.moddev")
}

// gradle.properties
val parchmentMinecraftVersion: String by extra
val parchmentMappingsVersion: String by extra
val minecraftVersion: String by extra
val minecraftVersionRangeStart: String by extra
val neoVersion: String by extra

val modId: String by extra
val modVersion: String by extra
val modJavaVersion: String by extra

val jeiVersion: String by extra
val farmersDelightVersion: String by extra
val jUnitVersion: String by extra

val curseProjectId: String by extra
val modrinthId: String by extra

// set by ORG_GRADLE_PROJECT_modrinthToken
val modrinthToken: String? by project
// set by ORG_GRADLE_PROJECT_curseforgeApikey
val curseforgeApikey: String? by project

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

tasks.withType<JavaCompile>().configureEach {
    dependencyProjects.forEach {
        source(it.sourceSets.main.get().allSource)
    }
}

tasks.withType<ProcessResources> {
    dependencyProjects.forEach {
        from(it.sourceSets.main.get().resources)
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
    dependencyProjects.forEach {
        implementation(it)
    }

    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")

    implementation("maven.modrinth:farmers-delight:${minecraftVersion}-${farmersDelightVersion}")
    runtimeOnly("maven.modrinth:hearth-and-harvest:6rnNHSe5")

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

neoForge {
    version = neoVersion
//    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    addModdingDependenciesTo(sourceSets.test.get())

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            for (dependencyProject in dependencyProjects) {
                sourceSet(dependencyProject.sourceSets.main.get())
            }
        }
    }

    runs {
        val client = create("client")
        client.client()
        client.gameDirectory = file("run/client")
        client.systemProperty("neoforge.enabledGameTestNamespaces", modId)

        val server = create("server")
        server.server()
        server.gameDirectory = file("run/server")
        server.programArgument("--nogui")
        server.systemProperty("neoforge.enabledGameTestNamespaces", modId)

        val gameTestServer = create("gameTestServer")
        gameTestServer.type = "gameTestServer"
        gameTestServer.systemProperty("neoforge.enabledGameTestNamespaces", modId)

        val data = create("data")
        data.data()
        data.gameDirectory = file("run-data")
        data.programArguments.addAll(
            "--mod",
            modId,
            "--all",
            "--output",
            file("src/generated/resources/").absolutePath,
            "--existing",
            file("src/main/resources/").absolutePath
        )
        dependencyProjects.stream().flatMap { it.sourceSets.main.get().resources.srcDirs.stream() }.forEach {
            data.programArguments.addAll("--existing", it.absolutePath)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = Level.DEBUG
        }
    }
}

tasks.jar {
    from(sourceSets.main.get().output)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().output)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().allJava)
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("sources")
}

publishMods {
    val publishType = System.getenv("PUBLISH_TYPE")

    if (publishType != null) {
        file.set(tasks.jar.get().archiveFile)
        type.set(ReleaseType.of(publishType.uppercase()))
        modLoaders.add("neoforge")
        displayName.set("$modVersion for NeoForge $minecraftVersion")
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
        }

        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken
            changelog.set(changelogMarkdown.singleFileContents())
            minecraftVersionRange {
                start = minecraftVersionRangeStart
                end = minecraftVersion
            }
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
    // Should be removed once tests are added
    failOnNoDiscoveredTests = false
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
