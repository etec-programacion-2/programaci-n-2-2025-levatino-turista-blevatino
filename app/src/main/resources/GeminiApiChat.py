import os
import google.generativeai as genai

class GeminiAsistente:
    def __init__(self):
        # Lee la API Key de forma segura desde una variable de entorno.
        self.api_key = os.getenv("GEMINI_API_KEY")
        if not self.api_key:
            raise ValueError("La variable de entorno 'GEMINI_API_KEY' no está configurada.")

        # Configura la librería con la clave de API.
        genai.configure(api_key=self.api_key)
        self.model = genai.GenerativeModel('gemini-1.5-flash')

    def obtener_respuesta(self, prompt: str):
        """
        Se conecta a la API de Gemini para obtener una respuesta basada en un prompt.
        Devuelve un diccionario con la respuesta o un error.
        """
        try:
            # Envía la pregunta al modelo y obtiene la respuesta.
            response = self.model.generate_content(prompt)

            # Devuelve el texto de la respuesta.
            return {"respuesta": response.text}
        except Exception as e:
            # Maneja errores de la API.
            return {"error": f"Error al llamar a Gemini: {e}"}