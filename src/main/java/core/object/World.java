/**
 *
 */
package core.object;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Game;
import core.math.Vector2D;
import lombok.Data;

/**
 * A World object containing anything from the world.
 *
 * @author Frédéric Delorme
 *
 */
@Data
public class World {

    @SuppressWarnings("unused")
	private Game game;

    private Map<String, Vector2D> forces = new ConcurrentHashMap<>();

    private Vector2D gravity = new Vector2D(0.0f, 9.81f);

    public World(Game game) {
        this.game = game;
        this.forces.put("gravity",gravity);
    }

    public Collection<Vector2D> getForces() {
        return forces.values();
    }

    public void addForce(Vector2D f) {
        forces.put(f.getName(), f);
    }

    public void removeForce(Vector2D f) {
        forces.remove(f.getName());
    }

    public void removeForce(String n) {
        forces.remove(n);
    }

    public void setGravity(Vector2D g) {
        this.gravity = g;
    }

    public Vector2D getGravity() {
        return gravity;
    }

    public Vector2D getForce(String name){
        return forces.get(name);
    }

	public boolean containsForce(String forceName) {
		return forces.containsKey(forceName);
	}

}
