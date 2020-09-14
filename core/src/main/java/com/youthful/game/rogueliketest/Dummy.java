package com.youthful.game.rogueliketest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dongbat.jbump.Collision;

public class Dummy extends Entity implements Slidable, Interactable {
	private static final float FRAME_DURATION = 0.2f; 
	private static final float MESSAGE_TIME = 1;
	private static final int MESSAGE_WIDTH = 75; 
	
	private String message;
	private Animation<TextureRegion> currAnim; 
	private BitmapFont font;
	private GlyphLayout layout;
	
	private float time;
	private float interactTime;
	private boolean interacting;
	
	public Dummy(float x, float y, int width, int height) {
		super();
		
		setBounds(x, y, 32, 32);
		
		TextureAtlas atlas = new TextureAtlas("Dawnlike.atlas");
		font = new BitmapFont(Gdx.files.internal("font.fnt"), atlas.findRegion("font"));
		font.getData().setScale(1 / 3f);
		font.setUseIntegerPositions(false);
		layout = new GlyphLayout();
		
		Texture spriteSheet = new Texture("entities/player/spritesheet.png");
		TextureRegion[][] regions = TextureRegion.split(spriteSheet, 32, 32);
		
		currAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[0]);
		
		currAnim.setPlayMode(PlayMode.LOOP);
	}
	
	public void act(float delta) {
		time += delta;
	}
	
	public void draw(Batch batch) {
		batch.draw(currAnim.getKeyFrame(time), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	
		if(interacting) {
			if (interactTime < MESSAGE_TIME) {
				layout.setText(font, message, 0, message.length(), getColor(), MESSAGE_WIDTH, Align.center, true, null);
				font.draw(batch, layout, getX() + (getWidth() - MESSAGE_WIDTH) / 2, getTop() + layout.height);
				interactTime += Gdx.graphics.getDeltaTime();
			}
			else {
				interacting = false;
				interactTime = 0;
			}
		}
	}
	
	public void interact(Player player) {
		if (interacting)
			return;
		message = Double.toString(Math.random());
		interacting = true;
	}
	
	public void processCollision(Collision collision) {}
}
