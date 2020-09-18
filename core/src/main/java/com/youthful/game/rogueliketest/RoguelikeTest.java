package com.youthful.game.rogueliketest;

import javax.swing.JFrame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.youthful.game.generator.DungeonGenerator;
import com.youthful.game.generator.Leaf;
import com.youthful.game.generator.Rectangle;

public class RoguelikeTest extends ApplicationAdapter {
	private static final float TIME_STEP = 1 / 120f;
	private static final int VELOCITY_ITERATIONS = 6, POSITION_ITERATIONS = 2;
	private float Box2DTime;
	
	private Player player;
	
	private Viewport viewport;

	private SpriteBatch batch;
	
	private TiledMap room;
	private OrthogonalTiledMapRenderer renderer;
	
	private World world;
	private Box2DDebugRenderer br;
	
	private Music BOTW;
	
	@Override
	public void create () {
		Box2D.init();
		Gdx.input.setCursorCatched(true); //What does this actually do? Seems like it just makes cursor dissapear	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		
		viewport = new StretchViewport(24, 13.5f);
		batch = new SpriteBatch();
		
		world = new World(new Vector2(0, 0), true);
		br = new Box2DDebugRenderer();
		
		player = new Player(560 / 16f, 458 / 16f, 2, 2, world);
		
		createDungeon();
		
		Gdx.input.setInputProcessor(player);
		
		BOTW = Gdx.audio.newMusic(Gdx.files.internal("BOTW.mp3"));
		BOTW.setVolume(0.1f);
		BOTW.setLooping(true);
		BOTW.play();
	}
	
	public void createDungeon() {
		DungeonGenerator generator = new DungeonGenerator(50, 6, true);
		TiledMap dungeon = new TiledMap();
		room = new TmxMapLoader().load("maps/walls.tmx");
		TiledMapTileLayer dungeonLayer = new TiledMapTileLayer(50, 50, 16, 16), roomLayer = (TiledMapTileLayer) room.getLayers().get(0);
		
		TiledMapTile[][] roomTiles = new TiledMapTile[4][4];
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++)
				roomTiles[i][j] = roomLayer.getCell(i, j).getTile();
		}
		
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 50; j++) {
				Cell cell = new Cell();
				
				dungeonLayer.setCell(i, j, cell);
				
				if (generator.corridors[i][j])
					cell.setTile(roomTiles[3][3]);
			}
		}
		
		for (Leaf roomLeaf : generator.leaves) {
			copyRoom(roomLeaf.room, dungeonLayer, roomTiles, generator.corridors);
		}
		
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 50; j++) {
				if(dungeonLayer.getCell(i, j).getTile() != null && dungeonLayer.getCell(i, j).getTile().getProperties().containsKey("blocked"))
					new Block(i, j, 1, 1, world);
			}
		}
		
		dungeon.getLayers().add(dungeonLayer);
		
		renderer = new OrthogonalTiledMapRenderer(dungeon, 1 / 16f);
		/*
		JFrame frame = new JFrame();
		frame.setTitle("Dungeon Drawer");
		frame.setContentPane(new DungeonDrawer(generator.size, generator.leaves, generator.corridors));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		*/
	}
	
	private void copyRoom(Rectangle rect, TiledMapTileLayer dungeonLayer, TiledMapTile[][] roomTiles, boolean[][] corridors) {
		dungeonLayer.getCell(rect.x, rect.y).setTile(roomTiles[0][0]);
		dungeonLayer.getCell(rect.x, rect.y + rect.height - 1).setTile(roomTiles[0][3]);
		dungeonLayer.getCell(rect.x + rect.width - 1, rect.y + rect.height - 1).setTile(roomTiles[2][3]);
		dungeonLayer.getCell(rect.x + rect.width - 1, rect.y).setTile(roomTiles[2][0]);
		
		for (int i = 1; i < rect.width - 1; i++) {
			if (!corridors[rect.x + i][rect.y])	
				dungeonLayer.getCell(rect.x + i, rect.y).setTile(roomTiles[1][0]);
			if(!corridors[rect.x + i][rect.y + rect.height - 1])
				dungeonLayer.getCell(rect.x + i, rect.y + rect.height - 1).setTile(roomTiles[1][3]);
			if(!corridors[rect.x + i][ rect.y + rect.height - 2])
				dungeonLayer.getCell(rect.x + i, rect.y + rect.height - 2).setTile(roomTiles[1][2]);
			
			for (int j = 1; j < rect.height - 2; j++)
				dungeonLayer.getCell(rect.x + i, rect.y + j).setTile(roomTiles[3][2]);
		}
		
		for (int i = 1; i < rect.height - 1; i++) {
			if(!corridors[rect.x][ rect.y + i])
				dungeonLayer.getCell(rect.x, rect.y + i).setTile(roomTiles[0][1]);
			if(!corridors[rect.x + rect.width - 1][ rect.y + i])
				dungeonLayer.getCell(rect.x + rect.width - 1, rect.y + i).setTile(roomTiles[2][1]);
		}
	}

	public void render() {
		//Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2); 
		
		float time = Gdx.graphics.getDeltaTime();

		processActs(time);
		
		Box2DTime += time; //Box2D Stuff
		
		while (Box2DTime >= TIME_STEP) {
			Box2DTime -= TIME_STEP;
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}

		processPositions();
		
		viewport.getCamera().position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0); //Rendering stage
		
		viewport.getCamera().update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		renderer.setView((OrthographicCamera) viewport.getCamera());
		
		renderer.render();
		
		batch.begin();
		player.draw(batch);
		batch.end();
		
		br.render(world, viewport.getCamera().combined);
	}
	
	private void processActs(float time) {
		player.act(time);
	}
	
	private void processPositions() {
		player.updatePosition();
	}
	
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	public void dispose () {
		renderer.dispose();
		batch.dispose();
		room.dispose();
		BOTW.dispose();
	}
	
	public static Animation<TextureRegion> makeAnimation(Texture tex, float frameDuration, int rows, int columns, int cellWidth, int cellHeight) {
		TextureRegion[][] split = TextureRegion.split(tex, cellWidth, cellHeight);
		
		TextureRegion[] reel = new TextureRegion[rows * columns];
		
		int index = 0;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				reel[index++] = split[i][j]; 
			}
		}
		
		return new Animation<TextureRegion>(frameDuration, reel);
	}
}
