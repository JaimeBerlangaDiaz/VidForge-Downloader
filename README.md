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

---

## IV. Citas y Recursos Adicionales

1.  **Librerías de Persistencia y Concurrencia:**
    * **Jackson:** [Tutorial básico de Jackson ObjectMapper (Baeldung)](https://www.baeldung.com/jackson-objectmapper-tutorial)
    * **SwingWorker (Concurrencia):** [Documentación oficial de Oracle sobre SwingWorker](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html)
2.  **Herramientas Externas:** `yt-dlp`, `ffmpeg`, Homebrew (macOS).
    * [yt-dlp](https://github.com/yt-dlp/yt-dlp), [ffmpeg](https://ffmpeg.org/)
3.  **Asistencia de IA:** Se utilizó **Google Gemini** (IA) para la depuración de errores, validación de sintaxis.
---

## V. Instrucciones de Construcción y Próximos Pasos

* **Requisitos:** JDK 25, Maven (configurado en el PATH).
* **Construcción:** Clic derecho en el proyecto -> **Clean and Build**.
* **Próximos Pasos:** El proyecto está listo para la implementación de la **Parte 2: Creación del Componente JavaBean de Polling** y la **Parte 3: Integración**.