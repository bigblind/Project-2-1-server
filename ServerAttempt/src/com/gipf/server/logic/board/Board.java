package com.gipf.server.logic.board;

import java.util.ArrayList;

import com.gipf.server.logic.Row;
import com.gipf.server.logic.gamelogic.GameLogic;
import com.gipf.server.logic.utils.Point;

public class Board {

	public static int GIPF_WHITE_VALUE = 11;
	public static int GIPF_BLACK_VALUE = 22;
	public static int WHITE_VALUE = 1;
	public static int BLACK_VALUE = 2;
	public static int BOARD_EDGE = -1;
	public static int EMPTY_TILE = 0;
	public static int VOID_TILE = -2;

	private int[][] grid;
	private GameLogic logic;

	public Board() {

	}

	public void setLogic(GameLogic l) {
		logic = l;
	}

	public Board(int[][] grid) {
		this.grid = grid;
	}

	public void basicInit() {
		this.grid = new int[9][9];

		for (int j = 8; j > 4; j--)
			for (int i = 0; i < 4 - (8 - j); i++)
				this.grid[i][j] = VOID_TILE;

		for (int j = 0; j < 4; j++)
			for (int i = 8; i > 4 + j; i--)
				this.grid[i][j] = VOID_TILE;

		for (int i = 4; i < 9; i++)
			this.grid[i][8] = BOARD_EDGE;

		for (int j = 8; j >= 4; j--)
			this.grid[8][j] = BOARD_EDGE;

		for (int i = 0; i < 5; i++)
			this.grid[i][0] = BOARD_EDGE;

		for (int j = 0; j < 5; j++)
			this.grid[0][j] = BOARD_EDGE;

		this.grid[5][1] = BOARD_EDGE;
		this.grid[6][2] = BOARD_EDGE;
		this.grid[7][3] = BOARD_EDGE;

		this.grid[1][5] = BOARD_EDGE;
		this.grid[2][6] = BOARD_EDGE;
		this.grid[3][7] = BOARD_EDGE;

		// placing the stones
		this.grid[1][1] = WHITE_VALUE;
		this.grid[4][1] = BLACK_VALUE;
		this.grid[7][4] = WHITE_VALUE;
		this.grid[7][7] = BLACK_VALUE;
		this.grid[4][7] = WHITE_VALUE;
		this.grid[1][4] = BLACK_VALUE;
	}

	public void standardInit() {
		this.grid = new int[9][9];

		for (int j = 8; j > 4; j--)
			for (int i = 0; i < 4 - (8 - j); i++)
				this.grid[i][j] = VOID_TILE;

		for (int j = 0; j < 4; j++)
			for (int i = 8; i > 4 + j; i--)
				this.grid[i][j] = VOID_TILE;

		for (int i = 4; i < 9; i++)
			this.grid[i][8] = BOARD_EDGE;

		for (int j = 8; j >= 4; j--)
			this.grid[8][j] = BOARD_EDGE;

		for (int i = 0; i < 5; i++)
			this.grid[i][0] = BOARD_EDGE;

		for (int j = 0; j < 5; j++)
			this.grid[0][j] = BOARD_EDGE;

		this.grid[5][1] = BOARD_EDGE;
		this.grid[6][2] = BOARD_EDGE;
		this.grid[7][3] = BOARD_EDGE;

		this.grid[1][5] = BOARD_EDGE;
		this.grid[2][6] = BOARD_EDGE;
		this.grid[3][7] = BOARD_EDGE;

		// placing the stones
		this.grid[1][1] = GIPF_WHITE_VALUE;
		this.grid[4][1] = GIPF_BLACK_VALUE;
		this.grid[7][4] = GIPF_WHITE_VALUE;
		this.grid[7][7] = GIPF_BLACK_VALUE;
		this.grid[4][7] = GIPF_WHITE_VALUE;
		this.grid[1][4] = GIPF_BLACK_VALUE;
	}

	public Board copy() {
		int[][] gridCopy = new int[9][9];

		for (int i = 0; i < gridCopy.length; i++)
			for (int j = 0; j < gridCopy[0].length; j++)
				gridCopy[i][j] = this.grid[i][j];

		return new Board(gridCopy);
	}

	public void print() {
		for (int j = 0; j < this.grid[0].length; j++) {
			for (int i = 0; i < this.grid.length; i++) {
				if (this.grid[i][j] > -1) System.out.print(" ");
				System.out.print(this.grid[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public boolean isValidMove(Point from, Point to) {
		if (grid[to.getX()][to.getY()] == BOARD_EDGE) return false;

		int x1 = from.getX();
		int x2 = to.getX();
		int y1 = from.getY();
		int y2 = to.getY();
		int xx = (int) (x2 - x1);
		int yy = (int) (y2 - y1);

		if (!isEmpty(to)) return isValidMove(to, new Point(x2 + xx, y2 + yy));
		return true;
	}

	public void place(int stone, Point from, Point to) {
		this.push(from, to);
		this.grid[to.getX()][to.getY()] = stone;
	}

	public void push(Point from, Point to) {
		int x1 = from.getX();
		int x2 = to.getX();
		int y1 = from.getY();
		int y2 = to.getY();
		int xx = (int) (x2 - x1);
		int yy = (int) (y2 - y1);

		if (!isEmpty(new Point(x2, y2))) push(to, new Point(x2 + xx, y2 + yy));
		if (grid[x1][y1] > 0) move(from, to);
	}

	public boolean[] containGipfStones() {
		boolean[] result = new boolean[2];

		for (int i = 1; i < this.grid.length - 1; i++) {
			for (int j = 1; j < this.grid[i].length - 1; j++) {
				if (this.grid[i][j] == GIPF_WHITE_VALUE) result[0] = true;
				else if (this.grid[i][j] == GIPF_BLACK_VALUE) result[1] = true;
			}
		}
		return result;
	}

	public void move(Point from, Point to) {
		int x1 = from.getX();
		int x2 = to.getX();
		int y1 = from.getY();
		int y2 = to.getY();
		this.grid[x2][y2] = this.grid[x1][y1];
		this.grid[x1][y1] = 0;
	}

	public boolean isEmpty(Point point) {
		return this.grid[(int) point.getX()][(int) point.getY()] <= 0;
	}

	public int[][] getGrid() {
		return this.grid;
	}

	public void setGrid(int[][] grid) {
		this.grid = grid;
	}

	public ArrayList<Row> checkForLines() {
		ArrayList<Row> lines = new ArrayList<Row>();
		int lineStartX = -1, lineStartY = -1, lineEndX = -1, lineEndY = -1;

		int counter = 0;
		int prevValue = -1;

		for (int i = 1; i < this.grid.length - 1; i++) {
			for (int j = 1; j < this.grid[i].length; j++) {
				if (((prevValue == Board.WHITE_VALUE) || (prevValue == Board.GIPF_WHITE_VALUE)) && ((this.grid[i][j] == Board.WHITE_VALUE) || (this.grid[i][j] == Board.GIPF_WHITE_VALUE))) {
					counter++;
				} else if (((prevValue == Board.BLACK_VALUE) || (prevValue == Board.GIPF_BLACK_VALUE)) && ((this.grid[i][j] == Board.BLACK_VALUE) || (this.grid[i][j] == Board.GIPF_BLACK_VALUE))) {
					counter++;
				} else {
					if (counter >= 4) {
						lineStartX = i;
						lineEndX = i;

						lineStartY = j - counter;
						lineEndY = j - 1;

						Point[][] extensionStones = getExtensionStones(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY));
						lines.add(new Row(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY), logic.checkPlayer(prevValue), counter, extensionStones[0], extensionStones[1]));
					}

					prevValue = this.grid[i][j];
					if (prevValue > 0) counter = 1;
					else counter = 0;
				}
			}
			counter = 0;
			prevValue = -1;
		}

		for (int j = 1; j < this.grid[0].length - 1; j++) {
			for (int i = 1; i < this.grid.length; i++) {
				if (((prevValue == Board.WHITE_VALUE) || (prevValue == Board.GIPF_WHITE_VALUE)) && ((this.grid[i][j] == Board.WHITE_VALUE) || (this.grid[i][j] == Board.GIPF_WHITE_VALUE))) {
					counter++;
				} else if (((prevValue == Board.BLACK_VALUE) || (prevValue == Board.GIPF_BLACK_VALUE)) && ((this.grid[i][j] == Board.BLACK_VALUE) || (this.grid[i][j] == Board.GIPF_BLACK_VALUE))) {
					counter++;
				} else {
					if (counter >= 4) {
						lineStartX = i - counter;
						lineEndX = i - 1;

						lineStartY = j;
						lineEndY = j;

						Point[][] extensionStones = getExtensionStones(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY));
						lines.add(new Row(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY), logic.checkPlayer(prevValue), counter, extensionStones[0], extensionStones[1]));
					}

					prevValue = this.grid[i][j];
					if (prevValue > 0) counter = 1;
					else counter = 0;
				}
			}
			counter = 0;
			prevValue = -1;
		}

		for (int k = 0; k < 4; k++) {
			for (int l = 0; l < 5 + k; l++) {
				if (((prevValue == Board.WHITE_VALUE) || (prevValue == Board.GIPF_WHITE_VALUE)) && ((this.grid[l + 1][4 - k + l] == Board.WHITE_VALUE) || (this.grid[l + 1][4 - k + l] == Board.GIPF_WHITE_VALUE))) {
					counter++;
				} else if (((prevValue == Board.BLACK_VALUE) || (prevValue == Board.GIPF_BLACK_VALUE)) && ((this.grid[l + 1][4 - k + l] == Board.BLACK_VALUE) || (this.grid[l + 1][4 - k + l] == Board.GIPF_BLACK_VALUE))) {
					counter++;
				} else {
					if (counter >= 4) {
						lineStartX = 1 + l - counter;
						lineEndX = l;

						lineStartY = 4 - k + l - counter;
						lineEndY = 4 - k + l - 1;

						Point[][] extensionStones = getExtensionStones(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY));
						lines.add(new Row(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY), logic.checkPlayer(prevValue), counter, extensionStones[0], extensionStones[1]));
					}

					prevValue = this.grid[l + 1][4 - k + l];
					if (prevValue > 0) counter = 1;
					else counter = 0;
				}
			}
			counter = 0;
			prevValue = -1;
		}

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 7 - k; l++) {
				if (((prevValue == Board.WHITE_VALUE) || (prevValue == Board.GIPF_WHITE_VALUE)) && ((this.grid[2 + k + l][1 + l] == Board.WHITE_VALUE) || (this.grid[2 + k + l][1 + l] == Board.GIPF_WHITE_VALUE))) {
					counter++;
				} else if (((prevValue == Board.BLACK_VALUE) || (prevValue == Board.GIPF_BLACK_VALUE)) && ((this.grid[2 + k + l][1 + l] == Board.BLACK_VALUE) || (this.grid[2 + k + l][1 + l] == Board.GIPF_BLACK_VALUE))) {
					counter++;
				} else {
					if (counter >= 4) {
						lineStartX = 2 + k + l - counter;
						lineEndX = 2 + k + l - 1;

						lineStartY = 1 + l - counter;
						lineEndY = l;

						Point[][] extensionStones = getExtensionStones(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY));
						lines.add(new Row(new Point(lineStartX, lineStartY), new Point(lineEndX, lineEndY), logic.checkPlayer(prevValue), counter, extensionStones[0], extensionStones[1]));
					}

					prevValue = this.grid[2 + k + l][1 + l];
					if (prevValue > 0) counter = 1;
					else counter = 0;
				}
			}
			counter = 0;
			prevValue = -1;
		}
		return lines;
	}

	private Point[][] getExtensionStones(Point start, Point end) {
		int deltaX = end.getX() - start.getX();
		int deltaY = end.getY() - start.getY();
		// normalise these deltas to 1 if it goes up, -1 if it goes down, 0 if
		// it remains the same.
		if (deltaX > 0) deltaX = 1;
		else if (deltaX < 0) deltaX = -1;
		else deltaX = 0;
		if (deltaY > 0) deltaY = 1;
		else if (deltaY < 0) deltaY = -1;
		else deltaY = 0;

		Point connectedStart = findConnectionEnd(start, -deltaX, -deltaY);
		Point connectedEnd = findConnectionEnd(end, deltaX, deltaY);

		int xx = start.getX() - connectedStart.getX();
		int yy = start.getY() - connectedStart.getY();

		ArrayList<Point> blackExtensions = new ArrayList<Point>();
		ArrayList<Point> whiteExtensions = new ArrayList<Point>();

		if (xx > 0) {
			if (yy > 0) {
				for (int i = connectedStart.getX(); i < start.getX(); i++) {
					for (int j = connectedStart.getY(); j < start.getY(); j++) {
						if (this.grid[i][j] == Board.WHITE_VALUE || this.grid[i][j] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(i, j));
						else blackExtensions.add(new Point(i, j));
					}
				}
			} else {
				for (int i = connectedStart.getX(); i < start.getX(); i++) {
					if (this.grid[i][start.getY()] == Board.WHITE_VALUE || this.grid[i][start.getY()] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(i, start.getY()));
					else blackExtensions.add(new Point(i, start.getY()));
				}
			}
		} else {
			for (int j = connectedStart.getY(); j < start.getY(); j++) {
				if (this.grid[start.getX()][j] == Board.WHITE_VALUE || this.grid[start.getX()][j] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(start.getX(), j));
				else blackExtensions.add(new Point(start.getX(), j));
			}
		}

		if (xx > 0) {
			if (yy > 0) {
				for (int i = end.getX(); i <= connectedEnd.getX(); i++) {
					for (int j = end.getY(); j < connectedEnd.getY(); j++) {
						if (this.grid[i][j] == Board.WHITE_VALUE || this.grid[i][j] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(i, j));
						else blackExtensions.add(new Point(i, j));
					}
				}
			} else {
				for (int i = end.getX(); i < connectedEnd.getX(); i++) {
					if (this.grid[i][start.getY()] == Board.WHITE_VALUE || this.grid[i][start.getY()] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(i, start.getY()));
					else blackExtensions.add(new Point(i, start.getY()));
				}
			}
		} else {
			for (int j = end.getY(); j < connectedEnd.getY(); j++) {
				if (this.grid[start.getX()][j] == Board.WHITE_VALUE || this.grid[start.getX()][j] == Board.GIPF_WHITE_VALUE) whiteExtensions.add(new Point(start.getX(), j));
				else blackExtensions.add(new Point(start.getX(), j));
			}
		}
		Point[] whiteExt = new Point[whiteExtensions.size()];
		Point[] blackExt = new Point[blackExtensions.size()];
		for (int i = 0; i < whiteExtensions.size(); i++) {
			whiteExt[i] = whiteExtensions.get(i);
		}
		for (int i = 0; i < blackExtensions.size(); i++) {
			blackExt[i] = blackExtensions.get(i);
		}
		return new Point[][] {whiteExt, blackExt};
	}

	public void removeRowAndExtensions(Row row) {
		Point start = row.getFromPoint();
		Point end = row.getToPoint();
		int deltaX = end.getX() - start.getX();
		int deltaY = end.getY() - start.getY();
		// normalize these deltas to 1 if it goes up, -1 if it goes down, 0 if
		// it remains the same.
		if (deltaX > 0) deltaX = 1;
		else if (deltaX < 0) deltaX = -1;
		else deltaX = 0;
		if (deltaY > 0) deltaY = 1;
		else if (deltaY < 0) deltaY = -1;
		else deltaY = 0;

		Point connectedStart = findConnectionEnd(start, -deltaX, -deltaY);
		Point connectedEnd = findConnectionEnd(end, deltaX, deltaY);
		removeLine(connectedStart, connectedEnd);
	}

	private Point findConnectionEnd(Point from, int deltaX, int deltaY) {
		Point p = from;
		while (!isEmpty(p)) {
			int x = p.getX() + deltaX;
			int y = p.getY() + deltaY;
			p = new Point(x, y);
		}
		return new Point(p.getX() - deltaX, p.getY() - deltaY);
	}

	private void removeLine(Point start, Point end) {
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

		for (int i = 0; i <= length; i++)
			this.grid[start.getX() + (i * dx)][start.getY() + (i * dy)] = Board.EMPTY_TILE;
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				result += this.grid[i][j] + " ";
			}
		}
		return result;
	}
}