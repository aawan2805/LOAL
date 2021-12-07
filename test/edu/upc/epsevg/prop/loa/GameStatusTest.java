/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.players.LOAL;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Usuari
 */
public class GameStatusTest {
    
    public GameStatusTest() {
    }

    
    @Test
    public void testGetHeuristic() {
        
int matrix[][] = new int[][] {
            {+0,-1,-1,+0,-1,-1,-1,+0},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,-1,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+0,-1,-1,-1,-1,-1,-1,+0}
        };
        GameStatus gs = new GameStatus(matrix);
        LOAL loal1 = new LOAL("KLK", 1);
        loal1.Eval(gs, CellType.PLAYER2); // Player -1

        /*
        System.out.println("=========================================================");


        int matrix2[][] = 
        new int[][] {
            {+0,-1,-1,-1,-1,-1,-1,+0},
            {+0,+0,+0,+0,+0,+0,+0,+0},
            {+0,+0,+0,+1,+0,+0,+0,+1},
            {+0,+0,+0,+1,+1,+1,+1,+0},
            {+0,+0,+0,+1,+1,+0,+0,+1},
            {+0,+0,+0,+1,+0,+0,+0,+1},
            {+0,+0,+0,+0,+0,+0,+0,+1},
            {+0,-1,-1,-1,-1,-1,-1,+0}
        };
        GameStatus gs2 = new GameStatus(matrix2);
        System.out.println(gs2.toString());
        System.out.println(gs2.getPos(7, 2));
        System.out.println("=========================================================");
        */
        
    }

 
    
}
