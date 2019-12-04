/**
 * McGivrer / Snapgames / prototype
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * @year 2019
 */
package core.system;

import core.Game;

/**
 * The AbstractSystem class will implment the default behavior for any
 * {@link System}
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * @since 2019
 */
public abstract class AbstractSystem implements System {

    /**
     * The parent {@link Game} object the {@link System} attached to.
     */
    protected Game game;

    /**
     * Build the system attached to the parent game.
     *
     * @param game the parent game for this system.
     */
    protected AbstractSystem(Game game) {
        this.game = game;
    }

    /**
     * return the name for this system.
     *
     * @return name of the system.
     */
    public abstract String getName();

}