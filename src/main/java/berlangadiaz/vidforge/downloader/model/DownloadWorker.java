package berlangadiaz.vidforge.downloader.model;

// Imports necesarios
import berlangadiaz.vidforge.downloader.view.BibliotecaPanel;
import berlangadiaz.vidforge.downloader.view.MainFrame;
import berlangadiaz.vidforge.downloader.view.MainViewPanel;
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


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
        
        //Variable del proceso para poder cerrarlo bien
        Process process = null;
        try {
            process = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(),"UTF-8"))) {
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
        } catch (Exception ex) {
            // Feedback visual
            JOptionPane.showMessageDialog(mainView, "Error durante la descarga: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            // Log del sistema (Punto 4 de la rúbrica)
            LoggerError.log("Fallo crítico en el proceso de descarga de vídeo", ex);

            // Esto soluciona el "missing return statement"
            return "ERROR: " + ex.getMessage();

        } finally {
            // Cerramos streams para evitar bloqueos en la siguiente descarga
            if (process != null) {
                try {
                    process.getInputStream().close();
                } catch (IOException ignored) {
                }
                try {
                    process.getErrorStream().close();
                } catch (IOException ignored) {
                }
                try {
                    process.getOutputStream().close();
                } catch (IOException ignored) {
                }
                process.destroy();
            }
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
     * Se ejecuta cuando doInBackground() termina. Aquí es donde se maneja la
     * persistencia en el JSON y la actualización de la GUI.
     */
    @Override
    public void done() {
        try {
            String resultado = get(); // Coge el resultado final (ÉXITO o ERROR)

            if (resultado.contains("ÉXITO")) {

                // --- 1. Lógica de persistencia (JSON) ---
                String rutaArchivoDescargado = mainView.getUltimoArchivoDescargado();

                if (rutaArchivoDescargado != null && !rutaArchivoDescargado.isEmpty()) {
                    java.io.File file = new java.io.File(rutaArchivoDescargado);

                    if (file.exists()) {
                        // Crea el objeto MediaFile (asumiendo que tiene un constructor que recibe File)
                        MediaFile mediaFile = new MediaFile(file);

                        String rutaCarpetaGuardado = parentFrame.getRutaGuardado();

                        // Llama al GestorJson para añadirlo al log
                        GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
                        gestor.anadirArchivo(mediaFile);
                        
                        // CORRECCIÓN DEL BUG DE SINCRONIZACIÓN (Refresco de la tabla) ⬇️
                        BibliotecaPanel bibliotecaPanel = parentFrame.getPanelBiblioteca();
                        if (bibliotecaPanel != null) {

                            // Usamos un Timer para dar tiempo al Sistema Operativo de finalizar la escritura
                            // del log.json (aprox. 100ms) antes de intentar leerlo de nuevo.
                            javax.swing.Timer timer = new javax.swing.Timer(100, new java.awt.event.ActionListener() {
                                @Override
                                public void actionPerformed(java.awt.event.ActionEvent e) {
                                    try {
                                        // Llama al método que recarga el log.json y actualiza la tabla
                                        bibliotecaPanel.aplicarFiltrosYOrden();
                                    } catch (IOException ex) {
                                        System.getLogger(DownloadWorker.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                                    }
                                    // Detenemos el Timer
                                    ((javax.swing.Timer) e.getSource()).stop();
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                        // ----------------------------------------------------------------------
                    }
                }

                // --- PREPARAR EL ICONO PARA EL DIÁLOGO EMERGENTE ---
                ImageIcon successIcon = null;
                try {
                    // Lógica de carga y redimensionado del icono
                    java.net.URL imageUrl = Thread.currentThread().getContextClassLoader().getResource("images/success_icon.png");
                    if (imageUrl != null) {
                        successIcon = new ImageIcon(imageUrl);
                        java.awt.Image img = successIcon.getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                        successIcon = new ImageIcon(img);
                    }
                } catch (Exception e) {
                    System.err.println("Error al preparar icono para JOptionPane: " + e.getMessage());
                }

                // --- MOSTRAR DIÁLOGO ---
                JOptionPane.showMessageDialog(mainView, resultado,
                        "Descarga Completada", JOptionPane.INFORMATION_MESSAGE, successIcon);

            } else {
                // Si falla la descarga
                JOptionPane.showMessageDialog(mainView, resultado,
                        "Error de Descarga", JOptionPane.ERROR_MESSAGE);
            }
        } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
            // Manejo de errores de la tarea asíncrona
            System.err.println("Error durante la ejecución asíncrona: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainView, "Error crítico: " + ex.getMessage(), "Error Fatal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Capturamos cualquier error en la persistencia o creación del MediaFile
            System.err.println("Error al procesar el archivo o actualizar la biblioteca: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainView, "Error de Persistencia: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // La descarga siempre termina, re-habilitamos el botón
            mainView.setBotonDescargarHabilitado(true);
        }
    }
}
