# VidForge-Downloader

Aplicación GUI simple para descargar vídeos y audio de plataformas online utilizando yt-dlp como motor.

**Autor:** Jaime Berlanga Diaz
**Curso:** Desarrollo de Interfaces - DI01 - 2025/2026

---

## Recursos Utilizados

Para el desarrollo de esta tarea, se han utilizado los siguientes recursos principales, además de los proporcionados en la unidad:

* **yt-dlp:** Herramienta de línea de comandos para la descarga. ([https://github.com/yt-dlp/yt-dlp](https://github.com/yt-dlp/yt-dlp))
* **ffmpeg:** Necesario para el post-procesado (conversión, extracción de audio). ([https://ffmpeg.org/](https://ffmpeg.org/))
* **Homebrew (macOS):** Para la instalación y gestión de `yt-dlp` y `ffmpeg`. ([https://brew.sh/index_es](https://brew.sh/index_es))
* **NetBeans IDE 27:** Entorno de desarrollo utilizado.
* **JDK 24:** Kit de desarrollo de Java.
* **Asistente AI (Gemini):** Para guiar el desarrollo paso a paso, depurar errores de código y lógica, y proporcionar soluciones a problemas específicos.

---

## Problemas Encontrados y Soluciones

Durante el desarrollo surgieron varios inconvenientes:

1.  **Instalación de Homebrew y herramientas:** Dificultades iniciales para instalar Homebrew en macOS y configurar correctamente el PATH para poder usar los comandos `brew`, `yt-dlp` y `ffmpeg`.
    * **Solución:** Reinstalar Homebrew siguiendo los pasos exactos, incluyendo la ejecución de los comandos "Next steps:" para añadir `brew` al `.zprofile` y reiniciar la terminal.

2.  **Autenticación de Git con GitHub:** Error al intentar clonar el repositorio privado usando la contraseña habitual. GitHub ya no la admite para operaciones Git.
    * **Solución:** Crear un **Token de Acceso Personal (PAT)** en la configuración de desarrollador de GitHub, asegurándose de marcar el permiso (`scope`) **`repo`**, y usar ese token en lugar de la contraseña al clonar o hacer `push`.

3.  **Configuración Inicial (NetBeans + Git):** Confusión al crear el proyecto Maven dentro del repositorio Git clonado. NetBeans usaba un nombre por defecto (`mavenproject3`) y a veces no reconocía la carpeta clonada correctamente. Además, hubo problemas con la estructura de carpetas (carpetas anidadas) y la falta de la carpeta `.git`.
    * **Solución:** Borrar proyectos/carpetas incorrectas, reiniciar NetBeans, asegurarse de seleccionar la carpeta clonada como "Project Location" al crear el proyecto. En el caso de la falta de `.git`, se tuvo que **re-clonar** el repositorio y **restaurar** la carpeta `src` manualmente, corrigiendo luego la estructura de carpetas anidadas moviendo el contenido al nivel superior y borrando la carpeta interior vacía.

4.  **Componentes de Menú Incorrectos:** Se usaron `JCheckBoxMenuItem` en lugar de `JMenuItem` para las opciones del menú, lo que provocó que se borrara el código de los eventos al corregirlo.
    * **Solución:** Reemplazar los componentes por `JMenuItem` en el Diseñador y volver a añadir el código de los eventos (`actionPerformed`) para "Salir", "Preferencias" y "Acerca de..." haciendo doble clic en los nuevos items y pegando el código correspondiente.

5.  **Pegar Texto (Cmd+V) en macOS:** El `JTextField` de la URL no permitía pegar con `Cmd+V`.
    * **Solución:** Añadir código específico en el constructor del `MainViewPanel` para registrar manualmente el `KeyStroke` de `Cmd+V` y asociarlo a la acción "paste".

6.  **Error `protected void done()` en `SwingWorker`:** El método `done()` daba error si se declaraba `protected` dentro de la clase interna.
    * **Solución:** Cambiar el modificador a `public void done()`.

7.  **Barra de Progreso con MP3:** La barra solo se actualizaba durante la descarga (`[download]`), no durante la conversión (`[ExtractAudio]`).
    * **Solución:** Modificar el método `process()` para que *también* parseara el porcentaje de las líneas `[ExtractAudio]`, usando `lastIndexOf` para encontrar el número de forma más robusta.

8.  **Botón "Reproducir Último Archivo" con MP3:** Fallaba porque guardaba la ruta del archivo temporal (`.webm`, `.m4a`) que `yt-dlp` borraba. También hubo un error inicial por declarar la variable `ultimoArchivoDescargado` dos veces.
    * **Solución:** Corregir la declaración duplicada y modificar el método `process()` para que *priorice* la captura de la ruta final de las líneas `[ExtractAudio] Destination:` o `[Merger] Merging formats into`, usando `MainViewPanel.this.ultimoArchivoDescargado` para asegurar el ámbito correcto.

9.  **Errores en Comandos `yt-dlp`:** Se corrigieron typos en los argumentos: `-x` (no `--x`), `Downloads` (no `Download`), `bestvideo[...]bestaudio/best[...]` (faltaba `best`), y la comparación `equals("720p")` (faltaba la 'p'). También se añadió `--ffmpeg-location` para que `yt-dlp` encuentre `ffmpeg`.


---

## Incidencias / Funcionalidades Pendientes

* La opción **"Crear .m3u para listas"** (`chkCrearM3u`) está en la GUI de Preferencias y se guarda/carga, pero la lógica para añadir el argumento correspondiente (ej. `--yes-playlist --write-playlist-metafiles`) al comando de `yt-dlp` **no está implementada** en `btnDescargarActionPerformed`.

---

## Funcionalidades Extra Implementadas

* **`JComboBox` Dinámico:** El desplegable de formatos cambia su contenido (vídeo/audio) según el estado de la casilla "Descargar solo audio", evitando combinaciones inválidas.
* **Detección Multiplataforma:** El valor por defecto para la ruta de `yt-dlp` se adapta a Windows (`yt-dlp.exe`) o Mac/Linux (`yt-dlp`).
* **Carga de Preferencias:** El panel de Preferencias muestra los valores guardados la última vez.
* **Límite de Velocidad:** La opción para limitar la velocidad de descarga está implementada y se pasa a `yt-dlp` con el argumento `-r`.
