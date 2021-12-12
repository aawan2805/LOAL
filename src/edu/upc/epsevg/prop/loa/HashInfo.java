/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;

/**
 *
 * @author Abdullah Bashir Yasmin, Marc Capdevila
 */
public class HashInfo {
    public int heuristica;
    public int profundidad;
    public Point mejorFicha;
    public Point mejorMejorMovimientoFicha;

    public HashInfo(int heuristica, int profundidad, Point mejorFicha, Point mejorMejorMovimientoFicha) {
        this.heuristica = heuristica;
        this.profundidad = profundidad;
        this.mejorFicha = mejorFicha;
        this.mejorMejorMovimientoFicha = mejorMejorMovimientoFicha;
    }
}
