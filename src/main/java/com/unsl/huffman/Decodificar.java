package com.unsl.huffman;

import static com.unsl.huffman.FilesClass.archivoCodificado;
import static com.unsl.huffman.FilesClass.getExtensionFiles;
import static com.unsl.huffman.FilesClass.obtenerNombre;
import static com.unsl.huffman.FilesClass.setArchivoCodificado;
import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Decodificar extends FilesClass {

    public static File archivoTabla;

    public static void descompactar() throws ClassNotFoundException {
        decodificacion(FilesClass.abrirMensajeCodificado(), archivoTabla);
    }

    //SETS
    public static void setArchivoTabla(String ruta) {

        String nombreArchivo = obtenerNombre(archivoCodificado);
        nombreArchivo = nombreArchivo.concat("_tabla.txt");
        archivoTabla = new File(nombreArchivo);
    }

    public static void setArchivoDecodificado(String ruta) {
        archivoDecodificado = ruta;
    }

//SELECCIONAR ARCHIVO PARA DFSCOMPACTAR
    public static void SelectArchivo() {

        JFileChooser jf = new JFileChooser(); //crea objeto de tipo FileChooser
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos .HUF, .DE1, .DE2, .DE3, .DC1, .DC2, .DC3", "huf", "DE1", "DE2", "DE3", "DC1", "DC2", "DC3");

        jf.setFileFilter(filtro); //filtra archivos 
        int select = jf.showOpenDialog(jf); //abre ventana

        if (select == JFileChooser.APPROVE_OPTION) {
            String ex = getExtensionFiles(jf.getSelectedFile().getAbsolutePath());

            if (!controlExtensionSalida(ex)) { // Si igualmente se selecciona un archivo que no es válido, muestra mensaje de error
                JOptionPane.showMessageDialog(null, "Debe seleccionar un archivo con una extensión válida (.huf, .DE1, .DE2, .DE3, .DC1, .DC2, .DC3)",
                        "Extensión inválida", JOptionPane.ERROR_MESSAGE);
            } else {
                String ruta = jf.getSelectedFile().getAbsolutePath();
                String nuevaRuta = ruta;

                setArchivoCodificado(ruta);
                nuevaRuta = nuevaRuta.replace(".huf", ".dhu");

                setArchivoDecodificado(nuevaRuta);
                setArchivoTabla(archivoCodificado);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se seleccionó ningun archivo.", "", JOptionPane.ERROR_MESSAGE);
        }

    }

//DESCOMPACTA EL ARCHIVO    
    public static void decodificacion(byte[] mensajeCodificado, File filePath) throws ClassNotFoundException {
        try {
            int[] size = {0};
            FileOutputStream archivo = new FileOutputStream(archivoDecodificado);

            Map<Character, String> dictHuffman = leerDiccionario(filePath, size);
            Map<String, Character> dictHuffmanInv = invertirDiccionario(dictHuffman);
            String bit = "";

            int decodedChars = 0;
            for (int i = 0; i < mensajeCodificado.length; i++) {

                int elemento = mensajeCodificado[i] & 0xFF;
                for (int j = 0; j < 8; j++) {
                    if (decodedChars < size[0]) {
                        int bitAux = (elemento & 0x80) >> 7;
                        bit += bitAux;
                        if (dictHuffmanInv.containsKey(bit)) {
                            archivo.write(dictHuffmanInv.get(bit));
                            bit = "";
                            decodedChars++;
                        }
                        elemento <<= 1;
                    }
                }

            }

            archivo.close();
            
            
            System.out.println("Caracteres decodificados: " + decodedChars + " de " + size[0]);
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Character, String> leerDiccionario(File file, int[] size) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            size[0] = (int) ois.readLong();
            return (Map<Character, String>) ois.readObject();
        }
    }

    private static Map<String, Character> invertirDiccionario(Map<Character, String> diccionario) {
        Map<String, Character> invertedDict = new HashMap<>();
        for (Map.Entry<Character, String> entry : diccionario.entrySet()) {
            invertedDict.put(entry.getValue(), entry.getKey());
        }
        return invertedDict;
    }

}
