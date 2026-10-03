import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("cuttingdelight-build")
    idea
    eclipse
    java
    `maven-publish`
}

val cuttingdelight = extensions.getByType<CuttingDelightBuildPlugin>()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(cuttingdelight.javaVersion.version)
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

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/jaronline/cuttingdelight")
            credentials {
                username = cuttingdelight.githubActor
                password = cuttingdelight.githubToken
            }
        }
    }
}
