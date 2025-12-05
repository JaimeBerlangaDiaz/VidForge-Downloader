package berlangadiaz.vidforge.downloader.events;

import java.util.EventListener;

/**
 * @author: Jaime Berlanga Diaz
 * 
 * Interfaz para escuchar cuando el Poller encuentra algo nuevo.
 */
public interface NewMediaListener extends EventListener {
    void onNewMediaFound(NewMediaEvent evt);
}