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