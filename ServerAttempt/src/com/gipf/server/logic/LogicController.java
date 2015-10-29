package com.gipf.server.logic;

import com.gipf.server.Server;
import com.gipf.server.logic.board.Board;
import com.gipf.server.logic.gamelogic.BasicGameLogic;
import com.gipf.server.logic.gamelogic.GameLogic;
import com.gipf.server.logic.player.Player;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class LogicController {

	private Server server;
	
	private GameLogic logic;
	private Game game;

	public LogicController(Server server) {
		this.server = server;
		this.game = new Game();
		this.game.setPlayerOne(new Player(Board.WHITE_VALUE));
		this.game.setPlayerTwo(new Player(Board.BLACK_VALUE));
		this.game.getBoard().basicInit();
		this.logic = new BasicGameLogic(this.game, this);
		this.logic.setCurrentPlayer(this.game.getPlayerOne());
	}
	
	public void clientInput(String text, int id) {
		if (text.contains("PlayerEvent")) {
			int x1 = Integer.parseInt(""+text.split("x = ")[1].charAt(0));
			int y1 = Integer.parseInt(""+text.split("y = ")[1].charAt(0));
			int x2 = Integer.parseInt(""+text.split("x = ")[2].charAt(0));
			int y2 = Integer.parseInt(""+text.split("y = ")[2].charAt(0));
			
			if (id == 1) {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerOne());
				this.logic.playerEventPerformed(pe);
			} else {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerTwo());
				this.logic.playerEventPerformed(pe);
			}
		}
	}
	
	public void logicInput(String text) {
		
	}

	public void sendClientInit() {
		String send = "/i " + Board.WHITE_VALUE + " 15 15 " + this.game.getBoard().toString();
		this.server.sendToClient(send, 0);
		send = "/i " + Board.BLACK_VALUE + " 15 15 " + this.game.getBoard().toString();
		this.server.sendToClient(send, 1);
	}

	public void sendGameUpdate() {
		String send;
		send = "/u " + this.game.getPlayerOne().getStoneAccount() + " " + this.game.getPlayerTwo().getStoneAccount() + " " + this.game.getBoard().toString();
		this.server.sendToClient(send, 0);
		send = "/u " + this.game.getPlayerTwo().getStoneAccount() + " " + this.game.getPlayerOne().getStoneAccount() + " " + this.game.getBoard().toString();
		this.server.sendToClient(send, 1);
	}
	
	public void rowRemoveRequestEventPerformed(RowRemovalRequestEvent e) {
		String send = "/s remove";
		for (int i = 0; i < e.getRows().size(); i++)
			send += " {" + e.getRows().get(i).getFromPoint() + " , " + e.getRows().get(i).getToPoint() + "}";

		if (e.getRows().get(0).getPlayer().getStoneColor() == Board.WHITE_VALUE) {
			this.server.sendToClient(send, 0);
			this.server.sendToClient("/s wait", 1);
		} else {
			this.server.sendToClient(send, 1);
			this.server.sendToClient("/s wait", 0);
		}
	}
	
	public void changeEventPerformed(PlayerChangeEvent e) {
		this.sendGameUpdate();
		if (e.getFromPlayer() == game.getPlayerOne()) {
			this.server.sendToClient("/s wait", 0);
			this.server.sendToClient("/s move", 1);
		} else {
			this.server.sendToClient("/s wait", 1);
			this.server.sendToClient("/s move", 0);
		}
	}
	
	public void sendMoveValidity(boolean valid) {
		if (valid) {
			if (this.game.getGameLogic().getCurrentPlayer().getStoneColor() == Board.WHITE_VALUE) this.server.sendToClient("/m valid", 0);
			else this.server.sendToClient("/m valid", 1);
		} else {
			if (this.game.getGameLogic().getCurrentPlayer().getStoneColor() == Board.WHITE_VALUE) this.server.sendToClient("/m invalid", 0);
			else this.server.sendToClient("/m invalid", 1);

		}
	}
	
	public void sendWinLoseUpdate(Player player) {
		if (player.getStoneColor() == Board.WHITE_VALUE) {
			this.server.sendToClient("/g win", 0);
			this.server.sendToClient("/g lose", 1);
		} else {
			this.server.sendToClient("/g win", 1);
			this.server.sendToClient("/g lose", 0);
		}
	}
}
