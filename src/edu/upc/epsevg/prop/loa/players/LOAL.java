package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.Random;

/**
 * Jugador humà de LOA
 * @author bernat
 */
public class LOAL implements IPlayer, IAuto {
    String name;
    CellType player;

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
        Point from = null;
        Point to = null;
        boolean win = false;
        
        this.player = s.getCurrentPlayer();
        // Recorremos el número de fichas que tenemos en la partida
        for (int i = 0; i < s.getNumberOfPiecesPerColor(this.player) && !win; i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(this.player, i);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: s.getMoves(posFicha)){
                GameStatus aux = new GameStatus(s);
                // TODO: mov és pieza del adversario?
                /*
                if(s.getPos(mov) != currPlayer){
                    // Se va a comer la pieza del enemigo!
                }
                */
                // Movemos la ficha
                aux.movePiece(posFicha, mov);
                if(aux.isGameOver() && aux.GetWinner() == this.player){
                    // Hemos ganado
                    from = posFicha;
                    to = mov;
                    win = true;
                    break;
                }
                // TODO: Check if is solution

            }
        }
        return new Move(from, to, 0, 0, SearchType.RANDOM);
    }

    /**
     *
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param profundidad Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @return El valor beta más pequeño posible a partir del tablero s.
     */
    public int MinValor(GameStatus s, int alfa, int beta, int profundidad, CellType jugador){
        if(profundidad == 0) return 0;
        CellType enemy;
        if(jugador == CellType.PLAYER1) enemy = CellType.PLAYER2;
        else enemy = CellType.PLAYER1;

        for (int i = 0; i < s.getNumberOfPiecesPerColor(enemy); i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(enemy, i);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: s.getMoves(posFicha)){
                GameStatus aux = new GameStatus(s);
                // TODO: mov és pieza del adversario?
                /*
                if(s.getPos(mov) != currPlayer){
                    // Se va a comer la pieza del enemigo!
                }
                */
                // Movemos la ficha, tener en cuenta si se come una ficha mía, eso me beneficia
                aux.movePiece(posFicha, mov);
                if(aux.isGameOver() && aux.GetWinner() == enemy){
                    // Vamos mal!
                    return Integer.MIN_VALUE;
                }

            }
        }

        return beta;
    }

    /**
     *
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param profundidad Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @return El valor alfa más grande posible a partir del tablero s.
     */
    public int MaxValor(GameStatus s, int alfa, int beta, int profundidad, CellType jugador){
        if(profundidad == 0) return 0;

        for (int i = 0; i < s.getNumberOfPiecesPerColor(jugador); i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(jugador, i);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: s.getMoves(posFicha)){
                GameStatus aux = new GameStatus(s);
                // TODO: mov és pieza del adversario?
                /*
                if(s.getPos(mov) != currPlayer){
                    // Se va a comer la pieza del enemigo!
                }
                */
                // Movemos la ficha, tener en cuenta si se come una ficha mía, eso me beneficia
                aux.movePiece(posFicha, mov);
                if(aux.isGameOver() && aux.GetWinner() == jugador){
                    // Vamos bien!
                    return Integer.MAX_VALUE;
                }

            }
        }

        return alfa;
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
