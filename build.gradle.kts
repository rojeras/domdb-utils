import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    application
}

group = "me.leo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("io.ktor:ktor-client-core:1.5.3")
    implementation("io.ktor:ktor-client-cio:1.5.3")
    implementation("io.ktor:ktor-client-serialization:1.5.3")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
