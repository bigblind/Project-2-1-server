package com.gipf.server.logic.player;

import java.awt.Point;
import java.util.ArrayList;

import com.gipf.server.logic.Game;
import com.gipf.server.logic.Row;

public class Player {

	private String name;
	private int stoneAccount;
	private int stoneColor;

	public Player(int stoneColor) {
		this.name = "Player " + String.valueOf(stoneColor);
		this.stoneAccount = 15;
		this.stoneColor = stoneColor;
	}

	public Player(String name, int stones, int stoneColor) {
		this.name = name;
		this.stoneAccount = stones;
		this.stoneColor = stoneColor;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStoneAccount() {
		if (name == null)
			this.name = "Player " + String.valueOf(stoneColor);
		return this.stoneAccount;
	}

	public void setStoneAccount(int stones) {
		this.stoneAccount = stones;
	}

	public void setStoneColor(int color) {
		this.stoneColor = color;
	}

	public int getStoneColor() {
		return this.stoneColor;
	}

	public String toString() {
		return this.name;
	}
}
