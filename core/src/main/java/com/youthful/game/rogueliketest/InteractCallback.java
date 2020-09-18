package com.youthful.game.rogueliketest;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class InteractCallback implements QueryCallback{

	ArrayList<Body> interacting;
	
	public InteractCallback(ArrayList<Body> interacting) {
		this.interacting = interacting;
	}

	public boolean reportFixture(Fixture fixture) {
		if(fixture.getBody().getUserData() instanceof Interactable)
			interacting.add(fixture.getBody());
		return true;
	}
}
