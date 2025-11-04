import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Aplica el plugin de Kotlin JVM
    kotlin("jvm") version "1.9.22"

    // Aplica el plugin de JavaFX para manejar las dependencias nativas
    id("org.openjfx.javafxplugin") version "0.1.0"

    // Aplica el plugin de Kotlinx Serialization para el manejo de JSON
    kotlin("plugin.serialization") version "1.9.22"

    // Aplica el plugin de aplicación (para crear la tarea run)
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Define la versión de Coroutines
val coroutinesVersion = "1.8.1"
// Define la versión de Ktor
val ktorVersion = "2.3.8"
// Define la versión de Kotlinx Serialization
val serializationVersion = "1.6.3"

dependencies {
    // ----------------------------------------------------
    // Kotlin y Testing
    // ----------------------------------------------------
    implementation(kotlin("stdlib"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // ----------------------------------------------------
    // JavaFX (Necesario para la GUI)
    // ----------------------------------------------------
    // El plugin de javafx ya inyecta las dependencias necesarias, pero puedes
    // especificar la versión si fuera necesario, aunque el bloque 'javafx' es preferido.


    // ----------------------------------------------------
    // Kotlin Coroutines (Necesario para concurrencia)
    // ----------------------------------------------------
    // Core de corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    // Adaptación para usar Dispatchers.Main con JavaFX (¡CRÍTICO para resolver el primer error!)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:$coroutinesVersion")


    // ----------------------------------------------------
    // Ktor (Cliente HTTP)
    // ----------------------------------------------------
    // Motor de conexión (CIO recomendado para cliente)
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    // Plugin de Content Negotiation (manejo de JSON)
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    // Adaptador de Kotlinx Serialization para Ktor
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")


    // ----------------------------------------------------
    // Kotlinx Serialization (Manejo de JSON)
    // ----------------------------------------------------
    // Runtime para la serialización
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    // Corrección errores
    implementation("org.slf4j:slf4j-simple:2.0.7") // Usa la versión actual
}

// --- Configuración Específica ---

// Configuración de JavaFX
javafx{
    // Versión de JavaFX (debe coincidir con la de tu JDK/entorno)
    version = "21"
    // Módulos de JavaFX que se van a usar
    modules = listOf("javafx.controls", "javafx.fxml")
}

// Configuración de Kotlin
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21" // O la versión de tu JDK (ej: "17")
}

// Configuración de la Aplicación
application {
    // Define la clase principal (el punto de entrada de la aplicación JavaFX)
    mainClass.set("org.example.MainJavaFX")
}

// Configuración para el empaquetado JAR (opcional, pero buena práctica)
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    // Incluye todas las dependencias en el JAR (fat jar)
    // Puede ser necesario para una ejecución más sencilla fuera de Gradle
    // from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    // duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}