plugins {
    id("cuttingdelight-convention")
    alias(libs.plugins.moddevgradle)
}

base {
    archivesName = "${cuttingdelight.modId.get()}-debug"
}

sourceSets {
    named("test") {
        resources {
            //The test module has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

neoForge {
    neoFormVersion = cuttingdelight.neoformVersion.version
}

val copyModMetadataToClasses = tasks.register<Copy>("copyModMetadataToClasses") {
    from(layout.buildDirectory.dir("resources/main/META-INF")) {
        include("neoforge.mods.toml")
    }
    into(layout.buildDirectory.dir("classes/java/main/META-INF"))
    dependsOn(
        tasks.named(sourceSets.main.get().compileJavaTaskName),
        tasks.named(sourceSets.main.get().processResourcesTaskName)
    )
}

tasks.named(sourceSets.main.get().classesTaskName) {
    dependsOn(copyModMetadataToClasses)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}