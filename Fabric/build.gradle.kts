import dev.jaronline.gradle.isolatedProjectDirectory
import me.modmuss50.mpp.ReleaseType

plugins {
    id("cuttingdelight-convention")
    id("cuttingdelight-test")
    alias(libs.plugins.modpublish)
    alias(libs.plugins.loom)
}

repositories {
    mavenCentral()
    fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) =
        exclusiveContent {
            forRepository { maven(url) }
            filter(filter)
        }
    exclusiveMaven("https://maven.parchmentmc.org") {
        @Suppress("UnstableApiUsage")
        includeGroupAndSubgroups("org.parchmentmc")
    }
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
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Cassian's Maven"
        url = uri("https://maven.cassian.cc")
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
    archivesName = "${cuttingdelight.modId.get()}-fabric"
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
    project(":Common")
)
val debugProjectDirectory = isolatedProjectDirectory(":Debug")

val debugSourceSet: SourceSet = sourceSets.create("debug") {
    java.srcDir(debugProjectDirectory.dir("src/main/java"))
    resources.srcDir(debugProjectDirectory.dir("src/main/resources"))
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += output + compileClasspath
}

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

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val changelogHtml: Configuration = configurations.create("changelogHtml") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogHtml"))
    }
}

val changelogMarkdown: Configuration = configurations.create("changelogMarkdown") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogMarkdown"))
    }
}

fun Configuration.singleFileContents(): Provider<String> =
    incoming
        .files
        .elements
        .map { elements -> elements.single() }
        .map { it.asFile.readText() }

dependencies {
    minecraft(libs.loom.minecraft)
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.minecraft.get()}:${libs.versions.parchment.mappings.get()}@zip")
    })
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)

    dependencyProjects.forEach {
        compileOnly(it)
        localRuntime(it)
    }

    modCompileOnly(libs.jei.common.api)
    modRuntimeOnly(libs.jei.fabric)
    compileOnly(libs.mezz.config.api)
    modLocalRuntime(libs.mezz.config.fabric)
    include(libs.mezz.config.fabric)
    modRuntimeOnly(libs.mezz.config.gui.fabric)

    modRuntimeOnly(libs.modmenu)

    modImplementation(libs.farmersdelight.fabric) {
        exclude(group = "net.fabricmc")
    }

    compileOnly(libs.jspecify)
    testCompileOnly(libs.jspecify)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    @Suppress("AvoidDuplicateDependencies")
    changelogHtml(project(":Changelog"))
    @Suppress("AvoidDuplicateDependencies")
    changelogMarkdown(project(":Changelog"))
}

loom {
    mods {
        create(cuttingdelight.modId.get()) {
            sourceSet(sourceSets.main.get())
            for (dependencyProject in dependencyProjects) {
                sourceSet(dependencyProject.sourceSets.main.get())
            }
        }
    }

    val loomDir = File("run")

    runs {
        named("client") {
            client()
            displayName = "Fabric Client"
            generateRunConfig = true
            runDirectory = file(loomDir.resolve("client").toString())
            jvmArguments.addAll(
                "-Dfabric.log.level=info"
            )
        }

        named("server") {
            server()
            displayName = "Fabric Server"
            generateRunConfig = true
            runDirectory = file(loomDir.resolve("server").toString())
            jvmArguments.addAll(
                "-Dfabric.log.level=info"
            )
        }

        create("clientDebug") {
            client()
            displayName = "Fabric Client Debug"
            generateRunConfig = true
            runDirectory = file(loomDir.resolve("client").toString())
            jvmArguments.addAll(
                "-Dfabric.log.level=debug"
            )
        }

        create("serverDebug") {
            server()
            displayName = "Fabric Server Debug"
            generateRunConfig = true
            runDirectory = file(loomDir.resolve("server").toString())
            jvmArguments.addAll(
                "-Dfabric.log.level=debug"
            )
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
        modId = cuttingdelight.modId.get()
    }
}

val debugClassesTask: TaskProvider<Task> = tasks.named(debugSourceSet.classesTaskName)
val debugModPath: String = layout.buildDirectory.dir("resources/${debugSourceSet.name}").get().asFile.absolutePath
val debugRunTasks: Set<String> = setOf("runClientDebug", "runServerDebug")
tasks.matching { it.name in debugRunTasks }.configureEach {
    dependsOn(debugClassesTask)
    if (this is JavaExec) {
        classpath(debugSourceSet.output)
        jvmArgs("-Dfabric.addMods=$debugModPath")
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
        modLoaders.add("fabric")
        displayName.set("${cuttingdelight.modVersion.version} for Fabric ${cuttingdelight.mcVersion.version}")
        version.set(project.version.toString())

        curseforge {
            projectId = cuttingdelight.curseProjectId
            accessToken = cuttingdelight.curseforgeApiKey
            changelog.set(changelogHtml.singleFileContents())
            changelogType = "html"
            minecraftVersionRange {
                start = cuttingdelight.mcVersion.version
                end = cuttingdelight.mcVersion.version
            }
            javaVersions.add(JavaVersion.toVersion(cuttingdelight.javaVersion.version))
            requires("farmers-delight-refabricated")
            client = true
            server = true
        }

        modrinth {
            projectId = cuttingdelight.modrinthId
            accessToken = cuttingdelight.modrinthToken
            changelog.set(changelogMarkdown.singleFileContents())
            minecraftVersionRange {
                start = cuttingdelight.mcVersion.version
                end = cuttingdelight.mcVersion.version
            }
            requires("farmers-delight-refabricated")
        }
    }
}

artifacts {
    archives(tasks.jar)
    archives(sourcesJarTask)
}

publishing {
    publications {
        register<MavenPublication>("fabricJar") {
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