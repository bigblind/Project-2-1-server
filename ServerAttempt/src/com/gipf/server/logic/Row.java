package com.gipf.server.logic;

import com.gipf.server.logic.player.Player;
import com.gipf.server.logic.player.PlayerEvent;
import com.gipf.server.logic.utils.Point;

public class Row extends PlayerEvent {
	
	private Point[] whiteExtensionStones;
	private Point[] blackExtensionStones;
	private int length;

	public Row(Point from, Point to, Player player, int length, Point[] whiteExtensionStones, Point[] blackExtensionStones) {
		super(from, to, player);
		this.whiteExtensionStones = whiteExtensionStones;
		this.blackExtensionStones = blackExtensionStones;
		this.length = length;
	}

	public int getLength() {
		return this.length;
	}

	public Point[] getWhiteExtensionStones() {
		return this.whiteExtensionStones;
	}

	public Point[] getBlackExtensionStones() {
		return this.blackExtensionStones;
	}

	public String toString() {
		String result = "[Row from: " + this.from + " to: " + this.to + " player: " + this.player + " length: " + this.length + " whiteext{";
		for (int i = 0; i < this.whiteExtensionStones.length; i++) {
			result += " " + this.whiteExtensionStones[i].toString();
		}
		result += "} blackext{";
		for (int i = 0; i < this.blackExtensionStones.length; i++) {
			result += " " + this.blackExtensionStones[i].toString();
		}
		result += "}]";
		return result;
	}
}
