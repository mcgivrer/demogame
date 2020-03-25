package core.system;

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
    int initialize(System game);

    /**
     * Dispose of all resources of the system.
     */
    void dispose();
}
