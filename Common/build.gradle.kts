import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("idea")
    id("java")
    id("maven-publish")
    id("org.spongepowered.gradle.vanilla")
}

repositories {
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

// gradle.properties
val parchmentVersionForge: String by extra
val minecraftVersion: String by extra
val spongeMixinVersion: String by extra
val modId: String by extra
val modJavaVersion: String by extra
val jeiVersion: String by extra
val farmersDelightVersion: String by extra
val guavaVersion: String by extra
val jUnitVersion: String by extra

val baseArchivesName = "${modId}-common"
base {
    archivesName.set(baseArchivesName)
}

sourceSets {
    val main = named("main") {
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

minecraft {
    version(minecraftVersion)
    accessWideners(file("src/main/resources/$modId.accesswidener"))
}

dependencies {
    compileOnly(
        group = "org.spongepowered",
        name = "mixin",
        version = spongeMixinVersion
    )
    implementation(
        group = "com.google.guava",
        name = "guava",
        version = guavaVersion
    )
    dependencyProjects.forEach {
        implementation(it)
    }
    implementation(
        group = "mezz.jei",
        name = "jei-$minecraftVersion-common-api",
        version = jeiVersion
    )
    compileOnly(
        group = "maven.modrinth",
        name = "farmers-delight",
        version = "$minecraftVersion-$farmersDelightVersion"
    )
    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter",
        version = jUnitVersion
    )
    testRuntimeOnly(
        group = "org.junit.platform",
        name = "junit-platform-launcher"
    )
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

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }
}

tasks.jar {
    // Exclude dev source set classes from jar output
    from(sourceSets.getByName("dev").output) {
        exclude("**/*")
    }
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
