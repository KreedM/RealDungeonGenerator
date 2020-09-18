package com.youthful.game.generator;

import java.util.ArrayList;

//Data type representing a leaf in a BSP tree

public class Leaf {
	public Leaf left, right;
	public Rectangle container, room;
	
	public Leaf(Rectangle container) {
		this.container = container;
	}
	
	public static Leaf splitLeaves(int iterations, Rectangle container, DungeonGenerator generator) { //Static method used to populate the tree
		Leaf leaf = new Leaf(container);

		//Prevents splitting if size is already too small
		if (iterations == 0 || container.width <= DungeonGenerator.MIN_ROOM_SIZE || container.height <= DungeonGenerator.MIN_ROOM_SIZE) 
			return leaf;
		
		Rectangle[] splitContainers = generator.splitRectangles(container);
		
		leaf.left = splitLeaves(iterations - 1, splitContainers[0], generator);
		leaf.right = splitLeaves(iterations - 1, splitContainers[1], generator);
		
		return leaf;
	}
	
	public void addLeaf(ArrayList<Leaf> leaves) { //Adds itself to a list if it is the lowest in the tree, meaning it doesn't have a right or left
		if (left == null && right == null)
			leaves.add(this);
		else {
			left.addLeaf(leaves);
			right.addLeaf(leaves);
		}	
	}
}
