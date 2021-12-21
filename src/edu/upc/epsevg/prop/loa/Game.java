package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.Level;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.players.*;

import javax.swing.SwingUtilities;

/**
 * Lines Of Action: el joc de taula.
 * @author bernat
 */
public class Game {
        /**
     * @param args
     */
    public static void main(String[] args) {
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                //IPlayer player1 = new MCCloudPlayer();
                IPlayer player1 = new BuckyPlayer(4);
                //IPlayer player1 = new RandomPlayer("Random");
                //IPlayer player1 = new HumanPlayer("españa");
                IPlayer player2 = new LOAL(4);
                                
                new Board(player1 , player2, 60, Level.DIFFICULT);
             }
        });
    }
}
