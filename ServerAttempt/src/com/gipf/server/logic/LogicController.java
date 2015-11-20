package com.gipf.server.logic;

import com.gipf.server.Server;
import com.gipf.server.logic.board.Board;
import com.gipf.server.logic.gamelogic.BasicGameLogic;
import com.gipf.server.logic.gamelogic.GameLogic;
import com.gipf.server.logic.gamelogic.StandardGameLogic;
import com.gipf.server.logic.player.Player;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class LogicController {

	private Game prevState;

	private Server server;

	private GameLogic logic;
	private Game game;

	public LogicController(Server server, String logic) {
		this.server = server;
		this.game = new Game();
		this.game.setPlayerOne(new Player(Board.WHITE_VALUE));
		this.game.setPlayerTwo(new Player(Board.BLACK_VALUE));
		if (logic.equals("basic")) {
			this.logic = new BasicGameLogic(this.game, this);
			this.game.getBoard().basicInit();
		} else {
			this.logic = new StandardGameLogic(this.game, this);
			this.game.getBoard().standardInit();
		}
		this.logic.setCurrentPlayer(this.game.getPlayerOne());
		this.game.setGameLogic(this.logic);
		this.prevState = this.game;
	}

	public void clientInput(String received, int id) {
		this.prevState = this.game.copy();
		if (received.contains("PlayerEvent")) {
			int x1 = Integer.parseInt("" + received.split("x = ")[1].charAt(0));
			int y1 = Integer.parseInt("" + received.split("y = ")[1].charAt(0));
			int x2 = Integer.parseInt("" + received.split("x = ")[2].charAt(0));
			int y2 = Integer.parseInt("" + received.split("y = ")[2].charAt(0));

			if (id == 1) {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerOne());
				this.logic.playerEventPerformed(pe);
			} else {
				PlayerEvent pe = new PlayerEvent(new Point(x1, y1), new Point(x2, y2), game.getPlayerTwo());
				this.logic.playerEventPerformed(pe);
			}
		} else if (received.startsWith("/removerow")) {
			String[] subPartsX = received.split("Point: x = ");
			String[] subPartsY = received.split("y = ");

			int x1 = Integer.parseInt(subPartsX[1].substring(0, 1));
			int y1 = Integer.parseInt(subPartsY[1].substring(0, 1));

			int x2 = Integer.parseInt(subPartsX[2].substring(0, 1));
			int y2 = Integer.parseInt(subPartsY[2].substring(0, 1));

			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);
			this.logic.removeRowFromPoints(start, end);
		} else if (received.startsWith("/removepoints")) {
			String[] subPartsX = received.split("Point: x = ");
			String[] subPartsY = received.split("y = ");

			Point[] points = new Point[subPartsX.length - 1];

			for (int i = 0; i < subPartsX.length - 1; i++) {
				int x = Integer.parseInt(subPartsX[1 + i].substring(0, 1));
				int y = Integer.parseInt(subPartsY[1 + i].substring(0, 1));

				points[i] = new Point(x, y);
			}

			if (received.endsWith("checkrows")) this.logic.removePoints(points, true);
			else this.logic.removePoints(points, false);
			this.sendGameUpdate();
		}
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
		for (int i = 0; i < e.getRows().size(); i++) {
			//			send += " {" + e.getRows().get(i).getFromPoint() + " , " + e.getRows().get(i).getToPoint() + "}";
			send += " " + e.getRows().get(i).toString() + "endRow ";
		}
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

	public void undo() {
		this.game = this.prevState.copy();
		this.logic.setGame(this.game);
		this.changeEventPerformed(new PlayerChangeEvent(this.logic.getCurrentPlayer(), this.logic.getDisabledPlayer()));
		this.logic.setCurrentPlayer(this.logic.getDisabledPlayer());
	}
}
