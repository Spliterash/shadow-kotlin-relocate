# ShadowKotlinRelocate

When you use relocate in shadow jar, it work ok, but not relocate kotlin metadata stuff, SOOOOOOOOOOOOOOO

Now you fix invalid kotlin metadata relocate with my plugin (actually stolen and
modified [exposed-gradle-plugin](https://github.com/JetBrains/exposed-intellij-plugin/tree/master/exposed-gradle-plugin))

If you want use it

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://repo.spliterash.ru/group/")
        }
    }
}
```

```kotlin
// build.gradle.kts
plugins {
    id("ru.spliterash.shadow-kotlin-relocate") version "1.0.0"
}

tasks.shadowJar {
    // use kotlinRelocate instead relocate
    kotlinRelocate("source", "destination")
}

// Call relocateKotlinMetadata task, bcause im have no clue how to call it in build script,
// bcause im launch it via cmd
```