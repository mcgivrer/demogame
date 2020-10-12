package core.behaviors;

import java.awt.Graphics2D;

import core.Game;
import core.object.GameObject;
import core.object.World;

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
    void initialize(Game dg);

    /**
     * this is where the GameObject entity creation must take place.
     * 
     * @param dg
     * @param w
     * @param go
     */
    void create(Game dg, World w, GameObject go);

    /**
     * manage input for this object.
     * 
     * @param dg
     * @param go
     */
    void input(Game dg, GameObject go);

    /**
     * The GameObjet update phase.
     * 
     * @param dg
     * @param go
     * @param elapsed
     */
    void update(Game dg, GameObject go, double elapsed);

    /**
     * Specific Render for this object
     * 
     * @param dg
     * @param go
     * @param g
     * 
     */
    void render(Game dg, GameObject go, Graphics2D g);

    /**
     * release resources for this object
     * 
     * @param dg
     * @param w
     * @param go
     * 
     */
    void dispose(Game dg, World w, GameObject go);

}