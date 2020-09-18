package com.youthful.game.rogueliketest;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class RoguelikeTest extends ApplicationAdapter {
	private static final float TIME_STEP = 1 / 120f;
	private static final int VELOCITY_ITERATIONS = 6, POSITION_ITERATIONS = 2;
	private float Box2DTime;
	
	private Player player;
	private Dummy dummy1, dummy2, dummy3;
	
	private Viewport viewport;

	private SpriteBatch batch;
	
	private TiledMap maze;
	private OrthogonalTiledMapRenderer renderer;
	
	private World world;
	private Box2DDebugRenderer br;
	private ArrayList<Body> interacting;
	private RayHandler handler;
	private InteractCallback callback;
	
	@Override
	public void create () {
		Box2D.init();
		Gdx.input.setCursorCatched(true); //What does this actually do? Seems like it just makes cursor dissapear	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		
		viewport = new StretchViewport(24, 13.5f);
		batch = new SpriteBatch();
		
		maze = new TmxMapLoader().load("maps/maze.tmx");
		renderer = new OrthogonalTiledMapRenderer(maze, 1 / 16f, batch);

		interacting = new ArrayList<Body>();
		
		world = new World(new Vector2(0, 0), true);
		br = new Box2DDebugRenderer();
		callback = new InteractCallback(interacting);
		handler = new RayHandler(world);
		handler.setAmbientLight(0, 0, 0, 0.05f);
		

		TiledMapTileLayer collision = (TiledMapTileLayer) maze.getLayers().get("collision");
		for (int i = 0; i < collision.getWidth(); i++) {
			for (int j = 0; j < collision.getHeight(); j++) {
				if (collision.getCell(i, j) != null)
					new Block(i, j, 1, 1, world);
			}
		}
		
		player = new Player(560 / 16f, 458 / 16f, 2, 2, world);
		new PointLight(handler, 100).attachToBody(player.getBody());
		
		dummy1 = new Dummy(528 / 16f, 512 / 16f, 2, 2, world);
		
		dummy2 = new Dummy(560 / 16f, 512 / 16f, 2, 2, world);
		
		dummy3 = new Dummy(592 / 16f, 512 / 16f, 2, 2, world);
		
		Gdx.input.setInputProcessor(player);
		Music BOTW = Gdx.audio.newMusic(Gdx.files.internal("BOTW.mp3"));
		BOTW.setVolume(0.1f);
		BOTW.setLooping(true);
		BOTW.play();
	}

	public void render () {
		//Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2); 
		
		float time = Gdx.graphics.getDeltaTime();

		processActs(time);
		
		Box2DTime += time; //Box2D Stuff
		
		while (Box2DTime >= TIME_STEP) {
			Box2DTime -= TIME_STEP;
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}

		processCollisions();
		processPositions();
		
		viewport.getCamera().position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0); //Rendering stage
		
		viewport.getCamera().update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		renderer.setView((OrthographicCamera) viewport.getCamera());
		
		renderer.render();
		
		batch.begin();
		player.draw(batch);
		dummy1.draw(batch);
		dummy2.draw(batch);
		dummy3.draw(batch);
		batch.end();
		
		br.render(world, viewport.getCamera().combined.translate(new Vector3(0.5f, 0.5f, 0)));
		
		handler.setCombinedMatrix((OrthographicCamera) viewport.getCamera());
		handler.updateAndRender();
	}
	
	private void processActs(float time) {
		player.act(time);
		dummy1.act(time);
		dummy2.act(time);
		dummy3.act(time);
	}
	
	private void processPositions() {
		player.updatePosition();
	}
	
	public void processCollisions() {
		/*
		for (int i = 0; i < result.projectedCollisions.size(); i++) {
			Collision collision = result.projectedCollisions.get(i);
			
			if (collision != null)
				((Entity) collision.other.userData).processCollision(collision);
		}
		*/
		
		if (player.getInteracting()) {
			interacting.clear();

			world.QueryAABB(callback, player.getBody().getPosition().x - 16, player.getBody().getPosition().y - 16, player.getBody().getPosition().x + 16, player.getBody().getPosition().y + 16);
			
			float distance = Float.MAX_VALUE, dist2 = 0, x = player.getBody().getPosition().x, y = player.getBody().getPosition().y;
			Interactable interactor = null;
			
			for (Body body : interacting) {
				dist2 = Vector2.dst2(x, y, body.getPosition().x, body.getPosition().y);
				
				if (dist2 < distance) {
					distance = dist2;
					interactor = (Interactable) body.getUserData();
				}
			}
			
			if(interactor != null)
				interactor.interact(player);
				
			player.setInteracting(false);
		}
	}
	
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	public void dispose () {
		renderer.dispose();
		maze.dispose();
		batch.dispose();
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
