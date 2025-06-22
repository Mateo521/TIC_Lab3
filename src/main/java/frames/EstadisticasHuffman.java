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

public class EstadisticasHuffman implements Serializable{
      private static final long serialVersionUID = 1L;

    private String nombreArchivo;
    private int tamanioCodificado;
    private int tamanioDecodificado;
    private LocalDateTime fechaHora;

    public EstadisticasHuffman(String nombreArchivo, int codificado, int decodificado) {
        this.nombreArchivo = nombreArchivo;
        this.tamanioCodificado = codificado;
        this.tamanioDecodificado = decodificado;
        this.fechaHora = LocalDateTime.now();
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public int getTamanioCodificado() {
        return tamanioCodificado;
    }

    public int getTamanioDecodificado() {
        return tamanioDecodificado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
