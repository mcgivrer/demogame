/**
 * 
 */
package core.math;

import java.util.ArrayList;
import java.util.List;

import core.Game;
import core.object.Camera;
import core.object.GameObject;
import core.object.World;
import core.scene.Scene;
import core.system.AbstractSystem;

/**
 * <p>
 * This is the Physic Engine computation system for the basic 3 kind of
 * GameObject moves.
 * <p>
 * This class will take in account:
 * <ul>
 * <li><code>STATIC</code> object are moving without any collision or limitation
 * but their own update() move,</li>
 * <li><code>KINETIC</code> object moves will be computed according to only
 * speed parameter,</li>
 * <li><code>DYNAMIC</code> object moves will be computed accord with a full
 * physic simulation according to some internal limitations.</li>
 * </ul>
 * 
 * @author Frédéric Delorme<frederic.Delorme@gmail.com>
 */
public class PhysicEngineSystem extends AbstractSystem {

	public enum PhysicType {
		STATIC, // object move without collision and limitation
		KINETIC, // object moves only on speed attributes with collision detection
		DYNAMIC // object will move according to a full physic simulation.
	}

	private static final double TIME_ACCURATY = 0.05;
	private static final double VELOCITY_THRESHOLD_MIN = 0.001;

	private Scene state;
	private World world;

	private List<GameObject> objects = new ArrayList<>();

	/**
	 * Create the Physic engine to process objetcs.
	 * 
	 * @param game the parent game instance
	 */
	public PhysicEngineSystem(Game game, World world) {
		super(game);
		this.world = world;
	}

	/**
	 * Compute position, speed and acceleration for object of the scene.
	 * 
	 * @param game    the parent Game object
	 * @param scn     the Scene containing all the objects
	 * @param elapsed the elapsed time since previous call.
	 */
	public void update(Game game, Scene scn, double elapsed) {
		this.state = scn;

		for (GameObject o : scn.getObjectManager().objects.values()) {
			// Process Camera or other object update
			if (o instanceof Camera) {

				// This is a camera object, need to be updated !
				((Camera) o).update(game, elapsed);

			} else if (o != null && o.pos != null && o.vel != null) {

				// This is a standard obect, must be updated.
				Vector2D position = o.pos;
				Vector2D nextPosition = o.newPos;
				Vector2D speed = o.vel;
				Vector2D acceleration = o.acc;
				Vector2D objectGameSpeed = o.vel;
				double friction = o.material.friction;
				double mass = o.mass;

				Vector2D vForces = new Vector2D(0.0f, 0.0f);

				switch (o.physicType) {
					case DYNAMIC:

						double t = elapsed;
						vForces.addAll(o.forces);
						vForces.addAll(world.getForces());

						// acceleration = acceleration.add(game.getWorld().getGravity());
						acceleration = acceleration.add(vForces);

						acceleration = acceleration.multiply(1.0 / o.getMass()).multiply(elapsed);

						// TODO add contact detection
						if (o.isContact) {
							acceleration = acceleration.multiply(o.getMaterial().friction);
						}

						speed = speed.add(acceleration.multiply(t * t)).threshold(1f);
						nextPosition = position.add(speed.multiply(0.5f * t)).threshold(1);
						break;

					case KINETIC:
						// TODO add contact detection

						if (o.isContact) {
							speed = speed.multiply(friction);
						}
						speed = speed.threshold(VELOCITY_THRESHOLD_MIN);
						// objectGameSpeed = speed.multiply(game.getGameSpeed());
						nextPosition = position.add(objectGameSpeed);

						break;
					case STATIC:

						break;
				}
				// Set next position, speed and acceleration.
				o.acc = acceleration;
				o.vel = speed;
				o.newPos = nextPosition;
			}
		}

	}

	@Override
	public String getName() {
		return "physic_engine";
	}

	public void run() {

	}

	@Override
	public int initialize(Game game) {
		return 0;
	}

	/**
	 * Add a new GameObject to the update system.
	 */
	public void add(GameObject o) {
		if (!objects.contains(o)) {
			objects.add(o);
		}
	}

	@Override
	public void dispose() {

	}

	public World getWorld() {
		return world;
	}
}