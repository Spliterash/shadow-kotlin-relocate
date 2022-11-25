import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    id("java-gradle-plugin")
}

repositories {
    gradlePluginPortal()
}

group = "ru.spliterash"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    compileOnly("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")

    api("org.ow2.asm", "asm", "9.2")
    api("org.ow2.asm", "asm-util", "9.2")
}

tasks.withType<KotlinCompile> {
    dependsOn
    kotlinOptions.jvmTarget = "1.8"
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

val name = "ru.spliterash.shadow-kotlin-relocate"

gradlePlugin {
    plugins {
        create(name) {
            id = name
            implementationClass = "ru.spliterash.shadowkotlinrelocate.ShadowKotlinRelocatePlugin"
            version = project.version
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.spliterash"
            artifactId = rootProject.name

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "nexus"
            url = uri("https://nexus.spliterash.ru/repository/" + rootProject.name)
            credentials {
                username = findProperty("SPLITERASH_NEXUS_USR")?.toString()
                password = findProperty("SPLITERASH_NEXUS_PSW")?.toString()
            }
        }
    }
}

