plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Plugin esencial para usar @Serializable en los modelos de datos
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation(libs.guava)

    // --- Dependencias para Coroutines y Ktor para comunicación con el servidor Python ---

    // 1. Kotlin Coroutines: Necesario para manejar la asincronía (funciones 'suspend' y 'runBlocking')
    val coroutinesVersion = "1.8.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // 2. Ktor Client: Core y el motor CIO (Content I/O) para realizar peticiones HTTP
    val ktorVersion = "2.3.11"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // 3. Ktor Content Negotiation y JSON Serialization: Permite enviar y recibir los Data Classes (@Serializable)
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Las siguientes dependencias fueron reemplazadas por las de Ktor/Serialization:
    // implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.slf4j:slf4j-nop:2.0.13")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "25" // Especifica la versión de JavaFX
    modules = listOf("javafx.controls", "javafx.fxml") // Módulos que necesitas
}

application {
    // Define the main class for the application.
    mainClass = "org.example.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

