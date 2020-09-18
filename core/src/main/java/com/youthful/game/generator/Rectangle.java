package com.youthful.game.generator;

//Simple data type representing the characteristics of simple rectangles

public class Rectangle {
	public int x, y, width, height;
	public Point center;
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		center = new Point(x + width / 2, y + height / 2); //Type contains static coordinates
	}
}
