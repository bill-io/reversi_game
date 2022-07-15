package reversi;

import java.util.ArrayList;
import java.util.Scanner;

public class main {

    int column;
    int row;
    int maxDepth;
    int movesFirst;

    public static void main(String[] args) {
        main window = new main(10);
        while (true){
            window.reversiGame();
            if (window.b.calculateValue(1) + window.b.calculateValue(2) == 64){
                System.out.printf("Player Final Score: %d", window.b.calculateValue(1));
                System.out.printf("Computer Final Score: %d", window.b.calculateValue(2));
                break;
            }
        }
    }

    public main(int depth) {
        maxDepth = depth;
        initialState();
        b = new board(gameBoard);
        printGameBoard();
    }

    public void reversiGame(){
        Scanner input = new Scanner(System.in);
        System.out.print("\n\nEnter Column ");
        column = input.nextInt();
        System.out.print("\nEnter Row ");
        row = input.nextInt();
        playerMoves(column, row);
        System.out.printf("%nPlayer Score: %d", b.calculateValue(1));
        System.out.printf("%nComputer Score: %d", b.calculateValue(2));
    }

    public void initialState() {

        gameBoard = new int[8][8];
        gameBoard[3][3] = 1;
        gameBoard[4][4] = 1;
        gameBoard[3][4] = 2;
        gameBoard[4][3] = 2;
    }

    int[][] gameBoard;
    board b;

    public void computerMoves(int x, int y, int depth) {

        if (b.getValidMoves(2).size() == 0) {
            initialState();
            printGameBoard();
            return;
        }

        if (b.gamestate[x][y] != 0)
            return;

        if (!b.makeMove(x, y, 2))
            return;


        printGameBoard();

        reversi.startTime = System.currentTimeMillis();
        reversi.maxTime = 1000; //Max searching time
        reversi.cutOff = false;

        int[][] boardCpy = board.cloneGrid(b.gamestate);

        for (int i = 1; i <= depth && !reversi.cutOff; i++) {

            reversi.maxLevel = i;
            reversi.minimax(true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, new board(boardCpy));

            if (!reversi.cutOff) {
                b = reversi.best;
            } else {
                System.out.println("\nComputer's Move \nMax depth completed: " + (i - 1));
            }
        }

        if (!reversi.cutOff)
            System.out.println("\nComputer's Move \nMax depth completed: " + depth + " (full)");

        printGameBoard();
    }
    
    void printGameBoard() {
        for (int y = 0; y < 8; y++){
            System.out.println("\n");
            for (int x = 0; x < 8; x++) {
                SetBlock(x, y, b.gamestate[x][y]);
            }
        }
    }

    public void playerMoves(int x, int y) {
        column = x;
        row = y;
        System.out.println("\nPlayer Chooses block (" + column + " , " + row + ")");
        computerMoves(column, row, maxDepth);
    }

    public void SetBlock(int x, int y, int c) {
        switch (c) {
            case 0:
                System.out.print("- ");
                break;
            case 1:
                System.out.print("X ");
                break;
            case 2:
                System.out.print("O ");
                break;
        }

    }
}
