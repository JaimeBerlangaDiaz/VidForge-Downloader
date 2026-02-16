package berlangadiaz.vidforge.downloader.model;

// Imports necesarios
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase "Modelo" arreglada para soportar archivos Locales y Remotos (Nube).
 */
public class MediaFile {
    
    // Atributos que guardaremos en el JSON
    private String nombre;
    private String ruta;
    private long tamanoBytes;
    private String tipoMime;
    private long fechaCreacionMs;
    
    // --- Constructores ---
    
    /**
     * Constructor Inteligente. 
     * Acepta archivos que existen (PC) y archivos que no existen aún (Nube).
     * @param archivo El puntero al archivo (puede ser real o virtual).
     */
    public MediaFile(File archivo) {
        // 1. Validación: Solo comprobamos que el objeto no sea nulo.
        // YA NO comprobamos !archivo.exists() para permitir archivos de la nube.
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        
        // 2. Datos básicos (Siempre los tenemos, existan o no)
        this.nombre = archivo.getName();
        this.ruta = archivo.getAbsolutePath();
        
        // 3. Lógica Diferenciada
        if (archivo.exists()) {
            // --- ES UN ARCHIVO LOCAL (PC) ---
            // Leemos los datos reales del disco
            this.tamanoBytes = archivo.length();
            
            try {
                // Intentar obtener tipo MIME
                this.tipoMime = Files.probeContentType(archivo.toPath());
                if (this.tipoMime == null) this.tipoMime = "application/octet-stream";

                // Intentar obtener fecha de creación
                BasicFileAttributes attrs = Files.readAttributes(archivo.toPath(), BasicFileAttributes.class);
                this.fechaCreacionMs = attrs.creationTime().toMillis();

            } catch (IOException e) {
                // Log para el archivo de texto
                berlangadiaz.vidforge.downloader.model.LoggerError.log("Error al leer atributos físicos del archivo: " + this.nombre, e);

                this.tipoMime = "desconocido";
                this.fechaCreacionMs = System.currentTimeMillis();
            }
        } else {
            // --- ES UN ARCHIVO REMOTO (SOLO NUBE) ---
            // Como no existe en el disco, ponemos datos vacíos para evitar errores
            this.tamanoBytes = 0;
            this.tipoMime = "nube/pendiente";
            this.fechaCreacionMs = 0; // Fecha 0 (1970) o puedes poner System.currentTimeMillis()
        }
    }
    
    /**
     * Constructor vacío (necesario para GSON/Jackson).
     */
    public MediaFile() {
    }

    // --- Getters ---
    
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
    
    // --- Setters ---

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public void setTamanoBytes(long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public void setFechaCreacionMs(long fechaCreacionMs) {
        this.fechaCreacionMs = fechaCreacionMs;
    }
    
    // --- Métodos de Ayuda (para la JTable) ---

    public String getTamanoFormateado() {
        // Si es 0 (archivo de nube), podemos devolver algo específico o "0 B"
        if (tamanoBytes == 0) return "---"; // Opcional: Indicar que no tiene tamaño
        
        if (tamanoBytes < 1024) {
            return tamanoBytes + " B";
        }
        int exp = (int) (Math.log(tamanoBytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", tamanoBytes / Math.pow(1024, exp), pre);
    }
    
    public String getFechaFormateada() {
        // Si la fecha es 0 (archivo de nube), devolvemos cadena vacía o "Pendiente"
        if (fechaCreacionMs == 0) return "En la Nube";
        
        Date fecha = new Date(this.fechaCreacionMs);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(fecha);
    }
    
    public File getFichero() {
        return new File(this.ruta);
    }
}