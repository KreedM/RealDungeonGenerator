package com.youthful.game.rogueliketest;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Block extends Entity implements Slidable{	
	
	public Block(float x, float y, float width, float height, World world) {
		super(x, y, width, height);
		
		createBody(world);
	}

	public void processCollision() {}

	public void createBody(World world) {
		BodyDef tileDef = new BodyDef();
		tileDef.type = BodyType.StaticBody;
		
		tileDef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
		
		Body tile = world.createBody(tileDef);
		
		tile.setUserData(this);
		
		PolygonShape tileShape = new PolygonShape();
		tileShape.setAsBox(getWidth() / 2, getWidth() / 2);
		
		tile.createFixture(tileShape, 0);
		
		tileShape.dispose();
		
		setBody(tile);
	}
}
