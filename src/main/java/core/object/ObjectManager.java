/**
 * SnapGames
 * <p>
 * Game Development Java
 * <p>
 * DemoGame
 *
 * @year 2019
 */
package core.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Game;
import core.system.AbstractSystem;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

/**
 * A manager to store GameObject on the game system..
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
@Slf4j
public class ObjectManager extends AbstractSystem implements System {

	public Map<String, GameObject> objects = new ConcurrentHashMap<>();

	public List<GameObject> objectsToDelete = new ArrayList<>();

	/**
	 * @param game
	 */
	public ObjectManager(Game game) {
		super(game);
	}

	@Override
	public int initialize(Game game) {
		log.info("ObjectManager ready");
		return 1;
	}

	@Override
	public void dispose() {
		objects.clear();
		objects = null;
	}

	@Override
	public String getName() {
		return ObjectManager.class.getCanonicalName();
	}

	public void add(GameObject go) {
		if (!objects.containsKey(go.name)) {
			objects.put(go.name, go);
			if (!go.child.isEmpty()) {
				putAll(go.child);
			}
		} else {
			log.error("object '{}' as {} is already managed", go.name, go.getClass().getCanonicalName());
		}
	}

	public void remove(String name) {
		objects.remove(name);
	}

	/**
	 * retrieve all GameObject which name contains filterName.
	 *
	 * @param filterName the string to filter objects on.
	 * @return a filtered list of GameObject.
	 */
	public List<GameObject> getFilteredObjectList(String filterName) {
		List<GameObject> filtered = new ArrayList<>();
		for (GameObject o : objects.values()) {
			if (o.name.contains(filterName)) {
				filtered.add(o);
			}
		}
		return filtered;
	}

	/**
	 * Update the GameObject duration and life status. If the duration object<0 then
	 * this object will be deleted.
	 *
	 * @param go
	 */
	public void updateObject(Game game, GameObject go, double elapsed) {
		if (go.enable) {
			// Update object
			go.update(game, elapsed);
			
			computeDuration(go, elapsed);
		}
	}

	/**
	 * Compute GameObject duration and estimate if it's displayed.
	 * 
	 * @param go      GameObject to be tested
	 * @param elapsed elapsed time since previous call.
	 */
	private void computeDuration(GameObject go, double elapsed) {
		// Compute Life duration for this GameObject.
		if (go.displayed && go.duration > 0.0) {
			go.duration -= elapsed;
			if (go.duration <= 0.0) {
				go.duration = 0.0;
				go.displayed = false;
				log.debug("the GameObject named '{}' is no more displayed", go.name);
			}
		}
	}

	/**
	 * Return a name's filtered list of GameObject.
	 * 
	 * @param filterClassName the name of the class to filter objects on.
	 * @return a filtered list of GameObject.
	 */
	public List<GameObject> getFilteredObjectList(Class<?> filterClassName) {
		List<GameObject> filtered = new ArrayList<>();
		for (GameObject o : objects.values()) {
			if (o.getClass().equals(filterClassName)) {
				filtered.add(o);
			}
		}
		return filtered;
	}

	/**
	 * Retrieve a specific named object in the map.
	 *
	 * @param name the name of the object to be retrieved.
	 * @return the corresponding GameObject
	 */
	public GameObject get(String name) {
		return objects.get(name);
	}

	/**
	 * Add all object from this map to the Object management system.
	 * 
	 * @param child
	 */
	public void putAll(Map<String, GameObject> objects) {
		for (GameObject go : objects.values()) {
			add(go);
		}

	}

	/**
	 * Add a bunch of object to the object management system.
	 *
	 * @param objects the list of core.object.GameObject to be added to the
	 *                core.Game#objects list.
	 */
	public void addAll(Collection<GameObject> objects) {
		for (GameObject o : objects) {
			add(o);
		}
	}

	/**
	 * remove one specific GameObject from object manager.
	 * 
	 * @param go the game object to be removed from Management.
	 */
	public void removeObject(GameObject go) {
		objects.remove(go.name);
		game.renderer.remove(go);
	}

	/**
	 * remove one specific GameObject on its name from object manager.
	 * 
	 * @param name name of the object to be removed.
	 */
	public void removeObject(String name) {
		if (objects.containsKey(name)) {
			GameObject go = objects.get(name);
			removeObject(go);
		}
	}

	/**
	 * Remove all object in list from the object manager.
	 * 
	 * @param objectsToBeRemoved
	 */
	public void removeAllObjects(List<GameObject> objectsToBeRemoved) {
		game.renderer.removeAll(objectsToBeRemoved);
		objects.values().removeAll(objectsToBeRemoved);
	}

	/**
	 * Remove all GameObject with name containing nameFilter.
	 * 
	 * @param nameFilter the filtering name to be applied.
	 */
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
	 * Remove all objects from object management.
	 */
	public void clear() {
		objects.clear();

	}

	/**
	 * Retrieve all object from object management.
	 * 
	 * @return
	 */
	public Collection<GameObject> getAll() {
		return objects.values();
	}

	/**
	 * Test if object management contains an object with the specific
	 * <code>name</code>.
	 * 
	 * @param name
	 * @return
	 */
	public boolean contains(String name) {
		return objects.containsKey(name);
	}

}
