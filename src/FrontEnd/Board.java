/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FrontEnd;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import ChessCore.*;
import ChessCore.Pieces.Pawn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author es-ahmedalizakaryah2
 */
public class Board extends javax.swing.JFrame {

    /**
     * Creates new form Board
     */
    private JFrame FlippedFrame = new JFrame();
    private JPanel chessBoard = new JPanel();
    private JPanel firstClickedTile;
    private JPanel secondClickedTile;
    private int firstTileRow;
    private int secondTileRow;
    private int firstTileColumn;
    private int secondTileColumn;
    private ArrayList<Color> ColorValidMoves = new ArrayList<>();
    private ClassicChessGame ClassicGame = new ClassicChessGame();
    private ArrayList<Square> validMoves = new ArrayList<>();
    private boolean isPawnPromotion = false;
    private boolean isEnpassenet = false;
    private Color kingColor;
    private Square highlightedSquare = null;
    private JPanel[][] board = new JPanel[8][8];
    private JPanel[][] board2 = new JPanel[8][8];
    private boolean isIncheck = false;
    private Stack<JPanel> firstTile = new Stack<>();
    private Stack<JPanel> secondTile = new Stack<>();
    private Stack<Integer> row1 = new Stack<>();
    private Stack<Integer> row2 = new Stack<>();
    private Stack<Integer> column1 = new Stack<>();
    private Stack<Integer> column2 = new Stack<>();
    private Stack<JLabel> capturedImage = new Stack<>();

    public Board() {
        initComponents();
        setTitle("Chess Board");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        FlippedFrame.setLayout(new BorderLayout());
        chessBoard.setLayout(new GridLayout(8, 8));
        this.add(this.creatingButton(), BorderLayout.NORTH);

        createChessBoard();
        this.add(chessBoard, BorderLayout.CENTER);
        CreateJFrame(FlippedFrame);
        addChessPiece(0, 0, "BR.gif");
        addChessPiece(0, 1, "BN.gif");
        addChessPiece(0, 2, "BB.gif");
        addChessPiece(0, 3, "BQ.gif");
        addChessPiece(0, 4, "BK.gif");
        addChessPiece(0, 5, "BB.gif");
        addChessPiece(0, 6, "BN.gif");
        addChessPiece(0, 7, "BR.gif");
        addChessPiece(1, 0, "BP.gif");
        addChessPiece(1, 1, "BP.gif");
        addChessPiece(1, 2, "BP.gif");
        addChessPiece(1, 3, "BP.gif");
        addChessPiece(1, 4, "BP.gif");
        addChessPiece(1, 5, "BP.gif");
        addChessPiece(1, 6, "BP.gif");
        addChessPiece(1, 7, "BP.gif");

        addChessPiece(7, 0, "WR.gif");
        addChessPiece(7, 1, "WN.gif");
        addChessPiece(7, 2, "WB.gif");
        addChessPiece(7, 3, "WQ.gif");
        addChessPiece(7, 4, "WK.gif");
        addChessPiece(7, 5, "WB.gif");
        addChessPiece(7, 6, "WN.gif");
        addChessPiece(7, 7, "WR.gif");
        addChessPiece(6, 0, "WP.gif");
        addChessPiece(6, 1, "WP.gif");
        addChessPiece(6, 2, "WP.gif");
        addChessPiece(6, 3, "WP.gif");
        addChessPiece(6, 4, "WP.gif");
        addChessPiece(6, 5, "WP.gif");
        addChessPiece(6, 6, "WP.gif");
        addChessPiece(6, 7, "WP.gif");
    }

    protected void CreateJFrame(JFrame flipped) {
        flipped.setSize(400, 400);
        flipped.setTitle("Flipped Chess Board");
        flipped.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        flipped.setLayout(new BorderLayout());

    }

    public JButton creatingButton() {
        JButton undoButton = new JButton("UNDO");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle button click
                System.out.println("Button clicked!");
                ClassicGame.undoMove();
                undoMove();
            }
        });
        undoButton.setSize(50, 50);
        return undoButton;
    }

    private void createChessBoard() {
        Color lightColor = new Color(240, 217, 181); // Light color
        Color darkColor = new Color(181, 136, 99);  // Dark color

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color tileColor = (row + col) % 2 == 0 ? lightColor : darkColor;

                JPanel tile = new JPanel();
                tile.setBackground(tileColor);
                tile.addMouseListener(new TileClickListener(row, col, this));
                chessBoard.add(tile);
                board[row][col] = tile;
                board2[row][col] = tile;
            }
        }
    }

    private void addChessPiece(int row, int col, String imagePath) {
        Component[] components = this.chessBoard.getComponents();
        int index = row * 8 + col;
        if (index >= 0) {
            if (components[index] instanceof JPanel) {
                JPanel tile = (JPanel) components[index];
                addChessPieceToTile(tile, imagePath);
            }
        }
    }

    private void addChessPieceToTile(JPanel tile, String imagePath) {
        ImageIcon icon = createImageIcon(imagePath);
        if (icon != null) {
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            tile.add(pieceLabel);
            tile.revalidate(); // Ensure the panel updates its layout
            tile.repaint();
        }
    }

    private ImageIcon createImageIcon(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                byte[] imageData = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(imageData);
                fis.close();
                return new ImageIcon(imageData);
            } else {
                System.err.println("File not found: " + path);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isTileEmpty(JPanel tile) {
        Component[] components = tile.getComponents();
        // If the tile has no child components, it is empty
        return components.length == 0;
    }

    public void handleMove() {

        BoardFile file = BoardFile.values()[this.firstTileColumn];
        BoardRank rank = BoardRank.values()[7 - this.firstTileRow];
        BoardFile file2 = BoardFile.values()[this.secondTileColumn];
        BoardRank rank2 = BoardRank.values()[7 - this.secondTileRow];
        Square fromSquare = new Square(file, rank);
        Square toSquare = new Square(file2, rank2);
        Move move = new Move(fromSquare, toSquare);
        //System.out.println(ClassicGame.getPieceAtSquare(fromSquare) + " " + ClassicGame.getWhoseTurn());
        JLabel image;
        boolean pawn = ClassicGame.getPieceAtSquare(fromSquare) instanceof Pawn;
        boolean move1 = ClassicGame.makeMove(move);
        if (ClassicGame.getGameStatus() == GameStatus.BLACK_WON) {
            JOptionPane.showMessageDialog(null, "Black Wins.");
        } else if (ClassicGame.getGameStatus() == GameStatus.WHITE_WON) {
            JOptionPane.showMessageDialog(null, "White Wins.");
        } else if (ClassicGame.getGameStatus() == GameStatus.INSUFFICIENT_MATERIAL) {
            JOptionPane.showMessageDialog(null, "Draw.. Insufficient material");
        } else if (ClassicGame.getGameStatus() == GameStatus.STALEMATE) {
            JOptionPane.showMessageDialog(null, "Stalemate");
        }
        boolean move2 = false;

        //usual move or capturing
        Square s = null;
        if (ClassicGame.getGameStatus() == GameStatus.WHITE_UNDER_CHECK) {
            this.isIncheck = true;
            s = ClassicGame.getKingSquare();
            this.highlightRED(ClassicGame.getWhiteKingSquare());
        } else if (ClassicGame.getGameStatus() == GameStatus.BLACK_UNDER_CHECK) {
            this.isIncheck = true;
            s = ClassicGame.getKingSquare();
            this.highlightRED(ClassicGame.getBlackKingSquare());
        } else if (move1 && this.isIncheck) {

            this.unhighlightRED(highlightedSquare);
            this.isIncheck = false;

        }
        if (!move1 && ClassicGame.getPromotion()) {
            createPromotionMove(fromSquare, toSquare);
            flipBoard(!move1, move2);
        }
        if (move1) {

            //System.out.println("MOVEEE" + move1);
            image = gettingPiece(this.firstClickedTile);
            removePiece(this.firstClickedTile, image);
            //handling Enpassent
            if (ClassicGame.getEnpassent() == true && ClassicGame.getWhoseTurn() == Player.BLACK) {
                System.out.println("INNNN");

                int row = this.secondTileRow + 1;
                System.out.println("rowwwwww" + row);
                System.out.println("FFFFF" + board[row][this.secondTileColumn].getComponents().length);
                removePiece(board[row][this.secondTileColumn], gettingPiece(board[row][this.secondTileColumn]));
                board[row][this.secondTileColumn].revalidate();
                board[row][this.secondTileColumn].repaint();
                ClassicGame.setEnpassent(false);
            } else if (ClassicGame.getEnpassent() == true && ClassicGame.getWhoseTurn() == Player.WHITE) {
                System.out.println("oNNNN");
                int row = this.secondTileRow - 1;
                System.out.println("rowwwwww" + row);
                removePiece(board[row][this.secondTileColumn], gettingPiece(board[row][this.secondTileColumn]));
                board[row][this.secondTileColumn].revalidate();
                board[row][this.secondTileColumn].repaint();
                ClassicGame.setEnpassent(false);
            } // end of handling enpassent
            //handling Castling
            else if (ClassicGame.getBlackKingCastle()) {
                ImageIcon icon = createImageIcon("BR.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                addPieceToTile(board[this.firstTileRow][this.firstTileColumn + 1], pieceLabel);
                Component[] components = getContentPane().getComponents();
                System.out.println("A7A7A7AINDEXXXXXXXXXXXXX" + components.length);
                removePiece(board[7][7], gettingPiece(board[7][7]));
                ClassicGame.setBlackKingCastle(false);
            } else if (ClassicGame.getBlackQueenCastle()) {
                ImageIcon icon = createImageIcon("BR.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                addPieceToTile(board[this.firstTileRow][this.firstTileColumn - 1], pieceLabel);
                Component[] components = getContentPane().getComponents();
                //System.out.println("A7A7A7AINDEXXXXXXXXXXXXX" + components.length);
                ClassicGame.setBlackQueenCastle(false);
                removePiece(board[7][0], gettingPiece(board[7][0]));
            } else if (ClassicGame.getWhiteKingCastle()) {
                addChessPiece(this.firstTileRow, this.firstTileColumn + 1, "WR.gif");
                ClassicGame.setWhiteKingCastle(false);
                removePiece(board[0][7], gettingPiece(board[0][7]));
            } else if (ClassicGame.getWhiteQueenCastle()) {
                addChessPiece(this.firstTileRow, this.firstTileColumn - 1, "WR.gif");
                ClassicGame.setWhiteQueenCastle(false);
                removePiece(board[0][0], gettingPiece(board[0][0]));
            } else if (!isTileEmpty(this.secondClickedTile)) {
                this.capturedImage.push(gettingPiece(this.secondClickedTile));
                removePiece(this.secondClickedTile, gettingPiece(this.secondClickedTile));

            }

            addPieceToTile(this.secondClickedTile, image);
        }
        flipBoard(move1, move2);

        //System.out.println("ROW" + this.firstTileRow + "Column" + this.firstTileColumn);
        //System.out.println("ROW" + this.secondTileRow + "Column" + this.secondTileColumn);
        this.firstTile.push(this.firstClickedTile);
        this.secondTile.push(this.secondClickedTile);
        this.setFirstClickTile(null);
        this.setSecondClickTile(null);
    }

    public void flipBoard(boolean move1, boolean move2) {
        if (move1 || move2) {
            if (ClassicGame.getWhoseTurn() == Player.BLACK) {

                this.setVisible(false);
                rotateFlipped(FlippedFrame);
                FlippedFrame.setVisible(true);
            } else {
                rotateOriginal(this);
                this.setVisible(true);

                FlippedFrame.setVisible(false);
            }
        }
    }

    public void addPieceToTile(JPanel tile, JLabel image) {
        tile.add(image);
        tile.revalidate();
        tile.repaint();
    }

    public JLabel gettingPiece(JPanel tile) {
        Component[] components = tile.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel pieceLabel = (JLabel) component;
                return pieceLabel;
            }
        }
        return null;
    }

    public void removePiece(JPanel tile, JLabel image) {
        // JLabel pieceLabel = gettingPiece(tile);
        if (image != null) {
            tile.remove(image);
        }
        tile.revalidate();
        tile.repaint();
    }

    private void createPromotionMove(Square fromSquare, Square toSquare) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        //creating a dialogue option message displaying choose message with title pawn promotion
        //setting option style with message giving array of options and making queen as default choice
        ClassicGame.setPromotion(false);
        int choice = JOptionPane.showOptionDialog(this,
                "Choose a piece for promotion",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        Move move = null;
        JLabel promoted = null;
        if (choice == 0) {
            move = new Move(fromSquare, toSquare, PawnPromotion.Queen);
            if (ClassicGame.getWhoseTurn() == Player.WHITE) {
                ImageIcon icon = createImageIcon("WQ.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            } else {
                ImageIcon icon = createImageIcon("BQ.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            }
        } else if (choice == 1) {
            move = new Move(fromSquare, toSquare, PawnPromotion.Rook);
            if (ClassicGame.getWhoseTurn() == Player.WHITE) {
                ImageIcon icon = createImageIcon("WR.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            } else {
                ImageIcon icon = createImageIcon("BR.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            }
        } else if (choice == 2) {
            move = new Move(fromSquare, toSquare, PawnPromotion.Bishop);
            if (ClassicGame.getWhoseTurn() == Player.WHITE) {
                ImageIcon icon = createImageIcon("WB.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            } else {
                ImageIcon icon = createImageIcon("BB.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            }
        } else if (choice == 3) {
            move = new Move(fromSquare, toSquare, PawnPromotion.Knight);
            if (ClassicGame.getWhoseTurn() == Player.WHITE) {
                ImageIcon icon = createImageIcon("WN.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            } else {
                ImageIcon icon = createImageIcon("BN.gif");
                JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
                promoted = (pieceLabel);
            }
        }
        ClassicGame.makeMove(move);
        removePiece(this.secondClickedTile, promoted);
        removePiece(this.firstClickedTile, promoted);
        addPieceToTile(this.secondClickedTile, promoted);

    }

    //Flip the board
    protected void rotateFlipped(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.repaint();
        frame.add(this.creatingButton(), BorderLayout.NORTH);
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                this.chessBoard.add(board2[row][col]);
            }
        }
        frame.add(chessBoard, BorderLayout.CENTER);
        frame.repaint();
        frame.setVisible(true);
        this.setVisible(false);

    }

    protected void rotateOriginal(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.repaint();
        this.add(this.creatingButton(), BorderLayout.NORTH);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.chessBoard.add(board[row][col]);
            }
        }
        this.add(chessBoard, BorderLayout.CENTER);
        frame.repaint();
        frame.setVisible(true);
        this.chessBoard.setVisible(true);
        //this.setVisible(false);

    }

    public void setFirstClickTile(JPanel tile) {
        this.firstClickedTile = tile;
    }

    public JPanel getFirstClickTile() {
        return this.firstClickedTile;
    }

    public void setSecondClickTile(JPanel tile) {
        this.secondClickedTile = tile;
    }

    public JPanel getSecondClickTile() {
        return this.secondClickedTile;
    }

    public void setFirstTileRow(int row) {
        this.firstTileRow = row;
        this.row1.push(row);
    }

    public void setFirstColumn(int column) {
        this.firstTileColumn = column;
        this.column1.push(column);
    }

    public void setSecondTileRow(int row) {
        this.secondTileRow = row;
        this.row2.push(row);
    }

    public void setSecondTileColumn(int col) {
        this.secondTileColumn = col;
        this.column2.push(col);
    }

    public void setEnpasssent(boolean enpassent) {
        this.isEnpassenet = enpassent;
    }

    public void setPromotion(boolean promotion) {
        this.isPawnPromotion = promotion;
    }

    public void highlightRED(Square kingSquare) {
        kingColor = board[7 - kingSquare.getRank().getValue()][kingSquare.getFile().getValue()].getBackground();
        board[7 - kingSquare.getRank().getValue()][kingSquare.getFile().getValue()].setBackground(Color.RED);
        highlightedSquare = kingSquare;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Board().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    public void higlightValidMoves(Square fromSquare) {

        ColorValidMoves.clear();
        //System.out.println("VALUESSSADSADASF"+fromSquare.getRank().getValue());
        for (int i = 0; i < this.validMoves.size(); i++) {
            this.ColorValidMoves.add(board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].getBackground());
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].setBackground(Color.GREEN);
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].revalidate();
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].repaint();
        }
    }

    public void unhiglightValidMoves(Square fromSquare) {
        //System.out.println("VALUESSSADSADASF"+fromSquare.getRank().getValue());
        for (int i = 0; i < this.validMoves.size(); i++) {
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].setBackground(this.ColorValidMoves.get(i));
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].revalidate();
            board[7 - this.validMoves.get(i).getRank().getValue()][this.validMoves.get(i).getFile().getValue()].repaint();

        }
    }

    public ClassicChessGame getGame() {
        return this.ClassicGame;
    }

    public void setValidMoves(ArrayList<Square> validMoves) {
        this.validMoves = validMoves;
    }

    private void unhighlightRED(Square kingSquare) {
        board[7 - kingSquare.getRank().getValue()][kingSquare.getFile().getValue()].setBackground(kingColor);
        board[7 - kingSquare.getRank().getValue()][kingSquare.getFile().getValue()].revalidate();
        board[7 - kingSquare.getRank().getValue()][kingSquare.getFile().getValue()].repaint();
        highlightedSquare = null;

    }

    public void undoMove() {
        Move lastMove = ClassicGame.getStack().peek().getLastMove();
        boolean isCastleBlackKing = ClassicGame.getStack().peek().getIsCastleBlackKing();
        boolean isCastleBlackQueen = ClassicGame.getStack().peek().getIsCastleBlackQueen();
        boolean isCastleWhiteKing = ClassicGame.getStack().peek().getIsCastleWhiteKing();
        boolean isCastleWhiteQueen = ClassicGame.getStack().peek().getIsCastleWhiteQueen();
        boolean isEnpassent = ClassicGame.getStack().peek().getIsEnpassent();
        boolean isPromotion = ClassicGame.getStack().peek().getIsPromotion();
        GameStatus GameStatus = ClassicGame.getStack().peek().getGameState();
        Player whoseTurn = ClassicGame.getStack().peek().getWhoseTurn();
        Square fromSquare;

        Square toSquare = null;
        if (lastMove == null) {

        } else {
            fromSquare = lastMove.getFromSquare();
            toSquare = lastMove.getToSquare();
        }

        JPanel firstClicked = this.firstTile.pop();
        JPanel secondClicked = this.secondTile.pop();
        int column2 = (int) this.column2.pop();
        int column1 = (int) this.column1.pop();
        //System.out.println("akjdgskjashdjkasd" + rankFrom + " " + fileFrom);
        JLabel image = gettingPiece(secondClicked);
        removePiece(secondClicked, image);
        System.out.println("ASDSADASDASFASFASFASFASFADF" + isEnpassent + whoseTurn);
        if (isEnpassent && whoseTurn == Player.BLACK) {
            System.out.println("INNNN");
            ImageIcon icon = createImageIcon("WP.gif");
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            int row = this.row2.pop() - 1;
            int column = (int) this.column2.pop();
            System.out.println("rowwwwww" + row + " " + column);
            System.out.println("FFFFF" + board[row][column].getComponents().length);
            removePiece(board[row][column], pieceLabel);
            System.out.println("FFFFF" + board[row][column].getComponents().length);
            addPieceToTile(board[row][column], pieceLabel);
            board[row][column].revalidate();
            board[row][column].repaint();
            ClassicGame.setEnpassent(true);
        } else if (isEnpassent && ClassicGame.getWhoseTurn() == Player.WHITE) {
            System.out.println("oNNNN");
            ImageIcon icon = createImageIcon("BP.gif");
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            int row = this.row2.pop() + 1;
            int column = this.column2.pop();
            System.out.println("rowwwwww" + row);
            removePiece(board[row][column], gettingPiece(board[row][column]));
            addPieceToTile(board[row][column], pieceLabel);
            board[row][column].revalidate();
            board[row][column].repaint();
            ClassicGame.setEnpassent(true);
        } else if  (lastMove != null && ClassicGame.getPieceAtSquare(toSquare) != null ) {
            if (!capturedImage.isEmpty()) {
                addPieceToTile(secondClicked, this.capturedImage.pop());
            }
        }
        addPieceToTile(firstClicked, image);

        flipBoard(true, true);

    }
}
