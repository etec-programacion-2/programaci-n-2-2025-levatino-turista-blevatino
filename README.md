_Nombre: Bruno Levatino_

**Issue 1.1: Creación de las data class y enum para el modelo de negocio:**

Usé data class para LugarTuristico y Actividad porque su función principal es almacenar datos, lo que va a servir en un futuro para procesar todos los lugares y actividades.

**Issue 1.2: Crear la interfaz LugarTuristicoRepository:**

Es preferible utilizar una interfaz ya que en el caso de querer modificar de donde obtengo los datos es algo facil y no debo modificar todo lo anterior.

**Issue 1.3: Implementar JsonLugarTuristicoRepository:**

Se puede remplazar por otra clase gracias a LugarTuristicoRepository que se encarga de hacer independiente a la misma, por lo que no afecta el funcionamiento de lo anterior

**Issue 1.4: Implementar ServicioRecomendaciones:**

La lógica de filtrado se coloca en el servicio por el Principio de Responsabilidad Única.

-El Repositorio solo se encarga del acceso a los datos.

-La Capa de UI solo se encarga de la presentación.

-El Servicio se encarga de las reglas de negocio.

Al poner la lógica de filtrado en el servicio, esta es reutilizable y el sistema es más modular, ya que cada componente solo tiene una razón para cambiar.

**Issue 2.2: Crear la interfaz AsistenteIA:**

La interfaz AsistenteIA aísla nuestra aplicación de los detalles de una librería o API específica de OpenAI.

-Nuestra lógica de negocio no depende directamente de una clase concreta, sino de una abstracción.

-Permite cambiar a otro proveedor de IA o usar una versión de prueba (mock) para testear, sin tener que modificar el resto de la aplicación.

**Issue 2.3: Implementar GeminiAsistente:**

Primero, inicio el servidor de Python en una terminal. Esto prepara mi servicio para recibir peticiones.

`python3 -m venv venv`

`source venv/bin/activate`

`pip install google-generativeai Flask`

`export OPENROUTER_API_KEY="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" && python3 ./app/src/main/resources/QwenOpenRouterServer.py`

Luego, ejecuto la aplicación de Kotlin en una segunda terminal.
`./gradlew run`

Mi aplicación de Kotlin enviará una petición a Python, que a su vez se comunicará con la API de Gemini. La respuesta de Gemini viajará de regreso por el mismo camino.

(_Dentro de App.tk hay una prueba_)

Para demostrar la seguridad, la API Key no está en el código, sino que la configuro en una variable de entorno. Esto la mantiene a salvo y hace que mi código sea flexible.

**Issue 2.4: Crear la clase ControladorPrincipal:**

(_Dentro de App.tk hay una prueba_)

**Issue 3.2: Crear la clase VistaConsola:**


La VistaConsola no accede a los servicios porque su único trabajo es la presentación. No debe saber cómo se obtienen los datos o cómo funciona la IA; solo se preocupa por leer y mostrar información al usuario.

El ControladorPrincipal actúa como intermediario para mantener estas dos partes del sistema separadas y organizadas.