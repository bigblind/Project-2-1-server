package com.gipf.server.logic;

import com.gipf.server.logic.board.Board;
import com.gipf.server.logic.gamelogic.GameLogic;
import com.gipf.server.logic.player.Player;

public class Game {

	private GameLogic logic;
	private Player playerOne, playerTwo;
	private Board board;

	public Game() {
		this.board = new Board();
		this.board.basicInit();
	}
	
	public Game(Board board, Player playerOne, Player playerTwo) {
		this.board = board;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}
	
	public void setPlayerOne(Player player) {
		this.playerOne = player;
	}
	
	public void setPlayerTwo(Player player) {
		this.playerTwo = player;
	}

	public void setGameLogic(GameLogic logic) {
		this.logic = logic;
		this.board.setLogic(logic);
	}

	public GameLogic getGameLogic() {
		return this.logic;
	}

	public Player getPlayerOne() {
		return this.playerOne;
	}

	public Player getPlayerTwo() {
		return this.playerTwo;
	}

	public Board getBoard() {
		return this.board;
	}
	
	public Game copy() {
		Game game = new Game(this.board.copy(), this.playerOne.copy(), this.playerTwo.copy());
		game.setGameLogic(this.logic);
		return game;
	}
}