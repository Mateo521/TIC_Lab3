/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frames;

/**
 *
 * @author mateo
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class HistorialHuffman {
       private static final String ARCHIVO = "estadisticas_huffman.ser";

    public static void guardar(EstadisticasHuffman stats) {
        List<EstadisticasHuffman> historial = cargar();
        historial.add(stats);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(historial);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<EstadisticasHuffman> cargar() {
        File file = new File(ARCHIVO);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<EstadisticasHuffman>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
