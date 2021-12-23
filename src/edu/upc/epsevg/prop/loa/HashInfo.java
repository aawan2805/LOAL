/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;

/**
 *
 * @author Abdullah Bashir Yasmin, Mario Konstanty Kochan
 */

public class HashInfo {
    public int heuristica;
    public int profundidad;
    public Point mejorMovimientoDesde;
    public Point mejorMejorMovimientoA;

    /**
     * Crea una estuctura con la info proporcionada.
     * @param heuristica La heuística obtenida
     * @param profundidad La profunidad llegada
     * @param mejorFicha Mejor ficha desde la cual se ha hecho el movimiento
     * @param mejorMejorMovimientoFicha  Mejor ficha a donde se ha hecho el movimiento
     */
    public HashInfo(int heuristica, int profundidad, Point mejorFicha, Point mejorMejorMovimientoFicha) {
        this.heuristica = heuristica;
        this.profundidad = profundidad;
        this.mejorMovimientoDesde = mejorFicha;
        this.mejorMejorMovimientoA = mejorMejorMovimientoFicha;
    }
}
