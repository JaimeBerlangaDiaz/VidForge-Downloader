/*
 * Panel de la Biblioteca de Medios (Tarea 2)
 */
package berlangadiaz.vidforge.downloader;

// --- Imports necesarios para la lógica ---
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
// --- Imports de nuestras clases ---
import berlangadiaz.vidforge.downloader.GestorJson;
import berlangadiaz.vidforge.downloader.MediaFile;
import berlangadiaz.vidforge.downloader.ColumnaOrden;
import berlangadiaz.vidforge.downloader.TipoMimeFiltro;

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
        // 1. Carga el diseño
        initComponents(); 
        
        // 2. Guarda la referencia al "cerebro"
        this.parentFrame = parent; 
        
        // --- Conectar los "Motores" ---
            
        // 3. Conecta el "motor" (TableModel) a la JTable
        tableModel = new MediaFileTableModel();
        tablaArchivos.setModel(tableModel);
        
        // 4. Configura el JComboBox<ColumnaOrden> (un <object>)
        cmbOrdenarPor.setModel(new javax.swing.DefaultComboBoxModel<>(new ColumnaOrden[] {
            new ColumnaOrden("Nombre", 0),
            new ColumnaOrden("Tamaño", 1),
            new ColumnaOrden("Fecha", 2)
        }));
        
        // 5. Configura el JList<TipoMimeFiltro> (un <object>)
        listFiltroTipo.setModel(new javax.swing.AbstractListModel<TipoMimeFiltro>() {
            TipoMimeFiltro[] filtros = new TipoMimeFiltro[] {
                new TipoMimeFiltro("Todos", "*"),
                new TipoMimeFiltro("Vídeos", "video/"),
                new TipoMimeFiltro("Audios", "audio/")
            };
            public int getSize() { return filtros.length; }
            public TipoMimeFiltro getElementAt(int i) { return filtros[i]; }
        });
        listFiltroTipo.setSelectedIndex(0); // Dejamos "Todos" seleccionado
        
        // --- CONECTAR EVENTOS ---
        // Esto garantiza que la tabla se refresca cuando se cambia el filtro o el orden
        listFiltroTipo.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    aplicarFiltrosYOrden();
                }
            }
        });
        
        cmbOrdenarPor.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarFiltrosYOrden();
            }
        });
        
        // --- FIN DE LA CONEXIÓN DE MOTORES ---
        
    }
    
     /**
     * Lanza la carga de datos. Es un simple lanzador del método principal.
     */
    public void cargarDatosDelJson() {
        aplicarFiltrosYOrden();
    }
    
    /**
     * ¡EL MÉTODO FINAL DE LA TAREA 2!
     * Carga los datos del JSON, los FILTRA (JList), los ORDENA (JComboBox) 
     * y los muestra en la tabla.
     */
    public void aplicarFiltrosYOrden() {
        System.out.println("Aplicando filtros y ordenación..."); 
        
        // 1. Coger la lista COMPLETA de archivos desde el JSON
        String rutaCarpetaGuardado = parentFrame.getRutaGuardado();
        GestorJson gestor = new GestorJson(rutaCarpetaGuardado);
        java.util.List<MediaFile> listaCompleta = gestor.leerArchivos();

        // 2. Obtener el estado de ordenación/dirección del MainFrame
        int columnaOrden = parentFrame.getColumnaOrdenActual();
        final boolean ASCENDENTE = parentFrame.isOrdenAscendente();

        // 3. Obtener el filtro seleccionado de la JList (Tipo MIME)
        TipoMimeFiltro filtroMime = listFiltroTipo.getSelectedValue();
        if (filtroMime == null) { return; }
        String tipoMime = filtroMime.getPrefijoMime(); // Ej: "video/" o "*"

        // 4. Filtrar la lista
        java.util.List<MediaFile> listaFiltrada = new java.util.ArrayList<>();
        if (tipoMime.equals("*")) {
            // Si es "Todos", añade la lista completa
            listaFiltrada.addAll(listaCompleta);
        } else {
            // Si es "video/" o "audio/", filtra la lista
            for (MediaFile archivo : listaCompleta) {
                if (archivo.getTipoMime().startsWith(tipoMime)) {
                    listaFiltrada.add(archivo);
                }
            }
        }
        
        // 5. Ordenar la lista filtrada (usando la lógica bidireccional del Comparador)
        java.util.Collections.sort(listaFiltrada, new java.util.Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile f1, MediaFile f2) {
                int resultadoComparacion;
                
                switch (columnaOrden) {
                    case 1: // Tamaño
                        resultadoComparacion = Long.compare(f1.getTamanoBytes(), f2.getTamanoBytes());
                        break;
                    case 2: // Fecha
                        resultadoComparacion = Long.compare(f1.getFechaCreacionMs(), f2.getFechaCreacionMs());
                        break;
                    case 0: // Nombre (por defecto)
                    default:
                        resultadoComparacion = f1.getNombre().compareToIgnoreCase(f2.getNombre());
                        break;
                }

                // Aplicamos la dirección: si es descendente (y la comparación no es igual), invertimos el resultado
                if (ASCENDENTE) {
                    return resultadoComparacion; // Devolvemos A->Z (o más pequeño a más grande)
                } else {
                    return -resultadoComparacion; // Devolvemos Z->A (o más grande a más pequeño)
                }
            }
        });

        // 6. Finalmente, le decimos al "motor" de la tabla que muestre la lista
        tableModel.setArchivos(listaFiltrada);
        System.out.println("Carga finalizada. Registros mostrados: " + listaFiltrada.size());
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
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        panelAcciones.add(btnActualizar);

        btnBorrar.setText("Borrar Seleccionado:");
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
        //Llama al método que carga y aplica filtros
        aplicarFiltrosYOrden();
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
                    cargarDatosDelJson();
                    
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
        java.util.List<MediaFile> listaCompleta = gestor.leerArchivos();

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
        
        // 4. Aplicar el filtro y orden
        aplicarFiltrosYOrden();
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
