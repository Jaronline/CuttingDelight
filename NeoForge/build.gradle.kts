import me.modmuss50.mpp.ReleaseType
import org.slf4j.event.Level

plugins {
    id("cuttingdelight-convention")
    id("me.modmuss50.mod-publish-plugin")
    id("net.neoforged.moddev")
}

// gradle.properties
val minecraftVersion = providers.gradleProperty("minecraftVersion")
val minecraftVersionRangeStart = providers.gradleProperty("minecraftVersionRangeStart")
    .orElse(minecraftVersion)
val parchmentMinecraftVersion = providers.gradleProperty("parchmentMinecraftVersion")
    .orElse(minecraftVersion)
val parchmentMappingsVersion = providers.gradleProperty("parchmentMappingsVersion")
val neoVersion = providers.gradleProperty("neoVersion")

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
    archivesName = "${modId.get()}-neoforge"
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
    dependencyProjects.forEach {
        implementation(it)
    }

    runtimeOnly("mezz.jei:jei-${minecraftVersion.get()}-neoforge:${jeiVersion.get()}")

    implementation("maven.modrinth:farmers-delight:${minecraftVersion.get()}-${farmersDelightVersion.get()}")

    testImplementation("org.junit.jupiter:junit-jupiter:${jUnitVersion.get()}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    @Suppress("AvoidDuplicateDependencies")
    changelogHtml(project(":Changelog"))
    @Suppress("AvoidDuplicateDependencies")
    changelogMarkdown(project(":Changelog"))
}

neoForge {
    version = neoVersion.get()
//    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    addModdingDependenciesTo(sourceSets.test.get())

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    mods {
        create(modId.get()) {
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
        client.systemProperty("neoforge.enabledGameTestNamespaces", modId.get())

        val server = create("server")
        server.server()
        server.gameDirectory = file("run/server")
        server.programArgument("--nogui")
        server.systemProperty("neoforge.enabledGameTestNamespaces", modId.get())

        val gameTestServer = create("gameTestServer")
        gameTestServer.type = "gameTestServer"
        gameTestServer.systemProperty("neoforge.enabledGameTestNamespaces", modId.get())

        val data = create("data")
        data.data()
        data.gameDirectory = file("run-data")
        data.programArguments.addAll(
            "--mod",
            modId.get(),
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
    val publishType = providers.environmentVariable("PUBLISH_TYPE").orNull

    if (publishType != null) {
        file.set(tasks.jar.get().archiveFile)
        type.set(ReleaseType.of(publishType.uppercase()))
        modLoaders.add("neoforge")
        displayName.set("${modVersion.get()} for NeoForge ${minecraftVersion.get()}")
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

tasks.test {
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}
