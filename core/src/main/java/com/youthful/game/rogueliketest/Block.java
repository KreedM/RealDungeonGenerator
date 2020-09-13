package com.youthful.game.rogueliketest;

import com.dongbat.jbump.Collision;

public class Block extends Entity implements Slidable{	
	
	public void processCollision(Collision collision) {
		/*
		if (collision.item.userData instanceof Player) {
			Player player = (Player) collision.item.userData;
			
			float x = player.getX() + 8, y = player.getY() + 6;
			if (collision.normal.x == -1 && collision.touch.x < x) {
				x = collision.touch.x;
			}

			else if (collision.normal.x == 1 && collision.touch.x > x) {
				x = collision.touch.x;
			}

			if (collision.normal.y == -1 && collision.touch.y < y) {
				y = collision.touch.y;
			}

			else if (collision.normal.y == 1 && collision.touch.y > y) {
				y = collision.touch.y;
			}
				
			player.setX(x - 8);
			player.setY(y - 6);
			
		}
		*/
	}
}
