package core.object;

import core.Game;
import core.system.AbstractSystem;
import core.system.System;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameObject manager to manage all objects from the game.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
public class GameObjectManager extends AbstractSystem implements System {

    private Map<String, GameObject> objects = new HashMap<>();

    /**
     * Build the system attached to the parent game.
     *
     * @param game the parent game for this system.
     */
    public GameObjectManager(Game game) {
        super(game);
    }

    public void add(GameObject go) {
        if (!objects.containsKey(go.name)) {
            objects.put(go.name, go);
        }
    }

    public void addAll(List<GameObject> all) {
        all.stream().forEach(o -> {
            add(o);
        });
    }

    public void addAll(Map<String, GameObject> all) {
        all.values().stream().forEach(o -> {
            add(o);
        });
    }

    public void remove(GameObject go) {
        objects.remove(go.name);
    }

    public void remove(String goName) {
        objects.remove(goName);
    }

    public GameObject get(String name) {
        return objects.getOrDefault(name, null);
    }

    @Override
    public String getName() {
        return GameObjectManager.class.getCanonicalName();
    }

    @Override
    public int initialize(Game game) {
        return 0;
    }

    @Override
    public void dispose() {

    }
}
