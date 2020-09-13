package com.youthful.game.rogueliketest;

import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;

public class RushCollisionFilter implements CollisionFilter {
	public Response filter(Item item, Item other) {
		if (other.userData instanceof Slidable)
			return Response.slide;
		else if(other.userData instanceof Touchable)
			return Response.touch;
		else if(other.userData instanceof Crossable)
			return Response.cross;
		else if (other.userData instanceof Bouncable)
			return Response.bounce;
		return null;
	}
}
