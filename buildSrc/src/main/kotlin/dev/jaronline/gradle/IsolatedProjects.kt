// https://github.com/mezz/JustEnoughItems/blob/26.3/buildSrc/src/main/kotlin/mezz/jei/gradle/IsolatedProjects.kt
package dev.jaronline.gradle

import org.gradle.api.Project
import org.gradle.api.file.Directory

@Suppress("UnstableApiUsage")
fun Project.isolatedProjectDirectory(path: String): Directory =
    project(path).isolated.projectDirectory
