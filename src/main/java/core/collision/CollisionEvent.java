package core.collision;

import core.map.MapLayer;
import core.map.MapObject;
import core.map.MapReader.TileType;
import core.math.Vector2D;
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
	public TileType tileType;
	public CollisionType type;
	public GameObject a;
	public GameObject b;
	public MapObject m2;
	public int mapX, mapY;
	public Vector2D penetrationVector;
	public String cpId;

	public CollisionEvent(TileType type, GameObject o1, GameObject o2, MapObject m2, MapLayer map, String cpId, int x, int y) {
		this.tileType = type;
		this.type = CollisionType.COLLISION_MAP;
		this.a = o1;
		this.b = o2;
		this.m2 = m2;
		this.map = map;
		this.mapX = x;
		this.mapY = y;
		this.cpId = cpId;
	}

	public CollisionEvent(GameObject go1, GameObject go2){
		this.type = CollisionType.COLLISION_OBJECT;
		this.a = go1;
		this.b = go2;
		this.penetrationVector=new Vector2D(go1.pos.sub(go2.pos));
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
