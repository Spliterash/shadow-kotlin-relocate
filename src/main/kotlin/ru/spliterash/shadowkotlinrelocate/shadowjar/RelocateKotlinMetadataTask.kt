package ru.spliterash.shadowkotlinrelocate.shadowjar

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class RelocateKotlinMetadataTask : DefaultTask() {
    @TaskAction
    open fun run() {
        val shadowTask = project.tasks.findByName("shadowJar") ?: return
        shadowTask as ShadowJar

        shadowTask.patchMetadata()
    }
}