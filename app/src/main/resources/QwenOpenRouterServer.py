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
# Ruta del servidor para recibir las peticiones de enriquecimiento
# -----------------------------------------------------------------------
@app.route('/ask', methods=['POST'])
def ask_qwen():
    # Validación: Ahora se esperan las claves 'lugar_nombre' y 'descripcion_actual'
    if not request.is_json or 'lugar_nombre' not in request.json or 'descripcion_actual' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaban las claves 'lugar_nombre' y 'descripcion_actual'."}), 400

    if asistente_ia is None:
        return jsonify({"error": "El servicio de IA no está configurado (Inicialización fallida)."}), 503

    # Extracción de datos
    lugar_nombre = request.json['lugar_nombre']
    descripcion_actual = request.json['descripcion_actual']

    print(f"Petición de enriquecimiento recibida para: {lugar_nombre}")

    # Llamada a la nueva función de enriquecimiento
    respuesta = asistente_ia.enriquecer_lugar_turistico(lugar_nombre, descripcion_actual)

    # Manejo de errores
    if 'error' in respuesta:
        print(f"Error interno de OpenRouter: {respuesta['error']}")
        return jsonify(respuesta), 500

    # Éxito: devuelve la descripción enriquecida
    return jsonify(respuesta)

if __name__ == '__main__':
    # Ejecutamos el servidor en el puerto 5000 y lo hacemos accesible externamente
    app.run(host='0.0.0.0', port=5000)