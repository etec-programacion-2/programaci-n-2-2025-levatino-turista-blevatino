import google.generativeai as genai
import os
from flask import Flask, request, jsonify

# --- Configuración de la API Key de Gemini ---
# Pega tu clave de la API en la siguiente línea.
genai.configure(api_key="AIzaSyBPSzWgyEnENenleqOD5K5hqWxnn_Bs5AQ")

app = Flask(__name__)
model = genai.GenerativeModel('gemini-1.5-flash')

# ----------------------------------------------------
# 1. Definimos una ruta para recibir las preguntas
# ----------------------------------------------------
@app.route('/ask', methods=['POST'])
def ask_gemini():
    # Verificamos si la solicitud es JSON y si contiene la clave 'pregunta'
    if not request.is_json or 'pregunta' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaba la clave 'pregunta'."}), 400

    pregunta_recibida = request.json['pregunta']
    print(f"Pregunta recibida desde Kotlin: {pregunta_recibida}")

    # ------------------------------------------------
    # 2. Enviamos la pregunta a la API de Gemini
    # ------------------------------------------------
    try:
        respuesta = model.generate_content(pregunta_recibida)
        
        # --------------------------------------------
        # 3. Preparamos y enviamos la respuesta como JSON
        # --------------------------------------------
        return jsonify({"respuesta": respuesta.text})

    except Exception as e:
        print(f"Error al llamar a Gemini: {e}")
        return jsonify({"error": f"Error del servidor: {e}"}), 500

if __name__ == '__main__':
    # El servidor se ejecuta en http://127.0.0.1:5000
    app.run(port=5000)