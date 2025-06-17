/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unsl.hamming;

/**
 *
 * @author mateo
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hamming {

    // Calcula el número de bits de paridad necesarios para un número de bits de datos dado
    public static int calcularParidadBitsHamming(int dataBits) { /* BIT GLOBAL AÑADIR */
        int m = 1;
        while (Math.pow(2, m) < dataBits + m + 1) {
            m++;
        }
        return m;
    }



    public static List<Integer> detectarYCorregirErrores(List<Integer> receivedData) {
        if (receivedData == null || receivedData.isEmpty()) {
            return new ArrayList<>();
        }
    
        // Verificar primero la paridad global
        int originalGlobalParity = receivedData.get(receivedData.size() - 1);
        int calculatedGlobalParity = calcularParidadGlobal(receivedData.subList(0, receivedData.size() - 1));
        
        boolean globalParityError = (originalGlobalParity != calculatedGlobalParity);
        
        // Crear una copia de los datos recibidos (sin el bit de paridad global)
        List<Integer> correctedData = new ArrayList<>(receivedData.subList(0, receivedData.size() - 1));
        
        // Calculamos el síndrome
        int errorPosition = 0;
        int m = calcularParidadBitsHamming(receivedData.size() - calcularParidadBitsHamming(receivedData.size()) - 1);
        
        // Verificar cada bit de paridad Hamming
        for (int i = 0; i < m; i++) {
            int parityBitIndex = (int) Math.pow(2, i);
            if (parityBitIndex <= correctedData.size()) {
                int expectedParity = 0;
                for (int j = 0; j < correctedData.size(); j++) {
                    if (j != parityBitIndex - 1 && ((j + 1) & parityBitIndex) != 0) {
                        expectedParity ^= correctedData.get(j);
                    }
                }
                if (expectedParity != correctedData.get(parityBitIndex - 1)) {
                    errorPosition += parityBitIndex;
                }
            }
        }
    
        // Analizar los resultados de ambas verificaciones
        if (!globalParityError && errorPosition == 0) {
            System.out.println("No se detectaron errores");
        } else if (globalParityError && errorPosition > 0) {
            System.out.println("Error simple detectado en la posición: " + errorPosition);
            // Corregir el error
            if (errorPosition <= correctedData.size()) {
                correctedData.set(errorPosition - 1, 1 - correctedData.get(errorPosition - 1));
            }
        } else {
            System.out.println("Se detectó un error que no se puede corregir (posible error múltiple)");
        }
    
        // Extraer solo los bits de datos
        List<Integer> dataOnly = new ArrayList<>();
        for (int i = 0; i < correctedData.size(); i++) {
            if (!esPotenciaDeDos(i + 1)) {
                dataOnly.add(correctedData.get(i));
            }
        }
        
        return dataOnly;
    }
    

// Función auxiliar para verificar si un número es potencia de 2
public static boolean esPotenciaDeDos(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}




// Función auxiliar para verificar la paridad de un bloque completo

public static boolean verificarCodigoVerificado(List<Integer> block) {
    // Verificar paridad global
    int originalGlobalParity = block.get(block.size() - 1);
    int calculatedGlobalParity = calcularParidadGlobal(block.subList(0, block.size() - 1));
    
    if (originalGlobalParity != calculatedGlobalParity) {
        return false;
    }
    
    // Verificar paridades Hamming
    int m = calcularParidadBitsHamming(block.size() - calcularParidadBitsHamming(block.size()) - 1);
    for (int i = 0; i < m; i++) {
        int parityBitIndex = (int) Math.pow(2, i);
        if (parityBitIndex <= block.size() - 1) { // -1 para excluir el bit global
            if (calcularParidadHamming(block, parityBitIndex) != block.get(parityBitIndex - 1)) {
                return false;
            }
        }
    }
    return true;
}



// También vamos a modificar la función calcularParidadHamming para hacerla más precisa
public static int calcularParidadHamming(List<Integer> data, int position) {
    int parity = 0;
    for (int i = position - 1; i < data.size(); i++) {
        if (((i + 1) & position) != 0) {  // Usando AND bit a bit
            parity ^= data.get(i);  // XOR para calcular la paridad
        }
    }
    return parity;
}

private static int calcularParidadGlobal(List<Integer> block) {
    int count = 0;
    for (int bit : block) {
        if (bit == 1) count++;
    }
    return count % 2; // 0 si es par, 1 si es impar
}



// Y modificar la función anadirBitsHamming para asegurar la correcta colocación de los bits
public static List<Integer> anadirBitsHamming(List<Integer> data) {
    int m = calcularParidadBitsHamming(data.size());
    List<Integer> encodedData = new ArrayList<>();
    
    // Inicializar el tamaño total (añadimos +1 para el bit de paridad global)
    for (int i = 0; i < data.size() + m + 1; i++) {
        encodedData.add(0);
    }
    
    // Colocar los bits de datos en las posiciones correctas
    int dataIndex = 0;
    for (int i = 1; i <= encodedData.size() - 1; i++) { // -1 para dejar espacio al bit global
        if (!esPotenciaDeDos(i)) {
            encodedData.set(i - 1, data.get(dataIndex++));
        }
    }
    
    // Calcular y colocar los bits de paridad Hamming
    for (int i = 0; i < m; i++) {
        int parityPos = (int)Math.pow(2, i) - 1;
        encodedData.set(parityPos, calcularParidadHamming(encodedData, parityPos + 1));
    }
    
    // Calcular y añadir el bit de paridad global al final
    encodedData.set(encodedData.size() - 1, calcularParidadGlobal(encodedData.subList(0, encodedData.size() - 1)));
    
    return encodedData;
}


public static String blocksToString(List<List<Integer>> blocks, int originalBlockSize) {
    StringBuilder result = new StringBuilder();
    List<Integer> allBits = new ArrayList<>();
    
    for (List<Integer> block : blocks) {
        // Asegurarse de no exceder el tamaño del bloque original
        int size = Math.min(block.size(), originalBlockSize);
        allBits.addAll(block.subList(0, size));
    }
    
    return bitsToString(allBits);
}


// Modificar la función anadirBitsHamming para asegurar el orden correcto


// Función para imprimir el estado de los bits en cada paso
public static void imprimirEstadoBit(String step, List<Integer> bits) {
    System.out.println(step + ": " + bits);
}


public static List<List<Integer>> procesoEnBloques(String text, int blockSize) {
    List<Integer> allBits = stringToBits(text);
    List<List<Integer>> blocks = new ArrayList<>();
    
    System.out.println("\nProcesando texto: " + text);
    System.out.println("Bits originales: " + allBits);
    
    // Dividir los bits en bloques
    for (int i = 0; i < allBits.size(); i += blockSize) {
        int end = Math.min(i + blockSize, allBits.size());
        List<Integer> block = new ArrayList<>(allBits.subList(i, end));
        
        // Rellenar con ceros si es necesario
        while (block.size() < blockSize) {
            block.add(0);
        }
        
        List<Integer> encodedBlock = anadirBitsHamming(block);
        blocks.add(encodedBlock);
        
        // Verificar el bloque
        verificarBloque(block, encodedBlock, extraerDatosSinCorreccion(encodedBlock));
    }
    
    return blocks;
}

public static void verificarBloque(List<Integer> original, List<Integer> encoded, List<Integer> decoded) {
    System.out.println("\nVerificacion de bloque:");
    System.out.println("Original : " + original);
    System.out.println("Codificado: " + encoded);
    System.out.println("Bit de paridad global: " + encoded.get(encoded.size() - 1));
    System.out.println("Decodificado: " + decoded);
    
    // Verificar que los bits de datos se mantienen
    boolean matches = true;
    int dataIndex = 0;
    for (int i = 0; i < encoded.size() - 1; i++) { // -1 para excluir el bit global
        if (!esPotenciaDeDos(i + 1)) {
            if (dataIndex < original.size() && original.get(dataIndex) != encoded.get(i)) {
                matches = false;
                break;
            }
            dataIndex++;
        }
    }
    System.out.println("Coinciden los bits de datos>> " + matches);
    System.out.println("Paridad global correcta>> " + 
        (calcularParidadGlobal(encoded.subList(0, encoded.size() - 1)) == encoded.get(encoded.size() - 1)));
}

    public static String codificarYDecodificarConBloques(String text, int blockSize) {
        try {
            // Codificar
            List<List<Integer>> BloqueCodificado = procesoEnBloques(text, blockSize);
            
            // Corregir y decodificar
            List<List<Integer>> correctedBlocks = correctBlocks(BloqueCodificado);
            
            // Convertir de vuelta a texto
            return blocksToString(correctedBlocks, blockSize);
        } catch (Exception e) {
            System.err.println("Error procesando el texto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<List<Integer>> correctBlocks(List<List<Integer>> BloqueCodificado) {
        List<List<Integer>> correctedBlocks = new ArrayList<>();
        
        for (List<Integer> block : BloqueCodificado) {
            List<Integer> correctedBlock = detectarYCorregirErrores(block);
            correctedBlocks.add(correctedBlock);
        }
        
        return correctedBlocks;
    }

  
public static void imprimirCaracterBits(String text) {
    System.out.println("\nCaracteres originales a bits:");
    for (char c : text.toCharArray()) {
        String bits = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
        System.out.printf("%c -> %s (ASCII: %d)\n", c, bits, (int)c);
    }
}


    // Convierte una cadena de caracteres a una lista de bits (0 y 1)
    public static List<Integer> stringToBits(String str) {
        List<Integer> bits = new ArrayList<>();
        for (char c : str.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < 8) {
                binary = "0" + binary; // Rellena con ceros a la izquierda para asegurar 8 bits
            }
            for (char bit : binary.toCharArray()) {
                bits.add(Character.getNumericValue(bit));
            }
        }
        return bits;
    }

    // Convierte una lista de bits (0 y 1) a una cadena de caracteres
    public static String bitsToString(List<Integer> bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.size(); i += 8) {
            int end = Math.min(i + 8, bits.size());
            List<Integer> chunk = bits.subList(i, end);
            int asciiValue = 0;
            for (int j = 0; j < chunk.size(); j++) {
                asciiValue += chunk.get(j) << (7 - j);
            }
            sb.append((char) asciiValue);
        }
        return sb.toString();
    }
    
    // Extrae los bits de datos sin corregir errores
    public static List<Integer> extraerDatosSinCorreccion(List<Integer> receivedData) {
        List<Integer> dataWithoutParity = new ArrayList<>();
        // Excluimos el último bit (bit global) del procesamiento
        for (int i = 0; i < receivedData.size() - 1; i++) {
            if (!esPotenciaDeDos(i + 1)) {
                dataWithoutParity.add(receivedData.get(i));
            }
        }
        return dataWithoutParity;
    }
    


  public static String cargarArchivo(Scanner scanner) {
        System.out.print("Ingrese la ruta del archivo .txt: ");
        String filePath = scanner.nextLine();
        if (!filePath.endsWith(".txt")) {
            System.out.println("El archivo debe tener extensión .txt");
            return null;
        }
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("Archivo cargado exitosamente.");
            return filePath;
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo: " + e.getMessage());
            return null;
        }
    }
public static void imprimirBloques(List<List<Integer>> bloques) {
    System.out.println("Contenido del archivo codificado:");
    for (int i = 0; i < bloques.size(); i++) {
        System.out.printf("Bloque %d: ", i + 1);
        for (Integer bit : bloques.get(i)) {
            System.out.print(bit);
        }
        System.out.println();
    }
}
public static void guardarArchivoCodificado(List<List<Integer>> bloques, String filePath) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (List<Integer> bloque : bloques) {
        for (Integer bit : bloque) {
            sb.append(bit);
        }
        sb.append("\n");
    }
    Files.write(Paths.get(filePath), sb.toString().getBytes());
}

public static List<List<Integer>> cargarArchivoCodificado(String filePath) throws IOException {
    List<List<Integer>> bloques = new ArrayList<>();
    List<String> lines = Files.readAllLines(Paths.get(filePath));
    
    for (String line : lines) {
        List<Integer> bloque = new ArrayList<>();
        for (char c : line.toCharArray()) {
            bloque.add(Character.getNumericValue(c));
        }
        bloques.add(bloque);
    }
    return bloques;
}

   public static void protegerArchivo(String inputPath, Scanner scanner) {
       
    System.out.println("Seleccione el tamaño de bloque:");
    System.out.println("1. 8 bits (.HA1)");
    System.out.println("2. 256 bits (.HA2)");
    System.out.println("3. 4096 bits (.HA3)");
    
    int ElegirBloque = scanner.nextInt();
    int bloqueTamanio;
    String extension;
    
    switch (ElegirBloque) {
        case 1: bloqueTamanio = 8; extension = ".HA1"; break;
        case 2: bloqueTamanio = 256; extension = ".HA2"; break;
        case 3: bloqueTamanio = 4096; extension = ".HA3"; break;
        default: 
            System.out.println("Opción inválida");
            return;
    }

    try {
        String contenido = new String(Files.readAllBytes(Paths.get(inputPath)));
        String outputPath = inputPath.replace(".txt", extension);
        
        System.out.println("Texto original: " + contenido);
        imprimirCaracterBits(contenido);
        
        List<List<Integer>> BloqueCodificado = procesoEnBloques(contenido, bloqueTamanio);
        guardarArchivoCodificado(BloqueCodificado, outputPath);
        
        System.out.println("Archivo protegido guardado como: " + outputPath);
        System.out.println("\nContenido del archivo protegido:");
        imprimirBloques(BloqueCodificado);
    } catch (IOException e) {
        System.out.println("Error al procesar el archivo: " + e.getMessage());
    }
}
   
   
   
   
   
   


    public static void introducirErrores(Scanner scanner) {
        System.out.print("Ingrese la ruta del archivo .HAx: ");
        String filePath = scanner.nextLine();
        
        try {
            List<List<Integer>> bloques = cargarArchivoCodificado(filePath);
            introducirErroresRandom(bloques);
            
            String outputPath = filePath.replace(".HA", ".HE");
            guardarArchivoCodificado(bloques, outputPath);
            
            System.out.println("Archivo con errores guardado como: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error al procesar el archivo: " + e.getMessage());
        }
    }

public static void introducirErroresRandom(List<List<Integer>> bloques) {
    Random random = new Random();
    System.out.println("\nIntroduciendo errores:");
    
    for (int i = 0; i < bloques.size(); i++) {
        List<Integer> bloque = bloques.get(i);
        if (random.nextDouble() < 0.5) { // 50% de probabilidad de error por bloque
            int position = random.nextInt(bloque.size());
            int originalBit = bloque.get(position);
            bloque.set(position, 1 - originalBit); // Cambiar 0 a 1 o 1 a 0
            
            System.out.printf("Bloque %d: Error introducido en posicion %d (cambio de %d a %d)\n",
                i+1, position, originalBit, bloque.get(position));
            
            // Verificar que el error es detectable
         /*   System.out.printf("Verificacion de paridad despues del error en bloque %d: %s\n",
                i+1, verificarCodigoVerificado(bloque) ? "valido" : "error detectado"); */
        } else {
            System.out.printf("Bloque %d: Sin cambios\n", i+1);
        }
    }
}

   

public static void decodificarSinCorreccion(Scanner scanner) { 
    System.out.print("Ingrese la ruta del archivo .HAx o .HEx: ");
    String filePath = scanner.nextLine();
    
    try {
        List<List<Integer>> blocks = cargarArchivoCodificado(filePath);
        List<List<Integer>> decodedBlocks = new ArrayList<>();
        
        for (List<Integer> block : blocks) {
            // Verificar paridad global antes de extraer datos
            int globalParity = block.get(block.size() - 1);
            int calculatedParity = calcularParidadGlobal(block.subList(0, block.size() - 1));
            
            if (globalParity != calculatedParity) {
                System.out.println("Advertencia: Error detectado en paridad global del bloque");
            }
            
            decodedBlocks.add(extraerDatosSinCorreccion(block));
        }
        
        String outputPath = filePath.replace(".HA", ".DE").replace(".HE", ".DE");
        String decodedText = blocksToString(decodedBlocks, 8);
        Files.write(Paths.get(outputPath), decodedText.getBytes());
        
        System.out.println("Archivo decodificado guardado como: " + outputPath);
    } catch (IOException e) {
        System.out.println("Error al procesar el archivo: " + e.getMessage());
    }
}

public static void decodificarConCorreccion(Scanner scanner) {
    System.out.print("Ingrese la ruta del archivo .HAx o .HEx: ");
    String filePath = scanner.nextLine();
    
    try {
        // Cargar y mostrar los bloques codificados
        List<List<Integer>> bloques = cargarArchivoCodificado(filePath);
        System.out.println("\nBloques codificados cargados:");
        imprimirBloques(bloques);

        // Corregir errores y mostrar el proceso
        System.out.println("\nProceso de corrección de errores:");
        List<List<Integer>> BloquesCorregidos = new ArrayList<>();
        for (int i = 0; i < bloques.size(); i++) {
            List<Integer> bloque = bloques.get(i);
            List<Integer> correctedBlock = detectarYCorregirErrores(bloque);
            BloquesCorregidos.add(correctedBlock);
            
            System.out.println("\nBloque " + (i+1) + ":");
            System.out.println("Original con error: " + bloque);
            System.out.println("Corregido: " + correctedBlock);
        }

        // Convertir a texto y mostrar resultado
        String TextoCorregido = bloquesToString(BloquesCorregidos, 8);
        System.out.println("\nTexto corregido: " + TextoCorregido);
        System.out.println("Bytes del texto corregido: " + bytesToHex(TextoCorregido.getBytes()));
        
        String outputPath = filePath.replace(".HA", ".DC").replace(".HE", ".DC");
        Files.write(Paths.get(outputPath), TextoCorregido.getBytes());
        
        System.out.println("Archivo corregido guardado como: " + outputPath);
    } catch (IOException e) {
        System.out.println("Error al procesar el archivo: " + e.getMessage());
        e.printStackTrace();
    }
}

// Función auxiliar para mostrar bytes en hexadecimal
public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X ", b));
    }
    return sb.toString();
}

// Función modificada para convertir bloques a string
public static String bloquesToString(List<List<Integer>> bloques, int originalBlockSize) {
    StringBuilder result = new StringBuilder();
    List<Integer> allBits = new ArrayList<>();
    
    // Juntar todos los bits
    for (List<Integer> bloque : bloques) {
        allBits.addAll(bloque);
    }
    
    // Convertir bits a caracteres
    System.out.println("\nConversion de bits a caracteres:");
    for (int i = 0; i < allBits.size(); i += 8) {
        if (i + 8 <= allBits.size()) {
            int charValue = 0;
            for (int j = 0; j < 8; j++) {
                charValue = (charValue << 1) | allBits.get(i + j);
            }
            System.out.printf("Bits %d-%d: %s -> %c (ASCII: %d)\n", 
                i, i+7, allBits.subList(i, i+8), (char)charValue, charValue);
            result.append((char)charValue);
        }
    }
    
    return result.toString();
}



}