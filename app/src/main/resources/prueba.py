import os
from openai import OpenAI

# 1. Configurar el cliente para OpenRouter
# La URL base y la clave API son específicas de OpenRouter,
# aunque el cliente 'OpenAI' se usa para la compatibilidad.

# La clave se lee automáticamente de la variable de entorno OPENROUTER_API_KEY
client = OpenAI(
    api_key=os.getenv("OPENROUTER_API_KEY"),
    base_url="https://openrouter.ai/api/v1"
)

# 2. Definir el modelo Qwen de OpenRouter
# Puedes consultar la lista de modelos de Qwen disponibles en OpenRouter.
# Ejemplo de modelos Qwen: "qwen/qwen2-72b-instruct", "qwen/qwen1.5-110b-chat", etc.
MODELO_QWEN = "qwen/qwen3-235b-a22b-2507"

# 3. Definir la conversación
messages = [
    {"role": "system", "content": "dime los lugares màs turisticos de mendoza argentina."}
]

# 4. Llamar a la API
print(f"Enviando solicitud a OpenRouter usando el modelo: {MODELO_QWEN}...")
try:
    completion = client.chat.completions.create(
        model=MODELO_QWEN,
        messages=messages,
        temperature=0.7,
        max_tokens=500
    )

    # 5. Imprimir la respuesta
    print("\n" + "="*50)
    print(f"--- Respuesta de Qwen vía OpenRouter ---")
    print("="*50)
    print(completion.choices[0].message.content.strip())
    print("="*50 + "\n")

except Exception as e:
    print(f"\nOcurrió un error al llamar a la API de OpenRouter: {e}")
    print("Asegúrate de que tu clave OPENROUTER_API_KEY esté configurada y el modelo sea correcto.")