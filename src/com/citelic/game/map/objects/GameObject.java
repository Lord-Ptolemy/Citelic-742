package com.citelic.game.map.objects;

import com.citelic.cache.impl.ObjectDefinitions;
import com.citelic.game.map.tile.Tile;
import com.citelic.utility.Utilities;

@SuppressWarnings("serial")
public class GameObject extends Tile {

	private int id;
	private int type;
	private int rotation;
	private int life;

	public GameObject(int id, int type, int rotation, int x, int y, int plane) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = Utilities.random(7);
	}

	public GameObject(int id, int type, int rotation, int x, int y, int plane,
			int life) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = life;
	}

	public GameObject(int id, int type, int rotation, Tile tile) {
		super(tile.getX(), tile.getY(), tile.getZ());
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = Utilities.random(7);
	}

	public GameObject(GameObject object) {
		super(object.getX(), object.getY(), object.getZ());
		this.id = object.id;
		this.type = object.type;
		this.rotation = object.rotation;
		this.life = object.life;
	}

	public void decrementObjectLife() {
		this.life--;
	}

	public ObjectDefinitions getDefinitions() {
		return ObjectDefinitions.getObjectDefinitions(id);
	}

	public int getId() {
		return id;
	}

	public int getLife() {
		return life;
	}

	public int getRotation() {
		return rotation;
	}

	public int getType() {
		return type;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlane() {
		return plane;
	}

}
