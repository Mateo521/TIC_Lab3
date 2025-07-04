package com.unsl.huffman;

import static com.unsl.huffman.FilesClass.controlExtensionEntrada;
import static com.unsl.huffman.FilesClass.getExtensionFiles;
import static com.unsl.huffman.FilesClass.setArchivoCodificado;
import static com.unsl.huffman.FilesClass.setArchivoEntrada;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Codificar extends FilesClass {

    public static String archivoTabla;

    //SET
    public static void setArchivoTabla() {
        String nombreArchivo = obtenerNombre(archivoCodificado);
        archivoTabla = nombreArchivo.concat("_tabla.txt");
    }

    //SELECCIONAR ARCHIVO PARA COMPACTAR
    public static void SelectArchivo() {

        JFileChooser jf = new JFileChooser(); //crea objeto de tipo FileChooser
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT .DOCX", "txt", "docx"); //crea filtro .txt
        jf.setFileFilter(filtro); //filtra archivos .txt
        int select = jf.showOpenDialog(jf); //abre ventana

        if (select == JFileChooser.APPROVE_OPTION) {
            String ex = getExtensionFiles(jf.getSelectedFile().getAbsolutePath());

            if (!controlExtensionEntrada(ex)) { //Si igualmente se selecciona un archivo que no es .txt, muestra mensaje de error
                JOptionPane.showMessageDialog(null, "Debe seleccionar un archivo con extensión .txt o .docx", "Extensión inválida", JOptionPane.ERROR_MESSAGE);

            } else {
                String ruta = jf.getSelectedFile().getAbsolutePath();
                String nuevaRuta = ruta;

                setArchivoEntrada(ruta);
                if (getExtensionFiles(ruta).equals("txt")) {

                    nuevaRuta = nuevaRuta.replace(".txt", ".huf");
                } else {
                    nuevaRuta = nuevaRuta.replace(".docx", ".huf");
                }

                setArchivoCodificado(nuevaRuta);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se seleccionó ningun archivo.", "", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void seleccionarArchivoConFiltro(String[] extensionesPermitidas, String descripcion, JTextField rutaField, JTextArea vistaPrevia, int co) {
        JFileChooser jf = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(descripcion, extensionesPermitidas);
        jf.setFileFilter(filtro);

        int select = jf.showOpenDialog(jf);
        if (select == JFileChooser.APPROVE_OPTION) {
            String ruta = jf.getSelectedFile().getAbsolutePath();
            String extension = Codificar.getExtensionFiles(ruta); // puedes mover esta función si quieres desacoplar más

            boolean permitido = Arrays.stream(extensionesPermitidas).anyMatch(ext -> extension.equalsIgnoreCase(ext));
            if (!permitido) {
                JOptionPane.showMessageDialog(null, "Archivo inválido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            rutaField.setText(ruta);

            try {
                String contenido = new String(Files.readAllBytes(Paths.get(ruta)));
                if (co == 1) {
                    vistaPrevia.setText(contenido.replaceAll("\\s+", ""));
                } else {
                    vistaPrevia.setText(contenido);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //COMPACTA EL ARCHIVO
    public static void codificacion(String mensaje, long size) throws IOException {
        OutputStream archivo = new FileOutputStream(new File(archivoCodificado));
        setArchivoTabla();
        List<Character> limes = stringToList(mensaje);
        Map<Character, String> dictHuffman = TablaHuffman.MaketablaHuffman(Probabilidades.probabilidad(limes));
        generarArchivoExtra(dictHuffman, size);
        int binario = 0;
        String text = "";
        // Asegurarse de que todos los caracteres tienen código
    for (char c : mensaje.toCharArray()) {
        if (!dictHuffman.containsKey(c)) {
            throw new IOException("Carácter sin código Huffman: " + c);
        }
    }
        for (int i = 0; i < mensaje.length(); i++) {
            text = text + dictHuffman.get(mensaje.charAt(i));
        }
        /*
System.out.println("Bytes estimados: " + (text.length() / 8.0));
         */
        for (int j = 0; j < text.length(); j++) {
            binario = (binario << 1) | (text.charAt(j) - '0');
            if (j % 8 == 7) {
                archivo.write(binario);
                binario = 0;
            }
        }
        int restantes = text.length() % 8;
        if (restantes != 0) {
            binario = binario << (8 - restantes);
            archivo.write(binario);
        }
        
        
        
        archivo.close();
    }

    public static void generarArchivoExtra(Map<Character, String> diccionario, long size) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoTabla))) {
            oos.writeLong(size); // Guarda el tamaño como binario
            oos.writeObject(diccionario); // Guarda el diccionario como objeto
        }
    }

    public static String escapeMapToString(Map<Character, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<Character, String> entry : map.entrySet()) {
            sb.append(escape(String.valueOf(entry.getKey()))).append("=").append(escape(entry.getValue().toString())).append(", ");
        }
        if (!map.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the trailing comma and space
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escape(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace(",", "coma")
                .replace("=", "igual");
    }

    public static void compactar() throws IOException {
        codificacion(FilesClass.abrirMensajeOriginal(), FilesClass.tamanioOriginal());
        File fileToDelete = new File("auxiliar");
        fileToDelete.delete();
    }

    public static List<Character> stringToList(String str) {
        List<Character> list = new ArrayList<>();
        for (char c : str.toCharArray()) {
            list.add(c);
        }
        return list;
    }

}