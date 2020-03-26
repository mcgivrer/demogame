package core.system;

import core.Game;

/**
 * Interface to define and manage a new System for the game.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @see core.system.SystemManager
 */
public interface System {
    /**
     * return the name of the system.
     *
     * @return
     */
    String getName();

    /**
     * Initialize the system.
     *
     * @param game
     * @return
     */
    int initialize(Game game);

    /**
     * Dispose of all resources of the system.
     */
    void dispose();
}
