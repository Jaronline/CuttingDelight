import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("idea")
    id("java")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

// gradle.properties
val jUnitVersion = providers.gradleProperty("jUnitVersion")
val modId = providers.gradleProperty("modId")
val modJavaVersion = providers.gradleProperty("modJavaVersion")

dependencies {
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("org.jetbrains:annotations:26.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:${jUnitVersion.get()}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    named("main") {
        //The Core has no resources
        resources.setSrcDirs(emptyList<String>())
    }
    named("test") {
        //The test module has no resources
        resources.setSrcDirs(emptyList<String>())
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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion.get()))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    javaToolchains {
        compilerFor {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion.get()))
        }
    }
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar")

base {
    archivesName = "${modId.get()}-core"
}

artifacts {
    archives(tasks.jar.get())
    archives(sourcesJarTask.get())
}

publishing {
    publications {
        register<MavenPublication>("coreJar") {
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
        for (fileName in listOf("build", "run", "run-data", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}
