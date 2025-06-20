package com.unsl.huffman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;

public class FilesClass {

    public static String archivoEntrada = "";
    public static String archivoCodificado = "";
    public static String archivoDecodificado = "";

    public static void setArchivoEntrada(String path) {
        archivoEntrada = path;
    }

    public static String getArchivoEntrada() {
        return archivoEntrada;
    }

    public static void setArchivoCodificado(String path) {
        archivoCodificado = path;
    }

    public static String getArchivoCodificado() {
        return archivoCodificado;
    }

    public static void setArchivoDecodificado(String path) {
        archivoEntrada = path;
    }

    public static String getArchivoDecodificado() {
        return archivoDecodificado;
    }

    public static Boolean controlExtensionEntrada(String ext) { //Controla la extension sin errores

        return (ext.equals("txt") || ext.equals("docx"));
    }

    public static Boolean controlExtensionSalida(String ext) { //Controla la extension sin errores

        return (ext.equals("huf"));
    }

    public static String getExtensionFiles(String ruta) {
        int i = ruta.lastIndexOf('.');
        if (i > 0 && i < ruta.length() - 1) {
            return ruta.substring(i + 1); // sin el punto
        }
        return "";
    }

    public static long tamanioOriginal() {
        File aLeer = new File(archivoEntrada);
        try {
            return Files.size(Paths.get(aLeer.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static File readFile(File path) {
        try {
            byte[] fileContents = Files.readAllBytes(Paths.get(path.getAbsolutePath()));
            File newFile = new File("auxiliar");
            FileWriter writer = new FileWriter(newFile);
            writer.write(new String(fileContents));
            writer.close();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    
    public static String abrirMensajeOriginal() throws IOException {
    String mensaje = "";

    if (archivoEntrada.endsWith(".docx")) {
        mensaje = ReadDocxFile.readDocxFile(archivoEntrada);
    } else {
        mensaje = new String(Files.readAllBytes(Paths.get(archivoEntrada)));
    }

 
    return mensaje.replaceAll("\\s+", "");
}
    

    public static byte[] abrirMensajeCodificado() {
        File aLeer = new File(archivoCodificado);
        try {
            byte[] mensaje2 = Files.readAllBytes(Paths.get(aLeer.getAbsolutePath()));
            return mensaje2;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long tamanioCodificado() {
        File aLeer = new File(archivoCodificado);
        try {
            return Files.size(Paths.get(aLeer.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long tamanioDecodificado() {
        File aLeer = new File(archivoDecodificado);
        try {
            return Files.size(Paths.get(aLeer.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String chequeoOriginalDecodificado() throws IOException {
        File aLeer = new File(archivoEntrada);
        File aLeeraux = new File(archivoDecodificado);
        try {
            byte[] mensaje = Files.readAllBytes(Paths.get(aLeer.getAbsolutePath()));
            byte[] mensaje2 = Files.readAllBytes(Paths.get(aLeeraux.getAbsolutePath()));
            String original = new String(mensaje);
            String decodificado = new String(mensaje2);
            if (original.equals(decodificado)) {
                return "SON IGUALES!";
            } else {
                return "SON DIFERENTES!";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //OBTIENE EL NOMBRE DEL ARCHIVO SIN SU EXTENSION, ME SIRVE PARA GENERAR EL ARCHIVO DECODIFICADO
    public static String obtenerNombre(String texto) {
        StringBuilder resultado = new StringBuilder();

        for (char c : texto.toCharArray()) {
            if (c == '.') {
                break;
            }
            resultado.append(c);
        }

        return resultado.toString();
    }

    public static void main(String[] args) throws IOException {
        //SelectArchivo();
        System.out.println("Tamaño original: " + tamanioOriginal());
        System.out.println("Tamaño codificado: " + tamanioCodificado());
        System.out.println("Tamaño decodificado: " + tamanioDecodificado());
        System.out.println("Mensaje original: " + abrirMensajeOriginal());
        File fileToDelete = new File("auxiliar");
        fileToDelete.delete();
        System.out.println("Mensaje codificado: " + abrirMensajeCodificado());
        System.out.println(chequeoOriginalDecodificado());

    }

}
