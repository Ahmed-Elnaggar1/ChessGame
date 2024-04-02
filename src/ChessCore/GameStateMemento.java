/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore;

import ChessCore.Pieces.Piece;

/**
 *
 * @author es-ahmedalizakaryah2
 */
public class GameStateMemento {
    //Gamestate properties
    private ChessBoard boardState;
    //private Square whiteKingSquare;
    //private Square BlackKingSquare;
    private Move lastMove;
    private boolean isCastleWhiteQueen = false;
    private boolean isCastleWhiteKing = false;
    private boolean isCastleBlackQueen = false;
    private boolean isCastleBlackKing = false;

    private boolean isEnpassent = false;
    private boolean isPromotion = false;
    private GameStatus gameState;
    private Player whoseTurn;

    public ChessBoard getBoardState() {
        return boardState;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public boolean getIsCastleWhiteQueen() {
        return isCastleWhiteQueen;
    }

    public boolean getIsCastleWhiteKing() {
        return isCastleWhiteKing;
    }

    public boolean getIsCastleBlackQueen() {
        return isCastleBlackQueen;
    }

    public boolean getIsCastleBlackKing() {
        return isCastleBlackKing;
    }

    public boolean getIsEnpassent() {
        return isEnpassent;
    }

    public boolean getIsPromotion() {
        return isPromotion;
    }

    public GameStatus getGameState() {
        return gameState;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }
    public GameStateMemento(ChessBoard board, Player whoseTurn, GameStatus gameState, Move lastMove,
            boolean isCastleWhiteQueen, boolean isCastleWhiteKing, boolean isCastleBlackQueen,
            boolean isCastleBlackKing, boolean isEnpassent, boolean isPromotion) {
        this.boardState = board;
        this.whoseTurn = whoseTurn;
        this.gameState = gameState;
        this.isCastleBlackKing = isCastleBlackKing;
        this.isCastleBlackQueen = isCastleBlackQueen;
        this.isCastleWhiteKing = isCastleWhiteKing;
        this.isCastleWhiteQueen = isCastleWhiteQueen;
        this.lastMove = lastMove;
        this.isEnpassent = isEnpassent;
        this.isPromotion = isPromotion;
        
        

    }
    
}
