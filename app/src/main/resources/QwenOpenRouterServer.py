import os
from flask import Flask, request, jsonify
import sys

# Importa la clase de la lógica de la IA
# NOTA: Asegúrate de que QwenOpenRouterAsistente.py esté en la misma carpeta.
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
# Ruta 1: Enriquecimiento de Lugares (/ask)
# -----------------------------------------------------------------------
@app.route('/ask', methods=['POST'])
def ask_qwen():
    # Validación: Se esperan las claves 'lugar_nombre' y 'descripcion_actual'
    if not request.is_json or 'lugar_nombre' not in request.json or 'descripcion_actual' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaban las claves 'lugar_nombre' y 'descripcion_actual'."}), 400

    if asistente_ia is None:
        return jsonify({"error": "El servicio de IA no está configurado (Inicialización fallida)."}), 503

    # Extracción de datos
    lugar_nombre = request.json['lugar_nombre']
    descripcion_actual = request.json['descripcion_actual']

    print(f"Petición de enriquecimiento recibida para: {lugar_nombre}")

    # Llamada a la función de enriquecimiento (que incluye la lógica de etiquetado)
    respuesta = asistente_ia.enriquecer_lugar_turistico(lugar_nombre, descripcion_actual)

    # Manejo de errores
    if 'error' in respuesta:
        print(f"Error interno de OpenRouter: {respuesta['error']}")
        return jsonify(respuesta), 500

    # Éxito: devuelve la descripción enriquecida (que ya lleva el prefijo: PotenciadoIA: o BaseDeDatos:)
    return jsonify(respuesta)

# -----------------------------------------------------------------------
# Ruta 2: Chat Contextual con Memoria (/chat)
# -----------------------------------------------------------------------
@app.route('/chat', methods=['POST'])
def chat_contextual():
    # CAMBIO: Ahora se espera la clave 'historial_mensajes'
    if not request.is_json or 'historial_mensajes' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaba la clave 'historial_mensajes'."}), 400

    if asistente_ia is None:
        return jsonify({"error": "El servicio de IA no está configurado (Inicialización fallida)."}), 503

    # Extracción del historial de mensajes
    historial_mensajes = request.json.get('historial_mensajes')

    # Asumimos que el último mensaje es la pregunta actual para el log
    ultima_pregunta = historial_mensajes[-1].get('content', 'Pregunta sin contenido visible') if historial_mensajes else 'Historial vacío'
    print(f"Petición de chat contextual recibida. Última pregunta: {ultima_pregunta[:50]}...")

    # Llamada a la función de chat genérica con el historial (memoria)
    respuesta = asistente_ia.obtener_respuesta_generica(historial_mensajes)

    # Manejo de errores
    if 'error' in respuesta:
        print(f"Error interno de OpenRouter en chat: {respuesta['error']}")
        return jsonify(respuesta), 500

    return jsonify(respuesta)


if __name__ == '__main__':
    # Ejecutamos el servidor en el puerto 5000 y lo hacemos accesible externamente
    app.run(host='0.0.0.0', port=5000)