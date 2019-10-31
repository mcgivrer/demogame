package core.collision;

import core.map.MapObject;
import core.object.GameObject;

public class CollisionEvent {
    public enum CollisionType {
        COLLISION_MAP,
        COLLISION_OBJECT,
        COLLISION_ITEM;
    }

    public CollisionType type;
    public GameObject o1;
    public GameObject o2;
    public MapObject m2;
}
