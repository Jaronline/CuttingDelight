plugins {
    id("cuttingdelight-convention")
}

repositories {
    mavenCentral()
}

val cuttingDelight = extensions.getByType<CuttingDelightBuildPlugin>()

dependencies {
    implementation(libs.guava)
    implementation(libs.jetbrains.annotations)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
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
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar")

val baseArchivesName = "${cuttingDelight.modId.get()}-core"
base {
    archivesName.set(baseArchivesName)
}

artifacts {
    archives(tasks.jar)
    archives(sourcesJarTask)
}

publishing {
    publications {
        register<MavenPublication>("coreJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(sourcesJarTask)
        }
    }
}
