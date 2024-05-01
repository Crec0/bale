plugins {
    val kotlinVersion = "1.9.23"

    kotlin("jvm") version (kotlinVersion)
    kotlin("plugin.serialization") version (kotlinVersion)

    id("fabric-loom") version "1.6-SNAPSHOT"
    java
}

repositories {
    maven {
        name = "Ktor"
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    mavenCentral()
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "net.fabricmc")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

val ktorVersion = "2.3.10"
val exposedVersion = "0.49.0"

val archives_base_name: String by project
val version: String by project
val group: String by project
val fabric_kotlin_version: String by project
val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    transitiveInclude(implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-server-cio:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-server-auth:$ktorVersion")!!)
    transitiveInclude(implementation("io.ktor:ktor-server-html-builder:$ktorVersion")!!)

    transitiveInclude(implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")!!)

    transitiveInclude(implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")!!)
    transitiveInclude(implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")!!)
    transitiveInclude(implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")!!)
    transitiveInclude(implementation("org.xerial:sqlite-jdbc:3.45.3.0")!!)

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

tasks {
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
