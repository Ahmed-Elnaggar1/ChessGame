/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FrontEnd;

import ChessCore.BoardFile;
import ChessCore.BoardRank;
import ChessCore.GameStatus;
import ChessCore.Square;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author es-ahmedalizakaryah2
 */
public class TileClickListener extends MouseAdapter {

    private int row;
    private int column;
    private Board board;

    public TileClickListener(int row, int col, Board board) {
        this.row = row;
        this.column = col;
        this.board = board;
    }

    public void mouseClicked(MouseEvent e) {
        // Handle the click on the tile (row, col)
        JPanel clickedPanel = (JPanel) e.getSource();
        Component[] components = clickedPanel.getComponents();
        //System.out.println(components.length);
        ArrayList<Square> validMoves = new ArrayList<>();
        BoardFile file1 = BoardFile.values()[this.column];
                   
            if (board.getFirstClickTile() == null) {
                //first click
                 BoardRank rank1 = BoardRank.values()[7-this.row];
                    Square fromSquare1 = new Square(file1, rank1);
     // System.out.println("INIASAJAJDDAD"+board.getGame().getPieceAtSquare(fromSquare1)+" " +board.getGame().getPieceAtSquare(fromSquare1).getOwner());
                if (!board.isTileEmpty(clickedPanel)) {
                    // Store information of the first clicked tile
                    board.setFirstClickTile(clickedPanel);
                    board.setFirstColumn(this.column);
                    board.setFirstTileRow(row);
                    BoardFile file = BoardFile.values()[this.column];
                    BoardRank rank = BoardRank.values()[7-this.row];
                    Square fromSquare = new Square(file, rank);
                    validMoves = board.getGame().getAllValidMovesFromSquare(fromSquare);
                    board.setValidMoves(validMoves);
                    board.higlightValidMoves(fromSquare);
                     
                }
            } else {
                if (clickedPanel != board.getFirstClickTile()) {
                    board.setSecondClickTile(clickedPanel);
                    board.setSecondTileColumn(this.column);
                    board.setSecondTileRow(this.row);
                    BoardFile file = BoardFile.values()[this.column];
                    BoardRank rank = BoardRank.values()[7-this.row];
                    Square fromSquare = new Square(file, rank);
                    board.unhiglightValidMoves(fromSquare);
                    board.handleMove();

                }
            }
        

        //System.out.println("Clicked on tile: (" + row + ", " + column + ")");
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }
}
