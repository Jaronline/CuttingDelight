plugins {
    id("cuttingdelight-convention")
    alias(libs.plugins.moddevgradle.legacyforge)
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

legacyForge {
    mcpVersion = cuttingdelight.mcVersion.version
}

val copyModMetadataToClasses = tasks.register<Copy>("copyModMetadataToClasses") {
    from(layout.buildDirectory.dir("resources/main/META-INF")) {
        include("mods.toml")
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