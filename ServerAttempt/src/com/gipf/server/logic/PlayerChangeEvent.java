package com.gipf.server.logic;

import com.gipf.server.logic.player.Player;

public class PlayerChangeEvent {

	private Player fromPlayer, toPlayer;
	
	public PlayerChangeEvent(Player fromPlayer, Player toPlayer) {
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
	}
	
	public Player getFromPlayer() {
		return this.fromPlayer;
	}
	
	public Player getToPlayer() {
		return this.toPlayer;
	}
	
	public String toString() {
		return "[ChangePlayerEvent: " + this.fromPlayer + " to " + this.toPlayer + "]";
	}
}
