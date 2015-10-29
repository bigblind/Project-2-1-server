package com.gipf.server.logic;

import java.util.ArrayList;

public class RowRemovalRequestEvent {

	private ArrayList<Row> rows;
	
	public RowRemovalRequestEvent(ArrayList<Row> rows) {
		this.rows = rows;
	}
	
	public ArrayList<Row> getRows() {
		return this.rows;
	}
}
