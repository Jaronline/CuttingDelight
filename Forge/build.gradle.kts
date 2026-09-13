import me.modmuss50.mpp.ReleaseType
import org.slf4j.event.Level

plugins {
    id("cuttingdelight-convention")
    alias(libs.plugins.modpublish)
    alias(libs.plugins.moddevgradle.legacyforge)
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

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
        compileOnly(it)
        testImplementation(it)
    }

    annotationProcessor(variantOf(libs.mixin) {
        classifier("processor")
    })

    compileOnlyApi(libs.jei.common.api)
    modRuntimeOnly(libs.jei.forge)
    modImplementation(libs.farmersdelight)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    @Suppress("AvoidDuplicateDependencies")
    changelogHtml(project(":Changelog"))
    @Suppress("AvoidDuplicateDependencies")
    changelogMarkdown(project(":Changelog"))
}

mixin {
    add(sourceSets.main.get(), "${cuttingDelight.modId.get()}.refmap.json")
    config("${cuttingDelight.modId.get()}-common.mixins.json")
}

legacyForge {
    validateAccessTransformers = true
//    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    enable {
        forgeVersion = cuttingDelight.forgeMCVersion
        enabledSourceSets = setOf(sourceSets.main.get(), sourceSets.test.get())
        isDisableRecompilation = false
    }

    addModdingDependenciesTo(sourceSets.test.get())

    parchment {
        mappingsVersion = cuttingDelight.parchmentMappingsVersion.version
        minecraftVersion = cuttingDelight.parchmentMCVersion.version
    }

    mods {
        create(cuttingDelight.modId.get()) {
            sourceSet(sourceSets.main.get())
            dependencyProjects.forEach {
                sourceSet(it.sourceSets.main.get())
            }
        }
    }

    runs {
        create("clientDev") {
            client()
            systemProperty("forge.logging.console.level", "debug")
            gameDirectory = file("run/client/Dev")
            logLevel = Level.DEBUG
        }
        create("clientPlayer1") {
            client()
            systemProperty("forge.logging.console.level", "debug")
            gameDirectory = file("run/client/Player1")
            programArguments.addAll("--username", "Player1")
            logLevel = Level.DEBUG
        }
        create("server") {
            server()
            systemProperty("forge.logging.console.level", "debug")
            gameDirectory = file("run/server")
            programArguments.add("nogui")
            logLevel = Level.DEBUG
        }
        create("data") {
            data()
            systemProperty("forge.logging.console.level", "debug")
            gameDirectory = file("run-data")
            programArguments.addAll(
                "-mixin.config=${cuttingDelight.modId.get()}-common.mixins.json",
                "--mod",
                cuttingDelight.modId.get(),
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
            dependencyProjects.stream().flatMap { it.sourceSets.main.get().resources.srcDirs.stream() }.forEach {
                programArguments.addAll("--existing", it.absolutePath)
            }
        }
    }
}

tasks.withType<Jar> {
    manifest.attributes(mapOf(
        "MixinConfigs" to "${cuttingDelight.modId.get()}-common.mixins.json"
    ))
}

tasks.jar {
    from(sourceSets.main.get().output)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().output)
    }

    exclude("data/cuttingdelight-dev/**", "assets/cuttingdelight-dev/**")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
            accessToken = cuttingDelight.curseforgeApiKey
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

val reobfJarTask = tasks.named<AbstractArchiveTask>("reobfJar")

artifacts {
    archives(reobfJarTask)
    archives(sourcesJarTask)
}

publishing {
    publications {
        register<MavenPublication>("forgeJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(sourcesJarTask)
        }
    }
}

tasks.test {
    include("dev/jaronline/cuttingdelight/**/*Test")
}
