rootProject.name = "001"

val projectProperties = java.util.Properties()
file("gradle.properties").inputStream().use {
    projectProperties.load(it)
}

val versions: String by projectProperties

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }
    
    versionCatalogs {
        create("versions") { from("dev.lounres:versions:$versions") }
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}