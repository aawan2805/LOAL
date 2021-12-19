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
