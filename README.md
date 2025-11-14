_Nombre: Bruno Levatino_
## Instrucciones de Ejecución

### 1. Requisitos

* **JDK 21.**
* **Python 3.8**.

### 2. Configuración del Entorno

1.  **Clonar/Descargar el Proyecto:**
    ```bash
    git clone git@github.com:etec-programacion-2/programaci-n-2-2025-levatino-turista-blevatino.git
    cd programaci-n-2-2025-levatino-turista-blevatino
    ```

2.  **Instalar Dependencias de Python:**
    ```bash
    rm -rf venv
    
    python3 -m venv venv
    
    source venv/bin/activate
    
    pip install openai Flask dotenv
    ```

3.  **Configurar Variables de Entorno y ejecutar Python:**
    ```bash
    export OPENROUTER_API_KEY="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" && python3 ./app/src/main/resources/QwenOpenRouterServer.py
    ```

4. **Ejecutar Kotlin (En una nueva terminal):**
    ```bash
   ./gradlew run
    ```

5. **Abrir la página web:**
   http://0.0.0.0:8080
