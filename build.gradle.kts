plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kmp)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "cn.enaium"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(25)

    // JVM
    jvm()

    // Apple: macOS
    macosX64()
    macosArm64()

    // Apple: iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Apple: tvOS
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    // Apple: watchOS
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    // Linux
    linuxX64()
    linuxArm64()

    // Windows
    mingwX64()

    android {
        namespace = "cn.enaium.lsp"
        compileSdk = 36
        minSdk = 24
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

// ===== Publishing (com.vanniktech.maven.publish) =====
mavenPublishing {
    coordinates("cn.enaium", "lsp-kmp", project.version.toString())

    pom {
        name = "lsp-kmp"
        description = "Kotlin Multiplatform implementation of the Language Server Protocol and Debug Adapter Protocol"
        url = "https://github.com/Enaium/lsp-kmp"
        licenses {
            license {
                name = "MIT License"
                url = "https://spdx.org/licenses/MIT.html"
            }
        }
        developers {
            developer {
                id = "enaium"
                name = "Enaium"
                url = "https://github.com/Enaium"
            }
        }
        scm {
            url = "https://github.com/Enaium/lsp-kmp"
            connection = "scm:git:git@github.com:Enaium/lsp-kmp.git"
            developerConnection = "scm:git:git@github.com:Enaium/lsp-kmp.git"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}