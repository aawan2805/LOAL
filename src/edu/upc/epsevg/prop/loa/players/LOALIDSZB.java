package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.DisjointSet;
import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.HashInfo;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import edu.upc.epsevg.prop.loa.ZobristHashing;
import java.awt.Point;
import java.awt.geom.Point2D;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;


/**
 * Jugador humà de LOA
 * @author bernat
 */
public class LOALIDSZB implements IPlayer, IAuto {
    CellType player;
    boolean timeout;
    int nodosExplorados;
    int nodosExploradosTotal = 0;
    int matrix_valorcasilla[][] = new int[][] {
            {21,21,21,21,21,21,21,21},
            {21,23,23,23,23,23,23,21},
            {21,23,25,25,25,25,23,21},
            {21,23,25,27,27,25,23,21},
            {21,23,25,27,27,25,23,21},
            {21,23,25,25,25,25,23,21},
            {21,23,23,23,23,23,23,21},
            {21,21,21,21,21,21,21,21}
    };
    HashMap<Integer, HashInfo> zh = new HashMap<>();
    int[][][] bitString = new int[8][8][2];
    
    
    public LOALIDSZB(){
        startZobrist();
        this.nodosExplorados = 0;
    }
    
    private void startZobrist(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 2; k++) {
                    bitString[i][j][k] = (int)((long)(Math.random() * Long.MAX_VALUE) & -1L);
                }
            }
        }
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
        zh.clear();
        
        this.timeout = false;
        
        int hash = hashBoard(s);
        this.nodosExplorados = 0;
        
        this.player = s.getCurrentPlayer();
        Point from = null;
        Point to = null;
        
        int Valor = Integer.MIN_VALUE;
        boolean win = false;
        
        int profExplorada = 0;
        
        while(!this.timeout && !win){
            // Recorremos el número de fichas que tenemos en la partida
            for (int i = 0; i < s.getNumberOfPiecesPerColor(this.player) && !win; i++) {
                // Cogemos la primera posición de la primera ficha
                Point posFicha = s.getPiece(this.player, i);
                // Iteramos sobre sus posibles movimientos
                for(Point mov: s.getMoves(posFicha)){
                    GameStatus aux = new GameStatus(s);
                    // TODO: mov és pieza del adversario?

                    /*if(s.getPos(mov) == enemy && s.getNumberOfPiecesPerColor(enemy) <= num_fichas_enemigas){
                        //System.out.println("Hola");
                        continue;
                    }*/
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
                    int x = MinValor(aux, Integer.MIN_VALUE, Integer.MAX_VALUE, profExplorada, this.player, hash);
                    if(x >= Valor){
                        from = posFicha;
                        to = mov;
                        Valor = x;
                    }
                }
            }
            if(!win) profExplorada += 1;
        }
        this.nodosExploradosTotal += this.nodosExplorados;
       // System.out.println("Saliendo en el move");
        return new Move(from, to, this.nodosExplorados, profExplorada, SearchType.RANDOM);
    }

    /**
     *
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param prof Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @param hash Zobrist hash del tablero
     * @return El valor beta más pequeño posible a partir del tablero s.
     */
    public int MinValor(GameStatus s, int alfa, int beta, int prof, CellType jugador, int hash){
        //int val1 = ProbeHash(hash, profundidad, alfa, beta);
        //if(val1 != -1) return val1;
        if(prof == 0 || this.timeout){
            this.nodosExplorados++;
            
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));
           // System.out.println(Heuristica1 + " " + Heuristica2 + " valor total " + (Heuristica1-Heuristica2));
            int heur = Heuristica1-Heuristica2;
            // RecordHash(hash, profundidad, heur, 0, new Point(0, 0), new Point(0,0));
            
            return heur;
        }
        
        CellType enemy = CellType.opposite(jugador);
        
        Point bestMoveFrom = null; 
        Point bestMoveTo = null;
        boolean foundMovimientos = true;
        
        Point bestMoveFromZB = null;
        Point bestMoveToZB = null;
        
        ArrayList<Point> froms = new ArrayList<>();
        for (int i = 0; i < s.getNumberOfPiecesPerColor(enemy); i++) {
            froms.add(s.getPiece(enemy, i));
        }
        // Tablero ya analizado?
        /*if(zh.containsKey(hash)){
            HashInfo hI = zh.get(hash);
            if(hI.profundidad >= prof) {
                return hI.heuristica;
            }
            
            if(hI.who == enemy){
                bestMoveFrom = hI.mejorMovimientoDesde;
                bestMoveTo = hI.mejorMejorMovimientoA;

                int indexFrom = froms.indexOf(bestMoveFrom);
                if(indexFrom == -1) {
                    foundMovimientos = false;
                } else {
                    Collections.swap(froms, indexFrom, 0);
                }
            } else {
                System.out.println("COLISIÓN min " + jugador);
            }
        }*/
        
        //TreeMap<Integer, List<Point>> bestMove = new TreeMap<>();
        
        for (Point posFicha: froms) {
            ArrayList<Point> moves = s.getMoves(posFicha);
            
            if(bestMoveFrom != null && posFicha == bestMoveFrom && foundMovimientos){
                int indexBestMoveTo = moves.indexOf(bestMoveTo);
                if(indexBestMoveTo != -1){
                    Collections.swap(moves, indexBestMoveTo, 0);
                }
            } else {
                moves = s.getMoves(posFicha);
            }
            // Iteramos sobre sus posibles movimientos
            for(Point mov: moves){               
                GameStatus aux = new GameStatus(s);
                aux.movePiece(posFicha, mov);
                
                int newHash = hash;
                newHash ^= bitString[posFicha.x][posFicha.y][CellType.toColor01(enemy)];
                newHash ^= bitString[mov.x][mov.y][CellType.toColor01(enemy)];
                if(s.getPos(mov) == jugador){
                    newHash ^= bitString[mov.x][mov.y][CellType.toColor01(jugador)];
                }
                    
                if(aux.isGameOver() && aux.GetWinner() == enemy){
                    // Vamos mal!
                    beta = Integer.MIN_VALUE;
                    bestMoveFromZB = posFicha;
                    bestMoveToZB = mov;
                } else {
                    // If < que beta
                    int valor = MaxValor(aux, alfa, beta, prof-1, jugador, newHash);
                    if(valor < beta){
                        beta = valor;
                        bestMoveFromZB = posFicha;
                        bestMoveToZB = mov;
                    }
                }

                // Guardamos el movimiento para posteriormente evaluar
                //bestMove.put(beta, Arrays.asList(posFicha, mov));

                if(beta <= alfa){
                    //RecordHash(newHash, profundidad, beta, 1, posFicha, mov);
                    return beta;
                }
            }
            
        }
        
               
        //List<Point> max = bestMove.get(bestMove.firstKey());
        //RecordHash(hash, prof, beta, 0, bestMoveFromZB, bestMoveToZB, enemy);
        return beta;
    }
    /**
     *
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param prof Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @param hash Zobrist hash del tablero
     * @return El valor alfa más grande posible a partir del tablero s.
     */
    public int MaxValor(GameStatus s, int alfa, int beta, int prof, CellType jugador, int hash){
        //int hash2 = this.hashBoard(s);
        //int val1 = ProbeHash(hash, profundidad, alfa, beta);
        //if(val1 != -1) return val1;
        
        if(prof == 0 || this.timeout){
            this.nodosExplorados++;
            
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));            
            int heur = Heuristica1-Heuristica2;
            
            return heur;
        }
        
        CellType enemy = CellType.opposite(jugador);
        // ========= Zobrist ========== //
        Point bestMoveFrom = null; 
        Point bestMoveTo = null;
        boolean foundMovimientos = true;
        
        Point bestMoveFromZB = null;
        Point bestMoveToZB = null;
        
        ArrayList<Point> froms = new ArrayList<>();
        for (int i = 0; i < s.getNumberOfPiecesPerColor(jugador); i++) {
            froms.add(s.getPiece(jugador, i));
        }
        
        if(zh.containsKey(hash)){
            HashInfo hI = zh.get(hash);
                        
            if(hI.who == jugador){
                if(hI.profundidad >= prof) {
                    return hI.heuristica;
                }
            
                bestMoveFrom = hI.mejorMovimientoDesde;
                bestMoveTo = hI.mejorMejorMovimientoA;

                int indexFrom = froms.indexOf(bestMoveFrom);
                if(indexFrom == -1) {
                    foundMovimientos = false;
                } else {
                    Collections.swap(froms, indexFrom, 0);
                }
            }
            else{
                //System.out.println("COLISIÓN MAX" + jugador);
            }
        }
        
        boolean maxValFound = false;
        // ========= Zobrist ========== //
        for (Point posFicha: froms) {
            if(maxValFound) break;
            
            ArrayList<Point> moves = s.getMoves(posFicha);
            
            if(bestMoveFrom != null && posFicha == bestMoveFrom && foundMovimientos){
                int indexBestMoveTo = moves.indexOf(bestMoveTo);
                if(indexBestMoveTo != -1){
                    Collections.swap(moves, indexBestMoveTo, 0);
                }
            } else {
                moves = s.getMoves(posFicha);
            }
            
            // Iteramos sobre sus posibles movimientos
            for(Point mov: moves){
                if(maxValFound) break;
                
                GameStatus aux = new GameStatus(s);
                aux.movePiece(posFicha, mov);
                
                int newHash = hash;
                newHash ^= bitString[posFicha.x][posFicha.y][CellType.toColor01(jugador)];
                newHash ^= bitString[mov.x][mov.y][CellType.toColor01(jugador)];
                
                // Deshacer movimiento. Porque se come una ficha.
                if(s.getPos(mov) == enemy){
                    newHash ^= bitString[mov.x][mov.y][CellType.toColor01(enemy)];
                }
                
                if(aux.isGameOver() && aux.GetWinner() == jugador){
                    // Vamos bien!
                    alfa = Integer.MAX_VALUE;
                    bestMoveFromZB = posFicha;
                    bestMoveToZB = mov;
                    maxValFound = true;
                } else {
                    int valor = MinValor(aux, alfa, beta, prof - 1, jugador, newHash);
                    if(valor > alfa){
                        alfa = valor;
                        bestMoveFromZB = posFicha;
                        bestMoveToZB = mov;
                    }
                }
                                
                if(alfa >= beta) {
                    return alfa;
                }
            }
        }
        
        if(bestMoveFromZB == null || bestMoveToZB == null) return alfa;
        
        RecordHash(hash, prof, alfa, 0, bestMoveFromZB, bestMoveToZB, jugador);
        return alfa;
    }


    /**
     * Añade a la tabla de zobrist hash una entrada con la información.
     * @param hash Zobrist hash a guardar
     * @param profundidad Produnidad máxima llegada
     * @param heuristica Heurística obtenida hasta la profunidad
     * @param from Mejor movimiento desde
     * @param to Mejor movimiento a
     * @param player Jugador que ha realizado el movimiento
     */
    public void RecordHash(int hash, int profundidad, int heuristica, int flag, Point from, Point to, CellType player){
        HashInfo hI = new HashInfo(heuristica, profundidad, from, to , player);
        zh.put(hash, hI);
    }
 
    /**
     * Da una puntuación al tablero teniendo en cuenta los grupos y las distancias mínimas entre ellos.
     * @param jugador Ficha del jugador (O ó @) (PLAYER1 o PLAYER2)
     * @param s Tablero
     * @param pendingAmazons Lista con las posiciones de las fichas para el jugador en el tablero s
     * @param ds Grupos de fichas del jugador en el tablero s
     * @param numeroSets Número de grupos
     * @return Puntuación para el tablero s
     */    
    public int puntuarTablero(CellType jugador, GameStatus s, ArrayList<Point> pendingAmazons, DisjointSet ds,  int numeroSets){
        int valorMinimo=0;
        //System.out.println(s);
        int[] distanciaMinima = new int[numeroSets];
        for (int i = 0; i < numeroSets; i++) {
            distanciaMinima[i] = -1;
        }
        Point puntoCuleable = new Point((int) s.getSize()/2, (int) s.getSize()/2);
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
                           valorMinimo+=valor;
//                           if(valorMinimoLista == -1 ) valorMinimoLista = valor;
//                           else if(valor<valorMinimoLista) valorMinimoLista = valor;
//                           System.out.println(auxPrimaria.get(k) + " " + auxSecundaria.get(l) + " " + valor);
                       }
//                    System.out.println("valorMinimo " + valorMinimoLista);
                    int valor = (int) primario.distance(puntoCuleable);
                    if(distanciaMinima[i]<valor) distanciaMinima[i] = valor;
//                    if(valorMinimoListaAuxiliar2 == -1 ) valorMinimoListaAuxiliar2 = valorMinimoLista;
//                    else if(valorMinimoLista<valorMinimoListaAuxiliar2) valorMinimoListaAuxiliar2 = valorMinimoLista;
                }  
//                System.out.println("ValorMinimoParaLaArray: " + valorMinimoListaAuxiliar2);   
                    
//                valorMinimo += valorMinimoListaAuxiliar2;
            }
        }
        int ValorMinimoMedio = 0;
        for (int i = 0; i < numeroSets; i++){
            ValorMinimoMedio+=distanciaMinima[i];
        }
        int valorMatriz=0;
        Point Matrix;
        for (int i = 0; i < s.getNumberOfPiecesPerColor(jugador); i++) {
            Matrix = s.getPiece(jugador, i);
            valorMatriz+=matrix_valorcasilla[Matrix.x][Matrix.y];
        }
//        System.out.println(prueba);
        // System.out.println(valorMinimo);
        int valorFinal = 10 * (s.getNumberOfPiecesPerColor(jugador) - numeroSets) + (200-valorMinimo) + 2*(valorMatriz);
        //System.out.println(valorFinal);
        //System.out.println(" ");
        return valorFinal;
    }
    
    /**
     * Calcula la heurística para el tablero s y el jugador.
     * @param s Tablero del juego
     * @param jugador Player1 o Player2
     * @return Heurística para el tablero s y el jugador.
     */
    public int Eval(GameStatus s, CellType jugador) {
        //System.out.println("entrando");
        int qn = s.getNumberOfPiecesPerColor(jugador);
        // System.out.println(jugador);
        DisjointSet ds = new DisjointSet();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            ds.create_set(q);
            pendingAmazons.add(s.getPiece(jugador, q));
        }
        for (int i = 0; i < pendingAmazons.size(); i++) {
            for (int j = 0; j < pendingAmazons.size(); j++) {
                // No hace falta realmente porque tenemos un set.
                Point first = pendingAmazons.get(i);
                Point second = pendingAmazons.get(j);
                if (!first.equals(second)) {
                    if (first.distance(second) == 1) {
                        // System.out.println(first + " -> " + second);
                        // System.out.println(i + " " + j);
                        ds.union(i, j);
                    }
                }
            }
        }
        //System.out.println(ds.getNumberofDisjointSets());
       
       //System.out.println("saliendo");
        return puntuarTablero(jugador, s, pendingAmazons, ds, ds.getNumberofDisjointSets());
    }
    

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        this.timeout = true;
        System.out.println("Eres un lento de mierda");
        
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "LOAL IDS";
    }
    
    private int hashBoard(GameStatus s) {
        int hash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CellType ficha = s.getPos(i, j);
                if(ficha != CellType.EMPTY){
                    int fichaNumero = CellType.toColor01(ficha);
                    hash ^= bitString[i][j][fichaNumero];
                }
            }
        }

        return hash;
    }
}