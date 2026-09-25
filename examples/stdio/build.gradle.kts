import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "cn.enaium"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set("cn.enaium.lsp.examples.stdio.MainKt")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":"))
        }
    }
}