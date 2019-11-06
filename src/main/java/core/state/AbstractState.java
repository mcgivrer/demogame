package core.state;

import core.Game;
import core.Renderer;
import core.object.Camera;
import core.object.GameObject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractState implements State, KeyListener {

    private Game game;
    protected String name;
    public Camera camera;
    public Map<String, GameObject> objects = new ConcurrentHashMap<>();

    public AbstractState() {

    }

    public AbstractState(Game g) {
        this.game = g;
    }

    public String getName() {
        return name;
    }


    public abstract void input(Game g);

    public abstract void initialize(Game g);

    public abstract void load(Game g);

    public abstract void update(Game g, float elapsed);

    public abstract void render(Game g, Renderer r);


    /**
     * Add a Game object to the managed objects list.
     * If the <code>go</code> core.object.GameObject is a core.object.Camera instance, it will be set as the default camera.
     *
     * @param go the core.object.GameObject to be added to the core.Game#objects list.
     */
    public void addObject(GameObject go) {
        if (go instanceof Camera) {
            this.camera = (Camera) go;
        } else if (objects != null && !objects.containsKey(go.name)) {

            objects.put(go.name, go);
            game.renderer.add(go);

        }
    }

    /**
     * Add a bunch of object to the game !
     *
     * @param objects the list of core.object.GameObject to be added to the core.Game#objects list.
     */
    public void addAllObject(List<GameObject> objects) {
        for (GameObject o : objects) {
            addObject(o);
        }
    }

    public void addAllObject(Collection<GameObject> objects) {
        for (GameObject o : objects) {
            addObject(o);
        }
    }


    public void removeObject(GameObject go) {
        objects.remove(go.name);
        game.renderer.remove(go);
    }

    public void removeObject(String name) {
        if (objects.containsKey(name)) {
            GameObject go = objects.get(name);
            removeObject(go);
        }
    }

    public void removeAllObjects(List<GameObject> objectsToBeRemoved) {
        game.renderer.removeAll(objectsToBeRemoved);
        objects.values().removeAll(objectsToBeRemoved);
    }

    public void removeFilteredObjects(String nameFilter) {
        List<GameObject> toBeRemoved = new ArrayList<>();
        for (GameObject go : objects.values()) {
            if (go.name.contains(nameFilter)) {
                toBeRemoved.add(go);
            }
        }
        if (!toBeRemoved.isEmpty()) {
            removeAllObjects(toBeRemoved);
            toBeRemoved.clear();
        }
    }

    public Camera getActiveCamera() {
        return camera;
    }

    public Map<String, GameObject> getObjects() {
        return objects;
    }

    public void setGame(Game g) {
        this.game = g;
    }

    /**
     * A Unicode key has been pressed.
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Process some keypressed events.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {

    }

    /**
     * Process some KeyReleased events.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        Renderer r = game.sysMan.getSystem(Renderer.class);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_D:
                // roll the debug level.
                game.config.debug = (game.config.debug < 6 ? game.config.debug + 1 : 0);
                break;
            case KeyEvent.VK_F3:
                r.saveScreenshot(game.config);
            default:
                break;
        }
    }
}
