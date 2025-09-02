import google.generativeai as genai
import os

# 1. Configuración de la API Key
# Pega tu clave de la API en la siguiente línea.
# Para obtenerla, ve a: https://aistudio.google.com/app/apikey
genai.configure(api_key="AIzaSyBPSzWgyEnENenleqOD5K5hqWxnn_Bs5AQ")

# 2. Seleccionamos el modelo y creamos una sesión de chat
model = genai.GenerativeModel('gemini-1.5-flash')
chat = model.start_chat(history=[])

print("Hola, soy Gemini. ¡Puedes hacerme todas las preguntas que quieras!")
print("Para terminar, simplemente escribe 'salir'.")

# 3. Bucle para hacer múltiples preguntas
while True:
    # Solicitamos la pregunta al usuario
    pregunta = input("\nTu pregunta: ")

    # Si el usuario escribe 'salir', terminamos el bucle
    if pregunta.lower() == 'salir':
        print("¡Hasta la próxima!")
        break

    # 4. Enviamos la pregunta al chat y mostramos la respuesta
    try:
        # Usamos `send_message` para que el modelo recuerde el contexto
        respuesta = chat.send_message(pregunta)
        print("\nGemini:", respuesta.text)
    except Exception as e:
        print(f"Ocurrió un error: {e}")
        print("Asegúrate de que tu API Key sea válida.")