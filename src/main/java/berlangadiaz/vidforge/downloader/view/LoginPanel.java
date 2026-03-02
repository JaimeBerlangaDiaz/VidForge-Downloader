package berlangadiaz.vidforge.downloader.view;

import com.berlangadiaz.dimedianet.api.Media;
import com.berlangadiaz.dimedianet.api.Usuari;
import com.berlangadiaz.dimedianet.api.ApiClient;
import com.berlangadiaz.dimedianet.component.DiMediaLink;
import persistence.GestorJson;
import utils.LoggerError;
import com.berlangadiaz.dimedianet.api.Usuari;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Panel que gestiona la interfaz de login. 
 * Codificado manualmente (sin NetBeans Designer) para cumplir el requisito.
 */
public class LoginPanel extends JPanel implements ActionListener {
    
    // --- Componentes de la Interfaz ---
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JCheckBox rememberMe;
    private final JButton loginButton;

    private final MainFrame parentFrame; 

    // URL BASE DE LA API
    private static final String API_BASE_URL = "https://dimedianetapi9.azurewebsites.net"; 

    public LoginPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        // 1. Configuración del Layout (Usando GridBagLayout)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configuración de márgenes y relleno
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        // --- Fila 0: Email ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; 
        add(new JLabel("Email de Usuario:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; 
        emailField = new JTextField(20);
        add(emailField, gbc);

        // --- Fila 1: Contraseña ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // --- Fila 2: Checkbox "Recordarme" ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.WEST; 
        gbc.fill = GridBagConstraints.NONE; 
        rememberMe = new JCheckBox("Recordarme en esta máquina (3 días)");
        add(rememberMe, gbc);
        
        // --- Fila 3: Botón de Login ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Iniciar Sesión");
        add(loginButton, gbc);
        
        // --- Añadir Listener y manejar persistencia ---
        loginButton.addActionListener(this);
        
        // Al pulsar la tecla Enter en el campo password se dispara el botón de login
        passwordField.addActionListener(e -> loginButton.doClick());
        
        // Al inicio, intenta cargar credenciales si existen
        loadRememberedUser(); 
    }
    
    // --- MÉTODOS DE LA CLASE ---

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLoginAttempt();
        }
    }

    /**
     * Limpia los campos de email y contraseña del formulario.
     * Llamado por MainFrame al cambiar a la vista de login.
     */
    public void clearFields() { 
        emailField.setText("");
        passwordField.setText("");
        rememberMe.setSelected(false);
        loginButton.setEnabled(true);
    } 

    /**
     * Intenta iniciar sesión con el token guardado para la función "Remember Me".
     */
    private void loadRememberedUser() {
        String token = GestorJson.getToken();
        long expiration = GestorJson.getTokenExpirationTime();

        // 1. Verificar si hay token guardado Y si no ha expirado
        if (token != null && !token.isEmpty() && System.currentTimeMillis() < expiration) {

            // 2. Intentamos reanudar la sesión en el MainFrame (el gestor del ApiClient)
            try {
                // Llama al método de servicio que debe existir en MainFrame
                parentFrame.resumeSession(token);

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Sesión reanudada automáticamente.",
                            "Auto-Login", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.mostrarVistaPrincipal();
                });

            } catch (Exception e) {
                // Si falla la reanudación (ej. token caducado en el servidor), limpiamos y forzamos login
                GestorJson.clearToken();
                System.err.println("Error al reanudar la sesión: " + e.getMessage());
            }
        } else if (expiration != 0 && System.currentTimeMillis() >= expiration) {
            // Limpiar si el token está expirado
            GestorJson.clearToken();
        }
    }

    /**
     * Lógica para el intento de login tras pulsar el botón. 
     * Ejecuta la llamada a la API en un hilo separado para evitar bloquear la UI.
     */
// Dentro de LoginPanel.java:

    private void handleLoginAttempt() {
        loginButton.setEnabled(false);

        final String email = emailField.getText();
        final String password = new String(passwordField.getPassword());
        final boolean shouldRemember = rememberMe.isSelected();

        // Ejecutar la llamada a la API en un Thread (delegando la llamada a MainFrame)
        new Thread(() -> {
            Usuari usuarioLogueado = null;
            String errorMessage = null;

            try {
                // 1. LLAMADA DELEGADA: El MainFrame gestiona el ApiClient.
                usuarioLogueado = parentFrame.attemptLogin(email, password);

                if (usuarioLogueado != null) {

                    // 2. Persistencia (Usamos el token que está ahora guardado en MainFrame)
                    if (shouldRemember) {
                        // Obtener el token del MainFrame y guardarlo
                        String token = parentFrame.getCurrentJwtToken();
                        long expirationTime = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000);
                        GestorJson.saveToken(token, expirationTime);
                    } else {
                        GestorJson.clearToken();
                    }

                    // 3. Cambiar a la vista principal (EDT)
                    final Usuari finalUser = usuarioLogueado;
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                // ¡CORRECCIÓN! Acceder directamente al campo público
                                "¡Login exitoso! Bienvenido, " + finalUser.nickName + ".",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.mostrarVistaPrincipal();
                    });

                } else {
                    // Esto solo debería ocurrir si attemptLogin devolvió null, lo cual lanza excepción.
                    // Pero lo dejo como doble chequeo.
                    errorMessage = "Credenciales incorrectas o usuario no encontrado.";
                }

            } catch (Exception ex) {
                // 1. Guardamos el error en el archivo de texto (Puntos extra UX)
                LoggerError.log("Fallo en el intento de autenticación del usuario: " + email, ex);

                // 2. Preparamos el mensaje para la interfaz
                errorMessage = "Error de Autenticación: " + ex.getMessage();
            }

            // 4. Manejo de errores (Volver al EDT)
            if (errorMessage != null) {
                final String finalErrorMessage = errorMessage;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            finalErrorMessage,
                            "Error de Autenticación",
                            JOptionPane.ERROR_MESSAGE);

                    // UX: Limpiar password y habilitar botón para reintento
                    passwordField.setText("");
                    passwordField.requestFocus(); // Pone el foco para volver a escribir
                    loginButton.setEnabled(true);
                });
            }
        }).start();
    }
}
