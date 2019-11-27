package core.state;

import core.Game;
import core.gfx.Renderer;
import core.object.Camera;
import core.object.GameObject;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the AbstractState is the default implementation for a State interface. It
 * will provide all necessary default behaviors and processing for a State. All
 * specifics would be implemented in the inheriting class.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @see core.state.State
 * @since 2019
 */
@Slf4j
public abstract class AbstractState implements State, KeyListener {

	// the parent game.
	protected Game game;
	// the name fo this state.
	protected String name;
	// a State must have a Camera.
	public Camera camera;
	// all objects to be managed and rendered by this state.
	public Map<String, GameObject> objects = new ConcurrentHashMap<>();

	/**
	 * the default constructor.
	 */
	public AbstractState() {

	}

	/**
	 * Initialize the AbstractState with the parent game
	 *
	 * @param g the parent game.
	 */
	public AbstractState(Game g) {
		this.game = g;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract void input(Game g);

	@Override
	public abstract void initialize(Game g);

	@Override
	public abstract void load(Game g);

	@Override
	public abstract void update(Game g, float elapsed);

	@Override
	public abstract void render(Game g, Renderer r);

	@Override
	public void onFocus(Game g) {
		log.debug("{} state get focus", this.getName());
	}

	@Override
	public void lostFocus(Game g) {
		log.debug("{} state lost focus", this.getName());
	}

	/**
	 * Add a Game object to the managed objects list. If the <code>go</code>
	 * core.object.GameObject is a core.object.Camera instance, it will be set as
	 * the default camera.
	 *
	 * @param go the core.object.GameObject to be added to the core.Game#objects
	 *           list.
	 */
	public void addObject(GameObject go) {
		if (go instanceof Camera) {
			this.camera = (Camera) go;
		} else if (objects != null && !objects.containsKey(go.name)) {
			objects.put(go.name, go);
			game.renderer.add(go);
			if (!go.child.isEmpty()) {
				objects.putAll(go.child);
				game.renderer.addAll(go.child);
			}

		}
	}

	/**
	 * Add a bunch of object to the game !
	 *
	 * @param objects the list of core.object.GameObject to be added to the
	 *                core.Game#objects list.
	 */
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

	/**
	 * return the current active camera.
	 *
	 * @return
	 */
	public Camera getActiveCamera() {
		return camera;
	}

	/**
	 * return all the objects of the state.
	 *
	 * @return
	 */
	public Map<String, GameObject> getObjects() {
		return objects;
	}

	/**
	 * Define the parent Game.
	 *
	 * @param g the parent game for this state.
	 */
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
