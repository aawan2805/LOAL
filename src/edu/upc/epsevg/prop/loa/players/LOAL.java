package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.Random;

/**
 * Jugador humà de LOA
 * @author bernat
 */
public class LOAL implements IPlayer {

    String name;

    public LOAL(String name) {
        this.name = name;
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
        int Valor = Integer.MIN_VALUE;
        Move pos = null;
        
        CellType currPlayer = s.getCurrentPlayer();
        // Recorremos el número de fichas que tenemos en la partida
        for (int i = 0; i < s.getNumberOfPiecesPerColor(currPlayer); i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(currPlayer, i);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: s.getMoves(posFicha)){
                // TODO: mov és pieza del adversario?
                if(s.getPos(mov) != currPlayer){
                    // Se va a comer la pieza del enemigo!
                }
            }
        }
        
        return new Move(null, null, 0, 0, SearchType.RANDOM);
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("Bah! You are so slow...");
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "LOAL";
    }
}
