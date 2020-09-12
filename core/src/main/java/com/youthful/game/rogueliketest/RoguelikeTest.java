package com.youthful.game.rogueliketest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Response.Result;
import com.dongbat.jbump.World;

public class RoguelikeTest extends ApplicationAdapter {
	Player player;
	Viewport viewport;
	TiledMap maze;
	OrthogonalTiledMapRenderer renderer;
	SpriteBatch batch;
	World<Entity> world;
	
	@Override
	public void create () {
		Gdx.input.setCursorCatched(true); //What does this actually do? Seems like it just makes cursor dissapear	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		
		viewport = new StretchViewport(480, 270);
		batch = new SpriteBatch();
		
		maze = new TmxMapLoader().load("maps/maze.tmx");
		renderer = new OrthogonalTiledMapRenderer(maze, batch);
		
		TiledMapTileLayer collision = (TiledMapTileLayer) maze.getLayers().get("collision");
		world = new World<Entity>();
		for (int i = 0; i < collision.getWidth(); i++) {
			for (int j = 0; j < collision.getHeight(); j++) {
				if (collision.getCell(i, j) != null) {
					world.add(new Block().getItem(), i * 16, j * 16, 16, 16);
				}
			}
		}
		
		player = new Player(24, 24, 32, 32);
		world.add(player.getItem(), player.getX() + 8, player.getY() + 6, 16, 16);
		
		Gdx.input.setInputProcessor(player);
		Music BOTW = Gdx.audio.newMusic(Gdx.files.internal("BOTW.mp3"));
		BOTW.setVolume(0.1f);
		BOTW.setLooping(true);
		BOTW.play();
	}

	@Override
	public void render () {
		//Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2); 

		player.act(Gdx.graphics.getDeltaTime()); //Processing Stage
		
		processCollisions(world.move(player.getItem(), player.getX() + 8, player.getY() + 6, CollisionFilter.defaultFilter));
		
		viewport.getCamera().position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
		
		viewport.getCamera().update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //Rendering stage
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		renderer.setView((OrthographicCamera) viewport.getCamera());
		
		renderer.render();
		
		batch.begin();
		player.draw(batch, 1);
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
		for (int i = 0; i < result.projectedCollisions.size(); i++) {
			Collision collision = result.projectedCollisions.get(i);
			
			if (collision != null)
				((Entity) collision.other.userData).processCollision(collision);
		}
	}
}
