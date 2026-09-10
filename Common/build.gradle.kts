plugins {
    id("cuttingdelight-convention")
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
    archivesName = "${cuttingDelight.modId.get()}-common"
}

sourceSets {
    named("main") {
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    create("dev", Action<SourceSet> {
        resources {
            setSrcDirs(listOf("src/dev/resources"))
        }
        compileClasspath += main.get().output + configurations.compileClasspath.get()
        runtimeClasspath += main.get().output + configurations.runtimeClasspath.get()
    })
    named("test") {
        resources {
            //The test module has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

val dependencyProjects: List<Project> = listOf(
    project(":Core")
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.path)
}

neoForge {
    neoFormVersion = cuttingDelight.neoformVersion.version
    addModdingDependenciesTo(sourceSets.test.get())
    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")
}

dependencies {
    compileOnly(libs.mixin)
    dependencyProjects.forEach {
        implementation(it)
    }

    api(libs.jei.common.api)
    implementation(libs.farmersdelight)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

publishing {
    publications {
        register<MavenPublication>("commonJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))

            val dependencyInfos = dependencyProjects.map {
                mapOf(
                    "groupId" to it.group,
                    "artifactId" to it.base.archivesName.get(),
                    "version" to it.version
                )
            }

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                dependencyInfos.forEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    it.forEach { (key, value) ->
                        dependencyNode.appendNode(key, value)
                    }
                }
            }
        }
    }
}

tasks.test {
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}
