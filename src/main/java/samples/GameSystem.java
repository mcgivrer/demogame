package samples;

/**
 * Interface to define and manage a new System for the game.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @see core.system.SystemManager
 */
public interface GameSystem {
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
    int initialize(Sample game);

    /**
     * Dispose of all resources of the system.
     */
    void dispose();
}