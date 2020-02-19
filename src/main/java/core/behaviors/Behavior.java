package core.behaviors;

import java.awt.Graphics2D;

import core.Game;
import core.object.GameObject;
import core.object.World;
import core.resource.ResourceManager;

/**
 * The Behavior interface to enhance GameObject at instantiation time.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 1.0
 *
 */
public interface Behavior {

	/**
	 * This Behavior phase is called to intialize all needed resources.
	 * 
	 * @param dg
	 */
	void initialize(Game dg, ResourceManager rm);

	/**
	 * this is where the GameObject entity creation must take place.
	 * 
	 * @param dg
	 * @param w
	 * @param go
	 */
	void create(Game dg, World w, GameObject go);

	/**
	 * The GameObjet update phase.
	 * 
	 * @param dg
	 * @param go
	 * @param elapsed
	 */
	void update(Game dg, GameObject go, double elapsed);

	void render(Game dg, GameObject go, Graphics2D g);

	void dispose(Game dg, World w, GameObject go);

}
