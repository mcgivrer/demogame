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

	private static final double TIME_SCALE_FACTOR = 0.05;
	private static final double VELOCITY_THRESHOLD_MIN = 0.001;

	private Scene scene;
	private World world;

	private List<GameObject> objects;

	/**
	 * Create the Physic engine to process objetcs.
	 * 
	 * @param game the parent game instance
	 */
	public PhysicEngineSystem(final Game game, final World world) {
		super(game);
		this.world = world;
	}

	@Override
	public int initialize(final Game game) {
		if (objects == null) {
			objects = new ArrayList<>();
		}
		objects.clear();
		return 0;
	}

	/**
	 * Compute position, speed and acceleration for object of the scene.
	 * 
	 * @param game    the parent Game object
	 * @param scn     the Scene containing all the objects
	 * @param elapsed the elapsed time since previous call.
	 */
	public void update(final Game game, final Scene scn, final double elapsed) {
		this.scene = scn;

		objects.forEach(o -> {
			update(game, o, elapsed);
		});

	}

	public void update(final Game game, final GameObject o, final double elapsed) {
		// Process Camera or other object update
		if (o instanceof Camera) {

			// This is a camera object, need to be updated !
			((Camera) o).update(game, elapsed);

		} else if (o != null && o.pos != null && o.vel != null) {

			// This is a standard obect, must be updated.
			final Vector2D oldPosition = o.pos;
			Vector2D nextPosition = o.pos;
			Vector2D speed = o.vel;
			Vector2D acceleration = o.acc;
			final Vector2D objectGameSpeed = o.vel;
			final double friction = o.material.friction;
			final double mass = o.mass;

			final Vector2D vForces = new Vector2D(0.0f, 0.0f);

			switch (o.physicType) {
				case DYNAMIC:

					final double t = elapsed * TIME_SCALE_FACTOR;
					vForces.addAll(o.forces);
					vForces.addAll(world.getForces());

					acceleration = acceleration.add(world.getGravity());
					acceleration = acceleration.add(vForces);

					acceleration = acceleration.multiply(1.0 / o.getMass()).multiply(t);

					// TODO add contact detection
					if (o.isContact) {
						acceleration = acceleration.multiply(o.getMaterial().friction);
					}

					speed = speed.add(acceleration.multiply(t * t)).threshold(1f);
					nextPosition = nextPosition.add(speed.multiply(0.5f * t)).threshold(1);
					break;

				case KINETIC:
					// TODO add contact detection

					if (o.isContact) {
						speed = speed.multiply(friction);
					}
					speed = speed.threshold(VELOCITY_THRESHOLD_MIN);
					// objectGameSpeed = speed.multiply(game.getGameSpeed());
					nextPosition = nextPosition.add(objectGameSpeed);

					break;
				case STATIC:
					// NOTHING To DO !
					break;
			}
			// Set next position, speed and acceleration.
			o.acc = acceleration;
			o.vel = speed;
			o.newPos = nextPosition;
			o.forces.clear();
		}
	}

	@Override
	public String getName() {
		return "physic_engine";
	}

	public void run() {

	}

	/**
	 * Add a new GameObject to the update system.
	 */
	public void add(final GameObject o) {
		if (!objects.contains(o)) {
			objects.add(o);
			if (!o.child.isEmpty()) {
				o.child.values().forEach(go -> {
					objects.add(go);
				});
			}
		}
	}

	@Override
	public void dispose() {

	}

	public World getWorld() {
		return world;
	}
}
