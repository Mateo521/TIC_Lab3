
package com.mycompany.huffmanm;
import java.util.*;

public class Probabilidades {
    
    
    public static List<Object[]> probabilidad(List<Character> li) {
    Map<Character, Integer> frecuencia = new HashMap<>();
    for (Character c : li) {
        frecuencia.put(c, frecuencia.getOrDefault(c, 0) + 1);
    }

    int total = li.size();
    List<Object[]> listaProbabilidades = new ArrayList<>();

    for (Map.Entry<Character, Integer> entry : frecuencia.entrySet()) {
        double prob = (double) entry.getValue() / total;
        listaProbabilidades.add(new Object[]{prob, 1.0, entry.getKey()});
    }

    // Ordenar de mayor a menor probabilidad
    listaProbabilidades.sort((o1, o2) -> Double.compare((Double) o2[0], (Double) o1[0]));

    return listaProbabilidades;
}


    public static void main(String[] args) {
        List<Character> li = new ArrayList<>(Arrays.asList('a', 'b', 'c', 'a', 'b', 'a', 'd', 'e', 'f'));
        List<Object[]> result = probabilidad(li);
        for (Object[] o : result) {
            System.out.println("Probabilidad: " + o[0] + ", Valor: " + o[2]);
        }
    }
    
    public static List<Object[]> borrarDuplicados(List<Object[]> listaProbabilidades) {
    List<Object[]> uniqueProbabilidades = new ArrayList<>();

    for (Object[] prob : listaProbabilidades) {
        if (!containsProb(uniqueProbabilidades, prob)) {
            uniqueProbabilidades.add(prob);
        }
    }

    return uniqueProbabilidades;
}

public static boolean containsProb(List<Object[]> probList, Object[] prob) {
    for (Object[] p : probList) {
        if (p[2].equals(prob[2])) {
            return true;
        }
    }

    return false;
}


}
