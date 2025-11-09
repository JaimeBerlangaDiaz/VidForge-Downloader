# VidForge Downloader

Aplicación GUI simple para descargar vídeos y audio de plataformas online utilizando yt-dlp como motor.

**Autor:** Jaime Berlanga Diaz
**Curso:** Desarrollo de Interfaces - DI01 - 2024/2025

---

## I. Implementación de la Tarea DI01_2 (Modelo y persistencia de datos)

### 1. Requisitos de la Tarea DI01_2 (Componentes)
| **Clase Modelo** | Crear clase para representar el recurso. | 'MediaFile.java' (Clase principal para el log JSON) |
| **JTable** | Integración con tabla (AbstractTableModel). | 'MediaFileTableModel' implementa 'AbstractTableModel'. |
| **JList<Object>** | Usado para filtrar la biblioteca por tipo de contenido. | 'listFiltroTipo' utiliza objetos 'TipoMimeFiltro'. |
| **JComboBox<Object>**| Usado para ordenar la biblioteca por columna. | 'cmbOrdenarPor' utiliza objetos 'ColumnaOrden'. |

### 2. Funcionalidades Extra Implementadas (Finales)

Se han consolidado las funcionalidades extra en un solo punto, incluyendo la Biblioteca (DI01_2) y las de Calidad/Límite (DI01_1):

* **Biblioteca Basada en JSON:** La tabla de la biblioteca se carga a partir de un archivo 'log.json', permitiendo la gestión de un historial de descargas persistente.
* **Ordenación Bidireccional:** La selección repetida de una columna ('Nombre', 'Tamaño', 'Fecha') en el JComboBox invierte el orden (A->Z / Z->A).
* **Filtrado Activo:** La tabla se filtra automáticamente al seleccionar un tipo (Vídeos/Audios) en el JList.
* **Icono de Éxito Visual:** Se utiliza una imagen 'success_icon.png' para mostrar una confirmación de la descarga dentro del 'JOptionPane'.
* **Selección de Calidad de Vídeo/Audio:** Opciones específicas para 1080p/720p y calidad de audio (Buena/Normal).
* **'JComboBox' Dinámico:** El desplegable de formatos ('cmbFormato') cambia su contenido (vídeo/audio) según la casilla 'Descargar solo audio'.
* **Detección Multiplataforma:** El valor por defecto de 'yt-dlp' se adapta a Windows ('yt-dlp.exe') o Mac/Linux ('yt-dlp').
* **Límite de Velocidad:** La opción de 'JSpinner' para limitar la velocidad está implementada y se pasa a 'yt-dlp' (ej. '-r 500K').

---

## II. Recursos Utilizados

Para el desarrollo de esta tarea, se han utilizado los siguientes recursos principales, además de los proporcionados en la unidad:

* **yt-dlp (Recurso Externo):** Herramienta de línea de comandos fundamental para la descarga.
  * *Enlace:* <https://github.com/yt-dlp/yt-dlp>

* **ffmpeg (Recurso Externo):** Necesario para el post-procesado (conversión de formatos, extracción de audio).
  * *Enlace:* <https://ffmpeg.org/>

* **Homebrew (macOS) (Recurso Externo):** Para la instalación y gestión de 'yt-dlp' y 'ffmpeg' en el entorno de desarrollo de macOS.
  * *Enlace:* <https://brew.sh/index_es>

* **NetBeans IDE y JDK 24:** El entorno de desarrollo y el kit de Java proporcionados por el curso.

* **Asistente AI (Gemini) (LLM):** Se utilizó este LLM como asistente de consulta para validar sintaxis de patrones de diseño,confirmar la estructura del 'AbstractTableModel' y depurar errores complejos.

---

## III. Citas de Código y Conceptos Aplicados

La mayor parte del código de lógica no fue copiado directamente, sino implementado basándose en los siguientes conceptos estándar de Java y Swing:

1.  **Persistencia de Datos ('Gson'):**
    * **Propósito:** Guardar objetos Java complejos ('Configuracion', 'MediaFile') en archivos JSON ('config.json', 'log.json').
    * **Código Aplicado:** Toda la clase 'GestorJson.java' y las estructuras de datos dentro de 'MediaFile.java'.
    * **Concepto Base (Tutorial):** <https://www.baeldung.com/gson>

2.  **Ejecución de Procesos Externos ('ProcessBuilder' y 'SwingWorker'):**
    * **Propósito:** Ejecutar 'yt-dlp' de forma asíncrona y no bloquear la interfaz gráfica (GUI).
    * **Código Aplicado:** Implementación de 'DownloadWorker.java' (que usa 'SwingWorker') y la lógica de 'ProcessBuilder' para la ejecución y captura de la salida de consola.
    * **Concepto Base (Tutorial):** <https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html>

3.  **Patrón MVC - Adaptadores de Datos:**
    * **Propósito:** Conectar el modelo de datos (listas de objetos 'MediaFile') con los componentes visuales de Swing.
    * **Código Aplicado:** Implementación de 'MediaFileTableModel.java' (extiende AbstractTableModel) para la JTable y el uso de DefaultComboBoxModel y DefaultListModel para JComboBox y JList.

4.  **Atajo de Teclado Pegar (Cmd+V) en macOS:**
    * **Propósito:** Arreglar un bug conocido de Swing en macOS donde 'Cmd+V' no funciona para pegar en un 'JTextField'.
    * **Concepto Base (Hilo de StackOverflow):** <https://stackoverflow.com/questions/2114268/how-to-implement-cut-copy-paste-in-a-java-swing-application-on-mac-os-x>

5.  **Selector de Archivos ('JFileChooser'):**
    * **Propósito:** Permitir al usuario seleccionar la ruta de 'yt-dlp' y la carpeta de guardado.
    * **Concepto Base (Tutorial):** <https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html>

---

## IV. Problemas Encontrados y Soluciones (Consolidado)

1.  **Refactorización del Proyecto (Clean Code):**
    * **Problema:** Mover las clases a los paquetes `view` y `model` causó errores de `ClassNotFoundException` y problemas de visibilidad entre paquetes.
    * **Solución:** Se corrigió el `pom.xml` para actualizar la `exec.mainClass` a `berlangadiaz.vidforge.downloader.view.MainFrame` y se hicieron públicos los campos de la clase auxiliar `GestorJson.Configuracion` para permitir el acceso entre paquetes.

2.  **Bug de Persistencia:**
    * **Problema:** La configuración se perdía porque el 'config.json' se leía tarde o no se leía.
    * **Solución:** Se modificó el constructor de `MainFrame.java` para cargar los valores del `config.json` antes de la inicialización de los componentes.

3.  **Bug de Tipado/Compilación de Componentes (DI01_2):**
    * **Problema:** El Diseñador de NetBeans generaba problemas de 'incompatible types' con 'JComboBox' y 'JList' al intentar inyectar objetos complejos (`ColumnaOrden`, `TipoMimeFiltro`).
    * **Solución:** Se eliminaron los modelos de ejemplo del Diseñador y se inyectaron los modelos de objetos mediante código en el constructor de `BibliotecaPanel.java`.

4.  **Bug de Acceso a Componentes (Clean Code):**
    * **Problema:** La clase `DownloadWorker` no podía re-habilitar el botón `btnDescargar` porque era `private` en `MainViewPanel`.
    * **Solución:** Se implementó el método público **`setBotonDescargarHabilitado(boolean)`** en `MainViewPanel.java` para actuar como un método "puente" y mantener el encapsulamiento.

5.  **Bug de Layout (Visual):**
    * **Problema:** Después de la refactorización, la ventana principal aparecía en un tamaño diminuto, solo mostrando la barra de menú.
    * **Solución:** Se forzó el tamaño fijo de la ventana (`setSize(800, 600)`) y se corrigieron los `setBounds` del `panelContenedor` dentro de `MainFrame.initComponents()`.

6.  **Borrado y Reproducción de Archivos:**
    * **Problema:** El borrado de archivos y la reproducción del último archivo fallaban porque usaban rutas temporales o instancias desactualizadas de `java.io.File`.
    * **Solución:** Se corrigió el código para que siempre se obtenga la ruta final de las líneas de log de 'yt-dlp' y se usen nuevas instancias de `File` al ejecutar operaciones.

7.  **Problemas de Entorno (Antiguos):** Confusión al crear el proyecto Maven dentro del repositorio Git, autenticación de Git con GitHub (solucionado con PAT), uso de componentes de menú incorrectos y errores en comandos de 'yt-dlp'.

---

## V. Incidencias / Funcionalidades Pendientes

* La opción **'Crear .m3u para listas'** ('chkCrearM3u') está en la GUI de Preferencias y se guarda/carga, pero la lógica para añadir el argumento correspondiente al comando de 'yt-dlp' **no está implementada**.