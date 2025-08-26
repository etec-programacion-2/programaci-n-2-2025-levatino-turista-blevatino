package org.example
// Importa las clases necesarias
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.Chat

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // 1. Configura el modelo con tu clave de API
    val API_KEY = "AIzaSyAX6g_w_VeRAECmeQqDZEbbwfIxRZ-HYbA"
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = API_KEY
    )

    println("¡Hola! Soy un chatbot. Escribe 'salir' para terminar la conversación.")

    // 2. Inicia la conversación con el modelo
    val chat = generativeModel.startChat()

    // 3. Bucle para interactuar con el usuario
    while (true) {
        print("> Tú: ")
        val userPrompt = readlnOrNull()
        if (userPrompt.equals("salir", ignoreCase = true)) {
            break
        }

        // 4. Envía el mensaje y obtén la respuesta
        val response = chat.sendMessage(userPrompt)

        // 5. Imprime la respuesta del modelo
        val botResponse = response.text
        println("> Bot: $botResponse")
    }
}