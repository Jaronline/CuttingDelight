plugins {
    id("java")
}

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

dependencies {
    implementation(project(":Core"))
    implementation(project(":Common"))
    implementation(
        group = "org.jetbrains",
        name = "annotations",
        version = "26.0.2-1"
    )
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.compileClasspath.get()
}