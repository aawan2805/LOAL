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
        LOAL loal1 = new LOAL(4);
        
        // ------------- Detectar grupos ------------
        int matrix[][] = new int[][] {
            {+0,-1,-1,-1,-1,-1,-1,+0},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+0,-1,-1,-1,-1,-1,-1,+0}
        };
        GameStatus gs = new GameStatus(matrix);
        System.out.println(gs);
        gs.currentPlayer = CellType.PLAYER2;
        loal1.Eval(gs, CellType.PLAYER2);
        
        // Debería haber >=2 grupos pero con una distancia menor. Siendo nostros el jugador 2
        Move move1 = loal1.move(gs);
        System.out.println("Desde " + move1.getFrom() + " a " + move1.getTo());
        
        // ------------- Detectar grupos ------------
        
        // ------------- Última pieza por conectar ------------
        int matrix2[][] = new int[][] {
            {+0,+0,+0,+0,+0,+0,+0,+0},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,-1,-1,-1,-1,-1,-1,+1},
            {+1,-1,-1,-1,-1,-1,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+0,+0,+0,+0,+0,+0,-1,+0}
        };
        GameStatus gs2 = new GameStatus(matrix2);
        System.out.println(gs2);
        gs2.currentPlayer = CellType.PLAYER2;
        loal1.Eval(gs2, CellType.PLAYER2);
        
        // En este caso se deduce que la ficha que está debajo de todo es la que debería moverse.
        // Ya que sale más práctico mover esa ficha para no romper la conexión de las otras.
        // Pero
        Move move2 = loal1.move(gs2);
        System.out.println("Desde " + move2.getFrom() + " a " + move2.getTo());
        
        // ------------- Última pieza por conectar ------------
        
        // ------------- Última pieza por conectar ------------
        int matrix3[][] = new int[][] {
            {+0,+0,-1,-1,+0,+0,+0,+0},
            {+1,-1,-1,-1,-1,-1,-1,+1},
            {+1,-1,+0,+0,+0,+0,+0,+1},
            {+1,-1,+0,+0,+0,+0,+0,+1},
            {+1,-1,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+1,+0,+0,+0,+0,+0,+0,+1},
            {+0,+0,+0,+0,+0,+0,-1,+0}
        };
        GameStatus gs3 = new GameStatus(matrix3);
        System.out.println(gs3);
        gs3.currentPlayer = CellType.PLAYER2;
        loal1.Eval(gs3, CellType.PLAYER2);
        
        // En este caso se deduce que la ficha que está debajo de todo es la que debería moverse.
        // Ya que sale más práctico mover esa ficha para no romper la conexión de las otras.
        // Pero
        Move move3 = loal1.move(gs3);
        System.out.println("Desde " + move3.getFrom() + " a " + move3.getTo());
        
        // ------------- Última pieza por conectar ------------
    }

 
    
}
