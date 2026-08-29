import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    idea
    eclipse
    java
    `maven-publish`
}

// gradle.properties
val javaVersion = providers.gradleProperty("javaVersion")

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion.get())
    withSourcesJar()
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
        excludeDirs.addAll(listOf("build", "run", "run-data", "out", "logs").map { file(it) })
    }
}

eclipse {
    classpath {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
    // Should be removed once tests are added
    failOnNoDiscoveredTests = false
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/jaronline/cuttingdelight")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
}
