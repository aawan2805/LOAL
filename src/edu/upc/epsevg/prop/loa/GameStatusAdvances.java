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
public class GameStatusAdvances extends GameStatus {
    int hash;
    public GameStatusAdvances(int[][] gs){
        super(gs);
        this.hash = 0;
    }
    
    public GameStatusAdvances(GameStatus gs) {
        super(gs);
        this.hash = 0;
    }
    
    /**
     * Realiza la acción de mover la ficha además calculando el nuevo hash.
     * @param from Ficha que se mueve
     * @param to Ficha hacia donde se mueve
     * @param hash Hash del tablero
     * @param fromZB Hash del punto (ficha) que se mueve
     * @param toZB Hash del punto (ficha) a donde se mueve
     * @param eatPiece Indica si se ha comido la ficha del enemigo
     * @param eatPieceZB hash de la pieza que se ha comida
     * @return Hash calculado para la nueva posición.
     */
    public int movePiece(Point from, Point to, int hash, int fromZB, int toZB, boolean eatPiece, int eatPieceZB){
        this.hash = hash;
        this.hash ^= fromZB;
        this.hash ^= toZB;
        if(eatPiece) this.hash ^= eatPieceZB;

        super.movePiece(from, to);

        return this.hash;
    }
    
    @Override
    public void movePiece(Point from, Point to){
        super.movePiece(from, to);
    }
}
