package berlangadiaz.vidforge.downloader;

// Imports necesarios
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;


/**
 * Clase externa que maneja la descarga en segundo plano.
 */
public class DownloadWorker extends SwingWorker<String, String> {

    // Variables para los componentes de la GUI que necesitamos actualizar
    private final List<String> command;
    private final JProgressBar progressBar;
    private final JTextArea txtLog;
    private final JButton btnDescargar;
    private final MainViewPanel mainView; // Para llamar a get/setUltimoArchivo
    private final MainFrame parentFrame;  // Para saber dónde está el log.json

    /**
     * Constructor
     * Le pasamos todos los componentes que necesita para comunicarse.
     */
    public DownloadWorker(List<String> command,
                          JProgressBar progressBar,
                          JTextArea txtLog,
                          JButton btnDescargar,
                          MainViewPanel mainView,
                          MainFrame parentFrame) {
        
        this.command = command;
        this.progressBar = progressBar;
        this.txtLog = txtLog;
        this.btnDescargar = btnDescargar;
        this.mainView = mainView;
        this.parentFrame = parentFrame;
    }

    /**
     * El "trabajo sucio" en segundo plano.
     */
    @Override
    protected String doInBackground() throws Exception {
        System.out.println("Ejecutando comando: " + String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(this.command);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line); // Enviar al método process()
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "¡¡¡Descarga completada con ÉXITO!!!";
            } else {
                return "ERROR: La descarga falló (código de salida: " + exitCode + ")";
            }
        } catch (IOException | InterruptedException e) {
            return "ERROR CRÍTICO: " + e.getMessage();
        }
    }

    /**
     * Actualiza la GUI (barra de progreso y log) en tiempo real.
     */
    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {
            txtLog.append(line + "\n");
            
            // --- Lógica de la Barra de Progreso ---
            try {
                String percStr = null;
                if (line.contains("[download]") && line.contains("%")) {
                    int pEnd = line.indexOf("%"); int pStart = line.lastIndexOf(" ", pEnd) + 1; percStr = line.substring(pStart, pEnd);
                } else if (line.contains("[ExtractAudio]") && line.contains("%")) {
                    int pEnd = line.indexOf("%");
                    int pStart = line.lastIndexOf(" ", pEnd);
                    if (pStart == -1) pStart = line.lastIndexOf(":", pEnd);
                    percStr = line.substring(pStart + 1, pEnd);
                }
                if (percStr != null) { 
                    double percDouble = Double.parseDouble(percStr.trim());
                    int percInt = (int) percDouble; progressBar.setValue(percInt); }
            } catch (Exception e) { /* Ignorar error de parseo */ 
            }

            // --- Lógica de Capturar Ruta Final ---
            try {
                if (line.contains("[Merger] Merging formats into")) {
                    int start = line.indexOf("\"") + 1; int end = line.lastIndexOf("\"");
                    // Usa el método 'setter' público del MainViewPanel
                    mainView.setUltimoArchivoDescargado(line.substring(start, end)); 
                } else if (line.contains("[ExtractAudio] Destination:")) {
                    int start = line.indexOf("Destination:") + "Destination:".length();
                    mainView.setUltimoArchivoDescargado(line.substring(start).trim());
                } else if (line.contains("[download] Destination:") && !line.contains("ExtractAudio")) {
                    if (mainView.getUltimoArchivoDescargado().isEmpty()) { // Necesitamos un 'getter'
                        int start = line.indexOf("Destination:") + "Destination:".length();
                        mainView.setUltimoArchivoDescargado(line.substring(start).trim());
                    }
                }
            } catch (Exception e) { /* Ignorar error de parseo */ }
        }
    }

    /**
     * Se ejecuta cuando doInBackground() termina.
     * Aquí es donde escribiremos en el JSON.
     */
    @Override
    public void done() {
        try {
            String resultado = get(); // Coge el "¡Descarga completada!"

            // Si la descarga fue un éxito, lo guardamos en el JSON
            if (resultado.contains("ÉXITO")) {
                
                // 1. Coge la ruta del archivo que guardamos (ej. /Users/.../video.mp4)
                String rutaArchivoDescargado = mainView.getUltimoArchivoDescargado();
                
                if (rutaArchivoDescargado != null && !rutaArchivoDescargado.isEmpty()) {
                    // 2. Crea el objeto File
                    java.io.File file = new java.io.File(rutaArchivoDescargado);
                    
                    // 3. Crea el objeto MediaFile (el "molde")
                    MediaFile mediaFile = new MediaFile(file);
                    
                    // 4. Coge la ruta de guardado (ej. /Users/.../Downloads)
                    String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
                    
                    // 5. Llama al GestorJson para añadirlo al log
                    GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
                    gestor.anadirArchivo(mediaFile);
                    
                    System.out.println("Añadido al log.json: " + mediaFile.getNombre());
                }
            }
            // --- FIN DE LÓGICA JSON ---

            JOptionPane.showMessageDialog(mainView, resultado,
                    "Estado de la Descarga", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView,
                    "Error al finalizar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        btnDescargar.setEnabled(true); // Reactiva el botón
    }
}