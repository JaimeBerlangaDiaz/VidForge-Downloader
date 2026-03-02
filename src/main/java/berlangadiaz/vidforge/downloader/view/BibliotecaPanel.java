/*
 * Panel de la Biblioteca de Medios (Tarea 2)
 */
package berlangadiaz.vidforge.downloader.view;

import com.berlangadiaz.dimedianet.api.Media; // El objeto de la API
import java.util.Map;
import java.util.HashMap;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JTable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import persistence.GestorJson;
import berlangadiaz.vidforge.downloader.model.MediaFile;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author jaimeberlangadiaz
 */
/**
 *
 * @author jaimeberlangadiaz
 */
public class BibliotecaPanel extends javax.swing.JPanel {

    private MainFrame parentFrame;
    private MediaFileTableModel tableModel; // El "motor" de la tabla

    /**
     * Creates new form BibliotecaPanel
     */
    public BibliotecaPanel(MainFrame parent) {
        // 1. PRIMERO: Crear los componentes visuales (incluida la tabla)
        initComponents(); 
        
        // 2. Guardar referencia al padre (MainFrame)
        this.parentFrame = parent; 
        
        // 3. Crear y asignar el Modelo a la Tabla
        // IMPORTANTE: Esto define que la tabla tiene 5 columnas (la 0 es para el icono).
        tableModel = new MediaFileTableModel();
        tablaArchivos.setModel(tableModel);
        
        // 4. --- CONFIGURACIÓN VISUAL (ICONOS) ---
        // Ajustamos ancho columna 0 (Iconos) para que sea pequeña
        tablaArchivos.getColumnModel().getColumn(0).setMaxWidth(40);
        tablaArchivos.getColumnModel().getColumn(0).setMinWidth(40);
        
        // Asignamos el "Pintor" de iconos
        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                
                if (value instanceof Integer) {
                    int estado = (Integer) value;
                    switch (estado) {
                        case MediaFileTableModel.ESTADO_SINCRONIZADO: // 2
                            label.setText("✔"); 
                            label.setForeground(new java.awt.Color(0, 153, 0)); 
                            label.setToolTipText("Sincronizado: PC + Nube");
                            break;
                        case MediaFileTableModel.ESTADO_REMOTO: // 1
                            label.setText("☁"); 
                            label.setForeground(java.awt.Color.BLUE);
                            label.setToolTipText("Solo en la Nube");
                            break;
                        default: // 0
                            label.setText("💻"); 
                            label.setForeground(java.awt.Color.GRAY);
                            label.setToolTipText("Solo en Local");
                            break;
                    }
                }
                return label;
            }
        });
        
        // 5. Configurar el resto (Combos y Listas)
        cmbOrdenarPor.setModel(new javax.swing.DefaultComboBoxModel<>(new ColumnaOrden[] {
            new ColumnaOrden("Nombre", 0),
            new ColumnaOrden("Tamaño", 1),
            new ColumnaOrden("Fecha", 2)
        }));
        
        listFiltroTipo.setModel(new javax.swing.AbstractListModel<TipoMimeFiltro>() {
            TipoMimeFiltro[] filtros = new TipoMimeFiltro[] {
                new TipoMimeFiltro("Todos", "*"),
                new TipoMimeFiltro("Vídeos", "video/"),
                new TipoMimeFiltro("Audios", "audio/")
            };
            public int getSize() { return filtros.length; }
            public TipoMimeFiltro getElementAt(int i) { return filtros[i]; }
        });
        listFiltroTipo.setSelectedIndex(0); 
        
        // 6. Configurar Eventos (Listeners)
        // Usamos expresiones lambda (->) que son más limpias
        listFiltroTipo.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                applyingFiltrosYOrdenWrapper();
            }
        });
        
        cmbOrdenarPor.addActionListener(e -> {
             applyingFiltrosYOrdenWrapper();
        });
        
        // Borde para que se vean los botones de abajo
        panelAcciones.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // 1. Ajustar anchos de columnas
        if (tablaArchivos.getColumnCount() >= 5) {
            // Columna 0 (Icono): Ya está a 40px más arriba
            
            // Columna 1 (Nombre): Le damos mucho espacio (evita text overflow)
            tablaArchivos.getColumnModel().getColumn(1).setPreferredWidth(450); 
            
            // Columna 2 (Tamaño): Estrecha
            tablaArchivos.getColumnModel().getColumn(2).setMinWidth(70);
            tablaArchivos.getColumnModel().getColumn(2).setMaxWidth(90);
            
            // Columna 3 (Fecha): Mediana
            tablaArchivos.getColumnModel().getColumn(3).setMinWidth(110);
            tablaArchivos.getColumnModel().getColumn(3).setMaxWidth(130);
            
            // Columna 4 (Tipo MIME): Estrecha
            tablaArchivos.getColumnModel().getColumn(4).setMinWidth(80);
            tablaArchivos.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        // 2. Renderer con TOOLTIPS (Para leer texto cortado al pasar el ratón)
        DefaultTableCellRenderer tooltipRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setToolTipText(value.toString());
                }
                return c;
            }
        };
        // Aplicamos este renderer a las columnas de texto (1, 2 y 3)
        for (int i = 1; i < tablaArchivos.getColumnCount(); i++) {
            tablaArchivos.getColumnModel().getColumn(i).setCellRenderer(tooltipRenderer);
        }

        // 3. EVENTO DE DOBLE CLIC (Descargar de Nube o Abrir Local)
        tablaArchivos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble Clic detectado
                    gestionarDobleClic();
                }
            }
        });
    }
    
    // --- ESTE MÉTODO VA FUERA DEL CONSTRUCTOR, PERO DENTRO DE LA CLASE ---
    // Un pequeño ayudante para no repetir try-catch en los listeners
    private void applyingFiltrosYOrdenWrapper() {
        try {
            aplicarFiltrosYOrden();
        } catch (Exception ex) {
            System.err.println("Error al refrescar la tabla: " + ex.getMessage());
        }       
    }
    
    /**
     * Lanza la carga de datos. Es un simple lanzador del método principal.
     */
    public void cargarDatosDelJson() throws IOException{
        // ⬇️ AÑADIMOS EL TRY-CATCH OBLIGATORIO ⬇️
        try {
            aplicarFiltrosYOrden();
        } catch (IOException e) {
            // En caso de fallo de I/O (ej., archivo corrupto), lo informamos.
            System.err.println("Error al cargar datos del JSON: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error al cargar la biblioteca. El archivo de historial puede estar dañado.",
                    "Error de Lectura", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ¡EL MÉTODO FINAL DE LA TAREA 2!
     * Carga los datos del JSON, los FILTRA (JList), los ORDENA (JComboBox) 
     * y los muestra en la tabla.
     */
    public void aplicarFiltrosYOrden() throws IOException {
        System.out.println("Sincronizando biblioteca (Fusión Local + Nube)...");

        // 1. CARGAR ARCHIVOS LOCALES (Tu disco duro)
        String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
        GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
        List<MediaFile> listaLocal = gestor.leerArchivos();
        if (listaLocal == null) listaLocal = new ArrayList<>();

        // 2. CARGAR ARCHIVOS REMOTOS (Desde la API)
        List<Media> listaRemota = new ArrayList<>();
        try {
            // Pedimos el cliente al MainFrame (que usa el componente bueno)
            if (parentFrame.getApiClient() != null) {
                // Usamos el token actual
                String token = parentFrame.getCurrentJwtToken();
                if (token != null && !token.isEmpty()) {
                    listaRemota = parentFrame.getApiClient().getAllMedia(token);
                }
            }
        } catch (Exception ex) {
            // REGISTRO TÉCNICO 
            utils.LoggerError.log("Fallo al intentar recargar los datos de la biblioteca", ex);

            // FEEDBACK VISUAL
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar la lista de archivos.\n" + ex.getMessage(),
                    "Error de lectura", JOptionPane.ERROR_MESSAGE);
        }

        // 3. LA GRAN FUSIÓN
        // Creamos una lista combinada y un mapa de estados
        List<MediaFile> listaFinal = new ArrayList<>(listaLocal);
        Map<String, Integer> mapaEstados = new HashMap<>();

        // A) Marcamos todos los locales como LOCAL (0) por defecto
        for (MediaFile local : listaLocal) {
            mapaEstados.put(local.getNombre(), MediaFileTableModel.ESTADO_LOCAL);
        }

        // B) Cruzamos con la lista remota
        for (com.berlangadiaz.dimedianet.api.Media remoto : listaRemota) {
            boolean encontradoEnLocal = false;

            // --- CORRECCIÓN AQUÍ ---
            // Usamos la variable directa 'mediaFileName' porque es pública en tu clase Media
            String nombreRemoto = remoto.mediaFileName;

            // Protección por si viene nulo
            if (nombreRemoto == null) {
                continue;
            }

            // Buscamos coincidencia en los locales
            for (MediaFile local : listaLocal) {
                if (local.getNombre().equalsIgnoreCase(nombreRemoto)) {
                    // ¡COINCIDENCIA! Está en los dos sitios -> SINCRONIZADO (2)
                    mapaEstados.put(local.getNombre(), MediaFileTableModel.ESTADO_SINCRONIZADO);
                    encontradoEnLocal = true;
                    break;
                }
            }

            // Si NO está en local, lo añadimos como "Fantasma" (Solo Nube)
            if (!encontradoEnLocal) {
                // Creamos un MediaFile "falso" usando el nombre que nos da la API
                java.io.File dummyFile = new java.io.File(nombreRemoto);
                MediaFile remotoMF = new MediaFile(dummyFile);

                listaFinal.add(remotoMF);

                // Marcamos como REMOTO (1) en el mapa
                mapaEstados.put(remotoMF.getNombre(), MediaFileTableModel.ESTADO_REMOTO);
            }
        }

        // 4. FILTRADO (Igual que antes)
        TipoMimeFiltro filtroMime = listFiltroTipo.getSelectedValue();
        String tipoMime = (filtroMime != null) ? filtroMime.getPrefijoMime() : "*";

        List<MediaFile> listaFiltrada = new ArrayList<>();
        for (MediaFile f : listaFinal) {
            // Filtro simple: si es "*" pasa, si no, check startsWith
            if (tipoMime.equals("*") || (f.getTipoMime() != null && f.getTipoMime().startsWith(tipoMime))) {
                listaFiltrada.add(f);
            }
        }

        // 5. ORDENACIÓN (Igual que antes)
        int columnaOrden = parentFrame.getColumnaOrdenActual();
        final boolean ASCENDENTE = parentFrame.isOrdenAscendente();

        java.util.Collections.sort(listaFiltrada, (f1, f2) -> {
            int res = 0;
            switch (columnaOrden) {
                case 1: // Tamaño
                    res = Long.compare(f1.getTamanoBytes(), f2.getTamanoBytes()); 
                    break;
                case 2: // Fecha
                    res = Long.compare(f1.getFechaCreacionMs(), f2.getFechaCreacionMs()); 
                    break;
                default: // Nombre
                    res = f1.getNombre().compareToIgnoreCase(f2.getNombre()); 
                    break;
            }
            return ASCENDENTE ? res : -res;
        });

        // 6. ACTUALIZAR TABLA CON DATOS Y ESTADOS
        tableModel.setArchivos(listaFiltrada, mapaEstados);
        System.out.println("Biblioteca actualizada: " + listaFiltrada.size() + " elementos.");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPaneFiltro = new javax.swing.JScrollPane();
        listFiltroTipo = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        cmbOrdenarPor = new javax.swing.JComboBox<>();
        jScrollPaneTabla = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();
        panelAcciones = new javax.swing.JPanel();
        btnActualizar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        panelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Filtrar Por:");
        panelFiltros.add(jLabel1);

        jScrollPaneFiltro.setPreferredSize(new java.awt.Dimension(120, 80));

        jScrollPaneFiltro.setViewportView(listFiltroTipo);

        panelFiltros.add(jScrollPaneFiltro);

        jLabel2.setText("Ordenar por:");
        panelFiltros.add(jLabel2);

        cmbOrdenarPor.setPreferredSize(new java.awt.Dimension(120, 60));
        cmbOrdenarPor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOrdenarPorActionPerformed(evt);
            }
        });
        panelFiltros.add(cmbOrdenarPor);

        add(panelFiltros, java.awt.BorderLayout.PAGE_START);

        tablaArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneTabla.setViewportView(tablaArchivos);

        add(jScrollPaneTabla, java.awt.BorderLayout.CENTER);

        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnActualizar.setText("Actualizar Lista");
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        panelAcciones.add(btnActualizar);

        btnBorrar.setText("Borrar Seleccionado:");
        btnBorrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });
        panelAcciones.add(btnBorrar);

        txtBuscar.setColumns(20);
        panelAcciones.add(txtBuscar);

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        panelAcciones.add(btnBuscar);

        add(panelAcciones, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        try {
            //Llama al método que carga y aplica filtros
            aplicarFiltrosYOrden();
        } catch (IOException ex) {
            System.getLogger(BibliotecaPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        // 1. Averiguar qué fila está seleccionada
        int filaSeleccionada = tablaArchivos.getSelectedRow();

        // 2. Comprobar si realmente hay una fila seleccionada
        if (filaSeleccionada == -1) {
            // Si no hay nada seleccionado, mostrar un error y salir
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona un archivo de la tabla para borrar.",
                    "Nada seleccionado", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Obtener el objeto MediaFile de esa fila
        // (Usamos el 'tableModel' para traducir el índice de la fila al objeto)
        MediaFile archivoABorrar = tableModel.getFileAt(filaSeleccionada);

        // 4. Pedir confirmación (¡MUY IMPORTANTE!)
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres borrar permanentemente el archivo:\n"
                + archivoABorrar.getNombre() + "?",
                "Confirmar Borrado",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);

        // 5. Si el usuario pulsa "Sí" (YES_OPTION es 0)
        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            try {
                // 6. Borrar el archivo del DISCO
                java.io.File ficheroEnDisco = archivoABorrar.getFichero();
                if (ficheroEnDisco.delete()) {
                    // Si se borra del disco, lo borramos del JSON
                    
                    // 7. Borrar la entrada del log.json
                    String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
                    GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
                    gestor.eliminarArchivo(archivoABorrar); // (Este método ya lo creamos en GestorJson)

                    // 8. Refrescar la tabla
                    aplicarFiltrosYOrden();
                    
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Archivo borrado con éxito.",
                            "Borrado", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Si falla el borrado del disco (ej. archivo protegido)
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "No se pudo borrar el archivo del disco.",
                            "Error de Borrado", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Error al intentar borrar el archivo: " + e.getMessage(),
                        "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si el usuario pulsa "No", no hacemos nada.
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // 1. Coger el texto de la caja de búsqueda (y ponerlo en minúsculas)
        String textoBusqueda = txtBuscar.getText().toLowerCase();

        // 2. Coger la lista COMPLETA de archivos desde el JSON
        String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
        GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
        java.util.List<MediaFile> listaCompleta = null;
        try {
            listaCompleta = gestor.leerArchivos();
        } catch (IOException ex) {
            System.getLogger(BibliotecaPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        // 3. Si la barra de búsqueda está vacía, mostrarlo todo
        if (textoBusqueda.isEmpty()) {
            tableModel.setArchivos(listaCompleta);
            return; // Salimos del método
        }

        // 4. Si hay texto, filtrar la lista
        java.util.List<MediaFile> listaFiltrada = new java.util.ArrayList<>();

        // 5. Recorrer la lista completa
        for (MediaFile archivo : listaCompleta) {
            // Comprobar si el nombre del archivo (en minúsculas) contiene el texto de búsqueda
            if (archivo.getNombre().toLowerCase().contains(textoBusqueda)) {
                // Si lo contiene, lo añadimos a la lista filtrada
                listaFiltrada.add(archivo);
            }
        }

        // 6. Finalmente, le decimos al "motor" de la tabla que muestre
        //    SOLAMENTE la lista filtrada
        tableModel.setArchivos(listaFiltrada);        
    }//GEN-LAST:event_btnBuscarActionPerformed
    /**
     * Gestiona la acción al hacer doble clic en la tabla.
     * - Si es LOCAL: Abre el archivo.
     * - Si es REMOTO (Nube): Simula la descarga para sincronizarlo.
     */
    private void gestionarDobleClic() {
        int row = tablaArchivos.getSelectedRow();
        if (row == -1) return;

        // Obtenemos el archivo seleccionado
        MediaFile archivo = tableModel.getFileAt(row);
        
        // Consultamos su estado (Local, Remoto o Sincronizado)
        // Necesitamos acceder al mapa de estados del modelo. 
        // Si no tienes un método público 'getEstado(row)' en tu tableModel, 
        // usaremos la lógica visual:
        
        File ficheroFisico = archivo.getFichero();
        
        if (ficheroFisico.exists()) {
            // --- CASO 1: ARCHIVO LOCAL (Abrir) ---
            try {
                java.awt.Desktop.getDesktop().open(ficheroFisico);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se puede abrir el archivo: " + e.getMessage());
            }
        } else {
            // --- CASO 2: ARCHIVO SOLO EN NUBE (Descargar) ---
            int resp = JOptionPane.showConfirmDialog(this, 
                    "El archivo '" + archivo.getNombre() + "' está solo en la nube.\n¿Quieres descargarlo ahora?",
                    "Descargar de la Nube", JOptionPane.YES_NO_OPTION);
            
            if (resp == JOptionPane.YES_OPTION) {
                descargarDeNube(archivo);
            }
        }
    }

    /**
     * Simula la descarga de un archivo de la nube creando un fichero vacío 
     * o copiando datos (dependiendo de si tu API real devuelve bytes).
     * Para esta tarea, crearemos el archivo físico para que cambie de estado.
     */
    private void descargarDeNube(MediaFile archivoDummy) {
        try {
            // 1. Definir ruta destino (Carpeta de descargas configurada)
            String rutaDestino = parentFrame.getRutaGuardado() + File.separator + archivoDummy.getNombre();
            File nuevoFichero = new File(rutaDestino);
            
            // 2. Crear el archivo físico (Simulación de descarga exitosa)
            if (nuevoFichero.createNewFile()) {
                // Opcional: Escribir algo dentro para que no tenga 0 bytes
                // java.nio.file.Files.write(nuevoFichero.toPath(), "Contenido descargado de la nube".getBytes());
                
                // 3. Registrar en log.json para que sea persistente
                GestorJson gestor = new GestorJson(parentFrame.getRutaGuardado());
                // Necesitamos un objeto MediaFile real con ruta
                MediaFile nuevoReal = new MediaFile(nuevoFichero); 
                gestor.guardarArchivo(nuevoReal); // Asegúrate de tener este método o similar en GestorJson
                
                // 4. Feedback y Refresco
                JOptionPane.showMessageDialog(this, "¡Archivo descargado correctamente!");
                aplicarFiltrosYOrden(); // Recarga la tabla: ahora saldrá VERDE (Sincronizado)
                
            } else {
                JOptionPane.showMessageDialog(this, "Error: El archivo ya existe o no se puede crear.");
            }
        } catch (Exception e) {
            utils.LoggerError.log("Error al descargar de la nube", e);
            JOptionPane.showMessageDialog(this, "Error en la descarga: " + e.getMessage());
        }
    }
    private void cmbOrdenarPorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOrdenarPorActionPerformed
        ColumnaOrden nuevaColumna = (ColumnaOrden) cmbOrdenarPor.getSelectedItem();
        int nuevoIndice = nuevaColumna.getIndiceColumna();
        
        // 1.Obtener el estado actual del MainFrame
        int columnaAnterior = parentFrame.getColumnaOrdenActual();
        boolean ordenActual = parentFrame.isOrdenAscendente();
        
        // 2.Comprobar si es la misma columna
        if (columnaAnterior == nuevoIndice){
            //Si es la misma invertimos la dirección. (True se vuelve false)
            parentFrame.setOrdenAscendente(!ordenActual);
        } else {
            //Si es una columna nueva , reiniciamos a Ascendente(True).
            parentFrame.setOrdenAscendente(true);
        }
        
        // 3. Guardar la nueva columna en el MainFrame
        parentFrame.setColumnaOrdenActual(nuevoIndice);
        
        try {
            // 4. Aplicar el filtro y orden
            aplicarFiltrosYOrden();
        } catch (IOException ex) {
            System.getLogger(BibliotecaPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_cmbOrdenarPorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JComboBox<ColumnaOrden> cmbOrdenarPor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPaneFiltro;
    private javax.swing.JScrollPane jScrollPaneTabla;
    private javax.swing.JList<TipoMimeFiltro> listFiltroTipo;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JTable tablaArchivos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
