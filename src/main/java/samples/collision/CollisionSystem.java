package samples.collision;

import java.util.ArrayList;
import java.util.List;

import samples.Sample;
import samples.object.GameObject;
import samples.system.AbstractGameSystem;

public class CollisionSystem extends AbstractGameSystem {

    List<GameObject> objects = new ArrayList<>();

    protected CollisionSystem(Sample game) {
        super(game);
    }

    public void addObject(GameObject g) {
        if(!objects.contains(g)){
            objects.add(g);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public String getName() {
        return CollisionSystem.class.getSimpleName();
    }

}