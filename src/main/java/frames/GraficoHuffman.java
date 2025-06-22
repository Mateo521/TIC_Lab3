/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frames;

/**
 *
 * @author mateo
 */import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;
public class GraficoHuffman {
     public static void mostrarGrafico() {
        List<EstadisticasHuffman> historial = HistorialHuffman.cargar();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay estadísticas Huffman guardadas.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (EstadisticasHuffman stat : historial) {
            String etiqueta = stat.getNombreArchivo() + " (" + stat.getFechaHora().toLocalTime().withNano(0) + ")";
            dataset.addValue(stat.getTamanioCodificado(), "Tamaño codificado (Bytes)", etiqueta);
            dataset.addValue(stat.getTamanioDecodificado(), "Tamaño original (Bytes)", etiqueta);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Comparación de Tamaños - Huffman",
                "Archivos",
                "Tamaño (Bytes)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame("Estadísticas Huffman");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
