/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frames;

/**
 *
 * @author mateo
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

public class GraficoEstadisticas {

    public static void mostrarGrafico() {
        List<EstadisticasHamming> historial = HistorialHamming.cargar();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay estadísticas para mostrar.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (EstadisticasHamming est : historial) {
            String etiqueta = est.getNombreArchivo() + " (" + est.getFechaHora().toLocalTime().withNano(0) + ")";
            if (est.getTipo() == EstadisticasHamming.Tipo.PROTECCION) {
                dataset.addValue(est.getBloques(), "Bloques protegidos", etiqueta);
                dataset.addValue(est.getOverhead(), "Overhead (%)", etiqueta);
            } else if (est.getTipo() == EstadisticasHamming.Tipo.DESPROTECCION) {
                dataset.addValue(est.getBloques(), "Bloques desprotegidos", etiqueta);
                dataset.addValue(est.getErroresCorregidos(), "Errores corregidos", etiqueta);
                dataset.addValue(est.getTasaSinErrores(), "Tasa sin errores (%)", etiqueta);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Historial de Estadísticas",
                "Archivos procesados",
                "Valores",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame("Gráfico de Estadísticas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
