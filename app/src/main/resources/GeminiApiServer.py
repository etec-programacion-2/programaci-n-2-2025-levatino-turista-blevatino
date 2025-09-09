from flask import Flask, request, jsonify
from GeminiApiChat import GeminiAsistente # Importa la clase del otro archivo

app = Flask(__name__)

# Inicializamos el asistente fuera de la ruta para que solo se configure una vez.
try:
    asistente_ia = GeminiAsistente()
except ValueError as e:
    asistente_ia = None
    print(f"Error: {e}")

@app.route('/ask', methods=['POST'])
def ask_gemini():
    # Si la configuración falló, devuelve un error 503.
    if asistente_ia is None:
        return jsonify({"error": "El servicio de IA no está configurado."}), 503

    # Verifica si la solicitud es JSON y si contiene la clave 'pregunta'.
    if not request.is_json or 'pregunta' not in request.json:
        return jsonify({"error": "Petición JSON inválida. Se esperaba la clave 'pregunta'."}), 400

    pregunta_recibida = request.json['pregunta']
    print(f"Pregunta recibida: {pregunta_recibida}")

    # Llama a la lógica de la IA y obtiene la respuesta en un diccionario.
    respuesta = asistente_ia.obtener_respuesta(pregunta_recibida)

    # Si la respuesta contiene un error, se envía el código 500.
    if 'error' in respuesta:
        return jsonify(respuesta), 500

    return jsonify(respuesta)

if __name__ == '__main__':
    # Ejecutamos el servidor en el puerto 5000 y lo hacemos accesible externamente
    app.run(host='0.0.0.0', port=5000)