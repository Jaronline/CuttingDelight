plugins {
    id("cuttingdelight-convention")
}

repositories {
    mavenCentral()
}

// gradle.properties
val jUnitVersion = providers.gradleProperty("jUnitVersion")
val modId = providers.gradleProperty("modId")

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

val sourcesJarTask = tasks.named<Jar>("sourcesJar")

base {
    archivesName = "${modId.get()}-core"
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

tasks.test {
    include("dev/jaronline/cuttingdelight/**")
    exclude("dev/jaronline/cuttingdelight/lib/**")
}
