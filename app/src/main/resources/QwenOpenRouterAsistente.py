import os
from openai import OpenAI
import sys

class QwenOpenRouterAsistente:
    def __init__(self):
        # 1. Lee la API Key de forma segura desde una variable de entorno.
        self.api_key = os.getenv("OPENROUTER_API_KEY")
        if not self.api_key:
            raise ValueError("La variable de entorno 'OPENROUTER_API_KEY' no está configurada.")

        # 2. Configurar el cliente para OpenRouter.
        self.client = OpenAI(
            api_key=self.api_key,
            base_url="https://openrouter.ai/api/v1",
            timeout=60.0 # Timeout aumentado a 60 segundos
        )

        # 3. Definir el modelo.
        self.model_name = "mistralai/mixtral-8x7b-instruct"

        # 4. Ejecuta la verificación inicial.
        self._perform_initial_check()

    def _perform_initial_check(self):
        """
        Envía una pregunta trivial para verificar la conectividad y la validez de la clave.
        """
        print("Realizando verificación inicial de la API de OpenRouter...")
        try:
            messages = [{"role": "user", "content": "Responde 'OK' para confirmar la conexión."}]

            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.1
            )

            respuesta_texto = completion.choices[0].message.content.strip().upper()

            if "OK" in respuesta_texto:
                print("Verificación inicial EXITOSA. El servidor está listo para recibir peticiones.")
                return
            else:
                raise Exception(f"Verificación fallida: Respuesta inesperada del modelo: {respuesta_texto}")

        except Exception as e:
            print("--- DIAGNÓSTICO DE FALLO ---")
            print(f"FALLO CRÍTICO DE CONEXIÓN CON OPENROUTER: {e}")
            print("-----------------------------")
            raise Exception("Fallo en la verificación inicial. No se puede iniciar el servicio de IA.")

    def enriquecer_lugar_turistico(self, lugar_nombre: str, descripcion_actual: str):
        """
        Genera un prompt específico para enriquecer la descripción y OBLIGA el etiquetado.
        """
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

    def obtener_respuesta_generica(self, historial_mensajes: list): # <-- CAMBIO 1: Recibe el historial
        """
        Responde preguntas generales sobre turismo, manteniendo la memoria y aplicando restricción de contexto.
        """
        # Instrucción del sistema para limitar el alcance de las preguntas y establecer el rol
        system_instruction = (
            "Eres un asistente virtual experto en turismo, viajes y lugares de interés de Sudamérica. "
            "DEBES mantener el contexto de la conversación anterior provista en el historial. "
            "Debes responder preguntas ÚNICAMENTE sobre viajes, lugares turísticos, temporadas, "
            "clima, o consejos relacionados con el tema. "
            "Si el usuario hace una pregunta no relacionada (ej. matemáticas, física cuántica), "
            "debes responder amablemente que tu enfoque está en el turismo."
        )

        # CAMBIO 2: Crear la lista de mensajes con la instrucción del sistema al inicio
        # Los mensajes del historial deben tener el formato {"role": "user"/"assistant", "content": "..."}
        messages = [{"role": "system", "content": system_instruction}]
        messages.extend(historial_mensajes) # <-- CAMBIO 3: Añade el historial

        try:
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages, # <-- CAMBIO 4: Envía el historial completo
                temperature=0.7
            )
            respuesta_texto = completion.choices[0].message.content.strip()

            return {"respuesta": respuesta_texto}

        except Exception as e:
            return {"error": f"Error al llamar a OpenRouter: {e}"}