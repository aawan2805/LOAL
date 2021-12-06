package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Jugador humà de LOA
 * @author bernat
 */
public class LOAL implements IPlayer, IAuto {
    String name;
    CellType player;
    int profundidad = 1;

    public LOAL(String name) {
        this.name = name;
    }

    public LOAL(String name, int profundidad){
        this.name = name;
        this.profundidad = profundidad;
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
                int x = MinValor(aux, Integer.MIN_VALUE, Integer.MAX_VALUE, this.profundidad-1, this.player);
                if(x > Valor){
                    from = posFicha;
                    to = mov;
                }
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
        if(profundidad == 0) return Eval(s, jugador);
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
                beta = Math.min(beta, MaxValor(aux, alfa, beta, profundidad-1, jugador));
                if(beta <= alfa) return beta;
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
        if(profundidad == 0) return Eval(s, jugador);

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
                alfa = Math.max(alfa, MinValor(aux, alfa, beta, profundidad - 1, jugador));
                if(alfa >= beta) return alfa;
            }
        }

        return alfa;
    }
    
    public int puntuarTablero(CellType jugador, GameStatus s, ArrayList<Point> pendingAmazons, DisjointSet ds,  int numeroSets){
        int valorMinimo=0;
        //System.out.println(s);
        ArrayList<ArrayList<Integer>> list_disjoint=ds.get_set();
        ArrayList<Integer> auxPrimaria, auxSecundaria;
        int prueba = 0; 
        for (int i = 0; i < list_disjoint.size()-1; i++) {
            prueba++;
            auxPrimaria = list_disjoint.get(i);
            for (int j = i+1; j < list_disjoint.size(); j++) {
                prueba++;
                auxSecundaria=list_disjoint.get(j);
                int valorMinimoListaAuxiliar2 = -1; 
                    for (int k = 0; k < auxPrimaria.size(); k++) {
                        prueba++;
                        Point primario = pendingAmazons.get(auxPrimaria.get(k));
                        int valorMinimoLista = -1;
                           for (int l = 0; l < auxSecundaria.size(); l++) {
                               prueba++;
                               Point secundario = pendingAmazons.get(auxSecundaria.get(l));
                               int valor = (int) primario.distance(secundario);
                               if(valorMinimoLista == -1 ) valorMinimoLista = valor;
                               else if(valor<valorMinimoLista) valorMinimoLista = valor;
    //                           System.out.println(auxPrimaria.get(k) + " " + auxSecundaria.get(l) + " " + valor);
                           }
    //                    System.out.println("valorMinimo " + valorMinimoLista);
                        if(valorMinimoListaAuxiliar2 == -1 ) valorMinimoListaAuxiliar2 = valorMinimoLista;
                        else if(valorMinimoLista<valorMinimoListaAuxiliar2) valorMinimoListaAuxiliar2 = valorMinimoLista;
                }  
//                System.out.println("ValorMinimoParaLaArray: " + valorMinimoListaAuxiliar2);   
                    
                valorMinimo += valorMinimoListaAuxiliar2;
            }
        }
        
//        System.out.println(prueba);
      //  System.out.println(valorMinimo);
        int valorFinal = s.getNumberOfPiecesPerColor(jugador) - numeroSets + (100-valorMinimo);
        //System.out.println(valorFinal);
        //System.out.println(" ");
        return valorFinal;
    }
    
    public int Eval(GameStatus s, CellType jugador){
        
        int qn = s.getNumberOfPiecesPerColor(jugador);
        DisjointSet ds = new DisjointSet();
        
        
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++){
            ds.create_set(q);
            pendingAmazons.add(s.getPiece(jugador, q));
        }

        for(int i = 0; i < pendingAmazons.size(); i++){
            for(int j = 0; j < pendingAmazons.size(); j++){
                // No hace falta realmente porque tenemos un set.
                Point first = pendingAmazons.get(i);
                Point second = pendingAmazons.get(j);
                if(!first.equals(second)){
                    if(first.distance(second) == 1){
                        ds.union(i, j);
                    }
                }
            }
        }
        return 0;
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
