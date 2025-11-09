package berlangadiaz.vidforge.downloader;

// Imports necesarios para manejar archivos, fechas y tipos MIME
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase "Modelo" (el molde) para la Tarea 2.
 * Representa un archivo descargado. Guarda toda la información
 * que nos pide el cliente (nombre, tamaño, tipo, fecha).
 */
public class MediaFile {
    
    // Atributos que guardaremos en el JSON
    private String nombre;
    private String ruta;
    private long tamanoBytes; // long para tamaños grandes
    private String tipoMime;
    private long fechaCreacionMs; // long para la fecha en milisegundos
    
    // --- Constructores ---
    
    /**
     * Constructor principal. Recibe un objeto File (del disco)
     * y extrae toda la información necesaria.
     * * @param archivo El archivo físico del disco (ej. "video.mp4")
     */
    public MediaFile(File archivo) {
        if (archivo == null || !archivo.exists()) {
            throw new IllegalArgumentException("El archivo no puede ser nulo o no existe.");
        }
        
        this.nombre = archivo.getName();
        this.ruta = archivo.getAbsolutePath();
        this.tamanoBytes = archivo.length();
        
        // --- Lógica para obtener el Tipo MIME y la Fecha ---
        try {
            // Obtener el tipo MIME (ej. "video/mp4")
            this.tipoMime = Files.probeContentType(archivo.toPath());
            
            // Obtener los atributos básicos del archivo
            BasicFileAttributes attrs = Files.readAttributes(archivo.toPath(), BasicFileAttributes.class);
            
            // Guardar la fecha de creación en milisegundos (un número largo)
            this.fechaCreacionMs = attrs.creationTime().toMillis();
            
        } catch (IOException e) {
            System.err.println("Error al leer atributos del archivo: " + e.getMessage());
            this.tipoMime = "desconocido";
            this.fechaCreacionMs = System.currentTimeMillis(); // Pone la fecha de hoy
        }
    }
    
    /**
     * Constructor vacío (necesario para que la biblioteca GSON funcione).
     */
    public MediaFile() {
    }

    // --- Getters (Métodos para leer la información) ---
    // (Gson los usa para crear el JSON)
    
    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public long getFechaCreacionMs() {
        return fechaCreacionMs;
    }

    // --- Métodos de Ayuda (para la JTable) ---
    
    /**
     * Devuelve el tamaño en formato legible (ej. "10.5 MB").
     * Esto lo usará nuestra JTable.
     */
    public String getTamanoFormateado() {
        if (tamanoBytes < 1024) {
            return tamanoBytes + " B";
        }
        int exp = (int) (Math.log(tamanoBytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", tamanoBytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Devuelve la fecha en formato legible (ej. "28-10-2025 18:30").
     * Esto lo usará nuestra JTable.
     */
    public String getFechaFormateada() {
        Date fecha = new Date(this.fechaCreacionMs);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(fecha);
    }
    
    /**
     * Devuelve el objeto File original.
     * Lo usaremos para poder BORRAR el archivo.
     */
    public File getFichero() {
        return new File(this.ruta);
    }
}