import os
import sys
import json
from openai import OpenAI
from datetime import datetime
import pytz
import requests

# --- DEFINICIÓN DE FUNCIONES DE HERRAMIENTA (TOOLS) ---

def obtener_hora_actual(ubicacion: str = "Mendoza, Argentina"):
    """
    Obtiene la hora actual precisa forzando el huso horario de Mendoza (GMT-3).
    """
    tz = pytz.timezone('America/Argentina/Buenos_Aires')
    now = datetime.now(tz)
    hora_formateada = now.strftime("%I:%M:%S %p %Z")

    return json.dumps({
        "ubicacion": ubicacion,
        "hora_actual": hora_formateada
    })

def obtener_clima_actual(ciudad: str):
    """
    Obtiene el resumen del clima (temperatura, condiciones) para la ciudad de Mendoza.
    Nota: Esta es una implementación SIMULADA.
    """
    if "mendoza" in ciudad.lower():
        temperatura = "28°C"
        sensacion_termica = "27°C"
        condicion = "Soleado con nubes dispersas"
        humedad = "45%"

        # NOTA: La herramienta devuelve datos estructurados en JSON
        return json.dumps({
            "ciudad": "Mendoza",
            "temperatura": temperatura,
            "sensacion_termica": sensacion_termica,
            "condicion": condicion,
            "humedad": humedad
        })
    else:
        return json.dumps({
            "error": "La herramienta de clima solo está configurada para obtener datos de Mendoza, Argentina."
        })

# --- DESCRIPCIONES DE LAS HERRAMIENTAS PARA EL MODELO ---

TOOLS = [
    {
        "type": "function",
        "function": {
            "name": "obtener_hora_actual",
            "description": "Obtiene la hora actual precisa del servidor para ayudar al usuario.",
            "parameters": {
                "type": "object",
                "properties": {
                    "ubicacion": {
                        "type": "string",
                        "description": "La ciudad de la que el usuario quiere saber la hora (predeterminado: Mendoza, Argentina)."
                    }
                },
                "required": [],
            },
        }
    },
    {
        "type": "function",
        "function": {
            "name": "obtener_clima_actual",
            "description": "Obtiene la temperatura y las condiciones climáticas actuales. Útil si el usuario pregunta por el clima.",
            "parameters": {
                "type": "object",
                "properties": {
                    "ciudad": {
                        "type": "string",
                        "description": "El nombre de la ciudad para obtener el clima (debe ser Mendoza)."
                    }
                },
                "required": ["ciudad"],
            },
        }
    }
]

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

    def _handle_tool_call(self, messages: list, tool_call):
        """
        Ejecuta la función solicitada por el modelo y realiza la segunda llamada a la IA.
        """
        function_name = tool_call.function.name
        function_args = json.loads(tool_call.function.arguments)

        available_functions = {
            "obtener_hora_actual": obtener_hora_actual,
            "obtener_clima_actual": obtener_clima_actual,
        }

        if function_name not in available_functions:
            return {"error": f"Función desconocida solicitada: {function_name}"}

        function_to_call = available_functions[function_name]

        try:
            function_response_json = function_to_call(**function_args)
        except Exception as e:
            function_response_json = json.dumps({"error": f"Error al ejecutar la función {function_name}: {str(e)}"})

        # Paso 1: Añadir la respuesta de la llamada a la herramienta al historial
        messages.append({
            "tool_call_id": tool_call.id,
            "role": "tool",
            "name": function_name,
            "content": function_response_json,
        })

        # --- CORRECCIÓN CRÍTICA: Prompter más específico para evitar sintaxis de código ---
        messages.append({
            "role": "system",
            "content": (
                "Basándote ÚNICAMENTE en el contenido del rol 'tool', genera la respuesta FINAL al usuario. "
                "Transforma la información JSON recibida en una respuesta conversacional y amigable. "
                "ES CRÍTICO: NO uses llaves {}, corchetes [], sintaxis de código o de plantilla (como `{{...}}` o `$().`) en tu respuesta. "
                "Solo texto plano y cortés."
            )
        })
        # -------------------------

        # Paso 2: Segunda llamada a la IA con la respuesta de la herramienta y la nueva instrucción
        second_response = self.client.chat.completions.create(
            model=self.model_name,
            messages=messages,
            tools=TOOLS, # Aunque no debería usar herramientas aquí, la mantenemos por consistencia
            temperature=0.7,
        )

        # Devolver el contenido de la segunda respuesta (la respuesta final conversacional)
        return {"respuesta": second_response.choices[0].message.content.strip()}


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
        Responde preguntas generales sobre turismo, manteniendo la memoria, aplicando restricción
        de contexto y utilizando herramientas (Tool Calling) para clima/hora.
        """
        # Instrucción estricta para la primera llamada.
        system_instruction = (
            "Eres un asistente virtual experto en turismo en Mendoza, Argentina. "
            "Usa la información de tu historial para mantener el contexto. "
            "DEBES USAR las herramientas 'obtener_hora_actual' y 'obtener_clima_actual' cuando el usuario lo solicite. "
            "Si utilizas una herramienta, NO respondas conversacionalmente en la primera respuesta. "
            "Debes responder ÚNICAMENTE sobre viajes, turismo, o consejos relevantes. "
            "Si la pregunta es irrelevante, pide amablemente al usuario que se enfoque en el turismo en Mendoza. "
            "Responde en el mismo idioma que el usuario. NUNCA menciones las herramientas disponibles en tu respuesta."
        )

        # 1. Preparar el historial de mensajes
        messages = [{"role": "system", "content": system_instruction}]
        messages.extend(historial_mensajes.copy())

        try:
            # 2. Primera llamada a la IA con las herramientas disponibles
            completion = self.client.chat.completions.create(
                model=self.model_name,
                messages=messages,
                tools=TOOLS,
                temperature=0.7
            )

            # 3. Verificar si el modelo decidió usar una herramienta
            tool_calls = completion.choices[0].message.tool_calls

            if tool_calls:
                print(f"[*] Modelo solicitó llamar a {tool_calls[0].function.name}...")
                return self._handle_tool_call(messages, tool_calls[0])

            # 4. Si no hay llamada a herramienta, es una respuesta normal
            respuesta_texto = completion.choices[0].message.content.strip()

            return {"respuesta": respuesta_texto}

        except Exception as e:
            return {"error": f"Error al llamar a OpenRouter: {e}"}