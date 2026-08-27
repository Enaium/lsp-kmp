plugins {
    kotlin("jvm")
    application
}

group = "cn.enaium"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("cn.enaium.lsp.examples.stdio.MainKt")
}