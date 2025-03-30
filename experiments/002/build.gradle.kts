@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

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
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "DesarguesTheorem") {
                mainClass = "DesarguesTheoremKt"
            }
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "EulerLine") {
                mainClass = "EulerLineKt"
            }
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "KazakhstanNationalMathOlympiad2023.9.6") {
                mainClass = "KazakhstanNationalMathOlympiad2023_9_6Kt"
            }
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "KazakhstanNationalMathOlympiad2023.10.1") {
                mainClass = "KazakhstanNationalMathOlympiad2023_10_1Kt"
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
                optIn("dev.lounres.kone.ExperimentalKoneAPI")
            }
        }
        
        jvmMain {
            dependencies {
                // TODO: Replace with released versions
//                implementation(versions.kone.contexts)
//                implementation(versions.kone.algebraic)
//                implementation(versions.kone.algebraicExtra)
//                implementation(versions.kone.collections)
//                implementation(versions.kone.polynomial)
//                implementation(versions.kone.misc.planimetricsCalculation)
                implementation(kone.contexts)
                implementation(kone.algebraic)
                implementation(kone.algebraicExtra)
                implementation(kone.collections)
                implementation(kone.polynomial)
                implementation(kone.misc.planimetricsCalculus)
            }
        }
    }
}