/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore;

/**
 *
 * @author es-ahmedalizakaryah2
 */
public class observer {
    ChessGame game;
    
    public observer(ChessGame Game) {
        this.game = Game;
    }
    public void update()
    {
        
        System.out.print(this.game.getLastMove().getFromSquare().getFile() + "" + this.game.getLastMove().getFromSquare().getRank()) ;
        System.out.print(" " + this.game.getLastMove().getToSquare().getFile() +"" +this.game.getLastMove().getToSquare().getRank()) ;
    }
    
}
