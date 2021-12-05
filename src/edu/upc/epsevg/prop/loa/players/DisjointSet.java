/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa.players;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Abdullah Bashir Yasmin, Mario Konstanty Kochan
 */
public class DisjointSet {
    private Map<Point, Point> parent = new HashMap<>();
 
    // perform MakeSet operation
    public void makeSet(Point[] universe)
    {
        // create `n` disjoint sets (one for each item)
        for (Point i: universe) {
            parent.put(i, i);
        }
    }
 
    // Find the root of the set in which element `k` belongs
    public Point Find(Point k)
    {
        // if `k` is root
        if (parent.get(k) == k) {
            return k;
        }
 
        // recur for the parent until we find the root
        return Find(parent.get(k));
    }
 
    // Perform Union of two subsets
    public void Union(Point a, Point b)
    {
        // find the root of the sets in which elements `x` and `y` belongs
        Point x = Find(a);
        Point y = Find(b);
 
        parent.put(x, y);
    }
}
