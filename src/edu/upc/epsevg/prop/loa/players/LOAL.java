package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.DisjointSet;
import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.GameStatusAdvances;
import edu.upc.epsevg.prop.loa.HashInfo;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Jugador LOAL
 * @author Abdullah Bashir Yasmin, Mario Konstanty Kochan
 */
public class LOAL implements IPlayer, IAuto {
    CellType player;
    int profundidad;
    int nodosExplorados; // Nodos total explorados
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
    HashMap<Integer, HashInfo> zhPlayer = new HashMap<>();
    HashMap<Integer, HashInfo> zhEnemy = new HashMap<>();
    ArrayList<Integer> historialPartida = new ArrayList<>(); // Para evitar los ciclos
    int[][][] bitString = new int[8][8][2];
    
    
    public LOAL(int prof){
        startZobrist();
        this.nodosExplorados = 0;
        this.profundidad = prof;
    }
    
    /**
     * Incializa la matriz para tener valores random seguros, de esta forma asegurar que la hash será única.
     */
    public void startZobrist(){
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
        zhPlayer.clear();
        zhEnemy.clear();
        
        int hash = hashBoard(s);
        this.nodosExplorados = 0;
        
        this.player = s.getCurrentPlayer();
        Point from = null;
        Point to = null;
        
        int Valor = Integer.MIN_VALUE;
        boolean win = false;
                        
        CellType enemy = CellType.opposite(this.player);
        
        // Recorremos el número de fichas que tenemos en la partida
        for (int i = 0; i < s.getNumberOfPiecesPerColor(this.player) && !win; i++) {
            Point posFicha = s.getPiece(this.player, i);
            for(Point mov: s.getMoves(posFicha)){
                GameStatusAdvances aux = new GameStatusAdvances(s);
                
                int bitStringFrom = bitString[posFicha.x][posFicha.y][CellType.toColor01(this.player)];
                int bitStringTo = bitString[mov.x][mov.y][CellType.toColor01(this.player)];
                int eatPiecePos = bitString[mov.x][mov.y][CellType.toColor01(enemy)];
                int newHash = aux.movePiece(posFicha, mov, hash, bitStringFrom, bitStringTo, aux.getPos(mov) == this.player, eatPiecePos);
                
                // Añadimos el movimiento, para que evitar ciclos
                historialPartida.add(0, newHash);
                
                if(aux.isGameOver() && aux.GetWinner() == this.player){
                    // Hemos ganado
                    from = posFicha;
                    to = mov;
                    win = true;
                    historialPartida.remove(0);
                    break;
                }
                // TODO: Check if is solution
                int x = MinValor(aux, Integer.MIN_VALUE, Integer.MAX_VALUE, this.profundidad-1, this.player, hash);
                
                // Quitamos el movimiento.
                historialPartida.remove(0);
                
                if(x >= Valor){
                    from = posFicha;
                    to = mov;
                    Valor = x;
                }
            }
        }
        
        return new Move(from, to, this.nodosExplorados, this.profundidad, SearchType.RANDOM);
    }
    
    /**
     * Comprueba si el hash existe en la lista.
     * @param lista Lista en la cual buscar
     * @param hash Valor a buscar en la lista
     * @return True si existe o False si no existe
     */
    public boolean checkIfExistInList(ArrayList<Integer> lista, int hash){
        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i) == hash) return true;
        }
        return false;
    }
    
    /**
     * Minimiza y explora los "peores" movimientos del jugador, de tal forma que el jugador enemigo salgamos menos beneficiado.
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param prof Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @param hash Zobrist Hash del tablero s
     * @return El valor beta más pequeño posible a partir del tablero s.
     */
    public int MinValor(GameStatusAdvances s, int alfa, int beta, int prof, CellType jugador, int hash){
        if(prof == 0){
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));
            return Heuristica1-Heuristica2;
        }
        CellType enemy = CellType.opposite(jugador);
        
        Point bestMoveFrom = null; // El mejor "movimiento desde" del zobrist.
        Point bestMoveTo = null; // El mejor "movimiento a" del zobrist.
        boolean foundMovimientos = true;
        
        ArrayList<Point> froms = new ArrayList<>();
        for (int i = 0; i < s.getNumberOfPiecesPerColor(enemy); i++) froms.add(s.getPiece(enemy, i));

        // ========= Zobrist ========== //
        if(zhEnemy.containsKey(hash)){
            HashInfo hI = zhEnemy.get(hash);
            
            bestMoveFrom = hI.mejorMovimientoDesde;
            bestMoveTo = hI.mejorMejorMovimientoA;

            int indexFrom = froms.indexOf(bestMoveFrom);
            // Nos aseguramos que el movimiento exista. Más que nada por si se da el caso que ya esa ficha se la ha comido el enemigo.
            if(indexFrom == -1) {
                foundMovimientos = false;
            } else {
                // Ponemos el mejor "movimiento desde" como el primero a explorar.
                Collections.swap(froms, indexFrom, 0);
            }
            
        }
        // ========= Zobrist ========== //
        
        Point bestMoveFromZB = null;
        Point bestMoveToZB = null;
        
        for (Point posFicha: froms) {
            ArrayList<Point> moves = s.getMoves(posFicha);
            
            if(bestMoveFrom != null && posFicha == bestMoveFrom && foundMovimientos){
                int indexBestMoveTo = moves.indexOf(bestMoveTo);
                if(indexBestMoveTo != -1){
                    // Ponemos el mejor "movimiento a" como el primero a explorar.
                    Collections.swap(moves, indexBestMoveTo, 0);
                }
            } else {
                moves = s.getMoves(posFicha);
            }
            // Iteramos sobre sus posibles movimientos
            for(Point mov: moves){               
                GameStatusAdvances aux = new GameStatusAdvances(s);

                int bitStringFrom = bitString[posFicha.x][posFicha.y][CellType.toColor01(enemy)];
                int bitStringTo = bitString[mov.x][mov.y][CellType.toColor01(enemy)];
                int eatPiecePos = bitString[mov.x][mov.y][CellType.toColor01(jugador)];                
                int newHash = aux.movePiece(posFicha, mov, hash, bitStringFrom, bitStringTo, s.getPos(mov) == jugador, eatPiecePos);
                
                // Tenemos un ciclo
                if(checkIfExistInList(historialPartida, newHash)){
                    continue;
                }
                
                if(aux.isGameOver() && aux.GetWinner() == enemy){
                    // Vamos mal!
                    beta = Integer.MIN_VALUE;
                    bestMoveFromZB = posFicha;
                    bestMoveToZB = mov;
                } else {
                    int valor = MaxValor(aux, alfa, beta, prof-1, jugador, newHash);
                    if(valor < beta){
                        beta = valor;
                        bestMoveFromZB = posFicha;
                        bestMoveToZB = mov;
                    }
                }

                if(beta <= alfa){
                    return beta;
                }
            }
            
        }
        
        // Nos acordamos del movimiento.
        RecordHash(hash, prof, beta, bestMoveFromZB, bestMoveToZB, enemy);
        return beta;
    }

    /**
     * Maximiza y explora los mejores movimientos del jugador, de tal forma que el jugador salga más beneficiado.
     * @param s Estado del juego (tablero)
     * @param alfa Valor heurístico más grande hasta el momento
     * @param beta Valor heurístico más pequeño hasta el momento
     * @param prof Profundidad máxima a explorar
     * @param jugador Ficha del jugador (O ó @)
     * @param hash Zobrist Hash del tablero s
     * @return El valor alfa más grande posible a partir del tablero s.
     */
    public int MaxValor(GameStatusAdvances s, int alfa, int beta, int prof, CellType jugador, int hash){
        if(prof == 0){
            this.nodosExplorados++;
            
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));
            return Heuristica1-Heuristica2;
        }
        
        CellType enemy = CellType.opposite(jugador);
        
        Point bestMoveFrom = null; 
        Point bestMoveTo = null;
        boolean foundMovimientos = true;
        
        ArrayList<Point> froms = new ArrayList<>();
        for (int i = 0; i < s.getNumberOfPiecesPerColor(jugador); i++) {
            froms.add(s.getPiece(jugador, i));
        }
        
        // ========= Zobrist ========== //
        if(zhPlayer.containsKey(hash)){
            HashInfo hI = zhPlayer.get(hash);
                        
            bestMoveFrom = hI.mejorMovimientoDesde;
            bestMoveTo = hI.mejorMejorMovimientoA;

            int indexFrom = froms.indexOf(bestMoveFrom);
            if(indexFrom == -1) {
                foundMovimientos = false;
            } else {
                Collections.swap(froms, indexFrom, 0);
            }
            
        }
        // ========= Zobrist ========== //
        
        boolean maxValFound = false;
        Point bestMoveFromZB = null;
        Point bestMoveToZB = null;
        
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
                
                GameStatusAdvances aux = new GameStatusAdvances(s);
                                
                int bitStringFrom = bitString[posFicha.x][posFicha.y][CellType.toColor01(jugador)];
                int bitStringTo = bitString[mov.x][mov.y][CellType.toColor01(jugador)];
                int eatPiecePos = bitString[mov.x][mov.y][CellType.toColor01(enemy)];
                int newHash = aux.movePiece(posFicha, mov, hash, bitStringFrom, bitStringTo, s.getPos(mov) == enemy, eatPiecePos);
                
                // Tenemos un ciclo
                if(checkIfExistInList(historialPartida, newHash)){
                    continue;
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
        
        RecordHash(hash, prof, alfa, bestMoveFromZB, bestMoveToZB, jugador);
        return alfa;
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
        int[] distanciaMinima = new int[numeroSets];
        for (int i = 0; i < numeroSets; i++) distanciaMinima[i] = -1;
        
        Point puntoCentro = new Point((int) s.getSize()/2, (int) s.getSize()/2);
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
                       }
                    int valor = (int) primario.distance(puntoCentro);
                    if(distanciaMinima[i]<valor) distanciaMinima[i] = valor;
                }  
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

        int valorFinal = 10 * (s.getNumberOfPiecesPerColor(jugador) - numeroSets) + (200-valorMinimo) + 2*(valorMatriz);
        return valorFinal;
    }
    
    /**
     * Calcula la heurística para el tablero s y el jugador.
     * @param s Tablero del juego
     * @param jugador Player1 o Player2
     * @return Heurística para el tablero s y el jugador.
     */
    public int Eval(GameStatus s, CellType jugador) {
        int qn = s.getNumberOfPiecesPerColor(jugador);
        DisjointSet ds = new DisjointSet();
        ArrayList<Point> pendingAmazons = new ArrayList<>();

        for (int q = 0; q < qn; q++) {
            ds.create_set(q);
            pendingAmazons.add(s.getPiece(jugador, q));
        }
        
        for (int i = 0; i < pendingAmazons.size(); i++) {
            for (int j = 0; j < pendingAmazons.size(); j++) {
                Point first = pendingAmazons.get(i);
                Point second = pendingAmazons.get(j);
                if (!first.equals(second)) {
                    if (first.distance(second) == 1) {
                        ds.union(i, j);
                    }
                }
            }
        }
       
        return puntuarTablero(jugador, s, pendingAmazons, ds, ds.getNumberofDisjointSets());
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
    public void RecordHash(int hash, int profundidad, int heuristica, Point from, Point to, CellType player){
        HashInfo hI = new HashInfo(heuristica, profundidad, from, to);
        if(player == this.player) zhPlayer.put(hash, hI);
        else zhEnemy.put(hash, hI);
    }
    
    /**
     * Calcula el hash para un board.
     * @param s Tablero.
     * @return Hash para el tablero s.
     */
    public int hashBoard(GameStatus s) {
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


    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("Eres un lento de mierda");
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