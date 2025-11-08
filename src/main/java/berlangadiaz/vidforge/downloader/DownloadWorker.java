package berlangadiaz.vidforge.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Clase SwingWorker independiente que maneja la descarga en segundo plano.
 */
public class DownloadWorker extends SwingWorker<String, String> {

    // Componentes de la GUI que necesita actualizar
    private final List<String> command;
    private final JProgressBar progressBar;
    private final JTextArea txtLog;
    private final JButton btnDescargar;
    private final MainViewPanel mainView; // Referencia al panel principal

    /**
     * Constructor que recibe los componentes de la GUI a los que debe reportar.
     */
    public DownloadWorker(List<String> command, JProgressBar progressBar, JTextArea txtLog, JButton btnDescargar, MainViewPanel mainView) {
        this.command = command;
        this.progressBar = progressBar;
        this.txtLog = txtLog;
        this.btnDescargar = btnDescargar;
        this.mainView = mainView;
    }

    /**
     * Se ejecuta en un hilo separado (NO TOCAR LA GUI).
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
     * Se ejecuta en el hilo de la GUI cada vez que "publish()" es llamado.
     * (SÍ PODEMOS TOCAR LA GUI).
     */
    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {
            txtLog.append(line + "\n");
            try {
                String percStr = null;
                if (line.contains("[download]") && line.contains("%")) {
                    int pEnd = line.indexOf("%");
                    int pStart = line.lastIndexOf(" ", pEnd) + 1;
                    percStr = line.substring(pStart, pEnd);
                } else if (line.contains("[ExtractAudio]") && line.contains("%")) {
                    int pEnd = line.indexOf("%");
                    int pStart = line.lastIndexOf(" ", pEnd);
                    if (pStart == -1) pStart = line.lastIndexOf(":", pEnd);
                    percStr = line.substring(pStart + 1, pEnd);
                }
                if (percStr != null) {
                    double percDouble = Double.parseDouble(percStr.trim());
                    int percInt = (int) percDouble;
                    progressBar.setValue(percInt);
                }
            } catch (Exception e) { /* Ignorar error de parseo */ }

            try {
                if (line.contains("[Merger] Merging formats into")) {
                    int start = line.indexOf("\"") + 1;
                    int end = line.lastIndexOf("\"");
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
     * Se ejecuta en el hilo de la GUI *después* de que doInBackground() termina.
     */
    @Override
    public void done() {
        try {
            String resultado = get();
            JOptionPane.showMessageDialog(mainView, resultado,
                    "Estado de la Descarga", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView,
                    "Error al finalizar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        // Reactivar el botón
        btnDescargar.setEnabled(true);
    }
}