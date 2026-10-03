plugins {
    id("cuttingdelight-convention")
    id("cuttingdelight-test")
    alias(libs.plugins.moddevgradle.legacyforge)
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

base {
    archivesName = "${cuttingdelight.modId.get()}-common"
}

sourceSets {
    named("main") {
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    named("test") {
        resources {
            //The test module has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

legacyForge {
    validateAccessTransformers = true
    setAccessTransformers("src/main/resources/META-INF/accesstransformer.cfg")

    enable {
        mcpVersion = cuttingdelight.mcVersion.version
        enabledSourceSets = setOf(sourceSets.main.get(), sourceSets.test.get())
        isDisableRecompilation = false
    }

    addModdingDependenciesTo(sourceSets.test.get())
}

dependencies {
    compileOnly(libs.mixin)

    implementation(libs.jei.common.api)
    compileOnly(libs.farmersdelight)

    compileOnly(libs.jspecify)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

publishing {
    publications {
        register<MavenPublication>("commonJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))
        }
    }
}

tasks.test {
    include("dev/jaronline/cuttingdelight/**/*Test")
}
