package berlangadiaz.vidforge.downloader.view;

/**
 * Clase "Objeto" para el JList de filtrar (Tarea 2).
 * Guarda el nombre que ve el usuario ("Vídeos") y el prefijo
 * que usaremos para buscar ("video/").
 */
public class TipoMimeFiltro {
    
    private String nombreMostrado;
    private String prefijoMime;

    public TipoMimeFiltro(String nombreMostrado, String prefijoMime) {
        this.nombreMostrado = nombreMostrado;
        this.prefijoMime = prefijoMime;
    }

    public String getPrefijoMime() {
        return prefijoMime;
    }

    @Override
    public String toString() {
        // Esto es lo que verá el usuario en la JList
        return nombreMostrado; 
    }
}