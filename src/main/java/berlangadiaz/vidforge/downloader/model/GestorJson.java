package berlangadiaz.vidforge.downloader.model;

import java.util.prefs.Preferences;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.Reader;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Clase que gestiona la persistencia de datos del proyecto VidForge. Incluye
 * configuración de yt-dlp, historial local (log.json) y el token JWT
 * (Preferences).
 * 
 * @author Jaime Berlanga Diaz
 */
public class GestorJson {

    // --- PERSISTENCIA DEL TOKEN JWT (Función "Recordarme") ---
    // Objeto estático para acceder al sistema de preferencias de Java.
    private static final Preferences prefs
            = Preferences.userNodeForPackage(GestorJson.class);
    private static final String PREF_TOKEN = "jwt_token";
    private static final String PREF_EXPIRATION = "token_expiration_ms";

    // --- VARIABLES Y CONSTRUCTOR PARA CONFIGURACIÓN YT-DLP / HISTORIAL ---
    private final String rutaBase;
    private final String CONFIG_FILE_NAME = "config.json";
    private final String LOG_FILE_NAME = "log.json";

    // Usamos Jackson para manejar el JSON, ya que es la dependencia solicitada.
    private final ObjectMapper mapper = new ObjectMapper();

    // Clase interna para la configuración (Asumiendo que existe)
    public static class Configuracion {

        public String rutaYtDlp;
        public String rutaGuardado;
        public boolean crearM3u;
        public String limiteVelocidad;
    }

    public GestorJson(String rutaGuardado) {
        this.rutaBase = rutaGuardado;
    }
    
    //Método añadido para asegurar directorio base (FIX Issue)
    /*
    * Asegura que el directorio base donde se guardarán los archivos JSON exista.
    * Crea el directorio si no existe para evitar File Not Found Exceptions
    */
    
    private void asegurarRutaBase(){
        File dir = new File(rutaBase);
        if (!dir.exists()){
            dir.mkdirs();
        }
    }

    // --- MÉTODOS ESTÁTICOS PARA EL TOKEN JWT (Ya revisados) ---
    public static void saveToken(String token, long expirationTime) {
        prefs.put(PREF_TOKEN, token);
        prefs.putLong(PREF_EXPIRATION, expirationTime);
        try {
            prefs.flush();
        } catch (java.util.prefs.BackingStoreException e) {
            //El fallo al escribir es un problema del SO. Se registra la advertencia 
            // en consola para el desarrollador pero se permite la ejecución normal.
            System.err.println("Advertencia: Fallo al escribir el token de sesión en el disco." 
                          + "\n El sistema operativo debería resolverlo. Error: " + e.getMessage());
        }
    }
    
    /*
    * Elimina el token de sesión y el tiempo de expiración guardados en la 
    * persistencia del sistema (Java Preferences)
    * Se utiliza para registrar el cierre de sesión explícito del usuario y asegurar
    * que no se realice un "Auto-Login" la próxima vez que se ejecute la aplicación.
    */
    public static String getToken() {
        String token = prefs.get(PREF_TOKEN, "");
        return token.isEmpty() ? null : token;
    }

    public static long getTokenExpirationTime() {
        return prefs.getLong(PREF_EXPIRATION, 0L);
    }

    public static void clearToken() {
        prefs.remove(PREF_TOKEN);
        prefs.remove(PREF_EXPIRATION);
        try {
            prefs.flush();
        } catch (java.util.prefs.BackingStoreException e) {
            //Aviso para el desarrollador de fallo de escritura/borrado.
            System.err.println("Advertencia: Fallo al eliminar las claves de sesión del disco. " 
                          + "La lógica de la sesión se considera cerrada. Error: " + e.getMessage());}
    }

    // --- MÉTODOS DE HISTORIAL LOCAL (log.json) ---
    /**
     * Carga todos los archivos MediaFile guardados en el historial (log.json).
     * SOLUCIONA EL ERROR: gestor.leerArchivos()
     */
    /**
     * Carga todos los archivos MediaFile guardados en el historial (log.json).
     */
    // MODIFICACIÓN: Añadimos 'throws IOException' al método ⬇️
    public List<MediaFile> leerArchivos() throws IOException {
        asegurarRutaBase();
        File logFile = new File(rutaBase, LOG_FILE_NAME);

        // 1. Verificar y Crear Archivo
        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        // 2. Verificar Archivo Vacío
        if (logFile.length() == 0) {
            return new ArrayList<>();
        }

        // 3. Lectura y Deserialización
        // El try-with-resources ahora maneja la lectura, y la IOException se propaga.
        try (Reader reader = new FileReader(logFile)) {
            List<MediaFile> archivos = mapper.readValue(reader, new TypeReference<List<MediaFile>>() {
            });
            return (archivos != null) ? archivos : new ArrayList<>();
        }
    }

    /**
     * Añade un archivo al historial (log.json) y lo guarda.
     */
    public void anadirArchivo(MediaFile archivoNuevo) throws IOException {
        List<MediaFile> lista = leerArchivos(); // Lee la lista existente
        lista.add(0, archivoNuevo); // Añade el nuevo archivo al principio
        guardarArchivos(lista); // Guarda la lista completa
    }

    /**
     * Elimina una entrada del archivo JSON de historial y lo guarda. SOLUCIONA
     * EL ERROR: gestor.eliminarArchivo(archivoABorrar)
     */
    public void eliminarArchivo(MediaFile archivoABorrar) throws IOException {
        List<MediaFile> lista = leerArchivos(); // Lee la lista existente

        // Encuentra y elimina el archivo basándose en la ruta
        lista.removeIf(f -> f.getRuta().equals(archivoABorrar.getRuta()));

        guardarArchivos(lista); // Guarda la lista completa
    }

    /**
     * Método auxiliar para serializar y guardar la lista completa al disco
     * usando Jackson.
     */
// ⬇️ MODIFICACIÓN: Añadimos 'throws IOException' al método ⬇️
    private void guardarArchivos(List<MediaFile> lista) throws IOException {
        asegurarRutaBase();
        File logFile = new File(rutaBase, LOG_FILE_NAME);
        // Dejamos que Jackson lo maneje
        mapper.writeValue(logFile, lista);
    }

    // --- MÉTODOS DE PERSISTENCIA DE YT-DLP ---
    /**
     * Guarda la configuración actual de yt-dlp (rutas, opciones y límites) en
     * un archivo JSON ("config.json") usando la librería Jackson. * Este método
     * se encarga de serializar los parámetros de entrada en un objeto
     * {@code Configuracion} y persistirlo en la ruta base definida.
     *
     * @param ytDlp La ruta completa del ejecutable yt-dlp.
     * @param guardado La ruta de la carpeta donde se guardarán los archivos
     * descargados.
     * @param m3u Indica si se debe crear un archivo M3U para listas de
     * reproducción.
     * @param limite El límite de velocidad de descarga en formato String (ej.
     * "500K" o vacío).
     * @throws IOException Si ocurre un error durante la escritura del archivo
     * en el disco.
     */
    public void guardarConfiguracion(String ytDlp, String guardado, boolean m3u, String limite) throws IOException {

        // Crea el objeto con los datos recibidos
        Configuracion config = new Configuracion();
        config.rutaYtDlp = ytDlp;
        config.rutaGuardado = guardado;
        config.crearM3u = m3u;
        config.limiteVelocidad = limite;

        File configFile = new File(rutaBase, CONFIG_FILE_NAME);
        
        // Llamada añadida, necesario antes de escribir config.json 
        asegurarRutaBase();
        // Usamos Jackson (mapper) para SERIALIZAR (escribir)
        mapper.writeValue(configFile, config);
        System.out.println("Configuración de yt-dlp guardada exitosamente en: " + configFile.getAbsolutePath());
    }

    /**
     * Intenta cargar la configuración de yt-dlp desde el archivo JSON
     * ("config.json") utilizando la librería Jackson. 
     * Si el archivo no existe o está vacío, devuelve {@code null} para que el llamador (MainFrame)
     * pueda aplicar los valores por defecto del sistema.
     *
     * @return Un objeto {@code Configuracion} con los valores cargados, o
     * {@code null} si el archivo no se encuentra o la lectura falla.
     */
    public Configuracion leerConfiguracion() {
        File configFile = new File(rutaBase, CONFIG_FILE_NAME);

        if (!configFile.exists() || configFile.length() == 0) {
            return null; // Si no hay archivo, MainFrame usará los valores por defecto
        }

        try (Reader reader = new FileReader(configFile)) {
            // Devuelve el objeto Configuracion cargado
            Configuracion config = mapper.readValue(reader, Configuracion.class);
            return config;
        } catch (IOException e) {
            System.err.println("Error al leer config.json con Jackson: " + e.getMessage());
            return null;
        }
    }
}
