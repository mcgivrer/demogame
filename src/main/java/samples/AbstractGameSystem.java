package samples;

import core.Game;

/**
 * The AbstractSystem class will implment the default behavior for any
 * {@link System}
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * @since 2019
 */
public abstract class AbstractGameSystem implements GameSystem {

    /**
     * The parent {@link Game} object the {@link System} attached to.
     */
    protected Sample game;

    /**
     * Build the system attached to the parent game.
     *
     * @param game the parent game for this system.
     */
    protected AbstractGameSystem(Sample game) {
        this.game = game;
    }

    /**
     * return the name for this system.
     *
     * @return name of the system.
     */
    public abstract String getName();

    /**
     * Initialize the parent game attribute.
     */
    @Override
    public int initialize(Sample game) {
        this.game = game;
        return 1;
    }

}