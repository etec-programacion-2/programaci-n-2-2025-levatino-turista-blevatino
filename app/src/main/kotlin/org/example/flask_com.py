from flask import Flask, request, jsonify
from gemini_chat import get_gemini_response # Importamos la función del otro archivo

app = Flask(__name__)

# -----------------------------------------------------------------------
# Ruta del servidor para recibir las preguntas
# -----------------------------------------------------------------------
@app.route('/ask', methods=['POST'])
def ask_gemini():
    # Verificamos si la solicitud es JSON y si contiene la clave 'pregunta'
    if not request.is_json or 'pregunta' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaba la clave 'pregunta'."}), 400

    pregunta_recibida = request.json['pregunta']
    print(f"Pregunta recibida: {pregunta_recibida}")

    # Llamamos a la función del módulo `gemini_api`
    respuesta, error = get_gemini_response(pregunta_recibida)

    if error:
        # Si la función devolvió un error, lo retornamos al cliente
        return jsonify({"error": error}), 500
    else:
        # Si todo fue bien, retornamos la respuesta de Gemini
        return jsonify({"respuesta": respuesta})

if __name__ == '__main__':
    # Ejecutamos el servidor en el puerto 5000
    app.run(port=5000)