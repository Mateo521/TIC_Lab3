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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hamming {

    // Calcula el número de bits de paridad necesarios para un número de bits de datos dado
    public static int calcularParidadBitsHamming(int dataBits) {
        /* BIT GLOBAL AÑADIR */
        int m = 1;
        while (Math.pow(2, m) < dataBits + m + 1) {
            m++;
        }
        return m;
    }

    public static List<Integer> detectarYCorregirErrores(List<Integer> fullEncodedBlock) {
        if (fullEncodedBlock == null || fullEncodedBlock.size() < 8) {
            System.out.println("[ERROR] Bloque demasiado pequeño");
            return new ArrayList<>(fullEncodedBlock);
        }

        int n = fullEncodedBlock.size();          // Ej: 8, 256, 4096
        int dataSize = n - 1;                     // Último bit = paridad global
        List<Integer> hammingBlock = fullEncodedBlock.subList(0, dataSize);
        int receivedGlobalParity = fullEncodedBlock.get(dataSize);
        int computedGlobalParity = calcularParidadGlobal(hammingBlock);

        int syndrome = calculateSyndromeGeneral(hammingBlock);

        System.out.println("Syndrome: " + syndrome
                + ", Paridad global: "
                + (receivedGlobalParity == computedGlobalParity ? "válida" : "inválida"));

        // Corregir si posible
        if (syndrome != 0 && receivedGlobalParity != computedGlobalParity) {
            int errorPos = syndrome - 1; // índice 0-based
            if (errorPos >= 0 && errorPos < hammingBlock.size()) {
                hammingBlock.set(errorPos, 1 - hammingBlock.get(errorPos));
                System.out.println("[CORREGIDO] Bit corregido en posición " + errorPos);
            } else {
                System.out.println("[ERROR] Posición de error fuera de rango");
            }
        } else if (syndrome != 0) {
            System.out.println("[ERROR] Error no corregible (2 bits)");
        } else if (receivedGlobalParity != computedGlobalParity) {
            System.out.println("[AVISO] Solo error en bit de paridad global");
        }

        List<Integer> result = new ArrayList<>(hammingBlock);
        result.add(calcularParidadGlobal(hammingBlock));
        return result;
    }

    public static int calculateSyndromeGeneral(List<Integer> block) {
        int n = block.size();
        int syndrome = 0;

        // Asume paridad en posiciones 1, 2, 4, 8, ..., mientras estén dentro de n
        for (int i = 0; (1 << i) <= n; i++) {
            int parityPos = 1 << i; // 1, 2, 4, 8, ...
            int parity = 0;
            for (int j = 1; j <= n; j++) {
                if ((j & parityPos) != 0) {
                    parity ^= block.get(j - 1);
                }
            }
            if (parity != 0) {
                syndrome += parityPos;
            }
        }

        return syndrome;
    }

    public static int calcularParidadGlobal(List<Integer> bits) {
        return bits.stream().reduce(0, (a, b) -> a ^ b);
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

// Y modificar la función anadirBitsHamming para asegurar la correcta colocación de los bits
    public static List<Integer> anadirBitsHammingConLongitudTotal(List<Integer> data, int blockSizeTotal) {
        int r = calcularParidadBitsHamming(data.size());

        if (data.size() + r + 1 != blockSizeTotal) {
            throw new IllegalArgumentException("Tamaño de datos y paridad no coincide con el tamaño total esperado");
        }

        List<Integer> encodedData = new ArrayList<>(Collections.nCopies(blockSizeTotal, 0));

        int dataIndex = 0;
        for (int i = 1; i <= blockSizeTotal - 1; i++) { // Último bit es paridad global
            if (!esPotenciaDeDos(i)) {
                encodedData.set(i - 1, data.get(dataIndex++));
            }
        }

        for (int i = 0; i < r; i++) {
            int pos = (int) Math.pow(2, i) - 1;
            encodedData.set(pos, calcularParidadHamming(encodedData, pos + 1));
        }

        // Bit de paridad global
        encodedData.set(blockSizeTotal - 1, calcularParidadGlobal(encodedData.subList(0, blockSizeTotal - 1)));

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

    public static List<List<Integer>> procesoEnBloques(String text, int blockSizeTotal) {
        List<Integer> allBits = stringToBits(text);
        List<List<Integer>> blocks = new ArrayList<>();

        int bitsDatosPorBloque = calcularBitsDatosDesdeTotal(blockSizeTotal);

        for (int i = 0; i < allBits.size(); i += bitsDatosPorBloque) {
            int end = Math.min(i + bitsDatosPorBloque, allBits.size());
            List<Integer> block = new ArrayList<>(allBits.subList(i, end));

            while (block.size() < bitsDatosPorBloque) {
                block.add(0);
            }

            List<Integer> encodedBlock = anadirBitsHammingConLongitudTotal(block, blockSizeTotal);
            blocks.add(encodedBlock);
        }

        return blocks;
    }

    public static int calcularBitsDatosDesdeTotal(int blockSizeTotal) {
        // Buscamos el número de bits de datos que permite que:
        // datos + paridad + 1 (bit global) == blockSizeTotal
        for (int datos = 1; datos < blockSizeTotal; datos++) {
            int r = calcularParidadBitsHamming(datos);
            if (datos + r + 1 == blockSizeTotal) {
                return datos;
            }
        }
        throw new IllegalArgumentException("No se puede calcular un bloque válido con tamaño " + blockSizeTotal);
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
        System.out.println("Paridad global correcta>> "
                + (calcularParidadGlobal(encoded.subList(0, encoded.size() - 1)) == encoded.get(encoded.size() - 1)));
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
            System.out.printf("%c -> %s (ASCII: %d)\n", c, bits, (int) c);
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
        System.out.print("Ingrese la ruta del archivo: ");
        String filePath = scanner.nextLine();

        try {
            // Leer el contenido del archivo
            String contenido = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("Archivo cargado exitosamente.");
            return filePath;
        } catch (IOException e) {
            // Manejar el error en caso de que el archivo no se pueda cargar
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
            case 1:
                bloqueTamanio = 8;
                extension = ".HA1";
                break;
            case 2:
                bloqueTamanio = 256;
                extension = ".HA2";
                break;
            case 3:
                bloqueTamanio = 4096;
                extension = ".HA3";
                break;
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
                        i + 1, position, originalBit, bloque.get(position));

                // Verificar que el error es detectable
                /*   System.out.printf("Verificacion de paridad despues del error en bloque %d: %s\n",
                i+1, verificarCodigoVerificado(bloque) ? "valido" : "error detectado"); */
            } else {
                System.out.printf("Bloque %d: Sin cambios\n", i + 1);
            }
        }
    }

    public static void introducirUnErrorPorBloque(List<List<Integer>> bloques) {
        Random random = new Random();
        System.out.println("\nIntroduciendo un error por bloque:");

        for (int i = 0; i < bloques.size(); i++) {
            List<Integer> bloque = bloques.get(i);
            int position = random.nextInt(bloque.size());
            int originalBit = bloque.get(position);
            bloque.set(position, 1 - originalBit);

            System.out.printf("Bloque %d: Error introducido en posicion %d (cambio de %d a %d)\n",
                    i + 1, position, originalBit, bloque.get(position));
        }
    }

    public static void introducirDosErroresPorBloque(List<List<Integer>> bloques) {
        Random random = new Random();
        System.out.println("\nIntroduciendo dos errores por bloque:");

        for (int i = 0; i < bloques.size(); i++) {
            List<Integer> bloque = bloques.get(i);

            // primer error
            int position1 = random.nextInt(bloque.size());
            int originalBit1 = bloque.get(position1);
            bloque.set(position1, 1 - originalBit1);

            // otro error (se asegura que es en otra posicion al anterior)
            int position2;
            do {
                position2 = random.nextInt(bloque.size());
            } while (position2 == position1);

            int originalBit2 = bloque.get(position2);
            bloque.set(position2, 1 - originalBit2);

            System.out.printf("Bloque %d: Errores en posiciones %d (cambio de %d a %d) y %d (cambio de %d a %d)\n",
                    i + 1, position1, originalBit1, bloque.get(position1),
                    position2, originalBit2, bloque.get(position2));
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
            
            
            /*
            String decodedText = blocksToString(decodedBlocks, 8);
            Files.write(Paths.get(outputPath), decodedText.getBytes());
            */
            byte[] decodedBytes = blocksToBytes(decodedBlocks); // <- Necesitamos esta función
            Files.write(Paths.get(outputPath), decodedBytes);

            
            

            System.out.println("Archivo decodificado guardado como: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error al procesar el archivo: " + e.getMessage());
        }
    }
    
    
    public static byte[] blocksToBytes(List<List<Integer>> blocks) {
    List<Integer> allBits = new ArrayList<>();
    for (List<Integer> block : blocks) {
        allBits.addAll(block);
    }

    int byteCount = (allBits.size() + 7) / 8;
    byte[] result = new byte[byteCount];

    for (int i = 0; i < allBits.size(); i++) {
        int byteIndex = i / 8;
        result[byteIndex] <<= 1;
        result[byteIndex] |= allBits.get(i);
    }

    // Rellenar los bits restantes del último byte con ceros
    int remainingBits = allBits.size() % 8;
    if (remainingBits != 0) {
        result[byteCount - 1] <<= (8 - remainingBits);
    }

    return result;
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

                System.out.println("\nBloque " + (i + 1) + ":");
                System.out.println("Original con error: " + bloque);
                System.out.println("Corregido: " + correctedBlock);
            }

            // Convertir a texto y mostrar resultado
            byte[] TextoCorregido = blocksToBytes(BloquesCorregidos);
            
          
            
            
            System.out.println("\nTexto corregido: " + Arrays.toString(TextoCorregido));

            String outputPath = filePath.replace(".HA", ".DC").replace(".HE", ".DC");
            Files.write(Paths.get(outputPath), TextoCorregido);
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
                        i, i + 7, allBits.subList(i, i + 8), (char) charValue, charValue);
                result.append((char) charValue);
            }
        }

        return result.toString();
    }

}
