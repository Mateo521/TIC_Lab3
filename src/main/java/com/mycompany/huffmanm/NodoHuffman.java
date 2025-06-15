/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.huffmanm;

/**
 *
 * @author mateo
 */
public class NodoHuffman {
       char caracter;
    double probabilidad;
    NodoHuffman izquierdo;
    NodoHuffman derecho;

    NodoHuffman(char caracter, double probabilidad) {
        this.caracter = caracter;
        this.probabilidad = probabilidad;
    }

    NodoHuffman(double probabilidad, NodoHuffman izquierdo, NodoHuffman derecho) {
        this.caracter = '\0'; // nodo interno
        this.probabilidad = probabilidad;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}
