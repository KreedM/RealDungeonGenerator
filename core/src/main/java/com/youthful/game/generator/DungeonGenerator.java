package com.youthful.game.generator;

import java.util.ArrayList;
import com.badlogic.gdx.math.RandomXS128;

//Data type that generates a randomized dungeon using a BSP tree

public class DungeonGenerator {
		
		//Constants used in generation
	    public static final int MIN_ROOM_SIZE = 16;
	    public static final float SPLIT_PROBABILITY = 0.5f;
	    public static final float MIN_CONTAINER_HEIGHT_MULTIPLIER = 0.4f, MAX_CONTAINER_HEIGHT_MULTIPLIER = 0.6f;
	    public static final float MIN_CONTAINER_WIDTH_MULTIPLIER = 0.4f, MAX_CONTAINER_WIDTH_MULTIPLIER = 0.6f;
	    public static final float MIN_ROOM_HEIGHT_MULTIPLIER = 0.6f, MAX_ROOM_HEIGHT_MULTIPLIER = 0.8f;
	    public static final float MIN_ROOM_WIDTH_MULTIPLIER = 0.6f, MAX_ROOM_WIDTH_MULTIPLIER = 0.8f;
	    
	    public Leaf tree; //Starting node
	    public ArrayList<Leaf> leaves; //Arraylist of the lowest leaves
	    public boolean[][] corridors; //2D boolean array marking the places where corridors exist
	    public int size; //The square size of the entire dungeon
	    public int iterations; //# of times the BSP split will be iterated
	    public RandomXS128 randomGenerator;
	    
	    public DungeonGenerator(int size, int iterations, boolean startGeneration, long seed) {
	    	this.size = size;
	    	this.iterations = iterations;
	    	corridors = new boolean[size][size];
	    	
	    	randomGenerator = new RandomXS128(seed);
	    	
	    	if (startGeneration)
				generateDungeon();
	    }
	    
	    public DungeonGenerator(int size, int iterations, boolean startGeneration) {
	    	this.size = size;
	    	this.iterations = iterations;
	    	corridors = new boolean[size][size];
	    	
	    	randomGenerator = new RandomXS128();
	    	randomGenerator.setSeed((long) (Math.random() * randomGenerator.nextInt()));
	    	
	    	if (startGeneration)
				generateDungeon();
	    }

	    public void generateDungeon () { //Main method to generate the dungeon
	    	leaves = new ArrayList<Leaf>();
	    	
	    	generateContainers();
	    	
	    	generateCorridors(tree);
	    	
	    	generateRoom(tree);
	    	
	    	tree.addLeaf(leaves);
	    }
	    
	    private void generateContainers() { //Initializes the tree
	        tree = Leaf.splitLeaves(iterations, new Rectangle(0, 0, size, size), this);
	    }
	    
	    public Rectangle[] splitRectangles(Rectangle rect) { //Splits the container into 2 parts
			 Rectangle r1, r2; //1st part, 2nd part respectively
			 
			 if (randomGenerator.nextFloat() > SPLIT_PROBABILITY) {
				 //Horizontal cut
				 int randHeight = (int) randomRange(rect.height * MIN_CONTAINER_HEIGHT_MULTIPLIER, rect.height * MAX_CONTAINER_HEIGHT_MULTIPLIER);
				
				 if (randHeight % 2 == 1)
					 randHeight--;
				
				 r1 = new Rectangle(rect.x, rect.y, rect.width, randHeight);
				 r2 = new Rectangle(rect.x, rect.y + r1.height, rect.width, rect.height - r1.height);
			 } 
			 else {
				 //Vertical cut
				 int randWidth = (int) randomRange(rect.width * MIN_CONTAINER_WIDTH_MULTIPLIER, rect.width * MAX_CONTAINER_WIDTH_MULTIPLIER);
				 
				 if (randWidth % 2 == 1)
					 randWidth--;
				 
				 r1 = new Rectangle(rect.x, rect.y, randWidth, rect.height);
				 r2 = new Rectangle(rect.x + r1.width, rect.y, rect.width - r1.width, rect.height);
			 }
			 
			 return new Rectangle[] { r1, r2 };
		}
	    
	    private void generateCorridors(Leaf leaf) { //Method connects centers of each container, storing the result into the 2D boolean array
	    	if (leaf.left == null && leaf.right == null)
				return;
	    	
	    	Point leftCenter = leaf.left.container.center;
	    	Point rightCenter = leaf.right.container.center;

	    	if (rightCenter.x - leftCenter.x != 0) {
	    		int x = leftCenter.x;
	    		
	    		while (rightCenter.x - x != 0) {
		    		corridors[x][leftCenter.y] = true;	 
	    			x++;
	    		}
	    	}
	    	else {
	    		int y = leftCenter.y;
	    		
	    		while (rightCenter.y - y != 0) {
		    		corridors[leftCenter.x][y] = true;	
	    			y++;
	    		}
			}
	    	
	    	generateCorridors(leaf.left); //Part that recursively calls for corridor generation on all levels of the tree
	    	generateCorridors(leaf.right);
	    }
	    
	    public void generateRoom(Leaf leaf) { //Generates a randomly sized room within the constraints of its container
		    if (leaf.left == null && leaf.right == null) {
		    	Rectangle container = leaf.container;
		    	
		        int roomW = (int) (container.width * randomRange(MIN_ROOM_WIDTH_MULTIPLIER, MAX_ROOM_WIDTH_MULTIPLIER));
		       
		        if (roomW % 2 == 1)
					roomW++;
		        
		        int roomH = (int) (container.height * randomRange(MIN_ROOM_HEIGHT_MULTIPLIER, MAX_ROOM_HEIGHT_MULTIPLIER));
		        
		        if (roomH % 2 == 1)
					roomH++;
		        
		        Point center = container.center;
		        
		        int x = randomRange(Math.max(container.x, center.x - roomW),
		        Math.min(container.x + container.width - roomW, center.x));
		        
		        int y = randomRange(Math.max(container.y, center.y - roomH),
		        Math.min(container.y + container.height - roomH, center.y));
		        
		        leaf.room = new Rectangle(x, y, roomW, roomH);
		    } 
		    else {
		        generateRoom(leaf.left);
		        generateRoom(leaf.right);
		    }
		}
		
	    //Helper methods
	    
	    public float randomRange(float a, float b) { //Helper method that returns a random float between a and b, not including b
			if (a > b)
				return a + randomGenerator.nextFloat() * (a - b);
			return a + randomGenerator.nextFloat() * (b - a);
		}
		
		public int randomRange(int a, int b) { //Helper method that returns a random integer between a and up to and including b
			if (a > b)
				return (int) (b + (randomGenerator.nextInt() % (a - b + 1)));
			return (int) (a + (Math.abs(randomGenerator.nextInt()) % (b - a + 1)));
		}
}
