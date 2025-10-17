plugins {
    // 1. Aplicación del plugin JVM
    // La versión del plugin JVM debe coincidir con el plugin de Serialización
    val kotlinVersion = "1.9.22"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion

    // 2. Aplicación del plugin de SERIALIZACIÓN
    // IMPORTANTE: Definir la versión explícitamente resuelve el error de "Plugin not found".
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion

    // El resto de tus plugins (application, javafx, etc.)
    application
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

    // Esta dependencia es usada por la aplicación (Guava como ejemplo).
    implementation(libs.guava)

    // --- Dependencias para Coroutines y Ktor para comunicación con el servidor Python ---

    // 1. Kotlin Coroutines: Necesario para manejar la asincronía (funciones 'suspend' y 'runBlocking')
    val coroutinesVersion = "1.8.1" // Versión actualizada
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // 2. Ktor Client: Core y el motor CIO (Content I/O) para realizar peticiones HTTP
    val ktorVersion = "2.3.11" // Versión actualizada
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")


    // 3. Ktor Content Negotiation y JSON Serialization: Permite enviar y recibir los Data Classes (@Serializable)
    val serializationVersion = "1.6.3" // Versión del runtime JSON
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    // AÑADIDO: Dependencia explícita del runtime JSON para eliminar advertencias
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    // Dependencia de utilidad para manejar logging (slf4j)
    implementation("org.slf4j:slf4j-nop:2.0.13")

}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Configuración de JavaFX
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