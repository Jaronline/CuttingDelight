import me.modmuss50.mpp.ReleaseType
import org.slf4j.event.Level

plugins {
    id("cuttingdelight-convention")
    alias(libs.plugins.modpublish)
    alias(libs.plugins.moddevgradle)
}

val cuttingDelight = extensions.getByType<CuttingDelightBuildPlugin>()

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
    archivesName = "${cuttingDelight.modId.get()}-neoforge"
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


    runtimeOnly(libs.jei.neoforge)
    implementation(libs.farmersdelight)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    @Suppress("AvoidDuplicateDependencies")
    changelogHtml(project(":Changelog"))
    @Suppress("AvoidDuplicateDependencies")
    changelogMarkdown(project(":Changelog"))
}

neoForge {
    version = cuttingDelight.neoforgeVersion.version
//    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    addModdingDependenciesTo(sourceSets.test.get())

    parchment {
        mappingsVersion = cuttingDelight.parchmentMappingsVersion.version
        minecraftVersion = cuttingDelight.parchmentMCVersion.version
    }

    mods {
        create(cuttingDelight.modId.get()) {
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
        client.systemProperty("neoforge.enabledGameTestNamespaces", cuttingDelight.modId.get())

        val server = create("server")
        server.server()
        server.gameDirectory = file("run/server")
        server.programArgument("--nogui")
        server.systemProperty("neoforge.enabledGameTestNamespaces", cuttingDelight.modId.get())

        val gameTestServer = create("gameTestServer")
        gameTestServer.type = "gameTestServer"
        gameTestServer.systemProperty("neoforge.enabledGameTestNamespaces", cuttingDelight.modId.get())

        val data = create("data")
        data.data()
        data.gameDirectory = file("run-data")
        data.programArguments.addAll(
            "--mod",
            cuttingDelight.modId.get(),
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
        displayName.set("${cuttingDelight.modVersion.version} for NeoForge ${cuttingDelight.mcVersion.version}")
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
