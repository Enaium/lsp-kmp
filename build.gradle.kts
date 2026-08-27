plugins {
    kotlin("multiplatform") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
}

group = "cn.enaium"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)

    jvm()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}