package ru.spliterash.shadowkotlinrelocate

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.spliterash.shadowkotlinrelocate.shadowjar.RelocateKotlinMetadataTask

open class ShadowKotlinRelocatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("relocateKotlinMetadata", RelocateKotlinMetadataTask::class.java) {
            it.dependsOn(project.tasks.findByName("shadowJar"))
        }
    }
}