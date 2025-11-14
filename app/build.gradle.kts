plugins {
    // Configuración estándar de Kotlin JVM
    kotlin("jvm") version "1.9.23"
    // Plugin para serialización (necesario para Ktor JSON)
    kotlin("plugin.serialization") version "1.9.23"

    // SOLUCIÓN DEL ERROR ANTERIOR: plugin 'application'
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

// 🔥 SOLUCIÓN FINAL: Se añade el repositorio de Google 🔥
repositories {
    mavenCentral()
    google() // <-- AÑADIDO: Repositorio clave para artefactos de Kotlin/Android
    maven("https://maven.pkg.jetbrains.space/public/p/kotlin/p/kotlin")
}

dependencies {
    // --- LIBRERÍAS CORE ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // --- KTOR SERVER (Backend) ---
    implementation("io.ktor:ktor-server-netty:2.3.11")
    implementation("io.ktor:ktor-server-core-jvm:2.3.11")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-server-call-logging:2.3.11")
    implementation("io.ktor:ktor-server-default-headers:2.3.11")
    implementation("io.ktor:ktor-server-cors:2.3.11")

    // --- KTOR CLIENT (Para llamar al servidor Python de la IA) ---
    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-cio:2.3.11")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11")

    // --- LOGGING ---
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // --- TESTEO ---
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:2.3.11")
}

// Configuración para ejecutar el servidor Ktor desde el IDE
application {
    mainClass.set("org.example.ApplicationKt")
}

// Configuración de tareas
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}