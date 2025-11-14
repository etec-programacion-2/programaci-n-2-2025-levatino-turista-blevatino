import os
import sys
import json
from openai import OpenAI
from dotenv import load_dotenv # Añadido para asegurar la carga de variables de entorno

# --- ASISTENTE DE IA (CLASE PRINCIPAL) ---

class QwenOpenRouterAsistente:
    def __init__(self):
        self.api_key = os.getenv("OPENROUTER_API_KEY")
        if not self.api_key:
            raise ValueError("La variable de entorno 'OPENROUTER_API_KEY' no está configurada.")

        self.client = OpenAI(
            api_key=self.api_key,
            base_url="https://openrouter.ai/api/v1",
            timeout=60.0
        )
        self.model_name = "mistralai/mixtral-8x7b-instruct"
        self._perform_initial_check()

    def _perform_initial_check(self):
        # Realiza la verificación de la API de OpenRouter de forma silenciosa.
        try:
            messages = [{"role": "user", "content": "Responde 'OK' para confirmar la conexión."}]

            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.1
            )

            respuesta_texto = completion.choices[0].message.content.strip().upper()

            if "OK" in respuesta_texto:
                # Verificación exitosa. No se imprime nada.
                return
            else:
                raise Exception(f"Verificación fallida: Respuesta inesperada del modelo: {respuesta_texto}")

        except Exception as e:
            print("--- DIAGNÓSTICO DE FALLO ---")
            print(f"FALLO CRÍTICO DE CONEXIÓN CON OPENROUTER: {e}")
            print("-----------------------------")
            # El raise detiene la inicialización del servidor de Python.
            raise Exception("Fallo en la verificación inicial. No se puede iniciar el servicio de IA.")

    def enriquecer_lugar_turistico(self, lugar_nombre: str, descripcion_actual: str):
        # Instrucción del sistema para guiar el comportamiento de la IA
        system_instruction = (
            "Eres un escritor de viajes profesional especializado en marketing turístico. "
            "Tu tarea es mejorar dramáticamente una descripción simple, usando lenguaje vibrante y persuasivo. "
            "DEBES devolver SOLO el texto mejorado, siguiendo esta regla de etiquetado OBLIGATORIO: "
            "1. Si consideras que la 'Descripción actual' ya es perfecta y no necesita mejora, "
            "   tu respuesta DEBE empezar con 'BaseDeDatos:' seguido de la descripción actual."
            "2. Si la mejoras o la reescribes (lo cual debes hacer casi siempre), "
            "   tu respuesta DEBE empezar con 'PotenciadoIA:' seguido del nuevo texto enriquecido."
        )

        # Prompt del usuario con los datos específicos
        user_prompt = (
            f"Lugar turístico: {lugar_nombre}\n"
            f"Descripción actual (simple): {descripcion_actual}\n"
            "Tarea: Transforma la 'Descripción actual' en un texto de marketing de lujo de 3-4 párrafos. "
            "Aplica la regla de etiquetado OBLIGATORIO al inicio de tu respuesta."
        )

        try:
            messages = [
                {"role": "system", "content": system_instruction},
                {"role": "user", "content": user_prompt}
            ]

            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.7,
                max_tokens=2048
            )

            respuesta_texto = completion.choices[0].message.content.strip()

            return {"respuesta": respuesta_texto}
        except Exception as e:
            return {"error": f"Error al llamar a OpenRouter: {e}"}


    def obtener_respuesta_generica(self, historial_mensajes: list):
        """
        Responde preguntas generales sobre turismo, manteniendo la memoria y aplicando restricción
        de contexto. La funcionalidad de herramientas (hora/clima) fue removida.
        """
        # Instrucción estricta para la primera llamada.
        system_instruction = (
            "Eres un asistente virtual experto en turismo en Mendoza, Argentina. "
            "Usa la información de tu historial para mantener el contexto. "
            "Debes responder ÚNICAMENTE sobre viajes, turismo, o consejos relevantes. "
            "Si la pregunta es irrelevante, pide amablemente al usuario que se enfoque en el turismo en Mendoza. "
            "Responde en el mismo idioma que el usuario."
        )

        # 1. Preparar el historial de mensajes
        messages = [{"role": "system", "content": system_instruction}]
        messages.extend(historial_mensajes.copy())

        try:
            # 2. Llamada a la IA (sin herramientas)
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.7
            )

            respuesta_texto = completion.choices[0].message.content.strip()

            return {"respuesta": respuesta_texto}

        except Exception as e:
            return {"error": f"Error al llamar a OpenRouter: {e}"}