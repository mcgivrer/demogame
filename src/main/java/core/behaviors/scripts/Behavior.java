package core.behaviors.scripts;

import core.Game;
import core.map.MapLevel;
import core.object.GameObject;

public interface Behavior {

    void update(Game dg, MapLevel map, GameObject go, float elapsed);
}
