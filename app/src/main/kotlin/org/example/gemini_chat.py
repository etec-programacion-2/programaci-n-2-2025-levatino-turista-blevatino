import google.generativeai as genai
import os

# --- Configuración de la API Key de Gemini ---
# Pega tu clave de la API en la siguiente línea.
genai.configure(api_key="AIzaSyBPSzWgyEnENenleqOD5K5hqWxnn_Bs5AQ")

# -----------------------------------------------------------------------
# Función para obtener una respuesta de Gemini
# -----------------------------------------------------------------------
def get_gemini_response(prompt: str):
    """
    Se conecta a la API de Gemini para obtener una respuesta basada en un prompt.
    
    Args:
        prompt (str): La pregunta o instrucción para el modelo.
        
    Returns:
        tuple: Una tupla con la respuesta (str) y un posible error (str).
               Si no hay error, el segundo valor es None.
    """
    try:
        model = genai.GenerativeModel('gemini-1.5-flash')
        response = model.generate_content(prompt)
        return response.text, None
    except Exception as e:
        return None, f"Error al llamar a Gemini: {e}"