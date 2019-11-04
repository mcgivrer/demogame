package core.collision;

import core.map.MapLevel;
import core.map.MapObject;
import core.object.GameObject;

public class CollisionEvent {
    public MapLevel map;
    public CollisionType type;
    public GameObject o1;
    public GameObject o2;
    public MapObject m2;
    public int mapX, mapY;

    public CollisionEvent(CollisionType type, GameObject o1, GameObject o2, MapObject m2, MapLevel map, int x, int y) {
        this.type = type;
        this.o1 = o1;
        this.o2 = o2;
        this.m2 = m2;
        this.map = map;
        this.mapX = x;
        this.mapY = y;
    }

    public enum CollisionType {
        COLLISION_MAP,
        COLLISION_OBJECT,
        COLLISION_ITEM;
    }
}
