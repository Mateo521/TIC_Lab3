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

public class HistorialHamming {
    private static final String ARCHIVO = "estadisticas_hamming.ser";

    public static void guardar(EstadisticasHamming estadistica) {
        
            System.out.println("Guardando estadística Hamming..."); // Debug

            
        List<EstadisticasHamming> historial = cargar();
        historial.add(estadistica);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(historial);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<EstadisticasHamming> cargar() {
        File file = new File(ARCHIVO);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<EstadisticasHamming>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
