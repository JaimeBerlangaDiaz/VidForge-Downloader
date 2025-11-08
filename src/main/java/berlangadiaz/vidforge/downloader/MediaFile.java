package berlangadiaz.vidforge.downloader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase "Modelo" que representa un archivo descargado en la biblioteca.
 */
public class MediaFile {
    
    // --- Atributos ---
    private String nombre;
    private String ruta;
    private long tamanoEnBytes;
    private String tipoMime; // Ej. "video/mp4"
    private long fechaModificacion; // Se guarda como un número (timestamp)

    /**
     * Constructor que crea un MediaFile a partir de un objeto java.io.File.
     * @param file El archivo en el disco.
     */
    public MediaFile(File file) {
        this.nombre = file.getName();
        this.ruta = file.getAbsolutePath();
        this.tamanoEnBytes = file.length();
        this.fechaModificacion = file.lastModified();
        
        // --- Obtener el Tipo MIME (puede ser complicado) ---
        try {
            Path path = Paths.get(file.getAbsolutePath());
            this.tipoMime = Files.probeContentType(path);
            
            // Si Files.probeContentType falla (devuelve null), intentamos adivinar
            if (this.tipoMime == null) {
                if (nombre.endsWith(".mp4")) this.tipoMime = "video/mp4";
                else if (nombre.endsWith(".mkv")) this.tipoMime = "video/x-matroska";
                else if (nombre.endsWith(".webm")) this.tipoMime = "video/webm";
                else if (nombre.endsWith(".mp3")) this.tipoMime = "audio/mpeg";
                else this.tipoMime = "desconocido";
            }
        } catch (Exception e) {
            this.tipoMime = "error";
        }
    }

    // --- Getters (Métodos para leer los datos) ---
    // La JTable usará estos métodos para obtener los valores

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public long getTamanoEnBytes() {
        return tamanoEnBytes;
    }

    /**
     * Devuelve el tamaño formateado como "KB" o "MB" para que sea legible.
     * @return String del tamaño (ej. "10.5 MB")
     */
    public String getTamanoFormateado() {
        double kilobytes = this.tamanoEnBytes / 1024.0;
        double megabytes = kilobytes / 1024.0;
        
        if (megabytes > 1) {
            return String.format("%.2f MB", megabytes); // Formato con 2 decimales
        } else if (kilobytes > 1) {
            return String.format("%.2f KB", kilobytes);
        } else {
            return String.format("%d Bytes", this.tamanoEnBytes);
        }
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    /**
     * Devuelve la fecha formateada como un String legible.
     * @return String de la fecha (ej. "10/11/2025 08:30")
     */
    public String getFechaFormateada() {
        // Formato: día/mes/año hora:minuto
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(this.fechaModificacion));
    }

    /**
     * El método toString() es llamado por JComboBox y JList si no tienen un "Renderer".
     * Por ahora, haremos que devuelva el nombre.
     */
    @Override
    public String toString() {
        return nombre;
    }
}