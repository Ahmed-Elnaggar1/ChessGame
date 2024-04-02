/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore;

import ChessCore.Pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ChessGame {

    private ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;
    private Square whiteKingSquare;
    private Square BlackKingSquare;
    private Move lastMove;
    private int moveCount = 1;
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;

    private boolean isCastleWhiteQueen = false;
    private boolean isCastleWhiteKing = false;
    private boolean isCastleBlackQueen = false;
    private boolean isCastleBlackKing = false;

    private boolean isEnpassent = false;
    private boolean isPromotion = false;
    
    private Stack<GameStateMemento> Memento;
    
    private ArrayList <observer > ovservers = new ArrayList<>();
    observer o = new observer(this);
    protected ChessGame(BoardInitializer boardInitializer) {
        this.board = new ChessBoard(boardInitializer.initialize());
        this.Memento = new Stack<>();
        ChessBoard boardState = new ChessBoard(this.board);
        GameStateMemento memento = new GameStateMemento(boardState, this.whoseTurn, this.gameStatus, this.lastMove, this.isCastleWhiteQueen, this.isCastleWhiteKing, this.isCastleBlackQueen, this.isCastleWhiteKing, this.isEnpassent, this.isPromotion);
        this.Memento.push(memento);
        ovservers.add(o);
    }

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    public void setPromotion(boolean promotion) {
        this.isPromotion = promotion;
    }

    public boolean getPromotion() {
        return this.isPromotion;
    }

    public void setWhiteQueenCastle(boolean castle) {
        this.isCastleWhiteQueen = castle;
    }

    public void setWhiteKingCastle(boolean castle) {
        this.isCastleWhiteKing = castle;
    }

    public void setBlackQueenCastle(boolean castle) {
        this.isCastleBlackQueen = castle;
    }

    public void setBlackKingCastle(boolean castle) {
        this.isCastleBlackKing = castle;
    }

    public boolean getWhiteQueenCastle() {
        return this.isCastleWhiteQueen;
    }

    public boolean getWhiteKingCastle() {
        return this.isCastleWhiteKing;
    }

    public boolean getBlackQueenCastle() {
        return this.isCastleBlackQueen;
    }

    public boolean getBlackKingCastle() {
        return this.isCastleBlackKing;
    }

    public boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        // A player can't capture his own piece.
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    ChessBoard getBoard() {
        return board;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public boolean makeMove(Move move) {
        if (!isValidMove(move)) {
            return false;
        }

        Square fromSquare = move.getFromSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);

        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {
            if (fromPiece.getOwner() == Player.WHITE) {
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
            } else {
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleKingSide = false;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleKingSide = false;
                }
            }
        }

        // En-passant.
        if (fromPiece instanceof Pawn
                && move.getAbsDeltaX() == 1
                && !hasPieceIn(move.getToSquare())) {
            board.setPieceAtSquare(lastMove.getToSquare(), null);
            setEnpassent(true);
        }

        // Promotion
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {
                Player playerPromoting = toSquareRank == BoardRank.EIGHTH ? Player.WHITE : Player.BLACK;
                PawnPromotion promotion = move.getPawnPromotion();
                if (promotion == PawnPromotion.None) {
                    setPromotion(true);
                    return false;
                }

                switch (promotion) {
                    case Queen:
                        fromPiece = new Queen(playerPromoting);
                        break;
                    case Rook:
                        fromPiece = new Rook(playerPromoting);
                        break;
                    case Knight:
                        fromPiece = new Knight(playerPromoting);
                        break;
                    case Bishop:
                        fromPiece = new Bishop(playerPromoting);
                        break;
                    case None:
                        throw new RuntimeException("Pawn moving to last rank without promotion being set. This should NEVER happen!");
                }
            }
        }

        // Castle
        if (fromPiece instanceof King
                && move.getAbsDeltaX() == 2) {

            Square toSquare = move.getToSquare();
            if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.FIRST) {
                // White king-side castle.
                // Rook moves from H1 to F1
                Square h1 = new Square(BoardFile.H, BoardRank.FIRST);
                Square f1 = new Square(BoardFile.F, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(h1);
                board.setPieceAtSquare(h1, null);
                board.setPieceAtSquare(f1, rook);
                setWhiteKingCastle(true);
            } else if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black king-side castle.
                // Rook moves from H8 to F8
                Square h8 = new Square(BoardFile.H, BoardRank.EIGHTH);
                Square f8 = new Square(BoardFile.F, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(h8);
                board.setPieceAtSquare(h8, null);
                board.setPieceAtSquare(f8, rook);
                setBlackKingCastle(true);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.FIRST) {
                // White queen-side castle.
                // Rook moves from A1 to D1
                Square a1 = new Square(BoardFile.A, BoardRank.FIRST);
                Square d1 = new Square(BoardFile.D, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(a1);
                board.setPieceAtSquare(a1, null);
                board.setPieceAtSquare(d1, rook);
                setWhiteQueenCastle(true);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black queen-side castle.
                // Rook moves from A8 to D8
                Square a8 = new Square(BoardFile.A, BoardRank.EIGHTH);
                Square d8 = new Square(BoardFile.D, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(a8);
                board.setPieceAtSquare(a8, null);
                board.setPieceAtSquare(d8, rook);
                setBlackQueenCastle(true);
            }
        }

        board.setPieceAtSquare(fromSquare, null);
        board.setPieceAtSquare(move.getToSquare(), fromPiece);

        whoseTurn = Utilities.revertPlayer(whoseTurn);
        lastMove = move;
        updateGameStatus();
        ChessBoard boardState = new ChessBoard(this.board);
        GameStateMemento memento = new GameStateMemento(boardState, this.whoseTurn, this.gameStatus, this.lastMove, this.isCastleWhiteQueen, this.isCastleWhiteKing, this.isCastleBlackQueen, this.isCastleWhiteKing, this.isEnpassent, this.isPromotion);
        this.Memento.push(memento);
        this.moveCount++;
        this.Notify();
        return true;
    }

    private void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }

    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON
                || gameStatus == GameStatus.BLACK_WON
                || gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
         */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(new Square(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(new Square(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public ArrayList<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = new Square(i, j);
                if (isValidMove(new Move(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }

    public void setEnpassent(boolean enpassent) {
        this.isEnpassent = enpassent;
    }

    public boolean getEnpassent() {
        return this.isEnpassent;
    }

    public Square getKingSquare() {
        Square s = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BoardRank rank = BoardRank.values()[i];
                BoardFile file = BoardFile.values()[j];

                s = new Square(file, rank);
                if (board.getPieceAtSquare(s) instanceof King) {
                    if (board.getPieceAtSquare(s).getOwner() == Player.WHITE) {
                        this.whiteKingSquare = s;
                    } else if (board.getPieceAtSquare(s).getOwner() == Player.BLACK) {
                        this.BlackKingSquare = s;
                    }
                }
            }
        }
        return s;
    }

    public Square getWhiteKingSquare() {
        return this.whiteKingSquare;
    }

    public Square getBlackKingSquare() {
        return this.BlackKingSquare;
    }

    /* public void deepCopy(Piece[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    
    }*/
    public GameStateMemento saveStateToMemento() {
        return new GameStateMemento(this.board, this.whoseTurn, this.gameStatus, this.lastMove,
                this.isCastleWhiteQueen, this.isCastleWhiteKing, this.isCastleBlackQueen,
                this.isCastleBlackKing, this.isEnpassent, this.isPromotion);
    }

    public void restoreStateFromMemento(GameStateMemento memento) {
        this.board = memento.getBoardState();
        this.gameStatus = memento.getGameState();
        this.whoseTurn = memento.getWhoseTurn();
        this.isCastleBlackKing = memento.getIsCastleBlackKing();
        this.isCastleBlackQueen = memento.getIsCastleBlackQueen();
        this.isCastleWhiteKing = memento.getIsCastleWhiteKing();
        this.isCastleWhiteQueen = memento.getIsCastleWhiteQueen();
        this.isEnpassent = memento.getIsEnpassent();
        this.isPromotion = memento.getIsPromotion();
        this.lastMove = memento.getLastMove();
    }

    public void undoMove() {
        if (moveCount == 1) {
            ChessBoard BOARDSTATE = new ChessBoard(Memento.peek().getBoardState());
            this.board = BOARDSTATE;
            this.whoseTurn = Memento.peek().getWhoseTurn();
            this.isCastleBlackKing = Memento.peek().getIsCastleBlackKing();
            this.isCastleBlackQueen = Memento.peek().getIsCastleBlackQueen();
            this.isCastleWhiteKing = Memento.peek().getIsCastleWhiteKing();
            this.isCastleWhiteQueen = Memento.peek().getIsCastleWhiteQueen();
            this.isEnpassent = Memento.peek().getIsEnpassent();
            this.isPromotion = Memento.peek().getIsPromotion();
            this.gameStatus = Memento.peek().getGameState();
            this.lastMove = Memento.peek().getLastMove();
        } else {
            GameStateMemento hold = Memento.pop();
            ChessBoard BOARDSTATE = new ChessBoard(Memento.peek().getBoardState());
            this.board = BOARDSTATE;
            this.whoseTurn = Memento.peek().getWhoseTurn();
            this.isCastleBlackKing = Memento.peek().getIsCastleBlackKing();
            this.isCastleBlackQueen = Memento.peek().getIsCastleBlackQueen();
            this.isCastleWhiteKing = Memento.peek().getIsCastleWhiteKing();
            this.isCastleWhiteQueen = Memento.peek().getIsCastleWhiteQueen();
            this.isEnpassent = Memento.peek().getIsEnpassent();
            this.isPromotion = Memento.peek().getIsPromotion();
            this.gameStatus = Memento.peek().getGameState();
            this.lastMove = hold.getLastMove();
                    moveCount--;
        }
        /*for (BoardRank rank : BoardRank.values()) {
                for (BoardFile file : BoardFile.values()) {
                    Piece p = board.getPieceAtSquare(new Square(file, rank));
                    if (p != null) {
                        System.out.print(p + " ");
                    } else {
                        System.out.print("   ");
                    }
                    
                }
                System.out.println("");
        }*/

    }
    public Stack <GameStateMemento> getStack()
    {
        return this.Memento;
    }
    
     public void Add(observer o)
    {
        this.ovservers.add(o);
    }
    public void Notify()
    {
        for(int i =0 ; i<ovservers.size();i++)
        {
            ovservers.get(i).update();
        }
    }
}
