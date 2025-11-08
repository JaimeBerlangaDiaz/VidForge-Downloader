# VidForge Downloader

Aplicación GUI simple para descargar vídeos y audio de plataformas online utilizando yt-dlp como motor.

**Autor:** Jaime Berlanga Diaz
**Curso:** Desarrollo de Interfaces - DI01 - 2024/2025

---

## Recursos Utilizados

Para el desarrollo de esta tarea, se han utilizado los siguientes recursos principales, además de los proporcionados en la unidad:

* **yt-dlp (Recurso Externo):** Herramienta de línea de comandos fundamental para la descarga.
  * *Enlace:* <https://github.com/yt-dlp/yt-dlp>

* **ffmpeg (Recurso Externo):** Necesario para el post-procesado (conversión de formatos, extracción de audio).
  * *Enlace:* <https://ffmpeg.org/>

* **Homebrew (macOS) (Recurso Externo):** Para la instalación y gestión de 'yt-dlp' y 'ffmpeg' en el entorno de desarrollo de macOS.
  * *Enlace:* <https://brew.sh/index_es>

* **NetBeans IDE y JDK 24:** El entorno de desarrollo y el kit de Java proporcionados por el curso.

* **Asistente AI (Gemini) (LLM):** Se utilizó este LLM como asistente de "pair programming".
  * *Propósito:* Guiar el desarrollo paso a paso, generar los bloques de código para los conceptos listados abajo, depurar errores de lógica (ej. 'protected' vs 'public' en 'SwingWorker', typos en comandos de 'yt-dlp') y solucionar problemas específicos del entorno (ej. bug de 'Cmd+V' en macOS).

---

## Citas de Código y Conceptos Aplicados

La mayor parte del código de lógica no fue copiado directamente, sino implementado basándose en los siguientes conceptos estándar de Java y Swing:

1.  **Ejecución de Procesos Externos ('ProcessBuilder'):**
    * **Propósito:** Es el "motor" que permite a Java ejecutar el comando 'yt-dlp' en la terminal.
    * **Código Aplicado:** La lógica de 'new ProcessBuilder(command)', 'pb.redirectErrorStream(true)', 'pb.start()', y la lectura de 'InputStreamReader' en un 'BufferedReader' para capturar la salida de la consola.
    * **Concepto Base (Tutorial):** <https://www.baeldung.com/java-process-builder>

2.  **Concurrencia en Swing ('SwingWorker'):**
    * **Propósito:** Evitar que la interfaz gráfica (GUI) se "congele" durante la descarga (que es una tarea larga).
    * **Código Aplicado:** Toda la clase 'DownloadWorker' es una implementación de 'SwingWorker', usando 'doInBackground()' para la descarga, 'publish()' y 'process()' para actualizar el 'JTextArea' y la 'JProgressBar', y 'done()' para mostrar el 'JOptionPane' final y reactivar el botón.
    * **Concepto Base (Tutorial):** <https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html>

3.  **Atajo de Teclado Pegar (Cmd+V) en macOS:**
    * **Propósito:** Arreglar un bug conocido de Swing en macOS donde 'Cmd+V' no funciona para pegar en un 'JTextField'.
    * **Código Aplicado:** El bloque de código en el constructor de 'MainViewPanel' que usa 'KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK)', 'getActionMap()', y 'getInputMap()'.
    * **Concepto Base (Hilo de StackOverflow):** <https://stackoverflow.com/questions/2114268/how-to-implement-cut-copy-paste-in-a-java-swing-application-on-mac-os-x>

4.  **Selector de Archivos ('JFileChooser'):**
    * **Propósito:** Permitir al usuario seleccionar la ruta de 'yt-dlp' y la carpeta de guardado.
    * **Código Aplicado:** El código en los botones 'btnBuscarYtDlp' y 'btnBuscarTemporales' en 'PreferenciasPanel', usando 'setFileSelectionMode(JFileChooser.FILES_ONLY)' y 'setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)'.
    * **Concepto Base (Tutorial):** <https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html>

---

## Problemas Encontrados y Soluciones

Durante el desarrollo surgieron varios inconvenientes:

1.  **Configuración Inicial (NetBeans + Git):** Confusión al crear el proyecto Maven dentro del repositorio Git clonado (problemas con 'mavenproject3', carpetas anidadas y falta de la carpeta '.git').
    * **Solución:** Re-clonar el repositorio en una carpeta limpia, restaurar la carpeta 'src' manualmente y corregir la estructura de carpetas.

2.  **Autenticación de Git con GitHub:** Error al clonar, ya que GitHub no acepta contraseñas.
    * **Solución:** Crear un **Token de Acceso Personal (PAT)** en GitHub con el permiso ('scope') **'repo'** y usarlo como contraseña.

3.  **Componentes de Menú Incorrectos:** Se usaron 'JCheckBoxMenuItem' en lugar de 'JMenuItem', lo que borró el código de los eventos al corregirlo.
    * **Solución:** Reemplazar los componentes y volver a añadir el código de los eventos ('actionPerformed').

4.  **Refactorización del 'DownloadWorker':** Mover la clase interna 'DownloadWorker' a su propio archivo rompió la comunicación con la GUI.
    * **Solución:** Modificar el constructor de 'DownloadWorker' para que reciba los componentes de la GUI como parámetros y añadir métodos 'puente' ('get/setUltimoArchivoDescargado') en 'MainViewPanel'.

5.  **Barra de Progreso con MP3:** La barra de progreso solo funcionaba con '[download]' y no con '[ExtractAudio]'.
    * **Solución:** Modificar el método 'process()' para que *también* parseara el porcentaje de las líneas '[ExtractAudio]'.

6.  **Botón 'Reproducir Último Archivo' con MP3:** Fallaba porque guardaba la ruta del archivo temporal ('.webm') que 'yt-dlp' borraba.
    * **Solución:** Modificar el método 'process()' para que *priorice* la captura de la ruta final de las líneas '[ExtractAudio] Destination:' o '[Merger] Merging formats into'.

7.  **Errores en Comandos 'yt-dlp':** Se corrigieron typos en los argumentos: '-x' (no '--x'), 'Downloads' (no 'Download'), 'bestvideo[...]bestaudio/best[...]' (faltaba 'best'), y la comparación 'equals('720p')'.

---

## Incidencias / Funcionalidades Pendientes

* La opción **'Crear .m3u para listas'** ('chkCrearM3u') está en la GUI de Preferencias y se guarda/carga, pero la lógica para añadir el argumento correspondiente al comando de 'yt-dlp' **no está implementada**.

---

## Funcionalidades Extra Implementadas

* **Selección de Calidad de Vídeo:** Opciones específicas (1080p, 720p) además de 'Mejor disponible'.
* **Selección de Calidad de Audio:** Opciones 'Buena' y 'Normal' para descargas de solo audio.
* **'JComboBox' Dinámico:** El desplegable de formatos ('cmbFormato') cambia su contenido (vídeo/audio) según el estado de la casilla 'Descargar solo audio'.
* **Detección Multiplataforma:** El valor por defecto para la ruta de 'yt-dlp' se adapta a Windows ('yt-dlp.exe') o Mac/Linux ('yt-dlp').
* **Carga de Preferencias:** El panel de Preferencias muestra los valores guardados la última vez.
* **Límite de Velocidad:** La opción de 'JSpinner' para limitar la velocidad está implementada y se pasa a 'yt-dlp' (ej. '-r 500K').'JSpinner' para limitar la velocidad está implementada y se pasa a 'yt-dlp' (ej. '-r 500K').'JSpinner' para limitar la velocidad está implementada y se pasa a 'yt-dlp' (ej. '-r 500K').