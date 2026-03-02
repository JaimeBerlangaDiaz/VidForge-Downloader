package berlangadiaz.vidforge.downloader.view;

/**
 * Clase "Objeto" para el JComboBox de ordenar (Tarea 2).
 * Guarda el nombre que ve el usuario ("Nombre") y el índice 
 * real de la columna en el TableModel (0).
 */
public class ColumnaOrden {
    
    private String nombreMostrado;
    private int indiceColumna;

    public ColumnaOrden(String nombreMostrado, int indiceColumna) {
        this.nombreMostrado = nombreMostrado;
        this.indiceColumna = indiceColumna;
    }

    public int getIndiceColumna() {
        return indiceColumna;
    }

    @Override
    public String toString() {
        // Esto es lo que verá el usuario en el JComboBox
        return nombreMostrado; 
    }
}
