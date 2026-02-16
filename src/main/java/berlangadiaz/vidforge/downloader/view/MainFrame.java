package berlangadiaz.vidforge.downloader.view;

import berlangadiaz.vidforge.downloader.model.GestorJson;
import com.berlangadiaz.dimedianet.api.Media;
import com.berlangadiaz.dimedianet.api.Usuari;
import com.berlangadiaz.dimedianet.api.ApiClient;
import com.berlangadiaz.dimedianet.component.DiMediaLink;
import com.berlangadiaz.dimedianet.api.ApiClient;
import com.berlangadiaz.dimedianet.api.Usuari;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Clase principal de la interfaz gráfica (GUI) y Contenedor Principal (Controller).
 * Este frame inicializa todos los paneles, gestiona el flujo de la aplicación (Login/Vistas)
 * y almacena el estado global, incluyendo las preferencias de descarga y la información de la sesión de la API.
 * @author Jaime Berlanga Diaz
 */
public class MainFrame extends javax.swing.JFrame {
    
    // --- VARIABLES DE VISTA ---
    private LoginPanel loginPanel;
    private MainViewPanel mainViewPanel;
    private PreferenciasPanel panelPreferencias;
    private BibliotecaPanel panelBiblioteca;
    
    // --- VARIABLES DE PREFERENCIAS Y ESTADO ---
    // Valores por defecto al iniciar o si el archivo config.json no existe.
    private String rutaYtDlp = getDefaultYtDlpPath();
    private String rutaGuardado = System.getProperty("user.home") + File.separator +"Downloads"; 
    private boolean crearM3u = false;
    private String limiteVelocidad = "";
    
    // --- VARIABLES PARA LA ORDENACIÓN DE LA BIBLIOTECA ---
    private int columnaOrdenActual = 0; 
    private boolean ordenAscendente = true; 

    // --- VARIABLES PARA EL LOGIN ---
    // private ApiClient apiClient; // Instancia del cliente antigua
    private String currentJwtToken = null; // Token de la sesión activa
    private Usuari currentUser = null; // Objeto del usuario logueado
    private static final String API_BASE_URL = "https://difreenet9.azurewebsites.net"; // URL Base de la API
 
    /**
     * Constructor principal del MainFrame.
     * Carga las preferencias guardadas, inicializa el cliente API y gestiona el flujo de Auto-Login.
     */
    public MainFrame() {        
        // Definimos la ruta por defecto (Donde buscamos si no sabemos nada)
        String rutaUsuario = System.getProperty("user.home");
        // Usamos File.separator para que funcione bien en Windows y Mac
        this.rutaGuardado = rutaUsuario + File.separator + "Downloads";
        
        GestorJson gestorUsuario = new GestorJson(rutaUsuario);
        GestorJson.Configuracion config = gestorUsuario.leerConfiguracion();
        
        // 2. ¿Encontramos el mapa del tesoro?
        if (config != null) {
            // Cargamos valores básicos
            this.rutaYtDlp = config.rutaYtDlp;
            this.crearM3u = config.crearM3u;
            this.limiteVelocidad = config.limiteVelocidad;
            
            // LA CLAVE: ¿La ruta guardada es distinta a la del usuario?
            if (config.rutaGuardado != null && !config.rutaGuardado.isEmpty()) {
                // ¡SÍ! Actualizamos la ruta de trabajo a la carpeta personalizada (ej: D:\Pelis)
                this.rutaGuardado = config.rutaGuardado;
                System.out.println("Redireccionando trabajo a: " + this.rutaGuardado);
            }
        } else {
            // Primera vez: valores por defecto
            this.rutaYtDlp = getDefaultYtDlpPath();
            this.crearM3u = false;
            this.limiteVelocidad = "";
        }
        // Inicialización de ApiClients y GUI.
        // this.apiClient = new ApiClient(API_BASE_URL); 
        
        initComponents();
        setLocationRelativeTo(null);
        
        //Inicialización de Paneles
        mainViewPanel = new MainViewPanel(this);
        loginPanel = new LoginPanel(this);
        panelPreferencias = new PreferenciasPanel(this);
        panelBiblioteca = new BibliotecaPanel(this);
        //Muestra MainViewPanel por defecto (será ocultado si hay Login).
        panelContenedor.add(mainViewPanel, java.awt.BorderLayout.CENTER);
        
        //Lógica de Auto-Login y Flujo de Vista
        String token = GestorJson.getToken();
        long expiration = GestorJson.getTokenExpirationTime();
        
        if (token != null && !token.isEmpty() && System.currentTimeMillis() < expiration){
            // Token válido: Intentamos reanudar la sesión
            try {
                this.resumeSession(token); 
                mostrarVistaPrincipal();
            } catch (Exception e) {
                 // Si falla (si el token expiró en el servidor o fue inválido),
                 // forzamos el Login.
                 GestorJson.clearToken();
                 mostrarVistaLogin();
            }
        } else {
            // Token no existe o expiró: Muestra el Login.
            mostrarVistaLogin();
        }
       
        
        panelContenedor.revalidate();
        panelContenedor.repaint();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        diMediaNetPoller = new com.berlangadiaz.dimedianet.component.DiMediaLink();
        panelContenedor = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        itemLogout = new javax.swing.JMenuItem();
        itemSalir = new javax.swing.JMenuItem();
        menuVer = new javax.swing.JMenu();
        itemMostrarDescarga = new javax.swing.JMenuItem();
        itemMostrarBiblioteca = new javax.swing.JMenuItem();
        menuEditar = new javax.swing.JMenu();
        itemPreferencias = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        itemAcerdaDe = new javax.swing.JMenuItem();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jMenu5.setText("File");
        jMenuBar3.add(jMenu5);

        jMenu6.setText("Edit");
        jMenuBar3.add(jMenu6);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VidForge Downloader");
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        diMediaNetPoller.setPreferredSize(new java.awt.Dimension(800, 30));
        diMediaNetPoller.addNewMediaListener(new com.berlangadiaz.dimedianet.events.NewMediaListener() {
            public void onNewMediaFound(com.berlangadiaz.dimedianet.events.NewMediaEvent evt) {
                diMediaNetPollerOnNewMediaFound(evt);
            }
        });
        getContentPane().add(diMediaNetPoller, java.awt.BorderLayout.SOUTH);

        panelContenedor.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panelContenedor, java.awt.BorderLayout.CENTER);

        jMenuBar1.setPreferredSize(new java.awt.Dimension(128, 30));

        menuArchivo.setText("Archivo");

        itemLogout.setText("Cerrar Sesión");
        itemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemLogoutActionPerformed(evt);
            }
        });
        menuArchivo.add(itemLogout);

        itemSalir.setText("Salir");
        itemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSalirActionPerformed(evt);
            }
        });
        menuArchivo.add(itemSalir);

        jMenuBar1.add(menuArchivo);

        menuVer.setText("Ver");

        itemMostrarDescarga.setText("Panel De Descargas");
        itemMostrarDescarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMostrarDescargaActionPerformed(evt);
            }
        });
        menuVer.add(itemMostrarDescarga);

        itemMostrarBiblioteca.setText("Biblioteca");
        itemMostrarBiblioteca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMostrarBibliotecaActionPerformed(evt);
            }
        });
        menuVer.add(itemMostrarBiblioteca);

        jMenuBar1.add(menuVer);

        menuEditar.setText("Editar");

        itemPreferencias.setText("Preferencias");
        itemPreferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPreferenciasActionPerformed(evt);
            }
        });
        menuEditar.add(itemPreferencias);

        jMenuBar1.add(menuEditar);

        menuAyuda.setText("Ayuda");

        itemAcerdaDe.setText("Acerca De..");
        itemAcerdaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAcerdaDeActionPerformed(evt);
            }
        });
        menuAyuda.add(itemAcerdaDe);

        jMenuBar1.add(menuAyuda);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemPreferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPreferenciasActionPerformed
        panelPreferencias.cargarPreferenciasActuales();   
        panelContenedor.remove(mainViewPanel);
        panelContenedor.remove(panelBiblioteca);
        panelContenedor.add(panelPreferencias, java.awt.BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }//GEN-LAST:event_itemPreferenciasActionPerformed

    private void itemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalirActionPerformed
        Object[] options = {"Sí", "No"};
        int choice = javax.swing.JOptionPane.showOptionDialog(this, "¿Estás seguro de que desea SALIR?",
                "Confirmar Salida", javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_itemSalirActionPerformed

    private void itemAcerdaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAcerdaDeActionPerformed
        AboutDialog aboutDialog = new AboutDialog(this, true); 
        aboutDialog.setLocationRelativeTo(this); 
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_itemAcerdaDeActionPerformed

    private void itemMostrarBibliotecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMostrarBibliotecaActionPerformed
        try {
            panelBiblioteca.aplicarFiltrosYOrden();
        } catch (IOException ex) {
            System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        mostrarVistaBiblioteca();
    }//GEN-LAST:event_itemMostrarBibliotecaActionPerformed

    private void itemMostrarDescargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMostrarDescargaActionPerformed
        mostrarVistaPrincipal();
    }//GEN-LAST:event_itemMostrarDescargaActionPerformed

    private void itemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemLogoutActionPerformed
        // 1. Detener el componente (Poller)
        diMediaNetPoller.setRunning(false);
        diMediaNetPoller.setToken(null);

        // 2. Limpiar la persistencia
        GestorJson.clearToken();

        this.currentJwtToken = null;
        this.currentUser = null;

        mostrarVistaLogin();

        JOptionPane.showMessageDialog(this,
                "Sesión cerrada con éxito.",
                "Logout", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_itemLogoutActionPerformed

    private void diMediaNetPollerOnNewMediaFound(com.berlangadiaz.dimedianet.events.NewMediaEvent evt) {//GEN-FIRST:event_diMediaNetPollerOnNewMediaFound
        // 1. Extraemos la información del evento
        int nuevos = evt.getNewMediaList().size();
        String hora = evt.getDiscoveryTime(); // String que configuramos en el componente

        // 2. Creamos el mensaje
        String mensaje = "¡Sincronización completada!\n"
                + "Se han detectado " + nuevos + " archivos nuevos en la red.\n"
                + "Hora del hallazgo: " + hora;

        // 3. Mostramos el diálogo emergente
        javax.swing.JOptionPane.showMessageDialog(this,
                mensaje,
                "Novedades en Di Media Net",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // 4. (Opcional) Forzar refresco de la biblioteca local para ver cambios al momento
        try {
            if (panelBiblioteca != null) {
                panelBiblioteca.aplicarFiltrosYOrden();
            }
        } catch (java.io.IOException ex) {
            System.err.println("Error al refrescar biblioteca tras hallazgo: " + ex.getMessage());
        }
    }//GEN-LAST:event_diMediaNetPollerOnNewMediaFound

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo cargar FlatLaf. Se usará el diseño por defecto.");
        }

        /* Crear y mostrar la ventana */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // --- MÉTODOS DE SERVICIO (API Y LOGIN) ---
    /**
     * Implementa el proceso de Login completo: autenticación (login) y obtención 
     * de los datos del usuario (getMe).
     *
     * @param email Email del usuario.
     * @param password Contraseña del usuario.
     * @return El objeto {@code Usuari} si el login es exitoso.
     * @throws Exception Si falla la autenticación o la conexión.
     */
    public Usuari attemptLogin(String email, String password) throws Exception {
        // CAMBIO AQUÍ: Usamos el wrapper del componente en lugar de pedir el cliente al panel
        try {
            String token = diMediaNetPoller.login(email, password); // (Usamos el Wrapper)

            if (token != null && !token.isBlank()) {
                // Usamos el wrapper para obtener los datos del usuario
                Usuari user = diMediaNetPoller.getMe(token);

                if (user != null) {
                    this.currentJwtToken = token;
                    this.currentUser = user;
                    return user;
                }
            }
            throw new IOException("Fallo de autenticación o datos de usuario incorrectos.");
        } catch (Exception e) {
            // Registramos el intento fallido con el email para auditoría de errores
            berlangadiaz.vidforge.downloader.model.LoggerError.log("Fallo en intento de login para: " + email, e);

            // Re-lanzamos para que el LoginPanel muestre el JOptionPane al usuario
            throw e;
        }
    }

    /**
     * Reanuda una sesión previamente guardada utilizando un token JWT
     * persistente. Valida el token llamando al endpoint /getMe de la API.
     *
     * @param token El token JWT a validar.
     * @return El objeto {@code Usuari} si el token es válido.
     * @throws Exception Si el token no es válido o ha expirado en el servidor.
     */
    public Usuari resumeSession(String token) throws Exception {
        // CAMBIO AQUÍ: Usamos el componente directamente
        Usuari user = diMediaNetPoller.getMe(token);

        if (user != null) {
            this.currentJwtToken = token;
            this.currentUser = user;
            return user;
        }
        throw new IOException("Token no válido o expirado en el servidor.");
    }

    /**
     * Devuelve el token JWT activo. Usado por LoginPanel para persistir la sesión.
     *
     * @return El token JWT actual de la sesión.
     */
    public String getCurrentJwtToken() {
        return currentJwtToken;
    }
    
    // --- MÉTODOS DE NAVEGACIÓN Y VISTAS ---
    
    //Método público para cambiar a la vista de la Biblioteca.   
    public void mostrarVistaBiblioteca(){
        //Quita todos los otros paneles
        panelContenedor.remove(panelPreferencias);
        panelContenedor.remove(mainViewPanel);
        panelContenedor.remove(loginPanel);
        
        //Añade el panel de biblioteca
        panelContenedor.add(panelBiblioteca, java.awt.BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    
    // Método para mostrar el formulario de inicio de sesión (LoginPanel).
    public void mostrarVistaLogin() {
        panelContenedor.removeAll();

        // Configura el LoginPanel
        loginPanel.clearFields(); 

        panelContenedor.add(loginPanel, java.awt.BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();
        
        // Ocultar los menús al no estar logueado
        if (jMenuBar1 != null) {
            jMenuBar1.setVisible(false);
        }
    }

    // Método para mostrar el panel principal (MainViewPanel).
    public void mostrarVistaPrincipal() {
        // 1. Limpiamos solo el contenedor de las vistas (Login/Principal/Biblioteca)
        panelContenedor.removeAll();

        // 2. Añadimos la vista principal al centro
        panelContenedor.add(mainViewPanel, java.awt.BorderLayout.CENTER);

        // 3. Activamos el motor de búsqueda si tenemos el token
        if (this.currentJwtToken != null && !this.currentJwtToken.isEmpty()) {
            // Configuramos y encendemos
            diMediaNetPoller.setToken(this.currentJwtToken);
            diMediaNetPoller.setRunning(true);

            // Nos aseguramos de que el componente sea visible
            diMediaNetPoller.setVisible(true);

            // Forzamos el refresco para que se pinte en VERDE inmediatamente
            diMediaNetPoller.revalidate();
            diMediaNetPoller.repaint();

            System.out.println("DEBUG: Poller activado y visible.");
        }

        // 4. Refrescamos el contenedor de vistas
        panelContenedor.revalidate();
        panelContenedor.repaint();

        // Mostrar los menús
        if (jMenuBar1 != null) {
            jMenuBar1.setVisible(true);
        }
    }

    /**
     * Devuelve la instancia del panel de la biblioteca.
     * Es esencial para que otras vistas (como MainViewPanel o el DownloadWorker)
     * puedan notificar al panel de la biblioteca que debe recargar la lista
     * después de guardar un nuevo archivo en el log.json.
     * * @return La instancia del {@code BibliotecaPanel}.
     */
    public BibliotecaPanel getPanelBiblioteca() {
        return panelBiblioteca;
    }

    /**
     * Guarda todas las preferencias actuales (rutas, opciones) en las variables de clase
     * y las persiste en el archivo config.json llamando al GestorJson.
     *
     * @param ytDlp Ruta del ejecutable yt-dlp.
     * @param guardado Ruta de la carpeta de guardado de archivos.
     * @param m3u Si se debe crear un archivo M3U (usado para la opción 'Solo Audio').
     * @param limite El límite de velocidad de descarga.
     */
    public void guardarTodasLasPreferencias(String ytDlp, String guardado, boolean m3u, String limite){
        //Guardar en las variables de la clase
        this.setRutaYtDlp(ytDlp);
        this.setRutaGuardado(guardado);
        this.setCrearM3u(m3u);
        this.setLimiteVelocidad(limite);
        
        //Guardar en el archivo JSON
        //Nota: usamos la nueva ruta de guardado (el parámetro 'guardado') para saber dónde
        //poner el config.json.
        try {
            // Guardar en el archivo JSON
            GestorJson gestorReal = new GestorJson(guardado);
            gestorReal.guardarConfiguracion(ytDlp, guardado, m3u, limite);
            // Guardamos el "redireccionado" del json para recordar al reiniciar 
            // Esto sirve para que al abrir la app sepa ir al paso 1.
            if (!guardado.equals(System.getProperty("user.home"))) {
                GestorJson gestorPointer = new GestorJson(System.getProperty("user.home"));
                gestorPointer.guardarConfiguracion(ytDlp, guardado, m3u, limite);
            }
        } catch (IOException e) {
            // 1. Log del error para el archivo de texto
            berlangadiaz.vidforge.downloader.model.LoggerError.log("Error crítico al guardar la configuración en disco", e);

            // 2. Feedback visual
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la configuración.\n" + e.getMessage(),
                    "Error de Archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*Determina el comando yt-dlp por defecto basado en el Sistema Operativo
    *Esto asume que el usuario ha instalado la herramienta en su PATH
    *
    * @return "yt-dlp.exe" para windows, o "yt-dlp" para Mac/Linux
    */
    private String getDefaultYtDlpPath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "yt-dlp.exe";
        } else {
            return "yt-dlp";
        }
    }
    
    //Getters y Setters
    public String getRutaYtDlp(){
        return this.rutaYtDlp;
    }
    public void setRutaYtDlp(String ruta){
        this.rutaYtDlp = ruta;
    }
    
    public String getRutaGuardado(){
        return this.rutaGuardado;
    }
    public void setRutaGuardado(String ruta){
        this.rutaGuardado = ruta;
    }
    public boolean isCrearM3u(){
        return crearM3u;
    }
    public void setCrearM3u(boolean crearM3u){
        this.crearM3u = crearM3u;
    }
    public String getLimiteVelocidad(){
        return limiteVelocidad;
    }
    public void setLimiteVelocidad(String limiteVelocidad){
        this.limiteVelocidad = limiteVelocidad;
    }   
    public int getColumnaOrdenActual(){
        return columnaOrdenActual;
    }
    public void setColumnaOrdenActual(int columnaOrdenActual){
        this.columnaOrdenActual = columnaOrdenActual;
    }
    public boolean isOrdenAscendente(){
        return ordenAscendente;
    }
    public void setOrdenAscendente(boolean ordenAscendente){
        this.ordenAscendente = ordenAscendente;
    }
    // Getter para compartir la URL de la API con los hijos y el día de mañana
    // si se cambiara la URL no tener que cambiarla en todos lados.
    public String getApiUrl() {
        return API_BASE_URL;
    }

    /**
     * Método público para compartir el cliente API con los paneles hijos. Esto
     * permite a DownloadWorker usar la conexión del MainFrame.
     * @return 
     */
    public ApiClient getApiClient() {
        if (diMediaNetPoller != null) {
            return diMediaNetPoller.getApiClient();
        }
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.berlangadiaz.dimedianet.component.DiMediaLink diMediaNetPoller;
    private javax.swing.JMenuItem itemAcerdaDe;
    private javax.swing.JMenuItem itemLogout;
    private javax.swing.JMenuItem itemMostrarBiblioteca;
    private javax.swing.JMenuItem itemMostrarDescarga;
    private javax.swing.JMenuItem itemPreferencias;
    private javax.swing.JMenuItem itemSalir;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JMenu menuEditar;
    private javax.swing.JMenu menuVer;
    private javax.swing.JPanel panelContenedor;
    // End of variables declaration//GEN-END:variables
}
