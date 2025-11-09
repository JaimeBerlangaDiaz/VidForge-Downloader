package berlangadiaz.vidforge.downloader;

// Imports de Google Gson (el "traductor" de JSON)
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

// Imports de Java para leer y escribir archivos
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Gestora que se encarga de leer y escribir la lista
 * de objetos MediaFile en un archivo log.json.
 */
public class GestorJson {
    private final Gson gson;
    private final java.io.File directorioBase;
    private final String RUTA_LOG_JSON; // Ruta completa al archivo log.json
    private final String CONFIG_FILE_NAME = "config.json";

    /**
     * Constructor.
     * @param rutaCarpetaGuardado La carpeta donde se guardan las descargas (ej. /Users/tu/Downloads)
     */
    public GestorJson(String rutaCarpetaGuardado) {
        // El log se guardará en la misma carpeta que las descargas
        // Usamos File.separator para que funcione en Windows (\) y Mac (/)
        this.RUTA_LOG_JSON = rutaCarpetaGuardado + File.separator + "log.json";
        
        // Creamos un Gson "bonito" (con indentación)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.directorioBase = new java.io.File(rutaCarpetaGuardado);
    }

    /**
     * Lee todos los MediaFile guardados en log.json.
     * @return Una lista de MediaFile. Si el log no existe, devuelve una lista vacía.
     */
    public List<MediaFile> leerArchivos() {
        File log = new File(RUTA_LOG_JSON);

        // Si el archivo no existe, devuelve una lista vacía
        if (!log.exists()) {
            System.out.println("No se encontró log.json, creando uno nuevo.");
            return new ArrayList<>();
        }

        // Definir el "tipo" de la lista (para que Gson sepa cómo traducir)
        Type tipoLista = new TypeToken<ArrayList<MediaFile>>() {}.getType();

        try (FileReader reader = new FileReader(log)) {
            // Usa Gson para "traducir" el JSON a la Lista de objetos
            List<MediaFile> archivos = gson.fromJson(reader, tipoLista);
            
            // Si el archivo estaba vacío o mal formado, devuelve una lista vacía
            if (archivos == null) {
                return new ArrayList<>();
            }
            return archivos;

        } catch (IOException e) {
            System.err.println("Error al leer el log.json: " + e.getMessage());
            return new ArrayList<>(); // Devuelve lista vacía en caso de error
        }
    }

    /**
     * Añade un NUEVO MediaFile al log.json.
     * @param nuevoArchivo El MediaFile que se acaba de descargar.
     */
    public void anadirArchivo(MediaFile nuevoArchivo) {
        // 1. Lee la lista actual de archivos
        List<MediaFile> archivos = leerArchivos();
        
        // 2. Añade el nuevo archivo a la lista
        archivos.add(nuevoArchivo);
        
        // 3. Sobrescribe el log.json con la lista actualizada
        guardarArchivos(archivos);
    }
    
    /**
     * (Lo usaremos más tarde) Borra un MediaFile del log.json.
     * @param archivoABorrar El MediaFile a eliminar.
     */
    public void eliminarArchivo(MediaFile archivoABorrar) {
        // 1. Lee la lista actual
        List<MediaFile> archivos = leerArchivos();
        
        // 2. Busca y elimina el archivo (comparamos por la ruta, que es única)
        archivos.removeIf(mf -> mf.getRuta().equals(archivoABorrar.getRuta()));
        
        // 3. Sobrescribe el log.json
        guardarArchivos(archivos);
    }

    /**
     * Método privado para escribir la lista completa en el archivo.
     * @param archivos La lista de MediaFile a guardar.
     */
    private void guardarArchivos(List<MediaFile> archivos) {
        try (FileWriter writer = new FileWriter(RUTA_LOG_JSON)) {
            // Usa Gson para "traducir" la Lista de objetos a texto JSON
            gson.toJson(archivos, writer);
            System.out.println("log.json actualizado con éxito.");
            
        } catch (IOException e) {
            System.err.println("Error al guardar en log.json: " + e.getMessage());
        }
    }
    
    /**
     * Clase auxiliar para guardar las opciones simples de configuración
     */
    public static class Configuracion{
        String rutaYtDlp;
        String rutaGuardado;
        boolean crearM3u;
        String limiteVelocidad;
    }
    
    /**
     * Guarda las preferencias de la aplicación en config.json
     */
    public void guardarConfiguracion(String ytDlp, String guardado, boolean m3u, String limite) {
        File configFile = new File(directorioBase, CONFIG_FILE_NAME);

        Configuracion config = new Configuracion();
        config.rutaYtDlp = ytDlp;
        config.rutaGuardado = guardado;
        config.crearM3u = m3u;
        config.limiteVelocidad = limite;

        try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar la configuración: " + e.getMessage());
        }
    }

    /**
     * Lee las preferencias de la aplicación desde config.json.
     * Retorna null si el archivo no existe o hay error.
     */
    public Configuracion leerConfiguracion() {
        File configFile = new File(directorioBase, CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            return null; // No hay configuración guardada
        }

        try (java.io.Reader reader = new java.io.FileReader(configFile)) {
            // Leemos el objeto de configuración del JSON
            return gson.fromJson(reader, Configuracion.class);
        } catch (IOException e) {
            System.err.println("Error al leer la configuración: " + e.getMessage());
            return null;
        }
    }
}