import os
from openai import OpenAI
import json

# --- ASISTENTE DE IA (CLASE PRINCIPAL) ---

class QwenOpenRouterAsistente:
    def __init__(self):
        # NOTA: Asegúrate de configurar la variable de entorno OPENROUTER_API_KEY.
        self.api_key = os.getenv("OPENROUTER_API_KEY")
        if not self.api_key:
            raise ValueError("La variable de entorno 'OPENROUTER_API_KEY' no está configurada.")

        # Configuración del cliente OpenAI para usar OpenRouter
        self.client = OpenAI(
            api_key=self.api_key,
            base_url="https://openrouter.ai/api/v1",
            timeout=60.0
        )
        # Usamos un modelo potente (Mixtral 8x7B) para la creatividad y el chat
        self.model_name = "mistralai/mixtral-8x7b-instruct"
        print("Asistente IA inicializado con éxito y listo para la API de OpenRouter.")

    def obtener_respuesta_ia(self, data: dict):
        """
        Función central que maneja la lógica de la llamada a la IA (chat o enriquecimiento).

        :param data: Diccionario que contiene los datos del cliente Kotlin.
        :return: Diccionario con la clave 'respuesta' o 'error'.
        """
        lugar_nombre = data.get('lugar_nombre')
        descripcion_actual = data.get('descripcion_actual')
        historial_mensajes = data.get('historial_mensajes')

        # 1. Definir el contexto y el prompt basado en la tarea (Chat o Enriquecimiento)
        if historial_mensajes is not None:
            # --- TAREA: Chat Genérico ---
            system_instruction = (
                "Eres un asistente virtual experto en turismo de Mendoza, Argentina. "
                "Mantén el contexto de la conversación. "
                "Responde preguntas ÚNICAMENTE sobre viajes, lugares turísticos o consejos de Mendoza. "
                "Si la pregunta no está relacionada (ej. matemáticas, política), dirige cortésmente la conversación al tema de turismo."
            )

            messages = [{"role": "system", "content": system_instruction}]
            # El historial viene en el formato correcto desde Kotlin (lista de dicts con 'role' y 'content')
            messages.extend(historial_mensajes)

            temperature = 0.7

        elif lugar_nombre and descripcion_actual:
            # --- TAREA: Enriquecimiento de Lugar Turístico ---
            system_instruction = (
                "Eres un escritor de viajes profesional especializado en marketing turístico. "
                "Tu tarea es mejorar dramáticamente una descripción simple, usando lenguaje vibrante, persuasivo y de lujo. "
                "DEBES devolver SOLAMENTE el texto mejorado y nada más."
            )

            user_prompt = (
                f"Lugar turístico: {lugar_nombre}\n"
                f"Descripción actual (simple): {descripcion_actual}\n"
                "Tarea: Transforma la 'Descripción actual' en un texto de marketing de lujo de 3-4 párrafos. NO incluyas encabezados como 'Descripción de...'"
            )

            messages = [
                {"role": "system", "content": system_instruction},
                {"role": "user", "content": user_prompt}
            ]

            temperature = 0.5

        else:
            return {"error": "Petición de IA inválida: Faltan datos para chat o enriquecimiento."}

        # 2. Llamada a la API
        try:
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=temperature,
                max_tokens=2048
            )

            respuesta_texto = completion.choices[0].message.content.strip()
            return {"respuesta": respuesta_texto}

        except Exception as e:
            return {"error": f"Error al llamar a OpenRouter: {e}"}