package berlangadiaz.vidforge.downloader.view;

/**
 * Clase auxiliar para gestionar el orden de las columnas de la tabla.
 * @author jaimeberlangadiaz
 */
public class ColumnaOrden {
    
    private String nombreMostrado;
    private int indiceColumna;
    
    /**
     * Crea una nueva instancia de ColumnaOrden.
     *
     * @param nombreMostrado El nombre que se verá en el combo.
     * @param indiceColumna El índice real de la columna en el modelo.
     */
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
