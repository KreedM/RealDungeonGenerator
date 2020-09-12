package com.youthful.game.rogueliketest;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Collision;

public class Player extends Entity implements InputProcessor {
	private static final float FRAME_DURATION = 0.2f; 
	private static final float SPEED = 100;
	
	private Vector2 velocity;
	
	private boolean left, right, up, down;
	private boolean leftHold, rightHold, upHold, downHold;
	private boolean attacking;

	private byte xDir, yDir, prevXDir, prevYDir;
	
	private Animation<TextureRegion> currAnim; 
	private Animation<TextureRegion> upAnim, downAnim, leftAnim, rightAnim; 
	private Animation<TextureRegion> upLeftAnim, upRightAnim, downLeftAnim, downRightAnim;
	private Animation<TextureRegion> attackAnim;
	
	private float moveTime, attackTime;
	
	public Player(float x, float y, int width, int height) {
		super();
		
		setBounds(x, y, 32, 32);
		
		Texture spriteSheet = new Texture("entities/player/spritesheet.png");
		TextureRegion[][] regions = TextureRegion.split(spriteSheet, 32, 32);
		
		downAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[0]);
		upAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[5]);
		leftAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[3]);
		rightAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[4]);
		upLeftAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[6]);
		upRightAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[7]);
		downLeftAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[1]);
		downRightAnim = new Animation<TextureRegion>(FRAME_DURATION, regions[2]);
		
		downAnim.setPlayMode(PlayMode.LOOP);
		upAnim.setPlayMode(PlayMode.LOOP);
		leftAnim.setPlayMode(PlayMode.LOOP);
		rightAnim.setPlayMode(PlayMode.LOOP);
		upLeftAnim.setPlayMode(PlayMode.LOOP);
		upRightAnim.setPlayMode(PlayMode.LOOP);
		downLeftAnim.setPlayMode(PlayMode.LOOP);
		downRightAnim.setPlayMode(PlayMode.LOOP);
		
		attackAnim = makeAnimation(new Texture("entities/player/AttackAnim.png"), 1 / 10f, 2, 5, 32, 32);
		
		currAnim = upAnim;
		
		velocity = new Vector2();
	}
	
	public void act(float delta) {
		super.act(delta);
		
		processDirection(delta);
		
		processAnimations(delta);
		
		setX(getX() + velocity.x * delta);
		setY(getY() + velocity.y * delta);
	}
	
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(getColor().r, getColor().g, getColor().b, parentAlpha);
		
		batch.draw(currAnim.getKeyFrame(moveTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		
		if (attacking)
			batch.draw(attackAnim.getKeyFrame(attackTime), getX() + 64, getY(), 64, 64);
	}

	public void processDirection(float delta) {
		if(leftHold) {
			if(right) {
				velocity.x = Math.min(SPEED, velocity.x+SPEED/4);
				xDir = 1;
			}
			else {
				velocity.x = Math.max(-SPEED, velocity.x-SPEED/4);
				xDir = -1;
			}
		}
		else if(rightHold) {
			if(left) {
				velocity.x = Math.max(-SPEED, velocity.x-SPEED/4);
				xDir = -1;
			}
			else {
				velocity.x = Math.min(SPEED, velocity.x+SPEED/4);
				xDir = 1;
			}
		}
		
		if(!left && !right) {
			velocity.x = 0;
			xDir = 0;
		}
		
		if(downHold) {
			if(up) {
				velocity.y = Math.min(SPEED, velocity.y + SPEED / 4);
				yDir = 1;
			}
			else {
				velocity.y = Math.max(-SPEED, velocity.y - SPEED / 4);
				yDir = -1;
			}
		}
		else if(upHold) {
			if(down) {
				velocity.y = Math.max(-SPEED, velocity.y - SPEED / 4);
				yDir = -1;
			}
			else {
				velocity.y = Math.min(SPEED, velocity.y + SPEED / 4);
				yDir = 1;
			}
		}
		
		if(!up && !down) {
			velocity.y = 0;
			yDir = 0;
		}
	}
	
	public void processAnimations(float delta) {
		moveTime += delta;
		
		if (attacking) {
			attackTime += delta;
			if (attackAnim.isAnimationFinished(attackTime)) {
				attackTime = 0; 
				attacking = false; 
			}
		}
		
		if (prevXDir != xDir) { 
			prevXDir = xDir;
			moveTime = 0;
		}
		
		if (prevYDir != yDir) {
			prevYDir = yDir;
			moveTime = 0;
		}

		if (xDir == -1) {
			if (yDir == 0)
				currAnim = leftAnim;
			else if (yDir == -1)
				currAnim = downLeftAnim;
			else if (yDir == 1)
				currAnim = upLeftAnim;
		}
		
		else if (xDir == 0) {
			if (yDir == 0) {
				moveTime -= delta;
				return;
			}
			else if (yDir == -1)
				currAnim = downAnim;
			else if (yDir == 1)
				currAnim = upAnim;
		}
		
		else if (xDir == 1) {
			if (yDir == 0)
				currAnim = rightAnim;
			else if (yDir == -1)
				currAnim = downRightAnim;
			else if (yDir == 1)
				currAnim = upRightAnim;
		}
	}
	
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT: 
			case Input.Keys.A: 
				left = true; 
				if(!rightHold) 
					leftHold = true;
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D: 
				right = true;
				if(!leftHold)
					rightHold = true;
				break;
			case Input.Keys.UP:
			case Input.Keys.W: 
				up = true; 
				if(!downHold) 
					upHold = true;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S: 
				down = true;
				if(!upHold)
					downHold = true;
				break;
			case Input.Keys.SPACE:
				attacking = true;
		}
		return true;
	}

	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
			case Input.Keys.A: 
				left = false; 
				leftHold = false; 
				if (right)
					rightHold = true;
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D: 
				right = false; 
				rightHold = false;
				if (left)
					leftHold = true;
				break;
			case Input.Keys.UP:
			case Input.Keys.W: 
				up = false; 
				upHold = false; 
				if (down)
					downHold = true;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S: 
				down = false; 
				downHold = false;
				if (up)
					upHold = true;
		}
		return false;
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
	
	public boolean keyTyped(char character) {return false;}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}

	public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}

	public boolean mouseMoved(int screenX, int screenY) {return false;}

	public boolean scrolled(int amount) {return false;}
	
	public void processCollision(Collision collision) {}
}
