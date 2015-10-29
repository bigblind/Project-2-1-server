package com.gipf.server.logic.utils;

public class Point {

	private int x, y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			if (p.getX() == this.getX() && p.getY() == this.getY()) return true;
			else return false;
 		} else {
 			return false;
 		}
	}
	
	public String toString() {
		return "[Point: x = " + x + " | y = " + y + "]";
	}
}
