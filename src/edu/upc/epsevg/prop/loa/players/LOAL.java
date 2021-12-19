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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Jugador humà de LOA
 * @author bernat
 */
public class LOAL implements IPlayer, IAuto {
    String name;
    CellType player;
    int profundidad = 3;
    static int num_fichas_enemigas;
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
    ZobristHashing zobrist;
    
    
    public LOAL(String name) {
        this.name = name;
        zobrist = new ZobristHashing(2, 64);
    }

    public LOAL(String name, int profundidad){
        this.name = name;
        this.profundidad = profundidad;
        this.num_fichas_enemigas=0;
        zobrist = new ZobristHashing(2, 64);
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
        this.zobrist.reset();

        int Valor = Integer.MIN_VALUE;
        Point from = null;
        Point to = null;
        boolean win = false;
        this.player = s.getCurrentPlayer();
        this.num_fichas_enemigas=8;
        //s.getNumberOfPiecesPerColor(CellType.opposite(player));
        CellType enemy = CellType.opposite(player);
        //System.out.println("Entrando en el move");
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
                int x = MinValor(aux, Integer.MIN_VALUE, Integer.MAX_VALUE, this.profundidad-1, this.player);
                if(x >= Valor){
                    from = posFicha;
                    to = mov;
                    Valor = x;
                }
            }
        }
        
       // System.out.println("Saliendo en el move");
        return new Move(from, to,0, this.profundidad, SearchType.RANDOM);
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
         if(profundidad == 0){
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));
           // System.out.println(Heuristica1 + " " + Heuristica2 + " valor total " + (Heuristica1-Heuristica2));
            return Heuristica1-Heuristica2;
        }
        CellType enemy = CellType.opposite(jugador);
        
        for (int i = 0; i < s.getNumberOfPiecesPerColor(enemy); i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(enemy, i);
            ArrayList<Point> moves = new ArrayList<>();
            
            boolean seguir = false;
            int hashAux = zobrist.add(enemy, posFicha);
            if(zh.containsKey(hashAux)){
                HashInfo hI = zh.get(hashAux);
                if(profundidad <= hI.profundidad) {
                    System.out.println("Entrando ZH MAX");
                    return hI.heuristica;
                }
                else {
                    for(Point m: s.getMoves(posFicha)){
                        if(m == hI.mejorMejorMovimientoFicha) moves.add(0, m);
                        else moves.add(m);
                    }                    
                }
            }

            if(moves.size() < 1) moves = s.getMoves(posFicha);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: moves){               
                GameStatus aux = new GameStatus(s);
                // TODO: mov és pieza del adversario?
                /*if(s.getPos(mov) == enemy && s.getNumberOfPiecesPerColor(enemy) <= num_fichas_enemigas){
                    continue;
                }*/
                // Movemos la ficha, tener en cuenta si se come una ficha mía, eso me beneficia
                aux.movePiece(posFicha, mov);
                if(aux.isGameOver() && aux.GetWinner() == enemy){
                    // Vamos mal!
                    return Integer.MIN_VALUE;
                }
                beta = Math.min(beta, MaxValor(aux, alfa, beta, profundidad-1, jugador));

                if(!seguir){
                    HashInfo hI = new HashInfo(beta, profundidad, posFicha, mov);
                    zh.put(hashAux, hI);
                }
                
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
        if(profundidad == 0){
            int Heuristica1 =  Eval(s, jugador);
            int Heuristica2 = Eval(s, CellType.opposite(jugador));
           // System.out.println(Heuristica1 + " " + Heuristica2 + " valor total " + (Heuristica1-Heuristica2));
            return Heuristica1-Heuristica2;
        }
        CellType enemy = CellType.opposite(jugador);
        for (int i = 0; i < s.getNumberOfPiecesPerColor(jugador); i++) {
            // Cogemos la primera posición de la primera ficha
            Point posFicha = s.getPiece(jugador, i);
            ArrayList<Point> moves = new ArrayList<>();
            
            boolean seguir = false;
            int hashAux = zobrist.add(jugador, posFicha);
            if(zh.containsKey(hashAux)){
                HashInfo hI = zh.get(hashAux);
                if(profundidad <= hI.profundidad) {
                    System.out.println("Entrando ZH MAX");
                    return hI.heuristica;
                }
                else {
                    for(Point m: s.getMoves(posFicha)){
                        if(m == hI.mejorMejorMovimientoFicha) moves.add(0, m);
                        else moves.add(m);
                    }                    
                }
            }
            
            if(moves.size() < 1) moves = s.getMoves(posFicha);
            // Iteramos sobre sus posibles movimientos
            for(Point mov: s.getMoves(posFicha)){
                GameStatus aux = new GameStatus(s);
                // TODO: mov és pieza del adversario?
                /*if(s.getPos(mov) == enemy && s.getNumberOfPiecesPerColor(enemy) <= num_fichas_enemigas){
                    continue;
                }*/
                // Movemos la ficha, tener en cuenta si se come una ficha mía, eso me beneficia
                aux.movePiece(posFicha, mov);
                if(aux.isGameOver() && aux.GetWinner() == jugador){
                    // Vamos bien!
                    return Integer.MAX_VALUE;
                }
                alfa = Math.max(alfa, MinValor(aux, alfa, beta, profundidad - 1, jugador));
                
                if(!seguir){
                    HashInfo hI = new HashInfo(alfa, profundidad, posFicha, mov);
                    zh.put(hashAux, hI);
                }
                
                if(alfa >= beta) return alfa;
            }
        }

        return alfa;
    }
    
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
        int valorFinal = 10 * (s.getNumberOfPiecesPerColor(jugador) - numeroSets) + (200-valorMinimo) + valorMatriz;
        //System.out.println(valorFinal);
        //System.out.println(" ");
        return valorFinal;
    }
    
    
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
     * Añade a la tabla de zobrist hash una entrada con la información.
     * @param hash Zobrist hash a guardar
     * @param profundidad Produnidad máxima llegada
     * @param heuristica Heurística obtenida hasta la profunidad
     * @param from Mejor movimiento desde
     * @param to Mejor movimiento a
     * @param player Jugador que ha realizado el movimiento
     */
    public void RecordHash(int hash, int profundidad, int heuristica, Point from, Point to, CellType player){
        HashInfo hI = new HashInfo(heuristica, profundidad, from, to, player);
        zh.put(hash, hI);
    }

//     public int Eval(GameStatus s, CellType jugador){
//         int h=0;
//         
//                 for (int[] dir : direcciones) {
//                    int dirX = dir[0];
//                    int dirY = dir[1];
//                     h += calcularCasilla(i , j, s, jugador, dirX, dirY);   
//                 }
//    }
//     
//     
//   public int calcularCasilla(int fila, int columna, GameStatus s, CellType jugador,int dirX,int dirY) {
//       int numero = 0; 
//       if((fila>=1 && dirX == -1) || (fila<=s.getSize()-1 && dirX== 1)){
//           if((columna>=1 && dirY == -1) || (columna<=s.getSize()-1 && dirY== 1)){
//               if(s.getPos(fila+dirX,columna+dirY) == jugador) numero+=1;
//               else if(s.getPos(fila+dirX,columna+dirY) != jugador) numero-=1;
//           }
//           else{
//               if(s.getPos(fila+dirX,columna) == jugador) numero+=1;
//               else if(s.getPos(fila+dirX,columna) != jugador) numero-=1;
//           }
//       }
//       else if((columna>=1 && dirY == -1) || (columna<=s.getSize()-1 && dirY== 1)){
//           if(s.getPos(fila,columna+dirY) == jugador) numero+=1;
//               else if(s.getPos(fila,columna+dirY) != jugador) numero-=1;
//       }
//       return numero;
//   }

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