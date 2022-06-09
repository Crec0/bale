@file:Suppress("PropertyName", "VulnerableLibrariesLocal")

import com.google.common.io.Files
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val archives_base_name: String by project
val version: String by project
val group: String by project

val fabric_kotlin_version: String by project

val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project

val ktor_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization").version("1.6.21")
    id("fabric-loom")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.github.juuxel.loom-quiltflower") version "1.7.2"
    java
}

repositories {
    maven {
        name = "Ktor"
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }

    maven {
        name = "Cotton-Quiltflower"
        url = uri("https://server.bbkr.space/artifactory/libs-release/")
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")

    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    shadow("io.ktor:ktor-server-core-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-netty-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-auth-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    shadow("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
}

tasks {
    shadowJar {
        configurations[0] = project.configurations.shadow.get()
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
    }

    processResources {
        inputs.property("version", version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to version))
        }
    }

    jar {
        from("LICENSE")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    build {
        doLast {
            delete(shadowJar.get().archiveFile)
            File("D:/minecraft_utils/server/mods/$archives_base_name-$version.jar").delete()
            Files.copy(
                File("./build/libs/$archives_base_name-$version.jar"),
                File("D:/minecraft_utils/server/mods/$archives_base_name-$version.jar")
            )
        }
    }
}

java {
    withSourcesJar()
}
