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

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as String)
        vendor = JvmVendorSpec.matching(project.extra["jvmVendor"] as String)
    }
    jvm {
        binaries {
            listOf(
                executable(KotlinCompilation.MAIN_COMPILATION_NAME, "Version1") {
                    mainClass = "version1.MainKt"
                },
                executable(KotlinCompilation.MAIN_COMPILATION_NAME, "Version2") {
                    mainClass = "version2.MainKt"
                },
                executable(KotlinCompilation.MAIN_COMPILATION_NAME, "Version3") {
                    mainClass = "version3.MainKt"
                },
            ).forEach {
                it.get().standardInput = System.`in`
            }
        }
    }
    
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xconsistent-data-class-copy-visibility",
            "-Xcontext-sensitive-resolution",
            "-Xreturn-value-checker=full",
        )
    }
    
    sourceSets {
        all {
            languageSettings {
                progressiveMode = true
                enableLanguageFeature("ContextParameters")
                enableLanguageFeature("ValueClasses")
                enableLanguageFeature("ContractSyntaxV2")
                enableLanguageFeature("ExplicitBackingFields")
                enableLanguageFeature("NestedTypeAliases")
                optIn("kotlin.experimental.ExperimentalTypeInference")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.ExperimentalSubclassOptIn")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("dev.lounres.kone.annotations.UnstableKoneAPI")
                optIn("dev.lounres.kone.annotations.ExperimentalKoneAPI")
            }
        }
        
        jvmMain {
            dependencies {
                implementation(kone.util.misc)
                implementation(kone.algebraic)
                implementation(kone.algebraicExtra)
                implementation(kone.multidimensionalCollections)
                implementation(kone.computationalGeometry)
                implementation(kone.enumerativeCombinatorics)
            }
        }
    }
}