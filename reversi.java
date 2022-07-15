/**
 * Reversi assignment for EDA132
 * @author	Andreas Hansson, Fredrik Österberg
 */

package reversi;

import java.util.ArrayList;

public class reversi {
	
	public static int maxLevel = 10; // negative to remove limit
	public static long maxTime = 800; // milliseconds. stops tree expansion. takes some additional time to finalize calculations
	public static long startTime = 0;
	public static boolean cutOff = false;

	public static board best;
	
	/**
	 * Minimax function with alpha-beta pruning
	 * @param maximizingPlayer	Computer or opponent
	 * @param level	Subtree depth, starting with 0
	 * @param alpha	Computer assured of
	 * @param beta	Opponent assured of
	 * @param b	Board object
	 * @return	Node value
	 */
	public static int minimax(boolean maximizingPlayer, int level, int alpha, int beta, board b) {
		
		if (System.currentTimeMillis() - startTime >= maxTime) {  // possibly downsize maxTime to compensate for return path computation time
			cutOff = true;
			return b.calculateValueDiff();
		}
		else if (level > maxLevel) {
			return b.calculateValueDiff();
		}
		
		ArrayList<board> moves = b.getValidMoves(maximizingPlayer ? 1 : 2);
		
		if (moves.size() == 0) {
			return maximizingPlayer ? 64 : -64;
		}
		
		if (maximizingPlayer) {
			
			int top = 0;
			
			for (int i = 0; i < moves.size(); i++) {
				int score = minimax(false, level + 1, alpha, beta, moves.get(i));
				
				if (score > alpha) {
					alpha = score;
					top = i;
				}
				
				if (alpha >= beta)
					break;
			}
			
			if (level == 0) { // set board corresponding to optimal value (0-63)
				best = moves.get(top);
			}
			
			return alpha;
		} else {
			for (board i : moves) {
				int score = minimax(true, level + 1, alpha, beta, i);
				
				if (score < beta)
					beta = score;
				
				if (alpha >= beta)
					break;
			}
			return beta;
		}
		
	}
	
}

class board {
	
	public int[][] gamestate; // 8 x 8; Player number or 0 for empty
	
	public int movedX, movedY; // last called coordinates to makeMove()
	
	public board(int[][] gamestate) {
		
		this.gamestate = gamestate;
		
	}
	
	/**
	 * Puts down a piece and makes a move if valid
	 * @param X	x-coordinate
	 * @param Y y-coordinate
	 * @param player Player 1 or 2
	 * @return		Returns false if move is illegal
	 */
	public boolean makeMove(int X, int Y, int player) {
		
		if (gamestate[X][Y] != 0) // spot already occupied
			return false;
		
		boolean legalAtLeastOnce = false;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0)
					continue;
				
				boolean piecesToFlip = false, passedOpponent = false;
				int k = 1;
				
				while (X + j * k >= 0 && X + j * k < 8
						&& Y + i * k >= 0 && Y + i * k < 8) { // Stay inside board
					
					if (gamestate[X + j * k][Y + i * k] == 0 || 
							(gamestate[X + j * k][Y + i * k] == player && !passedOpponent))
					{
						break;
					}
					if (gamestate[X + j * k][Y + i * k] == player && passedOpponent) {
						piecesToFlip = true;
						break;
					}
					else if (gamestate[X + j * k][Y + i * k] == player % 2 + 1) {
						passedOpponent = true;
						k++;
					}
				}
				
				if (piecesToFlip) {
					
					gamestate[X][Y] = player;
					
					for (int h = 1; h <= k; h++) {
						gamestate[X + j * h][Y + i * h] = player;
					}
					
					legalAtLeastOnce = true;
				}
			}
		}
		
		this.movedX = X;
		this.movedY = Y;
		
		return legalAtLeastOnce;
	}

	
	/**
	 * Calculates the value difference of the board (in favor of player 1)
	 * @return	Value difference
	 */
	public int calculateValueDiff() {
		return calculateValue(1) - calculateValue(2);
	}
	
	/**
	 * Calculates the value for player 1 or 2
	 * @param player	Player 1 or 2
	 * @return	Value
	 */
	public int calculateValue(int player) {
		int v = 0;
		
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				if (gamestate[j][i] == player)
					v++;
			}
		
		return v;
	}
	
	/**
	 * Tries makeMove() for 64 combinations and saves the valid ones
	 * @param player	Player 1 or 2
	 * @return List of valid board outcomes
	 */
	public ArrayList<board> getValidMoves(int player) {
		
		ArrayList<board> boardList = new ArrayList<board>();
		board b = new board(cloneGrid(gamestate));
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				//board b = new board(cloneGrid(gamestate));
				
				if (b.makeMove(j, i, player)) {
					boardList.add(b);
					b = new board(cloneGrid(gamestate)); // if it would've been false it can be reused
				}
			}
		}
		
		return boardList;
	}
	
	/**
	 * Clones 2-dimensional array
	 * @param 2-dimensional array to clone
	 * @return	Cloned array
	 */
	public static int[][] cloneGrid(int[][] gamestate) {
	    int[][] r = new int[8][];
	    for (int i = 0; i < 8; i++) {
	        r[i] = gamestate[i].clone();
	    }
	    return r;
	}
}