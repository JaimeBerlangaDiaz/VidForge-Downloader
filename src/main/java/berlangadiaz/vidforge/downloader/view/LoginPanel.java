package berlangadiaz.vidforge.downloader.view;

import berlangadiaz.vidforge.downloader.api.ApiClient;
import berlangadiaz.vidforge.downloader.api.Usuari;
import berlangadiaz.vidforge.downloader.model.GestorJson; // Asumimos esta clase
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Panel que gestiona la interfaz de login. 
 * Cumple el requisito de ser codificado SIN el NetBeans Designer.
 */
public class LoginPanel extends JPanel implements ActionListener {
    
    // --- Componentes de la Interfaz ---
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JCheckBox rememberMe;
    private final JButton loginButton;

    // Referencia al Frame principal para cambiar la vista tras el login
    private final MainFrame parentFrame; 

    public LoginPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        // 1. Configuración del Layout (Usando GridBagLayout para control manual)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configuración de márgenes y relleno
        gbc.insets = new Insets(8, 8, 8, 8); // Margen de 8px alrededor
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rellena horizontalmente

        // --- Fila 0: Etiqueta Email ---
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.weightx = 0.0; 
        add(new JLabel("Email de Usuario:"), gbc);

        // --- Fila 0: Campo Email ---
        gbc.gridx = 1; // Columna 1
        gbc.gridy = 0; 
        gbc.weightx = 1.0; 
        emailField = new JTextField(20);
        add(emailField, gbc);

        // --- Fila 1: Etiqueta Contraseña ---
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 1; // Fila 1
        gbc.weightx = 0.0;
        add(new JLabel("Contraseña:"), gbc);

        // --- Fila 1: Campo Contraseña ---
        gbc.gridx = 1; // Columna 1
        gbc.gridy = 1; 
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // --- Fila 2: Checkbox "Recordarme" ---
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 2; // Fila 2
        gbc.gridwidth = 2; // Ocupa ambas columnas
        gbc.anchor = GridBagConstraints.WEST; // Alineado a la izquierda
        gbc.fill = GridBagConstraints.NONE; 
        rememberMe = new JCheckBox("Recordarme en esta máquina (3 días)");
        add(rememberMe, gbc);
        
        // --- Fila 3: Botón de Login ---
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 3; // Fila 3
        gbc.gridwidth = 2; // Ocupa ambas columnas
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Iniciar Sesión");
        add(loginButton, gbc);
        
        // --- Añadir Listener y manejar la persistencia de datos ---
        loginButton.addActionListener(this);
        
        // Intentar cargar credenciales al inicio
        loadRememberedUser(); 
    }
    
    // --- LÓGICA DE EVENTOS Y FUNCIONALIDAD ---

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLoginAttempt();
        }
    }
    
    /**
     * Limpia los campos de email y contraseña del formulario. ESTE ES EL NUEVO
     * MÉTODO DE INSTANCIA
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
        String token = GestorJson.getToken(); // Llama al método estático de GestorJson
        long expiration = GestorJson.getTokenExpirationTime();

        // 1. Verificar si hay token guardado Y si no ha expirado
        if (token != null && !token.isEmpty() && System.currentTimeMillis() < expiration) {
            
            // 2. Si el token es válido, notificar a la API y al MainFrame
            ApiClient.setAuthToken(token); // Establecer el token en el cliente API para futuras llamadas
            
            SwingUtilities.invokeLater(() -> {
                // Muestra directamente la vista principal (simulando login exitoso)
                JOptionPane.showMessageDialog(this, 
                    "Sesión reanudada automáticamente.", 
                    "Auto-Login", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.mostrarVistaPrincipal();
            });
            
        } else if (expiration != 0 && System.currentTimeMillis() >= expiration) {
             // Limpiar si el token está expirado
             GestorJson.clearToken();
        }
    }

    /**
     * Lógica para el intento de login tras pulsar el botón. 
     * Ejecuta la llamada a la API en un hilo separado.
     */
    private void handleLoginAttempt() {
        // Deshabilitar el botón para evitar múltiples clics
        loginButton.setEnabled(false);
        
        final String email = emailField.getText();
        final String password = new String(passwordField.getPassword());
        final boolean shouldRemember = rememberMe.isSelected();

        // Ejecutar la llamada a la API en un Thread para no bloquear la UI (EDT)
        new Thread(() -> {
            Usuari usuarioLogueado = null;
            String errorMessage = null;

            try {
                ApiClient client = new ApiClient(); 
                
                // Llama al método de login (asumimos que devuelve el objeto Usuari con el token)
                usuarioLogueado = client.login(email, password); 

                if (usuarioLogueado != null) {
                    
                    if (shouldRemember) {
                        // Guardar el token y la expiración (3 días = 3 * 24 * 60 * 60 * 1000 milisegundos)
                        long expirationTime = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000);
                        GestorJson.saveToken(usuarioLogueado.getToken(), expirationTime);
                    } else {
                         // Si el usuario se loguea pero no marca "recordarme", limpiar cualquier token previo.
                         GestorJson.clearToken();
                    }
                    
                    // Establecer el token globalmente para todas las llamadas API subsiguientes
                    ApiClient.setAuthToken(usuarioLogueado.getToken()); 
                    
                    // Cambiar a la vista principal
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "¡Login exitoso! Bienvenido, " + usuarioLogueado.getNickname() + ".", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.mostrarVistaPrincipal(); // Asume que MainFrame tiene este método
                    });

                } else {
                    errorMessage = "Credenciales incorrectas o usuario no encontrado.";
                }
                
            } catch (Exception ex) {
                // Error de red, 401 Unauthorized, o error de parsing de JSON
                errorMessage = "Error de Login: " + ex.getMessage();
            }

            // Volver al EDT para actualizar la UI (si hubo error o el proceso terminó)
            if (errorMessage != null) {
                final String finalErrorMessage = errorMessage;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                                                  finalErrorMessage, 
                                                  "Error de Autenticación", 
                                                  JOptionPane.ERROR_MESSAGE);
                    loginButton.setEnabled(true); // Re-habilitar el botón
                });
            }
        }).start(); // Iniciar el hilo
    }
}