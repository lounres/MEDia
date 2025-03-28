@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(versions.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
    mavenLocal()
}

val jvmTargetVersion : String by properties

kotlin {
    jvmToolchain(jvmTargetVersion.toInt())
    jvm {
        binaries {
            executable {
                mainClass = "MainKt"
            }
        }
    }
    
    sourceSets {
        all {
            languageSettings {
                progressiveMode = true
                enableLanguageFeature("ContextParameters")
                enableLanguageFeature("ValueClasses")
                enableLanguageFeature("ContractSyntaxV2")
                enableLanguageFeature("ExplicitBackingFields")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.ExperimentalSubclassOptIn")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.uuid.ExperimentalUuidApi")
            }
        }
        
        jvmMain {
            dependencies {
                implementation(versions.kone.contexts)
                implementation(versions.kone.algebraic)
                implementation(versions.kone.algebraicExtra)
                implementation(versions.kone.collections)
                implementation(versions.kone.enumerativeCombinatorics)
            }
        }
    }
}