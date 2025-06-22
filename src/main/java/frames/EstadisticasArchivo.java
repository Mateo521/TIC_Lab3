/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frames;

/**
 *
 * @author mateo
 */
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class EstadisticasArchivo implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Tipo {
        PROTECCION, DESPROTECCION
    }

    private final String id;
    private final Tipo tipo;
    private final String nombreArchivo;
    private final LocalDateTime fechaHora;
    private final int bloques;
    private final int bitsOriginales;
    private final int bitsProtegidos;
    private final int bitsRecuperados;
    private final int erroresDetectados;
    private final int erroresCorregidos;
    private final double tasaSinErrores;
    private final double overhead;

    public EstadisticasArchivo(
            Tipo tipo,
            String nombreArchivo,
            int bloques,
            int bitsOriginales,
            int bitsProtegidos,
            int bitsRecuperados,
            int erroresDetectados,
            int erroresCorregidos,
            double tasaSinErrores,
            double overhead
    ) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.nombreArchivo = nombreArchivo;
        this.fechaHora = LocalDateTime.now();
        this.bloques = bloques;
        this.bitsOriginales = bitsOriginales;
        this.bitsProtegidos = bitsProtegidos;
        this.bitsRecuperados = bitsRecuperados;
        this.erroresDetectados = erroresDetectados;
        this.erroresCorregidos = erroresCorregidos;
        this.tasaSinErrores = tasaSinErrores;
        this.overhead = overhead;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getBloques() {
        return bloques;
    }

    public int getBitsOriginales() {
        return bitsOriginales;
    }

    public int getBitsProtegidos() {
        return bitsProtegidos;
    }

    public int getBitsRecuperados() {
        return bitsRecuperados;
    }

    public int getErroresDetectados() {
        return erroresDetectados;
    }

    public int getErroresCorregidos() {
        return erroresCorregidos;
    }

    public double getTasaSinErrores() {
        return tasaSinErrores;
    }

    public double getOverhead() {
        return overhead;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s - Bloques: %d - Overhead: %.2f%% - Errores corregidos: %d",
                tipo, nombreArchivo, fechaHora, bloques, overhead, erroresCorregidos);
    }
}
