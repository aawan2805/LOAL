/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa.players;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Abdullah Bashir Yasmin, Mario Konstanty Kochan
 */
public class DisjointSet {
    private Map<Point, Point> parent = new HashMap<>();
 
    /**
     * Rellena el parent, lo que dice es que cada punto es parent de sí mismo.
     * @param universe Lista de puntos iniciales
     */
    public void makeSet(ArrayList<Point> universe){
        for (Point i: universe) {
            parent.put(i, i);
        }
    }
 
    /**
     * Busca el padre del punto.
     * @param k El punto a buscar
     * @return El padre del punto k.
     */
    public Point Find(Point k){
        if (parent.get(k) == k) {
            return k;
        }
 
        // recur for the parent until we find the root
        return Find(parent.get(k));
    }
 
    /**
     * Añade al mismo conjunto el punto a y b.
     * @param a Punto a unir.
     * @param b Punto a unir.
     */
    public void Union(Point a, Point b)
    {
        // find the root of the sets in which elements `x` and `y` belongs
        Point x = Find(a);
        Point y = Find(b);
 
        parent.put(x, y);
    }
}
