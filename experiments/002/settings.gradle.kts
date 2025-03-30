rootProject.name = "002"

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
        mavenLocal()
    }
    
    versionCatalogs {
        create("versions") { from("dev.lounres:versions:$versions") }
        create("kone") { from("dev.lounres:kone.versionCatalog:0.0.0-experiment") }
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