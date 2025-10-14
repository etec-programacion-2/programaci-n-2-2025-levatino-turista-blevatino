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
        # CORRECCIÓN DEFINITIVA: Usamos un modelo gratuito, potente y estable de OpenRouter
        # para garantizar que pase la verificación (ej: Mixtral o Mistral).
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
                # Si la llamada fue exitosa pero la respuesta no es 'OK', mostramos el fallo.
                raise Exception(f"Verificación fallida: Respuesta inesperada del modelo: {respuesta_texto}")

        except Exception as e:
            print("--- DIAGNÓSTICO DE FALLO ---")
            # En este punto, si falla, ya no es por Qwen, sino por la clave general de OpenRouter
            print(f"FALLO CRÍTICO DE CONEXIÓN CON OPENROUTER: {e}")
            print("-----------------------------")
            raise Exception("Fallo en la verificación inicial. No se puede iniciar el servicio de IA.")

    def enriquecer_lugar_turistico(self, lugar_nombre: str, descripcion_actual: str):
        """
        Genera un prompt para que la IA (Mixtral) potencie la descripción de un lugar turístico.
        """
        # --- PROMPT CLAVE PARA LA TAREA (PREGUNTA PRE-HECHA) ---
        prompt = f"""
        Eres un experto guía turístico de Argentina. Tu tarea es potenciar y enriquecer una descripción básica de un lugar turístico.

        Instrucciones Clave:
        1. Utiliza un tono profesional, emocionante y persuasivo para atraer al lector.
        2. Mantén el foco en el lugar, buscando información externa si la descripción actual es muy básica (Mixtral usará su vasto conocimiento).
        3. No incluyas advertencias, precios, o información de contacto. Solo enfócate en la descripción y la experiencia.
        4. DEVUELVE SOLO la nueva descripción enriquecida, sin encabezados ni frases introductorias (ej: 'La nueva descripción es:').

        LUGAR: {lugar_nombre}
        DESCRIPCIÓN ACTUAL: {descripcion_actual}

        Tu respuesta debe ser solo la descripción potenciada:
        """
        # --- FIN DEL PROMPT ---

        try:
            messages = [{"role": "user", "content": prompt}]

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