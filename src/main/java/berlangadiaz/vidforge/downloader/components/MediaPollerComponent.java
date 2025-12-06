package berlangadiaz.vidforge.downloader.components;

import berlangadiaz.vidforge.downloader.api.ApiClient;
import berlangadiaz.vidforge.downloader.api.Media;
import berlangadiaz.vidforge.downloader.api.Usuari;
import berlangadiaz.vidforge.downloader.events.NewMediaEvent;
import berlangadiaz.vidforge.downloader.events.NewMediaListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * @author: Jaime Berlanga Diaz
 * 
 * Componente Java Bean (JPanel) que realiza Polling al servidor.
 * Implementado manualmente sin Designer.
 */
public class MediaPollerComponent extends JPanel {

    // --- Propiedades del Bean ---
    private String apiUrl;
    private boolean running;
    private int pollingInterval = 10; // Segundos por defecto
    private String token;
    private String lastChecked; // Formato ISO-8601

    // --- Componentes Internos ---
    private ApiClient apiClient; // Instancia de tu cliente API
    private final Timer timer;
    private final JLabel statusLabel;

    // --- Lista de Listeners (Sin PropertyChangeSupport) ---
    private final List<NewMediaListener> listeners = new ArrayList<>();

    // Constructor
    public MediaPollerComponent() {
        super();
        
        // Configuración Visual (Icono/Texto centrado)
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        // Configuramos el texto inicial
        statusLabel = new JLabel(" Poller: Stopped"); 
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Cargamos y Redimensionamos el Icono
        try {
            // Buscamos la imagen dentro del paquete resources. 
            // LA RUTA DEBE EMPEZAR CON / Y SEGUIR LA ESTRUCTURA DE PAQUETES:
            java.net.URL imgUrl = getClass().getResource("/images/poller_icon.png");
            
            if (imgUrl != null) {
                // Cargamos la imagen original (que es grande)
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                
                // La redimensionamos a 20x20 píxeles para que quede elegante
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                
                // Se la ponemos al Label
                statusLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("⚠ No se encontró la imagen en '/images/poller_icon.png' ");
            }
        } catch (Exception e) {
            System.err.println("⚠ Error cargando el icono: " + e.getMessage());
        }
        
        add(statusLabel, BorderLayout.CENTER);

        // Inicializamos Timer (detenido)
        // Convierte segundos a milisegundos
        timer = new Timer(pollingInterval * 1000, e -> performPoll());
        timer.setRepeats(true);
        
        // Inicializamos LastChecked a "Ahora" para no bajar todo el historial al arrancar
        updateLastCheckedToNow();
    }

    // LÓGICA DE POLLING (El corazón del componente)

    private void performPoll() {
        // Validaciones de seguridad antes de llamar
        if (!running || token == null || token.isBlank() || apiClient == null) {
            return;
        }

        System.out.println("[MediaPoller] Buscando archivos nuevos desde: " + lastChecked);
        
        try {
            // Llamamos al método de la API que filtra por fecha
            // El API espera String ISO, lastChecked ya lo es.
            List<Media> newFiles = apiClient.getMediaAddedSince(lastChecked, token);

            if (newFiles != null && !newFiles.isEmpty()) {
                System.out.println("[MediaPoller] ¡Encontrados " + newFiles.size() + " archivos nuevos!");
                
                // Preparar el evento
                String now = OffsetDateTime.now(ZoneOffset.UTC).toString();
                NewMediaEvent event = new NewMediaEvent(this, newFiles, now);
                
                // Disparar evento a los suscriptores
                fireNewMediaEvent(event);
            }
            
            // Actualizamos la fecha de última comprobación
            updateLastCheckedToNow();
            
        } catch (Exception e) {
            System.err.println("[MediaPoller] Error durante el polling: " + e.getMessage());
            // No paramos el timer, lo reintentará en el siguiente ciclo
        }
    }

    private void updateLastCheckedToNow() {
        // Usamos OffsetDateTime para ser compatibles con la API
        this.lastChecked = OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    // WRAPPER METHODS (Exponen ApiClient al exterior)


    // Wrapper para LOGIN
    public String login(String email, String password) throws Exception {
        ensureClient();
        return apiClient.login(email, password);
    }

    // Wrapper para GET NICKNAME
    // La API pide ID para el nickname. Hacemos un truco: Pedimos "Me" y sacamos el nick.
    public String getNickName(String token) throws Exception {
        ensureClient();
        Usuari me = apiClient.getMe(token);
        return me.nickName; // Asumiendo que Usuari tiene campo público nickName
    }

    // Wrapper para GET ALL MEDIA
    public List<Media> getAllMedia(String token) throws Exception {
        ensureClient();
        return apiClient.getAllMedia(token);
    }
    
    // Wrapper para DOWNLOAD
    public void download(int mediaId, File destination, String token) throws Exception {
        ensureClient();
        apiClient.download(mediaId, destination, token);
    }

    // Wrapper para UPLOAD
    public void uploadFileMultipart(File file, String token) throws Exception {
        ensureClient();
        // Pasamos null como URL de origen si no aplica, o ajusta según necesitemos.
        apiClient.uploadFileMultipart(file, null, token);
    }

    // Método auxiliar para asegurar que el ApiClient existe
    private void ensureClient() {
        if (apiClient == null) {
            if (apiUrl != null && !apiUrl.isBlank()) {
                apiClient = new ApiClient(apiUrl);
            } else {
                throw new IllegalStateException("API URL no configurada en el Poller.");
            }
        }
    }

    // GETTERS & SETTERS (Propiedades del Bean)

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        // Si cambia la URL, hay que reiniciar el cliente
        if (apiUrl != null && !apiUrl.isBlank()) {
            this.apiClient = new ApiClient(apiUrl);
            statusLabel.setToolTipText("API: " + apiUrl);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if (running) {
            statusLabel.setText("Poller: Running");
            statusLabel.setForeground(new Color(0, 150, 0)); // Verde
            timer.start();
        } else {
            statusLabel.setText("Poller: Stopped");
            statusLabel.setForeground(Color.RED);
            timer.stop();
        }
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
        if (timer != null) {
            timer.setDelay(pollingInterval * 1000); // Actualizar timer Convierte 15s -> 15000ms
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(String lastChecked) {
        this.lastChecked = lastChecked;
    }

    // GESTIÓN DE EVENTOS (Custom Listener)

    public void addNewMediaListener(NewMediaListener listener) {
        listeners.add(listener);
    }

    public void removeNewMediaListener(NewMediaListener listener) {
        listeners.remove(listener);
    }

    private void fireNewMediaEvent(NewMediaEvent event) {
        for (NewMediaListener listener : listeners) {
            listener.onNewMediaFound(event);
        }
    }
}