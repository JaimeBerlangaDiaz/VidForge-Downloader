package berlangadiaz.vidforge.downloader.events;

import berlangadiaz.vidforge.downloader.api.Media; // Importamos la clase Media de la API
import java.util.EventObject;
import java.util.List;

/**
 * @author: Jaime Berlanga Diaz
 * 
 * Evento que transporta la lista de nuevos archivos encontrados.
 */
public class NewMediaEvent extends EventObject {
    
    private final List<Media> newMediaList;
    private final String detectionTime;

    public NewMediaEvent(Object source, List<Media> newMediaList, String detectionTime) {
        super(source);
        this.newMediaList = newMediaList;
        this.detectionTime = detectionTime;
    }

    public List<Media> getNewMediaList() {
        return newMediaList;
    }

    public String getDetectionTime() {
        return detectionTime;
    }
}