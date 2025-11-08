package berlangadiaz.vidforge.downloader;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Clase "Modelo" para la JTable
 * Hereda de AbstractTableModel para decirle a la JTable cómo mostrar
 * una lista de objetos MediaFile
 */
public class MediaFileTableModel extends AbstractTableModel{
    
    // La lista de datos (nuestros objetos MediaFile)
    private List<MediaFile> archivos;
    
    // Los nombres de las columnas que verá el usuario
    private final String[] columnas = {"Nombre", "Tamaño", "Fechas", "Tipo MIME"};
    
    /**
     * Constructor. Inicializa la lista de archivos vacía.
     */
    public MediaFileTableModel(){
        this.archivos = new ArrayList<>();
    }
    
    // Métodos obligatorios de AbstractModel
    
    /**
     * Devuelve el número de filas (cuántos archivos hay).
     */
    @Override
    public int getRowCount(){
        return archivos.size();
    }
    
    /**
     * Devuelve el número de columnas (lo definimos en nuestro array)
     */
    @Override
    public int getColumnCount(){
        return columnas.length;
    }
    
    /**
     * Devuelve el nombre de la columna para la cabecera de la tabla
     */
    @Override
    public String getColumnName(int columnIndex){
        return columnas[columnIndex];
    }
    
    /**
     * Este es el método más importante
     * Devuelve el valor que se debe mostrar en una celda específica.
     * @param rowIndex La fila (el MediaFile)
     * @param columnIndex La columna (Nombre, Tamaño, Fecha, etc..)
     * @param El dato (String) a mostrar.
     */
    @Override
    public Object getValueAt(int rawIndex, int columnIndex){
        // Obtener el MediaFile de la fila correspondiente
        MediaFile archivo = archivos.get(rawIndex);
        
        // Decidir qué Dato devolver según la columna.
        switch(columnIndex){
            case 0: //Columna "Nombre"
                return archivo.getNombre();
            case 1: //Columna "Tamaño"
                // Usamos el método formateado que creamos en MediaFile
                return archivo.getTamanoFormateado(); //"10.5MB"
            case 2: //Columna "Fecha"
                return archivo.getFechaFormateada(); // "10/05/2025 18:30"
            case 3: //Columna "Tipo MIME"
                return archivo.getTipoMime();
            default:
                return null; //No debería pasar
        }
    }
    
    // Métodos auxiliares para manejar los datos 
    
    /**
     * Establece la lista de archivos a mostrar y refrescar la tabla.
     * @param archivos La nueva lista de MediaFile
     */
    public void setArchivos(List<MediaFile> archivos){
        this.archivos = archivos;
        //Notifica a la JTable que todos los datos han cambiado.
        fireTableDataChanged();
    }
    
    /**
     * Devuelve el objeto MediaFile de una fila específica.
     * (Lo usaremos para el botón "Borrar").
     * @param rowIndex La fila seleccionada.
     * @return El objeto MediaFile.
     */
    public MediaFile getFileAt(int rowIndex){
        return archivos.get(rowIndex);
    }
    
    /**
     * Limpia la tabla
     */
    public void clear(){
        this.archivos.clear();
        fireTableDataChanged();
    }
}
