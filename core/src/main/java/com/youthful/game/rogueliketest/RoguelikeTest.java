package com.youthful.game.rogueliketest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.youthful.game.generator.DungeonGenerator;

public class RoguelikeTest extends ApplicationAdapter {
	private static final float TIME_STEP = 1 / 120f;
	private static final int VELOCITY_ITERATIONS = 6, POSITION_ITERATIONS = 2;
	private float Box2DTime;
	
	private Player player;
	
	private Viewport viewport;

	private SpriteBatch batch;

	private OrthogonalTiledMapRenderer renderer;
	
	private World world;
	private Box2DDebugRenderer br;
	
	private DungeonGenerator generator;
	
	private Music BOTW;
	
	@Override
	public void create () {
		Gdx.input.setCursorCatched(true); //What does this actually do? Seems like it just makes cursor dissapear	

		viewport = new StretchViewport(24, 13.5f);
		batch = new SpriteBatch();
		
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		br = new Box2DDebugRenderer();
		
		generator = new DungeonGenerator(50, 6, true, -1960201350);
		generator.generateMap(32);
		
		TiledMapTileLayer dungeonBackground = (TiledMapTileLayer) generator.dungeon.getLayers().get("background");
		
		int x = 0, y = 0;
		
		for(int i = generator.size - 1; i > -1; i--) {
			for (int j = generator.size - 1; j > -1; j--) {
				if (dungeonBackground.getCell(i, j).getTile() == null) {
					new Block(i, j, 1, 1, world);
				}	
				else if(dungeonBackground.getCell(i, j).getTile().getProperties().containsKey("blocked")) 
					new Block(i, j, 1, 1, world);
				else if(dungeonBackground.getCell(i, j).getTile().equals(generator.roomTiles[3][2])) {
					x = i; 
					y = j;
				}
			}	
		}
		
		player = new Player(x, y, 2, 2, world);
		
		renderer = new OrthogonalTiledMapRenderer(generator.dungeon, 1 / 32f);
		
		Gdx.input.setInputProcessor(player);
		
		BOTW = Gdx.audio.newMusic(Gdx.files.internal("BOTW.mp3"));
		BOTW.setVolume(0.1f);
		BOTW.setLooping(true);
		BOTW.play();
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
		
		//br.render(world, viewport.getCamera().combined);	
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
		generator.dispose();
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
