val archives_base_name: String by project
val version: String by project
val group: String by project

val fabric_kotlin_version: String by project

val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project

plugins {
    val kotlinVersion = "1.9.23"

    kotlin("jvm") version (kotlinVersion)
    kotlin("plugin.serialization") version (kotlinVersion)
    id("io.ktor.plugin") version "2.3.10"

    id("fabric-loom") version "1.6-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
}

repositories {
    maven {
        name = "Ktor"
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-html-builder")
}

tasks {
    shadowJar {
        configurations[0] = project.configurations.shadow.get()
        dependencies {
            exclude(dependency("net.fabricmc:*"))
            exclude(dependency("com.mojang:*"))
        }
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

    compileJava {
        options.compilerArgs.add("--enable-preview")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "21"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
}

java {
    withSourcesJar()
}
