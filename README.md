# VidForge Downloader: Cliente DI Media Network

Aplicación GUI simple para descargar vídeos y audio de plataformas online utilizando yt-dlp como motor. Ha sido extendida para funcionar como cliente de la **DI Media Network**, gestionando autenticación y persistencia de preferencias.

**Autor:** Jaime Berlanga Diaz
**Curso:** Desarrollo de Interfaces - DI01 - 2024/2025

---

## I. Implementación UT02: FASE DE LOGIN Y PERSISTENCIA (Parte 1 Finalizada)

### A. Autenticación y Flujo de Sesión (DI Media Network)

* **URL Base de la API:** `https://dimedianetapi9.azurewebsites.net`
* **Flujo de Sesión:** Implementación del flujo de **Auto-Login** y **Logout** guardando el token JWT en **Java Preferences**.
* **Cliente API:** **`ApiClient.java`** implementado para gestionar las peticiones de autenticación.

### B. Persistencia de Preferencias de Descarga (Jackson)

* **Librería:** **Jackson (versión 3.0.0)**.
* **Manejo de Preferencias:** El **`GestorJson.java`** se encarga de serializar y deserializar la configuración (`rutaYtDlp`, `limiteVelocidad`, `crearM3u`) en el archivo **`config.json`**.

### C. Consultas de Verificación (API Endpoints)

Se verificó el correcto funcionamiento de los *endpoints* de la DI Media Network:

| Endpoint | Método | Descripción |
| :--- | :--- | :--- |
| `/api/Users/register` | `POST` | Creación de un nuevo usuario. |
| `/api/Users/login` | `POST` | Obtención del token JWT. |
| `/api/Files/me` | `GET` | Verificación de token y obtención de archivos del usuario. |
| `/api/Files/upload` | `POST` | Subida de archivos multimedia (max 10MB). |
| `/api/Files/all` | `GET` | Obtención de todos los recursos de la red. |
| `/api/Files/{id}` | `GET` | Descarga de un archivo específico por su ID. |
| `/api/Users/{id}/nickname` | `GET` | Consulta del apodo del usuario que subió el archivo. |

---

## II. Implementación de la FASE BASE (DI01\_2)

* **Biblioteca Basada en JSON:** La tabla se carga a partir de un archivo **`log.json`**.
* **Componentes de Control:** **`MediaFileTableModel`** (para `JTable`), `JList<Object>` para filtrar por tipo de contenido, y `JComboBox<Object>` para ordenar por columna.
* **Ejecución de Procesos:** **`DownloadWorker.java`** utiliza **`SwingWorker`** para la ejecución asíncrona de `yt-dlp`.

## II.I Implementación UT02: COMPONENTE DE POLLING (Parte 2 Finalizada)

Esta fase se centró en la creación de un componente visual independiente (**Java Bean**) para la monitorización activa de la red.

### A. Componente `MediaPollerComponent` (Java Bean)
* **Desarrollo Manual:** Componente creado programáticamente heredando de `JPanel` (sin Designer).
* **Polling Activo:** Implementación de un `javax.swing.Timer` para realizar consultas periódicas a la API cada 15 segundos (configurable).
* **Feedback Visual:** Incluye un `JLabel` con un icono y texto ("Running" / "Stopped").
* **Wrappers:** Encapsula la lógica de `ApiClient`, exponiendo métodos como `login` y `getAllMedia`.

### B. Sistema de Eventos y Sincronización
* **`NewMediaEvent`:** Objeto que transporta la lista de nuevos archivos detectados.
* **`NewMediaListener`:** Interfaz para la suscripción de eventos.
* **Integración:** El componente se inicia tras el Login y, al detectar archivos, permite al usuario **recargar la biblioteca automáticamente**.

## III. Implementación UT03-04: USABILIDAD Y GESTIÓN DE ERRORES

Esta sección detalla las mejoras aplicadas siguientes:

### III.A Aspecto, Color e Iconografía
* **DarkLaf:** Integración del Look & Feel *DarkLaf* para una estética moderna y profesional.
* **Iconografía:** Inclusión de iconos dinámicos para estados de descarga y mensajes de sistema.

### III.B Affordance, Feedback y Restricciones
* **Affordance:** Uso de `HAND_CURSOR` en botones para indicar interactividad.
* **Feedback:** Implementación de `JOptionPane` informativos y barras de progreso activas durante la descarga.
* **Restricciones:** Bloqueo dinámico del botón de descarga para evitar duplicidad de procesos.
* **Feedback Contextual Dinámico en la barra de progreso** El sistema no solo muestra el progreso mediante la barra, sino que informa textualmente de la fase del proceso (Descargando, Fusionando). Para eliminar la incertidumbre del usuario ante las variaciones de velocidad o reinicios de la barra de progreso.

### III.C Gestión de Errores y Registro de Logs
* **Logs on Application Crash:** Implementación de la clase `LoggerError` para persistir fallos en `error_log.txt`.
* **Gestión Integral de Excepciones:** Se ha blindado el código en la capa de red (Login), procesos (Download), datos (GestorJson) e interfaz (Biblioteca).
---

## III. Problemas Encontrados y Soluciones (Consolidado y Referenciado)

### 1. Problemas de Compilación y Persistencia (UT02)

| Problema | Solución Implementada | Fuente Técnica |
| :--- | :--- | :--- |
| **Error de Compilación `IOException is never thrown`** | El compilador no reconocía que Jackson lanzaba la excepción. Se implementó el patrón **`throws IOException`** en `GestorJson.java` para obligar al compilador a aceptar el código. | [Manejo de Checked Exceptions (Oracle Docs)](https://docs.oracle.com/javase/tutorial/essential/exceptions/declaring.html) |
| **Conflicto de Dependencias/Caché** | El `Group ID` y `Version` de Jackson entraron en conflicto. Se resolvió mediante limpieza profunda y el ajuste explícito del `pom.xml` para la configuración de Java 25. | (Solución de entorno interno) |
| **Error de Sintaxis en Deserialización de Listas** | Fallo en la lectura de `List<MediaFile>` debido a la complejidad del tipado genérico. Se corrigió la sintaxis de la clase anónima. | [Stack Overflow: Deserializar Listas con TypeReference](https://stackoverflow.com/questions/2178229/how-to-deserialize-a-list-of-objects-with-jackson) |

### 2. Problemas de Interfaz y Lógica Base (DI01\_2)

| Problema | Solución Implementada | Fuente Técnica |
| :--- | :--- | :--- |
| **Bug de Acceso a Componentes** | `DownloadWorker` no podía re-habilitar el botón `btnDescargar`. Se implementó el método público **`setBotonDescargarHabilitado(boolean)`** para mantener el encapsulamiento. | (Patrón de diseño Adapter/Puente) |
| **Atajo de Teclado Pegar (macOS)** | El `Cmd+V` no funcionaba en `JTextField`. Se corrigió mediante la creación de un nuevo `KeyStroke` con `InputEvent.META_DOWN_MASK`. | [Stack Overflow: Implementar Cmd+V en JTextComponent](https://stackoverflow.com/questions/2114268/how-to-implement-cut-copy-paste-in-a-java-swing-application-on-mac-os-x) |
| **Ejecución Asíncrona Lenta** | El proceso `yt-dlp` bloqueaba la GUI. Se implementó **`DownloadWorker.java`** heredando de `SwingWorker`. | [Documentación oficial de Oracle sobre SwingWorker](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html) |
| **Bug de Tipado/Compilación Componentes** | El diseñador generaba errores con `JComboBox` y `JList` al inyectar objetos complejos. Se resolvió inyectando los modelos de objetos mediante código en `BibliotecaPanel.java`. | (Patrón MVC - Adaptadores) |

### 3. Problemas de la Fase de Usabilidad;
- **Escalado de Iconos de Estado:** Los iconos de éxito/error se veían desproporcionados en la tabla. Se implementó un método de reescalado dinámico a **32x32** usando `Image.SCALE_SMOOTH`.
- **Error Unreported Exception:** La llamada al token en `BibliotecaPanel` lanzaba una excepción que el catch de `IOException` no cubría. Se cambió a **`Exception` genérica** para asegurar el reporte en la interfaz.
- **Redundancia de Jackson en GestorJson:** NetBeans marcaba error al intentar capturar una excepción no detectada por el compilador. Se unificó el bloque en un `try-catch` de `Exception` para englobar fallos de serialización y de sistema.
- **Recuperabilidad del Botón:** El botón de descarga quedaba desactivado si ocurría un error antes de iniciar el Worker. Se añadió la reactivación del botón en el bloque `catch`.
- **Persistencia de Trazas:** Se implementó la clase **`LoggerError`** para evitar que los errores críticos se perdieran al cerrar la consola, volcándolos al archivo físico `error_log.txt`.

### 4. Retos Técnicos de la Parte 2 (Polling)

| Problema | Solución Implementada | Fuente Técnica |
| :--- | :--- | :--- |
| **Conversión de Tiempo** | El Timer usa milisegundos, la propiedad usa segundos. Se implementó conversión interna. | [Docs javax.swing.Timer](https://docs.oracle.com/javase/8/docs/api/javax/swing/Timer.html) |
| **Sincronización Hilos** | Conflictos de UI desde el Timer. Se utilizó `SwingUtilities.invokeLater()` en el Listener. | [Concurrencia en Swing](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html) |
---

## IV. Citas y Recursos Adicionales

1.  **Librerías de Persistencia y Concurrencia:**
    * **Jackson:** [Tutorial básico de Jackson ObjectMapper (Baeldung)](https://www.baeldung.com/jackson-objectmapper-tutorial)
    * **SwingWorker (Concurrencia):** [Documentación oficial de Oracle sobre SwingWorker](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html)

2.  **Polling y Timers (Parte 2):**
    * *Concepto:* Uso de `javax.swing.Timer` para tareas periódicas en interfaces gráficas sin bloquear el hilo principal.
    * *Referencia Oficial:* [How to Use Swing Timers (Oracle Docs)](https://docs.oracle.com/javase/tutorial/uiswing/misc/timer.html)
    * *Discusión Técnica:* [Diferencia entre javax.swing.Timer y java.util.Timer (Stack Overflow)](https://stackoverflow.com/questions/25025715/javax-swing-timer-vs-java-util-timer-inside-of-a-swing-application)
    * *Implementación:* [Cómo refrescar un componente periódicamente](https://stackoverflow.com/questions/33489705/java-using-swing-timer-to-do-a-task-every-1-100-seconds)

3. **Arquitectura de Componentes y Eventos (Parte 2):**
    * *Sistema de Eventos:* Implementación del patrón Observer mediante `EventObject` y `EventListener` para desacoplar el componente de la interfaz.
    * *Referencia:* [Writing Event Listeners (Oracle Java Tutorials)](https://docs.oracle.com/javase/tutorial/uiswing/events/index.html)

4.  **Herramientas Externas:** `yt-dlp`, `ffmpeg`, Homebrew (macOS).
    * [yt-dlp](https://github.com/yt-dlp/yt-dlp), [ffmpeg](https://ffmpeg.org/)
5.  **Asistencia de IA:** Se utilizó **Google Gemini** (IA) para la depuración de errores, validación de sintaxis.

---

## V. Instrucciones de Construcción y Próximos Pasos

* **Requisitos:** JDK 24, Maven (configurado en el PATH).
* **Construcción:** Clic derecho en el proyecto -> **Clean and Build**.

# VidForge Downloader (Entrega Final)

Este proyecto depende del componente externo `DiMediaNetComponent`.

## ⚠️ Instrucciones de Compilación (IMPORTANTE)

Para ejecutar este proyecto, debe seguir este orden estricto para que Maven encuentre las dependencias:

1.  Clone y abra el proyecto **DiMediaNetComponent**.
2.  Ejecute **Clean and Build** (o `mvn install`) para instalar el .jar en su repositorio local.
3.  Clone y abra este proyecto (**VidForge-Downloader**).
4.  Ejecute **Clean and Build**.
5.  Ejecute la aplicación (**Run**).

## Notas
- Se ha eliminado la clase `ApiClient` local para usar la del componente.
- La URL de la API ha sido actualizada a `difreenet9` (servidor nuevo).