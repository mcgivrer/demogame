package samples.render.collision;

import samples.collision.CollisionEvent;
import samples.object.entity.GameObject;
import samples.render.entity.MouseCursor;

public class ColEvent extends CollisionEvent {

    public ColEvent(GameObject o1, GameObject o2) {
        super(o1, o2);
        if (o1 instanceof MouseCursor) {
            this.type = CollisionType.COLLISION_MOUSE;
        } else {
            this.type = CollisionType.COLLISION_OBJECT;
        }
    }

}
