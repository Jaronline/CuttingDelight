plugins {
    id("cuttingdelight-convention")
}

repositories {
    mavenCentral()
}

// gradle.properties
val jUnitVersion = providers.gradleProperty("jUnitVersion")
val modId = providers.gradleProperty("modId")
val guavaVersion = providers.gradleProperty("guavaVersion")

dependencies {
    implementation("com.google.guava:guava:${guavaVersion.get()}")
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
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar")

val baseArchivesName = "${modId.get()}-core"
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
