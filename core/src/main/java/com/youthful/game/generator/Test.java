package com.youthful.game.generator;

import javax.swing.JFrame;

public class Test {
	public static void main(String[] args) {
		DungeonGenerator dungeon = new DungeonGenerator(86, 7, true);
		
		JFrame frame = new JFrame();
		frame.setTitle("Dungeon Drawer");
		frame.setContentPane(new DungeonDrawer(dungeon.size, dungeon.leaves, dungeon.corridors));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}