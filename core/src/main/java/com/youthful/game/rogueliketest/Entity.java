package com.youthful.game.rogueliketest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.Item;

public abstract class Entity {
	private Item<Entity> self;
	private float x, y;
	private float width, height;
	private float originX, originY;
	private float scaleX = 1, scaleY = 1;
	private float rotation;
	private final Color color = new Color(1, 1, 1, 1);

	public Entity() {
		self = new Item<Entity>(this);
	}
	
	public abstract void processCollision(Collision collision);
	
	public void draw (Batch batch) {
		
	}

	public void act (float delta) {
		
	}

	public Item<Entity> getItem() {
		return self;
	}
	
	public float getX () {
		return x;
	}

	public void setX (float x) {
		if (this.x != x) {
			this.x = x;
			positionChanged();
		}
	}

	public float getY () {
		return y;
	}

	public void setY (float y) {
		if (this.y != y) {
			this.y = y;
			positionChanged();
		}
	}

	public void setPosition (float x, float y) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;
			positionChanged();
		}
	}

	public void moveBy (float x, float y) {
		if (x != 0 || y != 0) {
			this.x += x;
			this.y += y;
			positionChanged();
		}
	}

	public float getWidth () {
		return width;
	}

	public void setWidth (float width) {
		if (this.width != width) {
			this.width = width;
			sizeChanged();
		}
	}

	public float getHeight () {
		return height;
	}

	public void setHeight (float height) {
		if (this.height != height) {
			this.height = height;
			sizeChanged();
		}
	}

	public float getTop () {
		return y + height;
	}

	public float getRight () {
		return x + width;
	}

	private void positionChanged () {
	}

	private void sizeChanged () {
	}

	private void rotationChanged () {
	}

	public void setSize (float width, float height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			sizeChanged();
		}
	}

	public void sizeBy (float size) {
		if (size != 0) {
			width += size;
			height += size;
			sizeChanged();
		}
	}

	public void sizeBy (float width, float height) {
		if (width != 0 || height != 0) {
			this.width += width;
			this.height += height;
			sizeChanged();
		}
	}

	public void setBounds (float x, float y, float width, float height) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;
			positionChanged();
		}
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			sizeChanged();
		}
	}

	public float getOriginX () {
		return originX;
	}

	public void setOriginX (float originX) {
		this.originX = originX;
	}

	public float getOriginY () {
		return originY;
	}

	public void setOriginY (float originY) {
		this.originY = originY;
	}

	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
	}

	public float getScaleX () {
		return scaleX;
	}

	public void setScaleX (float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY () {
		return scaleY;
	}

	public void setScaleY (float scaleY) {
		this.scaleY = scaleY;
	}

	public void setScale (float scaleXY) {
		this.scaleX = scaleXY;
		this.scaleY = scaleXY;
	}

	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public void scaleBy (float scale) {
		scaleX += scale;
		scaleY += scale;
	}

	public void scaleBy (float scaleX, float scaleY) {
		this.scaleX += scaleX;
		this.scaleY += scaleY;
	}

	public float getRotation () {
		return rotation;
	}

	public void setRotation (float degrees) {
		if (this.rotation != degrees) {
			this.rotation = degrees;
			rotationChanged();
		}
	}

	public void rotateBy (float amountInDegrees) {
		if (amountInDegrees != 0) {
			rotation = (rotation + amountInDegrees) % 360;
			rotationChanged();
		}
	}

	public void setColor (Color color) {
		this.color.set(color);
	}

	public void setColor (float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}

	public Color getColor () {
		return color;
	}
}

