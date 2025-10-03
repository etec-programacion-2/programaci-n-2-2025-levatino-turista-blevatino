import os
from flask import Flask, request, jsonify
import sys

# Importa la clase del archivo con la lógica de la IA
from QwenOpenRouterAsistente import QwenOpenRouterAsistente

# --- Inicialización de la aplicación Flask ---
app = Flask(__name__)

# Inicialización del asistente.
try:
    asistente_ia = QwenOpenRouterAsistente()
except Exception as e:
    asistente_ia = None
    print("\n******************************************************************")
    print(f"ERROR: No se pudo inicializar el Asistente IA. Causa: {e}")
    print("El servidor NO se iniciará hasta que se resuelva este problema.")
    print("******************************************************************")
    sys.exit(1)

# -----------------------------------------------------------------------
# Ruta del servidor para recibir las preguntas
# -----------------------------------------------------------------------
@app.route('/ask', methods=['POST'])
def ask_qwen():
    if not request.is_json or 'pregunta' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaba la clave 'pregunta'."}), 400

    if asistente_ia is None:
        # Esto solo debería ocurrir si el error de inicialización no forzó el sys.exit(1)
        return jsonify({"error": "El servicio de IA no está configurado (Inicialización fallida)."}), 503

    pregunta_recibida = request.json['pregunta']
    print(f"Pregunta recibida: {pregunta_recibida}")

    respuesta = asistente_ia.obtener_respuesta(pregunta_recibida)

    # Si la respuesta contiene un error, se propaga con un 500.
    if 'error' in respuesta:
        print(f"Error interno de OpenRouter: {respuesta['error']}")
        return jsonify(respuesta), 500

    # Éxito
    return jsonify(respuesta)

if __name__ == '__main__':
    # Ejecutamos el servidor en el puerto 5000 y lo hacemos accesible externamente
    app.run(host='0.0.0.0', port=5000)