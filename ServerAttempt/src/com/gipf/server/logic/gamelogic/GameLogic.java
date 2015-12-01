package com.gipf.server.logic.gamelogic;

import java.util.ArrayList;

import com.gipf.server.logic.Game;
import com.gipf.server.logic.LogicController;
import com.gipf.server.logic.PlayerChangeEvent;
import com.gipf.server.logic.Row;
import com.gipf.server.logic.RowRemovalRequestEvent;
import com.gipf.server.logic.board.Board;
import com.gipf.server.logic.player.Player;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class GameLogic {

	public LogicController controller;

	public Game game;
	public Player currentPlayer;
	public ArrayList<Row> removeOptions = new ArrayList<Row>();
	public RowRemovalRequestEvent rowRemovalEvent;
	
	private boolean standard;

	public GameLogic(Game game, LogicController controller, boolean standard) {
		this.controller = controller;
		this.game = game;
		this.standard = standard;
	}

	public void playerEventPerformed(PlayerEvent e) {
		if (!this.game.getBoard().isValidMove(e.getFromPoint(), e.getToPoint())) {
			this.controller.sendMoveValidity(false);
			return;
		}
		this.controller.sendMoveValidity(true);
		this.game.getBoard().place(e.getPlayer().getStoneColor(), e.getFromPoint(), e.getToPoint());
		this.getCurrentPlayer().setStoneAccount(this.getCurrentPlayer().getStoneAccount() - 1);

		if (this.handleRows()) return;

		this.moveToNextPlayer();
		if (this.checkForWin()) {
			this.controller.sendWinLoseUpdate(this.returnWinner());
		}
	}
	
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}

	public void removeRowFromPoints(Point start, Point end) {
		ArrayList<Row> rows = this.rowRemovalEvent.getRows();
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).getFromPoint().equals(start) && rows.get(i).getToPoint().equals(end)) {
				this.game.getBoard().removeRowAndExtensions(rows.get(i));
				rows.get(i).getPlayer().setStoneAccount(rows.get(i).getPlayer().getStoneAccount() + rows.get(i).getLength());
				this.handleExtensions(rows.get(i));
				break;
			}
		}
		this.rowRemovalEvent = null;
		this.controller.sendGameUpdate();
		if (!this.handleRows()) {
			moveToNextPlayer();
		}
	}

	private boolean containsGipfStone(Point start, Point end) {
		int xx = end.getX() - start.getX();
		int yy = end.getY() - start.getY();

		int dx, dy;
		if (xx == 0) dx = 0;
		else dx = 1;
		if (yy == 0) dy = 0;
		else dy = 1;

		int length;
		if (xx == 0) length = yy;
		else if (yy == 0) length = xx;
		else length = xx;
		length++;
		for (int j = 0; j < length; j++) {
			int x = start.getX() + (j * dx);
			int y = start.getY() + (j * dy);
			if (this.currentPlayer.getStoneColor() == Board.BLACK_VALUE) {
				if (this.game.getBoard().getGrid()[x][y] == Board.GIPF_BLACK_VALUE) return true;
			} else {
				if (this.game.getBoard().getGrid()[x][y] == Board.GIPF_WHITE_VALUE) return true;
			}
		}
		return false;
	}

	private boolean extCurrentPlayerContainGipf(Point[] whiteExt, Point[] blackExt) {
		this.game.getBoard().print();
		if (this.currentPlayer.getStoneColor() == Board.BLACK_VALUE) {
			for (Point p : blackExt)
				if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.GIPF_BLACK_VALUE) return true;
		} else {
			for (Point p : whiteExt)
				if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.GIPF_WHITE_VALUE) return true;
		}
		return false;
	}

	public boolean handleRows() {
		ArrayList<Row> rows = this.game.getBoard().checkForLines();
		if (rows.size() == 1 && !containsGipfStone(rows.get(0).getFromPoint(), rows.get(0).getToPoint())) {
			if (extCurrentPlayerContainGipf(rows.get(0).getWhiteExtensionStones(), rows.get(0).getBlackExtensionStones())) {
				this.controller.sendGameUpdate();
				ArrayList<Row> activeRows = rowsForPlayer(this.currentPlayer.getStoneColor(), rows);
				this.emitRowRemovalRequest(new RowRemovalRequestEvent(activeRows));
				return true;
			} else {
				Row row = rows.get(0);
				int stones = row.getLength();
				this.game.getBoard().removeRowAndExtensions(row);
				handleExtensions(row);
				row.getPlayer().setStoneAccount(row.getPlayer().getStoneAccount() + stones);
			}
		} else if (rows.size() > 0) {
			this.controller.sendGameUpdate();
			ArrayList<Row> activeRows = rowsForPlayer(this.currentPlayer.getStoneColor(), rows);
			this.emitRowRemovalRequest(new RowRemovalRequestEvent(activeRows));
			return true;
		}
		return false;
	}

	public void removePoints(Point[] points, boolean checkRows) {
		if (this.currentPlayer.getStoneColor() == Board.WHITE_VALUE) {
			for (Point p : points) {
				if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.WHITE_VALUE) {
					this.currentPlayer.setStoneAccount(this.currentPlayer.getStoneAccount() + 1);
				} else if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.GIPF_WHITE_VALUE) {
					this.currentPlayer.setStoneAccount(this.currentPlayer.getStoneAccount() + 2);
				}
				this.game.getBoard().getGrid()[p.getX()][p.getY()] = Board.EMPTY_TILE;
			}
		} else {
			for (Point p : points) {
				if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.BLACK_VALUE) {
					this.currentPlayer.setStoneAccount(this.currentPlayer.getStoneAccount() + 1);
				} else if (this.game.getBoard().getGrid()[p.getX()][p.getY()] == Board.GIPF_BLACK_VALUE) {
					this.currentPlayer.setStoneAccount(this.currentPlayer.getStoneAccount() + 2);
				}
				this.game.getBoard().getGrid()[p.getX()][p.getY()] = Board.EMPTY_TILE;
			}
		}
		if (checkRows) {
			if (!this.handleRows()) {
				moveToNextPlayer();
			}
		}
	}

	public ArrayList<Row> rowsForPlayer(int color, ArrayList<Row> possibleRows) {
		ArrayList<Row> rowsForPlayer = new ArrayList<Row>();

		for (int x = 0; x < possibleRows.size(); x++) {
			Row tmp = possibleRows.get(x);
			if (color == tmp.getPlayer().getStoneColor()) rowsForPlayer.add(new Row(tmp.getFromPoint(), tmp.getToPoint(), tmp.getPlayer(), tmp.getLength(), tmp.getWhiteExtensionStones(), tmp.getBlackExtensionStones()));
		}
		return rowsForPlayer;
	}

	public void moveToNextPlayer() {
		if (this.currentPlayer == game.getPlayerOne()) {
			this.currentPlayer = game.getPlayerTwo();
			this.controller.changeEventPerformed(new PlayerChangeEvent(game.getPlayerOne(), game.getPlayerTwo()));
		} else {
			this.currentPlayer = game.getPlayerOne();
			this.controller.changeEventPerformed(new PlayerChangeEvent(game.getPlayerTwo(), game.getPlayerOne()));
		}
	}

	public void handleExtensions(Row row) {
		if (row.getPlayer().getStoneColor() == Board.WHITE_VALUE) row.getPlayer().setStoneAccount(row.getPlayer().getStoneAccount() + row.getWhiteExtensionStones().length);
		else row.getPlayer().setStoneAccount(row.getPlayer().getStoneAccount() + row.getBlackExtensionStones().length);
	}

	public Player checkPlayer(int stoneColor) {
		if (stoneColor == Board.BLACK_VALUE) return game.getPlayerTwo();
		return game.getPlayerOne();
	}

	public boolean checkForWin() {
		if (game.getPlayerOne().getStoneAccount() == 0 || game.getPlayerTwo().getStoneAccount() == 0) return true;
		boolean[] containGipfStones = this.game.getBoard().containGipfStones();

		if (standard) {
			if (!containGipfStones[0]) return true;
			else if (!containGipfStones[1]) return true;
		}
		return false;
	}

	public Player returnWinner() {
		if (game.getPlayerOne().getStoneAccount() == 0) return game.getPlayerTwo();
		if (game.getPlayerTwo().getStoneAccount() == 0) return game.getPlayerOne();

		if (standard) {
			boolean[] containGipfStones = this.game.getBoard().containGipfStones();
			
			if (!containGipfStones[0]) return this.game.getPlayerOne();
			else if (!containGipfStones[1]) return this.game.getPlayerTwo();
		}
		return null;
	}

	public void emitRowRemovalRequest(RowRemovalRequestEvent e) {
		this.rowRemovalEvent = e;
		this.controller.rowRemoveRequestEventPerformed(e);
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Player getDisabledPlayer() {
		if (this.currentPlayer == this.game.getPlayerOne()) return this.game.getPlayerTwo();
		else return this.game.getPlayerOne();
	}

	public void setGame(Game game) {
		this.game = game;
	}
}