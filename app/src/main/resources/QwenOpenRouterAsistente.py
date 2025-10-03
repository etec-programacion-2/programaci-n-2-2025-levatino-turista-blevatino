import os
from openai import OpenAI
import sys # Importar para forzar la salida del script

class QwenOpenRouterAsistente:
    def __init__(self):
        # 1. Lee la API Key de forma segura desde una variable de entorno.
        self.api_key = os.getenv("OPENROUTER_API_KEY")
        if not self.api_key:
            raise ValueError("La variable de entorno 'OPENROUTER_API_KEY' no está configurada.")

        # 2. Configurar el cliente para OpenRouter.
        # Usa el cliente OpenAI con la URL base de OpenRouter.
        self.client = OpenAI(
            api_key=self.api_key,
            base_url="https://openrouter.ai/api/v1"
        )

        # 3. Definir el modelo Qwen de OpenRouter.
        self.model_name = "qwen/qwen3-235b-a22b-2507" # El modelo que seleccionaste

        # 4. Ejecuta la verificación inicial.
        self._perform_initial_check()

    def _perform_initial_check(self):
        """
        Envía una pregunta trivial a Qwen (vía OpenRouter) para verificar la conectividad y la validez de la clave.
        """
        print("Realizando verificación inicial de la API de OpenRouter...")
        try:
            # Cuerpo de la petición de verificación
            messages = [{"role": "user", "content": "Responde OK"}]

            # Realiza la petición
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.1
            )

            respuesta_texto = completion.choices[0].message.content.strip().upper()

            # Verificar si la respuesta es válida
            if "OK" in respuesta_texto:
                print("Verificación inicial EXITOSA. El servidor está listo para recibir peticiones.")
                return
            else:
                # Si la llamada a la API funcionó pero la respuesta fue extraña.
                raise Exception(f"Verificación fallida: Respuesta inesperada del modelo: {respuesta_texto}")

        except Exception as e:
            # Capturar errores de red o de API (clave inválida, modelo no encontrado, etc.)
            print("--- DIAGNÓSTICO DE FALLO ---")
            print(f"FALLO CRÍTICO DE CONEXIÓN CON OPENROUTER: {e}")
            print("-----------------------------")
            # Forzamos la salida si la inicialización falló.
            raise Exception("Fallo en la verificación inicial. No se puede iniciar el servicio de IA.")

    def obtener_respuesta(self, prompt: str):
        """
        Se conecta a la API de OpenRouter para obtener una respuesta.
        """
        try:
            messages = [{"role": "user", "content": prompt}]

            # Envía la pregunta al modelo
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                temperature=0.7,
                max_tokens=2048 # Límite generoso para asegurar una buena respuesta
            )

            respuesta_texto = completion.choices[0].message.content

            # Devuelve el texto de la respuesta.
            return {"respuesta": respuesta_texto}
        except Exception as e:
            # Maneja errores de la API.
            return {"error": f"Error al llamar a OpenRouter: {e}"}
