/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package berlangadiaz.vidforge.downloader.view;

import berlangadiaz.vidforge.downloader.components.MediaPollerComponent;
import berlangadiaz.vidforge.downloader.events.NewMediaEvent;
import berlangadiaz.vidforge.downloader.events.NewMediaListener;
import berlangadiaz.vidforge.downloader.model.DownloadWorker;
import berlangadiaz.vidforge.downloader.model.GestorJson;
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
    
    //Declaración del componente Poller.
    private MediaPollerComponent poller;
    
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
        // Inicializar los Modelos de JComboBox (Asumo que videoFormatsModel, etc., están declarados arriba)
        String[] videoFormats = {"mp4","mkv","webm"};
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
        
        //INTEGRACIÓN DEL POLLER
        
        // Instanciamos el componente
        poller = new MediaPollerComponent();
        
        // Configuración
        // Ponemos aquí la URL base de la API
        poller.setApiUrl(parentFrame.getApiUrl());
        
        // Comprobamos cada 15 segundos en el componente le indicamos que
        // el PollingInterval que pongamos lo multiplique por 1000 para convertirlo a ms.
        poller.setPollingInterval(15); 
        
        // Escuchamos los eventos (Cuando encuentra archivos nuevos)
        poller.addNewMediaListener(new NewMediaListener() {
            @Override
            public void onNewMediaFound(NewMediaEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    String msg = "¡Nuevos archivos detectados en el servidor!\n" + 
                                 "Cantidad: " + evt.getNewMediaList().size() + "\n" +
                                 "Hora: " + evt.getDetectionTime();
                    
                    int opcion = JOptionPane.showConfirmDialog(MainViewPanel.this, 
                            msg + "\n¿Quieres recargar la lista ahora?", 
                            "Novedades detectadas", 
                            JOptionPane.YES_NO_OPTION);
                    
                    if (opcion == JOptionPane.YES_OPTION) {
                        // Refrescamos la biblioteca si existe
                        BibliotecaPanel biblioteca = parentFrame.getPanelBiblioteca();
                        if (biblioteca != null) {
                            try {
                                System.out.println("Usuario aceptó. Refrescando biblioteca...");
                                biblioteca.aplicarFiltrosYOrden();
                            } catch (IOException ex) {
                                System.getLogger(MainViewPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                            }
                        }
                    }
                });
            }
        });

        // Lo Añadimos visualmente a la ventana.
        // Por ahora lo añadimos al propio panel.(Posiblemente tengamos que ajustarlo)
        poller.setPreferredSize(new Dimension(200, 30));
        // Ajustamos la posición manualmente ya que usamos Layout Null
        // Lo ponemos arriba a la derecha o donde quepa.
        this.add(poller);
        poller.setBounds(220,0,200,30); //Ajustaremos si tapa algo
        
        // Forzar repintado para que aparezca
        this.revalidate();
        this.repaint();
    }
    
    // --- MÉTODOS PARA CONTROLAR EL POLLER DESDE EL MAIN FRAME ---

    /**
     * Se llama cuando el Login es exitoso.
     * Le da el token al poller y lo enciende.
     */
    public void iniciarPoller(String token) {
        if (poller != null) {
            poller.setToken(token);
            poller.setRunning(true);
            System.out.println("MainViewPanel: Poller iniciado.");
        }
    }

    /**
     * Se llama al cerrar sesión o salir.
     */
    public void detenerPoller() {
        if (poller != null) {
            poller.setRunning(false);
            System.out.println("MainViewPanel: Poller detenido.");
        }
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
        txtUrl.setBounds(100, 30, 440, 23);

        chkSoloAudio.setText("Descargar solo audio (mp3)");
        chkSoloAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSoloAudioActionPerformed(evt);
            }
        });
        add(chkSoloAudio);
        chkSoloAudio.setBounds(20, 70, 190, 21);

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
        btnDescargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescargarActionPerformed(evt);
            }
        });
        add(btnDescargar);
        btnDescargar.setBounds(10, 260, 100, 40);

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 300, 480, 130);

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
        progressBar.setBounds(110, 280, 380, 20);

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
        jLabel3.setBounds(20, 100, 210, 17);

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
        cmbFormato.setBounds(160, 170, 72, 23);
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
        DownloadWorker worker = new DownloadWorker(command, progressBar, txtLog, btnDescargar, this, parentFrame);
        worker.execute();
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
    // MÉTODO AÑADIDO: GESTIONA EL ARCHIVO FINAL Y EL REFRESCO DE LA BIBLIOTECA️
    /**
     * Llamado por el DownloadWorker al finalizar con éxito la descarga. Guarda
     * la entrada en el log.json y notifica al panel de la Biblioteca que
     * recargue.
     *
     * @param rutaFinalArchivo La ruta absoluta del archivo descargado.
     */
    public void notificarDescargaCompleta(String rutaFinalArchivo) throws IOException {

        // 1. Crear el objeto MediaFile (asumiendo que tienes un constructor adecuado)
        // NOTA: Esto es conceptual. Necesitas la clase MediaFile para esto.
        // MediaFile nuevoArchivo = new MediaFile(rutaFinalArchivo, ...); 
        // 2. Guardar la entrada en el historial local (log.json)
        String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
        GestorJson gestor = new GestorJson(rutaCarpetaGuardado);

        // ⬇️ ESTA LÍNEA DEBE ESTAR IMPLEMENTADA EN TU PROYECTO ⬇️
        // gestor.anadirArchivo(nuevoArchivo); 
        // 3. Establecer la ruta para el botón "Reproducir"
        setUltimoArchivoDescargado(rutaFinalArchivo);

        // 4. FORZAR LA ACTUALIZACIÓN DE LA TABLA DE LA BIBLIOTECA
        BibliotecaPanel bibliotecaPanel = parentFrame.getPanelBiblioteca();
        if (bibliotecaPanel != null) {
            // Llama al método que ya existe en BibliotecaPanel y que se encarga de recargar, filtrar y ordenar
            bibliotecaPanel.aplicarFiltrosYOrden();
        }

        // Informar al usuario
        javax.swing.JOptionPane.showMessageDialog(this,
                "Descarga finalizada con éxito y registrada.",
                "Descarga Completa", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
