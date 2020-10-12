package samples.render.collision;

import samples.collision.CollisionEvent;
import samples.object.entity.GameObject;

public class ColEvent extends CollisionEvent {

    public ColEvent(GameObject o1, GameObject o2) {
        super(o1, o2);
        if (o1.getClass().getSimpleName().equals("MouseCursor") && !o2.getClass().getSimpleName().equals("Camera")) {
            this.type = CollisionType.COLLISION_MOUSE;
            o1.attributes.put("collidingWith", o2.getName());
        } else {
            this.type = CollisionType.COLLISION_OBJECT;
        }
    }

}
