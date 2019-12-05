/**
 * SnapGames
 * 
 * Game Development Java
 * 
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
 * @author Frédéric Delorme
 * @since 2019
 */
@Slf4j
public class ObjectManager extends AbstractSystem implements System {

	public Map<String, GameObject> objects = new ConcurrentHashMap<>();

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
	 * 
	 * @param filterClassName the name of the class to filter objects on.
	 * @returna filtered list of GameObject.
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

	public void putAll(Map<String, GameObject> child) {
		objects.putAll(child);

	}

	/**
	 * Add a bunch of object to the game !
	 *
	 * @param objects the list of core.object.GameObject to be added to the
	 *                core.Game#objects list.
	 */
	public void addAllObject(Collection<GameObject> objects) {
		for (GameObject o : objects) {
			add(o);
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

	public void clear() {
		objects.clear();
		
	}

	public Object getAll() {
		return objects.values().toArray();
	}
	
	public boolean contains(String name) {
		return objects.containsKey(name);
	}

}
