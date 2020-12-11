package CounterStrike;

import java.io.Serializable;

public class BoardPlayer implements Serializable {

	private static final long serialVersionUID = 1L;
	private int[][] board;
	private Player player2, player3;

	/**
	 * basically a class that contains 3 classes to write on socket instead of
	 * writing 3 diffrenet objects
	 * 
	 * @param board   a int[][] that shows the position of players and robots and
	 *                anything in game
	 * @param player2 first player object
	 * @param player3 second player object
	 */
	public BoardPlayer(int[][] board, Player player2, Player player3) {
		super();
		this.board = board;
		this.player2 = player2;
		this.player3 = player3;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = board;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Player getPlayer3() {
		return player3;
	}

	public void setPlayer3(Player player3) {
		this.player3 = player3;
	}

}
