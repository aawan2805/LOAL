package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jugador aleatori
 * @author bernat
 */
public class RandomPlayer implements IPlayer, IAuto {

    private String name;
    private GameStatus s;

    public RandomPlayer(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {

        CellType color = s.getCurrentPlayer();
        this.s = s;
        int qn = s.getNumberOfPiecesPerColor(color);
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getPiece(color, q));
        }
        // Iterem aleatòriament per les peces fins que trobem una que es pot moure.
        Point queenTo = null;
        Point queenFrom = null;
        while (queenTo == null) {
            Random rand = new Random();
            int q = rand.nextInt(pendingAmazons.size());
            queenFrom = pendingAmazons.remove(q);
            queenTo = posicioRandomAmazon(s, queenFrom);
        }

        // "s" és una còpia del tauler, per es pot manipular sense perill
        s.movePiece(queenFrom, queenTo);

        return new Move(queenFrom, queenTo, 0, 0, SearchType.RANDOM);
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }

    private Point posicioRandomAmazon(GameStatus s, Point pos) {
                
        ArrayList<Point> points =  s.getMoves(pos);
        if (points.size() == 0) {
            return null;//no es pot moure
        }
        
        Random rand = new Random();
        int p = rand.nextInt(points.size());
        return points.get(p);
    }

    private boolean isInBounds(int x, int y) {
        return (x >= 0 && x < s.getSize())
                && (y >= 0 && y < s.getSize());
    }

    private Point posicioRandom(GameStatus s) {
        int n = s.getEmptyCellsCount();

        Random rand = new Random();
        int p = rand.nextInt(n) + 1;//de 1 a n
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i, j) == CellType.EMPTY) {
                    p--;
                    if (p == 0) {
                        return new Point(i, j);
                    }
                }
            }
        }
        throw new RuntimeException("Random exhausted");
    }

}
