package berlangadiaz.vidforge.downloader.view;

import berlangadiaz.vidforge.downloader.model.MediaFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de tabla mejorado para soportar estados de sincronización.
 */
public class MediaFileTableModel extends AbstractTableModel {
    
    private List<MediaFile> archivos;
    // Mapa para guardar el estado de cada archivo: 
    // Key: Nombre del archivo , Value: Estado (0=Local, 1=Remoto, 2=Sincronizado)
    private Map<String, Integer> estados; 

    // Añadimos la columna "Estado" al principio o al final
    private final String[] columnas = {"Estado", "Nombre", "Tamaño", "Fechas", "Tipo MIME"};
    
    public static final int ESTADO_LOCAL = 0;
    public static final int ESTADO_REMOTO = 1;
    public static final int ESTADO_SINCRONIZADO = 2;

    public MediaFileTableModel() {
        this.archivos = new ArrayList<>();
        this.estados = new HashMap<>();
    }

    @Override
    public int getRowCount() {
        return archivos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnas[columnIndex];
    }
    
    // IMPORTANTE: Decimos qué tipo de objeto hay en cada columna para que el Renderer funcione
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; // La columna estado es un número (que pintaremos como icono)
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MediaFile archivo = archivos.get(rowIndex);
        
        switch(columnIndex){
            case 0: // Columna ESTADO
                // Buscamos el estado en el mapa, si no está, asumimos Local (0)
                return estados.getOrDefault(archivo.getNombre(), ESTADO_LOCAL);
            case 1: return archivo.getNombre();
            case 2: return archivo.getTamanoFormateado();
            case 3: return archivo.getFechaFormateada();
            case 4: return archivo.getTipoMime();
            default: return null;
        }
    }

    public void setArchivos(List<MediaFile> archivos, Map<String, Integer> estados){
        this.archivos = archivos;
        this.estados = estados;
        fireTableDataChanged();
    }
    
    // Sobrecarga para compatibilidad si solo pasas lista (asume todo local)
    public void setArchivos(List<MediaFile> archivos){
        this.archivos = archivos;
        this.estados = new HashMap<>();
        fireTableDataChanged();
    }

    public MediaFile getFileAt(int rowIndex){
        if (rowIndex >= 0 && rowIndex < archivos.size()) {
            return archivos.get(rowIndex);
        }
        return null;
    }
}