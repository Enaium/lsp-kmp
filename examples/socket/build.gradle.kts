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
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("cn.enaium.lsp.examples.socket.MainKt")
}