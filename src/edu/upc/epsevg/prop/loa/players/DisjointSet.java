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
    int[] rank, parent;
    int n;
    
    public DisjointSet(int n){
        this.rank = new int[n];
        this.parent = new int[n];
        this.n = n;
        makeSet();
    }
 
    /**
     * Rellena el parent, lo que dice es que cada punto es parent de sí mismo.
     * @param universe Lista de puntos iniciales
     */
    public void makeSet(){
        for(int i = 0; i < this.n; i++) parent[i] = i;
    }
 
    /**
     * Busca el padre del punto.
     * @param k El punto a buscar
     * @return El padre del punto k.
     */
    public int Find(int p){
        if(parent[p] != p) parent[p] = Find(parent[p]);
        
        // recur for the parent until we find the root
        return parent[p];
    }
 
    /**
     * Añade al mismo conjunto el punto a y b.
     * @param a Punto a unir.
     * @param b Punto a unir.
     */
    public void Union(int x, int y)
    {
        // Find representatives of two sets
        int xRoot = Find(x), yRoot = Find(y);
  
        // Elements are in the same set, no need
        // to unite anything.
        if (xRoot == yRoot)
            return;
  
        // If x's rank is less than y's rank
        if (rank[xRoot] < rank[yRoot])
  
            // Then move x under y  so that depth
            // of tree remains less
            parent[xRoot] = yRoot;
  
        // Else if y's rank is less than x's rank
        else if (rank[yRoot] < rank[xRoot])
  
            // Then move y under x so that depth of
            // tree remains less
            parent[yRoot] = xRoot;
  
        else // if ranks are the same
        {
            // Then move y under x (doesn't matter
            // which one goes where)
            parent[yRoot] = xRoot;
  
            // And increment the result tree's
            // rank by 1
            rank[xRoot] = rank[xRoot] + 1;
        }
    }
    
    /**
     * Imprime por pantalla el conjunto parent.
     */
    public void printSet(){
        for (int i = 0; i < this.n; i++) {
            System.out.println(parent[i] + " => " + rank[i]);
        }
    }
}
