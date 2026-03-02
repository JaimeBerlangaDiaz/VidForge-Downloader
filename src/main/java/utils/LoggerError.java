package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gestiona el registro de fallos en un archivo externo.
 * Cumple con el requisito de "Logs on application crash" de la Tarea DI04.
 */
public class LoggerError {
    
    // Nombre del archivo que aparecerá en la raíz de tu proyecto
    private static final String FILE_NAME = System.getProperty("user.home") + java.io.File.separator + "VidForge_error_log.txt";

    /**
     * Escribe un error en el archivo log.
     * @param mensaje Descripción personalizada del contexto del error.
     * @param e La excepción capturada.
     */
    public static void log(String mensaje, Exception e) {
        // 'true' en FileWriter activa el modo "append" (añadir al final sin borrar lo anterior)
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaActual = dtf.format(LocalDateTime.now());
            
            pw.println("=== REPORTE DE ERROR [" + fechaActual + "] ===");
            pw.println("CONTEXTO: " + mensaje);
            
            if (e != null) {
                pw.println("MENSAJE DE EXCEPCIÓN: " + e.getMessage());
                pw.println("TRAZA COMPLETA:");
                e.printStackTrace(pw); // Escribe toda la pila de error en el archivo
            }
            
            pw.println("=========================================================\n");
            
        } catch (IOException ioEx) {
            // Si falla el log, al menos lo mostramos por consola
            System.err.println("No se pudo escribir en el archivo de log: " + ioEx.getMessage());
        }
    }
}