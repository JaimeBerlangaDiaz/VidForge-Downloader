/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package berlangadiaz.vidforge.downloader.view;

import controller.DownloadWorker;
import persistence.GestorJson;
import utils.LoggerError;
import berlangadiaz.vidforge.downloader.model.MediaFile;
import com.berlangadiaz.dimedianet.api.ApiClient;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 * Panel principal de descarga de VidForge Downloader.
 * Gestiona la interfaz de usuario para introducir la URL, seleccionar formatos/calidades,
 * iniciar la descarga y visualizar el progreso y el log.
 *
 * @author Jaime Berlanga Diaz
 */
public class MainViewPanel extends javax.swing.JPanel {

    private String ultimoArchivoDescargado = "";
    
    // (Esta variable la necesitaremos para el 'parentFrame' de PreferenciasPanel, 
    // la añado aquí para que el código compile si lo usamos
    private MainFrame parentFrame; 
    
    private DefaultComboBoxModel<String> videoFormatsModel;
    private DefaultComboBoxModel<String> audioFormatsModel;

    /**
     * Crea un nuevo formulario MainViewPanel.
     * Inicializa los modelos de ComboBox y aplica las preferencias de persistencia.
     * @param parent La instancia del MainFrame que actúa como controlador.
     */
    public MainViewPanel(MainFrame parent) {
        initComponents();
        this.parentFrame = parent; 

        // Código para redimensionar el icono Download y que no se vea gigante
        try {
            java.net.URL imgUrl = getClass().getResource("/images/download_icon.png");
            if (imgUrl != null) {
                javax.swing.ImageIcon iconOriginal = new javax.swing.ImageIcon(imgUrl);
                // 20x20 es el tamaño ideal para que no rompa el botón
                java.awt.Image imgEscalada = iconOriginal.getImage().getScaledInstance(40, 32, java.awt.Image.SCALE_SMOOTH);
                btnDescargar.setIcon(new javax.swing.ImageIcon(imgEscalada));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + e.getMessage());
        }

        // 2. Aprovechamos para poner el cursor de mano (Affordance)
        btnDescargar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // 3. Comprobación de validación de la URL
        btnDescargar.setEnabled(false);
        txtUrl.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validar(); }
            
            private void validar() {
                String texto = txtUrl.getText().trim();
                btnDescargar.setEnabled(!texto.isEmpty() && texto.contains("http"));
            }
        });
        txtUrl.addActionListener(e -> {
            if (btnDescargar.isEnabled()) {
                btnDescargar.doClick();
            }
        });
        // Inicializar los Modelos de JComboBox (Asumo que videoFormatsModel, etc., están declarados arriba)
        String[] videoFormats = {"mp4", "mkv", "webm"};
        videoFormatsModel = new javax.swing.DefaultComboBoxModel<>(videoFormats);
        String[] audioFormats = {"mp3","m4a","wav","flac"};
        audioFormatsModel = new javax.swing.DefaultComboBoxModel<>(audioFormats);
        
        // Conectar los modelos y la interfaz por defecto
        cmbFormato.setModel(videoFormatsModel);
        
        // Cargar el estado guardado del CheckBox "Solo Audio" (crearM3u)
        boolean soloAudioGuardado = parentFrame.isCrearM3u();
        chkSoloAudio.setSelected(soloAudioGuardado);

        // Si el CheckBox de Solo Audio estaba marcado, ajustamos el ComboBox de Formato.
        if (soloAudioGuardado) {
            cmbFormato.setModel(audioFormatsModel);
            // Opcional: cmbFormato.setSelectedItem("mp3"); 
        } else {
            // Si no es solo audio, debe estar en la configuración de video
            cmbFormato.setModel(videoFormatsModel);
        }
        
        // Arreglo para PEGAR (Cmd+V) en macOS
        javax.swing.Action pasteAction = txtUrl.getActionMap().get("paste");
        javax.swing.KeyStroke commandV = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.META_DOWN_MASK
        );
        txtUrl.getInputMap().put(commandV, "paste");

        // Deshabilitar botón por defecto
        btnDescargar.setEnabled(false);

        // Escuchar lo que el usuario escribe en la caja de URL
        txtUrl.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validar();
            }

            private void validar() {
                // Solo activamos si hay texto y contiene "http"
                String texto = txtUrl.getText().trim();
                btnDescargar.setEnabled(!texto.isEmpty() && texto.contains("http"));
            }
        });
    }
    
    // Método getter para el ApiClient
    public ApiClient getApiClient() {
        return parentFrame.getApiClient(); 
    } 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        chkSoloAudio = new javax.swing.JCheckBox();
        rbVideo1080 = new javax.swing.JRadioButton();
        rbVideoMejor = new javax.swing.JRadioButton();
        btnDescargar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnAbrirVideo = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        rbVideo720 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        rbAudioBuena = new javax.swing.JRadioButton();
        rbAudioNormal = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        cmbFormato = new javax.swing.JComboBox<>();
        lblStatus = new javax.swing.JLabel();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText("jCheckBoxMenuItem2");

        jCheckBoxMenuItem3.setSelected(true);
        jCheckBoxMenuItem3.setText("jCheckBoxMenuItem3");

        setLayout(null);

        jLabel1.setText("Url del Vídeo:");
        add(jLabel1);
        jLabel1.setBounds(20, 30, 80, 20);

        txtUrl.setToolTipText("Pega Aquí la URL del Vídeo");
        add(txtUrl);
        txtUrl.setBounds(100, 30, 440, 22);

        chkSoloAudio.setText("Descargar solo audio (mp3)");
        chkSoloAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSoloAudioActionPerformed(evt);
            }
        });
        add(chkSoloAudio);
        chkSoloAudio.setBounds(20, 70, 190, 20);

        buttonGroup1.add(rbVideo1080);
        rbVideo1080.setText("1080p");
        add(rbVideo1080);
        rbVideo1080.setBounds(270, 130, 80, 20);

        buttonGroup1.add(rbVideoMejor);
        rbVideoMejor.setSelected(true);
        rbVideoMejor.setText("Mejor Disponible");
        rbVideoMejor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbVideoMejorActionPerformed(evt);
            }
        });
        add(rbVideoMejor);
        rbVideoMejor.setBounds(130, 130, 130, 21);

        btnDescargar.setText("Descargar");
        btnDescargar.setToolTipText("Haz clic para iniciar la descarga ! ");
        btnDescargar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDescargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescargarActionPerformed(evt);
            }
        });
        add(btnDescargar);
        btnDescargar.setBounds(10, 260, 260, 40);

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 300, 590, 130);

        btnAbrirVideo.setText("Reproducir último archivo descargado");
        btnAbrirVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirVideoActionPerformed(evt);
            }
        });
        add(btnAbrirVideo);
        btnAbrirVideo.setBounds(10, 430, 290, 23);

        progressBar.setStringPainted(true);
        add(progressBar);
        progressBar.setBounds(270, 260, 330, 20);

        jLabel2.setText("Calidad de Video:");
        add(jLabel2);
        jLabel2.setBounds(20, 120, 110, 30);

        buttonGroup1.add(rbVideo720);
        rbVideo720.setText("720p");
        rbVideo720.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbVideo720ActionPerformed(evt);
            }
        });
        add(rbVideo720);
        rbVideo720.setBounds(350, 130, 70, 21);

        jLabel3.setText("Calidad de Audio (solo si es mp3):");
        add(jLabel3);
        jLabel3.setBounds(20, 100, 210, 16);

        buttonGroup2.add(rbAudioBuena);
        rbAudioBuena.setSelected(true);
        rbAudioBuena.setText("Buena (192kbps)");
        add(rbAudioBuena);
        rbAudioBuena.setBounds(240, 100, 130, 21);

        buttonGroup2.add(rbAudioNormal);
        rbAudioNormal.setText("Normal (128kbps)");
        add(rbAudioNormal);
        rbAudioNormal.setBounds(390, 100, 130, 21);

        jLabel4.setText("Formato de Salida: ");
        add(jLabel4);
        jLabel4.setBounds(20, 160, 130, 40);

        cmbFormato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "mp4", "mkv", "webm", "mp3" }));
        add(cmbFormato);
        cmbFormato.setBounds(160, 170, 72, 22);
        add(lblStatus);
        lblStatus.setBounds(270, 280, 330, 20);
    }// </editor-fold>//GEN-END:initComponents

    private void chkSoloAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSoloAudioActionPerformed
        if (chkSoloAudio.isSelected()){
            cmbFormato.setModel(audioFormatsModel);
            cmbFormato.setSelectedItem("mp3");
        }else {
            cmbFormato.setModel(videoFormatsModel);
            cmbFormato.setSelectedItem("mp4");
        }
    }//GEN-LAST:event_chkSoloAudioActionPerformed

    private void rbVideoMejorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbVideoMejorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbVideoMejorActionPerformed

    private void rbVideo720ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbVideo720ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbVideo720ActionPerformed

    private void btnDescargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescargarActionPerformed
        //Preparamos la GUI para la descarga
        progressBar.setValue(0);
        txtLog.setText("");
        btnDescargar.setEnabled(false);
        
        //Leemos todas las opciones de la GUI
        String url = txtUrl.getText();
        if (url == null || url.trim().isEmpty() || url.equals("Pega Aquí la URL del vídeo")){
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor introduce una URL válida.","Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            btnDescargar.setEnabled(true);
            return;
        }
        boolean soloAudio = chkSoloAudio.isSelected();
        String formato = cmbFormato.getSelectedItem().toString();
        
        String calidadVideo = "";
        if (rbVideoMejor.isSelected()) {
            calidadVideo = "mejor";
        } else if (rbVideo1080.isSelected()) {
            calidadVideo = "1080";
        } else if (rbVideo720.isSelected()) {
            calidadVideo = "720";
        }
        String calidadAudio = "";
        if (rbAudioBuena.isSelected()) {
            calidadAudio = "buena";
        } else if (rbAudioNormal.isSelected()) {
            calidadAudio = "normal";
        }

        List<String> command = new ArrayList<>();

        // Configurar el comando base (ruta yt-dlp)
        String rutaYtDlp = parentFrame.getRutaYtDlp();
        command.add(rutaYtDlp);
        command.add("--restrict-filenames");

        // Determinar la ruta de ffmpeg (necesario para fusionar y convertir formatos)
        try {
            File ytDlpFile = new File(rutaYtDlp);
            // Verificamos que el archivo existe y tiene un directorio padre válido
            // (Esto evita el error si rutaYtDlp es solo "yt-dlp.exe")
            if (ytDlpFile.exists() && ytDlpFile.getParentFile() != null) {
                String ffmpegDirectory = ytDlpFile.getParentFile().getAbsolutePath();
                command.add("--ffmpeg-location");
                command.add(ffmpegDirectory);
            }
        } catch (Exception e) {
            System.err.println("No se pudo determinar la ruta de ffmpeg automáticamente (se usará PATH).");
        }
            // Lógica de opciones (Audio o Vídeo)
            if (soloAudio) {
                command.add("-x");
            command.add("--audio-format");
            command.add(formato);
            
            command.add("-f");
            command.add("bestaudio");

            if (calidadAudio.equals("buena")) {
                command.add("--audio-quality");
                command.add("192K");
            } else if (calidadAudio.equals("normal")) {
                command.add("--audio-quality");
                command.add("128K");
            }
        } else { 
            // --- CORRECCIÓN VÍDEO: COMPATIBILIDAD MP4 ---
            command.add("--merge-output-format");
            command.add(formato);
            
            // Estrategia de selección de calidad inteligente
            if (formato.equals("mp4")) {
                // Si elige MP4, intentamos H264+AAC para máxima compatibilidad en Windows
                if (calidadVideo.equals("1080p")){
                    command.add("-f");
                    command.add("bestvideo[height<=1080][ext=mp4]+bestaudio[ext=m4a]/bestvideo[height<=1080]+bestaudio/best");
                } else if (calidadVideo.equals("720p")){
                    command.add("-f");
                    command.add("bestvideo[height<=720][ext=mp4]+bestaudio[ext=m4a]/bestvideo[height<=720]+bestaudio/best");
                } else {
                    // Mejor disponible
                    command.add("-f");
                    command.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/bestvideo+bestaudio/best");
                }
            } else {
                // Si elige MKV/WebM, buscamos la mejor calidad absoluta (VP9/Opus)
                if (calidadVideo.equals("1080p")){
                    command.add("-f");
                    command.add("bestvideo[height<=1080]+bestaudio/best[height<=1080]");
                } else if (calidadVideo.equals("720p")){
                    command.add("-f");
                    command.add("bestvideo[height<=720]+bestaudio/best[height<=720]");
                }
                // Si es "Mejor Disponible", yt-dlp decide (suele ser la máxima)
            }
        }
        // Rutas de salida y límite de velocidad (leídas desde las preferencias guardadas).
        command.add("-o");

        // CORRECCIÓN: Declaramos la variable String y la envolvemos en comillas para el ejecutable
        // Obtenemos la ruta y usamos el separador de archivos del sistema operativo
        String outputPath = parentFrame.getRutaGuardado() + java.io.File.separator + "%(title)s.%(ext)s";

        // Añadimos la ruta al comando, envuelta en comillas dobles (") para manejar espacios en las carpetas de Windows.
        command.add("\"" + outputPath + "\"");
        // Añadir límite de velocidad si está configurado en las preferencias.
        String limite = parentFrame.getLimiteVelocidad();
        if (limite != null && !limite.trim().isEmpty() && !limite.equals("0")) {
            command.add("-r");
            command.add(limite + "K");
        }

        // Añadir URL y Ejecutar Worker.
        command.add(url);
        try {
            // Intentamos ejecutar el Worker
            DownloadWorker worker = new DownloadWorker(command, progressBar, txtLog, btnDescargar, this, parentFrame);
            worker.execute();

        } catch (Exception ex) {
            // REGISTRO TÉCNICO: Se guarda en el error_log.txt
            LoggerError.log("Error crítico al intentar iniciar el hilo de descarga", ex);

            // FEEDBACK VISUAL: Diálogo para el usuario
            JOptionPane.showMessageDialog(this,
                    "No se pudo iniciar el proceso de descarga.\nDetalle: " + ex.getMessage(),
                    "Fallo de Inicio",
                    JOptionPane.ERROR_MESSAGE);

            // RESTAURACIÓN: Devolvemos el control al botón
            btnDescargar.setEnabled(true);
        }
    }//GEN-LAST:event_btnDescargarActionPerformed
    /* 
    * Lógica de reproducción
    */
    private void btnAbrirVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirVideoActionPerformed
        // Primero, comprobamos si la variable "ultimoArchivoDescargado" tiene algo.
        if (ultimoArchivoDescargado == null || ultimoArchivoDescargado.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Aún no se ha descargado ningún archivo en esta sesión.",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return; // No seguimos
        }

        try {
            // Creamos un objeto File con la ruta que guardamos
            java.io.File fileToOpen = new java.io.File(ultimoArchivoDescargado);

            // Comprobamos si el archivo existe de verdad
            if (!fileToOpen.exists()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "No se encuentra el archivo en la ruta: " + ultimoArchivoDescargado,
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Le pide al "Escritorio" que abra ese archivo
            // con el programa por defecto (QuickTime, VLC, etc.)
            java.awt.Desktop.getDesktop().open(fileToOpen);

        } catch (Exception e) {
            // Por si algo falla manejo de errores de I/O o si Desktop no está soportado.
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el archivo. Error: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAbrirVideoActionPerformed
    
    //MÉTODOS PÚBLICOS PARA EL WORKER
    /**
     * Llamado por el DownloadWorker al finalizar con éxito la descarga. Guarda
     * la entrada en el log.json y notifica al panel de la Biblioteca que
     * recargue.
     *
     * @param rutaFinalArchivo La ruta absoluta del archivo descargado.
     * Crea el objeto MediaFile, lo guarda en el historial y actualiza la interfaz.
     */
    public void notificarDescargaCompleta(String rutaFinalArchivo) throws IOException {
        
        // 1. Convertir la ruta (String) en un archivo real (File)
        File archivoEnDisco = new File(rutaFinalArchivo);

        // Verificamos que exista para evitar errores
        if (archivoEnDisco.exists()) {
            
            // Crear el objeto MediaFile
            // El constructor de MediaFile analiza el archivo para sacar tamaño, fecha, etc.
            MediaFile nuevoArchivo = new MediaFile(archivoEnDisco);
            
            // 3. Obtener la ruta de guardado ACTUAL del MainFrame
            // (Para asegurarnos de que guardamos el log.json en la carpeta correcta)
            String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
            
            // 4. Inicializar el Gestor en esa carpeta y GUARDAR
            GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
            gestor.anadirArchivo(nuevoArchivo);

            // 5. Actualizar la UI de este panel (para que el botón reproducir funcione)
            setUltimoArchivoDescargado(rutaFinalArchivo);
            btnAbrirVideo.setEnabled(true); 

            // 6. FORZAR LA ACTUALIZACIÓN DE LA BIBLIOTECA
            // Esto hace que el archivo aparezca en la otra pestaña inmediatamente
            BibliotecaPanel bibliotecaPanel = parentFrame.getPanelBiblioteca();
            if (bibliotecaPanel != null) {
                bibliotecaPanel.aplicarFiltrosYOrden();
            }

            // 7. Informar al usuario
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Descarga finalizada y registrada en la biblioteca.",
                    "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);      
        } else {
            System.err.println("Error: El archivo descargado no aparece en el disco: " + rutaFinalArchivo);
        }
    }
    
    //Método para actualizar el texto de estado desde el Worker
    public void setEstadoLabel(String texto) {
        lblStatus.setText(texto);
    }

    /**
     * Permite al DownloadWorker guardar la ruta del archivo final.
     * @param ruta La ruta del archivo descargado
     */
    public void setUltimoArchivoDescargado(String ruta){
        this.ultimoArchivoDescargado = ruta;
    }
    
    /**Permite al DownloadWorker comprobar si ya se ha guardado una ruta.
     * @return La ruta del último archivo descargado
     */
    public String getUltimoArchivoDescargado(){
        return this.ultimoArchivoDescargado;
    }

    /**
     * Permite a otras clases como DownloadWorker Habilitar/Deshabilitar el botón Descargar.
     */
    public void setBotonDescargarHabilitado(boolean enabled){
        btnDescargar.setEnabled(enabled);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirVideo;
    private javax.swing.JButton btnDescargar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox chkSoloAudio;
    private javax.swing.JComboBox<String> cmbFormato;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rbAudioBuena;
    private javax.swing.JRadioButton rbAudioNormal;
    private javax.swing.JRadioButton rbVideo1080;
    private javax.swing.JRadioButton rbVideo720;
    private javax.swing.JRadioButton rbVideoMejor;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
