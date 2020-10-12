package samples.collision;

import samples.object.entity.GameObject;

public class CollisionEvent {

    public CollisionType type;
    public GameObject a;
    public GameObject b;
    public double vx;
    public double vy;

    public CollisionEvent(GameObject go1, GameObject go2) {
        this.type = CollisionType.COLLISION_OBJECT;
        this.a = go1;
        this.b = go2;
        this.vx = go1.x - go2.x;
        this.vy = go1.y - go2.y;
    }

    public enum CollisionType {
        COLLISION_OBJECT("game_object"),
        COLLISION_MOUSE("mouse_cursor");

        private String value = "";

        CollisionType(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}