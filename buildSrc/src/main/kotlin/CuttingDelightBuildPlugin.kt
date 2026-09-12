import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

abstract class CuttingDelightBuildPlugin(val project: Project) {
    val libs = findVersionCatalog("libs")

    val modId = gradleProperty("modId")
    val modName = gradleProperty("modName")
    val modLicense = gradleProperty("modLicense")
    val modGroupId = gradleProperty("modGroupId")
    val modAuthors = gradleProperty("modAuthors")
    val modCredits = gradleProperty("modCredits")
    val modDescription = gradleProperty("modDescription")

    val curseProjectId = gradleProperty("curseProjectId")
    val modrinthId = gradleProperty("modrinthId")

    val modVersion = versionLookup("cuttingdelight")
    val mcVersion = versionLookup("minecraft")
    val javaVersion = versionLookup("java")

    val forgeVersion = versionLookup("forge")
    val fmlVersion = versionLookup("forge.fml")

    val parchmentMCVersion = versionLookup("parchment.minecraft")
    val parchmentMappingsVersion = versionLookup("parchment.mappings")
    val parchmentVersionForge: String get() = "${parchmentMappingsVersion.version}-${parchmentMCVersion.version}"

    val farmersDelightVersion = versionLookup("farmersdelight")

    // set by ORG_GRADLE_PROJECT_modrinthToken
    val modrinthToken = gradleProperty("modrinthToken")
    // set by ORG_GRADLE_PROJECT_curseforgeApikey
    val curseforgeApikey = gradleProperty("curseforgeApikey").orElse("0")

    val githubActor = environmentVariable("GITHUB_ACTOR").orNull
    val githubToken = environmentVariable("GITHUB_TOKEN").orNull

    fun gradleProperty(propertyName: String): Provider<String> {
        return project.providers.gradleProperty(propertyName)
    }

    fun environmentVariable(variableName: String): Provider<String> {
        return project.providers.environmentVariable(variableName)
    }

    fun versionLookup(id: String): GeoVersionConstraint {
        return GeoVersionConstraint(project.provider {
            libs.findVersion(id)
                .orElseThrow {
                    NoSuchElementException("Version '${id.replace('.', '-')}' does not exist in version catalogue '${libs.name}'!")
                }
        })
    }

    fun findVersionCatalog(name: String): VersionCatalog {
        return project.extensions.getByType<VersionCatalogsExtension>().find(name).orElseThrow {
            NoSuchElementException("No version catalogue '$name' exists!")
        }
    }

    class GeoVersionConstraint(private val constraint: Provider<VersionConstraint>): Provider<VersionConstraint> by constraint {
        val version: String get() {
            val constraint = get()
            val version = constraint.preferredVersion

            if (version.isEmpty())
                return constraint.requiredVersion

            return version
        }

        val range: String get() {
            val constraint = get()
            var range = constraint.requiredVersion

            if (range.isEmpty()) {
                range = constraint.strictVersion

                if (range.isEmpty())
                    throw IllegalArgumentException("Version constraint '${constraint.displayName}' does not have a required or strict version declared!")
            }

            return range
        }

        override fun toString(): String {
            return version
        }
    }
}