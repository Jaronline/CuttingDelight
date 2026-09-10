import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
    id("cuttingdelight-build")
    alias(libs.plugins.gitchangelog)
}

val cuttingDelight = extensions.getByType<CuttingDelightBuildPlugin>()

val changelogUntaggedName = cuttingDelight.modVersion.map { "Version $it" }

val makeHtmlChangelog = tasks.register<GitChangelogTask>("makeHtmlChangelog") {
    val output = layout.buildDirectory.file("CHANGELOG.html")

    fromRepo.set(project.rootProject.rootDir.absolutePath)
	file.set(output.get().asFile)
	untaggedName.set(changelogUntaggedName)
	fromRevision.set("HEAD~30")
	toRevision.set("HEAD")
	templateContent.set(file("changelog.mustache").readText())

    outputs.file(output)
}

val makeMarkdownChangelog = tasks.register<GitChangelogTask>("makeMarkdownChangelog") {
    val output = layout.buildDirectory.file("CHANGELOG.md")

	fromRepo.set(project.rootProject.rootDir.absolutePath)
	file.set(output.get().asFile)
	untaggedName.set(changelogUntaggedName)
	fromRevision.set("HEAD~30")
	toRevision.set("HEAD")
	templateContent.set(
        file("changelog-markdown.mustache").readText()
            .split("\n")
            .joinToString("\n", transform = String::trim)
    )

    outputs.file(output)
}

tasks.withType<GitChangelogTask> {
	outputs.upToDateWhen { false }
}

val changelogHtml = configurations.create("changelogHtml")
changelogHtml.isCanBeConsumed = true
changelogHtml.isCanBeResolved = false
changelogHtml.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogHtml"))
changelogHtml.outgoing.artifact(makeHtmlChangelog.map { it.outputs.files.singleFile }) {
    type = "html"
}

val changelogMarkdown = configurations.create("changelogMarkdown")
changelogMarkdown.isCanBeConsumed = true
changelogMarkdown.isCanBeResolved = false
changelogMarkdown.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("changelogMarkdown"))
changelogMarkdown.outgoing.artifact(makeMarkdownChangelog.map { it.outputs.files.singleFile }) {
    type = "markdown"
}
