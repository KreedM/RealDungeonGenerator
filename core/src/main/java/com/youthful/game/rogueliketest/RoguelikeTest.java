package com.youthful.game.rogueliketest;

import java.util.ArrayList;

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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
//import com.dongbat.jbump.Collision;
import com.dongbat.jbump.Response.Result;
import com.dongbat.jbump.World;

public class RoguelikeTest extends ApplicationAdapter {
	private Player player;
	private Dummy dummy1, dummy2, dummy3;
	private Viewport viewport;
	private TiledMap maze;
	private OrthogonalTiledMapRenderer renderer;
	private SpriteBatch batch;
	private World<Entity> world;
	private RushCollisionFilter filter;
	private ArrayList<Item> interacted;
	
	@Override
	public void create () {
		Gdx.input.setCursorCatched(true); //What does this actually do? Seems like it just makes cursor dissapear	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		
		viewport = new StretchViewport(384, 216);
		batch = new SpriteBatch();
		
		maze = new TmxMapLoader().load("maps/maze.tmx");
		renderer = new OrthogonalTiledMapRenderer(maze, batch);
		
		world = new World<Entity>();
		filter = new RushCollisionFilter();
		interacted = new ArrayList<Item>();

		TiledMapTileLayer collision = (TiledMapTileLayer) maze.getLayers().get("collision");
		for (int i = 0; i < collision.getWidth(); i++) {
			for (int j = 0; j < collision.getHeight(); j++) {
				if (collision.getCell(i, j) != null) {
					world.add(new Block().getItem(), i * 16, j * 16, 16, 16);
				}
			}
		}
		
		player = new Player(560, 458, 32, 32);
		world.add(player.getItem(), player.getX() + 8, player.getY() + 6, 16, 16);
		
		dummy1 = new Dummy(528, 512, 32, 32);
		world.add(dummy1.getItem(), dummy1.getX() + 8, dummy1.getY(), 16, 16);
		
		dummy2 = new Dummy(560, 512, 32, 32);
		world.add(dummy2.getItem(), dummy2.getX() + 8, dummy2.getY(), 16, 16);
		
		dummy3 = new Dummy(592, 512, 32, 32);
		world.add(dummy3.getItem(), dummy3.getX() + 8, dummy3.getY(), 16, 16);
		
		Gdx.input.setInputProcessor(player);
		Music BOTW = Gdx.audio.newMusic(Gdx.files.internal("BOTW.mp3"));
		BOTW.setVolume(0.1f);
		BOTW.setLooping(true);
		BOTW.play();
	}

	@Override
	public void render () {
		//Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2); 
		
		float time = Gdx.graphics.getDeltaTime();
		
		player.act(time); //Processing Stage
		dummy1.act(time);
		dummy2.act(time);
		dummy3.act(time);
		
		processCollisions(world.move(player.getItem(), player.getX() + 8, player.getY() + 6, filter));
		
		viewport.getCamera().position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
		
		viewport.getCamera().update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Rendering stage
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		renderer.setView((OrthographicCamera) viewport.getCamera());
		
		renderer.render();
		
		batch.begin();
		player.draw(batch);
		dummy1.draw(batch);
		dummy2.draw(batch);
		dummy3.draw(batch);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose () {
		renderer.dispose();
		maze.dispose();
		batch.dispose();
	}
	
	public void processCollisions(Result result) {
		/*
		for (int i = 0; i < result.projectedCollisions.size(); i++) {
			Collision collision = result.projectedCollisions.get(i);
			
			if (collision != null)
				((Entity) collision.other.userData).processCollision(collision);
		}
		*/
		
		player.setPosition(world.getRect(player.getItem()).x - 8, world.getRect(player.getItem()).y - 6);
		
		if (player.getInteracting()) {
			world.queryRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), null, interacted);
			
			Rect playerRect = world.getRect(player.getItem());
			
			Item<Entity> interacting = null;
			Rect interactingRect = null;
			
			for (Item<Entity> item : interacted) {
				if (item.userData instanceof Interactable) {
					Rect itemRect = world.getRect(item);
					if (interacting == null) {
						interacting = item;
						interactingRect = world.getRect(interacting);
					}
					else {
						if(Vector2.dst2(playerRect.x, playerRect.y, itemRect.x, itemRect.y) < Vector2.dst2(playerRect.x, playerRect.y, interactingRect.x, interactingRect.y)) {
							interacting = item;
							interactingRect = world.getRect(interacting);
						}
					}
				}
			}
			
			if (interacting != null)
				((Interactable) interacting.userData).interact(player);

			player.setInteracting(false);
		}
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
