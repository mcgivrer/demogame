package core.collision;

import core.map.MapLayer;
import core.map.MapObject;
import core.map.MapReader.TileType;
import core.object.GameObject;

/**
 * The ColisionEvent is raised by the MapCollidingService. It will be managed by
 * a OnCollision interface implementation in your game.
 * 
 * 
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
public class CollisionEvent {
	public MapLayer map;
	public TileType type;
	public GameObject o1;
	public GameObject o2;
	public MapObject m2;
	public int mapX, mapY;

	public CollisionEvent(TileType type, GameObject o1, GameObject o2, MapObject m2, MapLayer map, int x, int y) {
		this.type = type;
		this.o1 = o1;
		this.o2 = o2;
		this.m2 = m2;
		this.map = map;
		this.mapX = x;
		this.mapY = y;
	}

	public enum CollisionType {
		COLLISION_MAP("map"), COLLISION_OBJECT("object"), COLLISION_ITEM("item");

		private String value = "";

		CollisionType(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}
}
