import org.gradle.kotlin.dsl.maven

pluginManagement {
    repositories {
        fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) =
            exclusiveContent {
                forRepository { maven(url) }
                filter(filter)
            }
        maven("https://maven.minecraftforge.net") {
            content {
                includeGroupByRegex("net\\.minecraftforge.*")
            }
        }
        exclusiveMaven("https://maven.parchmentmc.org") {
            includeGroupByRegex("org\\.parchmentmc.*")
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            content {
                includeGroupByRegex("org\\.spongepowered.*")
                includeGroupByRegex("net\\.minecraftforge.*")
            }
        }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

plugins {
    // https://plugins.gradle.org/plugin/org.danilopianini.gradle-pre-commit-git-hooks
    id("org.danilopianini.gradle-pre-commit-git-hooks") version ("2.1.20")
}

gitHooks {
    preCommit {
        from("#!/bin/sh") {
            """
            echo "*********************************************************"
            echo "Running git pre-commit hook. Running Spotless Apply... "
            echo "*********************************************************"
            
            stagedFiles=$(git diff --staged --name-only)
            
            """
        }
        tasks("spotlessApply")
        appendScript {
            """
            status=$?

            if [ "${'$'}status" = 0 ] ; then
                echo "Static analysis found no problems."
                # Add staged file changes to git
                for file in ${'$'}stagedFiles; do
                  if test -f "${'$'}file"; then
                    git add "${'$'}file"
                  fi
                done
                #Exit
                exit 0
            else
                echo "*********************************************************"
                echo "       ********************************************      "
                echo 1>&2 "Spotless Apply found violations it could not fix."
                echo "Run spotless apply in your terminal and fix the issues before trying to commit again."
                echo "       ********************************************      "
                echo "*********************************************************"
                #Exit
                exit 1
            fi
            """
        }
    }

    commitMsg {
        conventionalCommits()
    }

    createHooks(true)
}

val modId: String by settings
val minecraftVersion: String by settings

rootProject.name = "$modId-$minecraftVersion"
include(
    "Core", "Processor",
    "Changelog",
    "Common",
    "Forge"
)
