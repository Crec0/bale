rootProject.name = "mcserverapi"

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }

        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("fabric-loom") version "0.11-SNAPSHOT"
        id("org.jetbrains.kotlin.jvm") version "1.6.10"
    }
}
