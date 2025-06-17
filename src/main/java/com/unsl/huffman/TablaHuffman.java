package com.unsl.huffman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TablaHuffman {

    public static Map<Character, String> MaketablaHuffman(List<Object[]> li) {
        return construirTablaHuffman(li);
    }

    public static Map<Character, String> construirTablaHuffman(List<Object[]> listaProbabilidades) {
        PriorityQueue<NodoHuffman> cola = new PriorityQueue<>(Comparator.comparingDouble(n -> n.probabilidad));

        for (Object[] entrada : listaProbabilidades) {
            char c = (Character) entrada[2];
            double p = (Double) entrada[0];
            cola.add(new NodoHuffman(c, p));
        }

        while (cola.size() > 1) {
            NodoHuffman n1 = cola.poll();
            NodoHuffman n2 = cola.poll();
            NodoHuffman combinado = new NodoHuffman(n1.probabilidad + n2.probabilidad, n1, n2);
            cola.add(combinado);
        }

        NodoHuffman raiz = cola.poll();
        Map<Character, String> tabla = new HashMap<>();
        construirCodigos(raiz, "", tabla);
        return tabla;
    }

    private static void construirCodigos(NodoHuffman nodo, String codigo, Map<Character, String> tabla) {
        if (nodo == null) {
            return;
        }

        if (nodo.esHoja()) {
            tabla.put(nodo.caracter, codigo.isEmpty() ? "0" : codigo); // protección para un solo carácter
            return;
        }

        construirCodigos(nodo.izquierdo, codigo + "0", tabla);
        construirCodigos(nodo.derecho, codigo + "1", tabla);
    }

}
