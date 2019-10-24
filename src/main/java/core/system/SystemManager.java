/**
 * McGivrer / Snapgames / prototype
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * @year 2019
 */
package core.system;

import core.Game;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * The <code>SystemManager</code> class will ensure the good management of all
 * game's systems.
 * <p>
 * Developer will be able to create `System` based interface to provide any
 * service to the game. It would extends the <code>AbstractSystem</code> and
 * extends the <code>System</code> interface.
 *
 * @author Frédéric Delorme
 * @see System
 * @see @AbstractSystem
 */
@Slf4j
public class SystemManager {
    private static Map<Class<?>, AbstractSystem> systems = new HashMap<>();

    Game game;

    /**
     * Start the Game System Manager.
     *
     * @param game the parent game for all systems.
     */
    private SystemManager(Game game) {
        this.game = game;
    }

    /**
     * Initialize the System Manager.
     *
     * @param game
     * @return
     */
    public static SystemManager initialize(Game game) {
        return new SystemManager(game);
    }

    /**
     * retrieve a System on its implementation class Name.
     *
     * @param <T>        the Type of the system to be retrieved.
     * @param systemName The name of the system to retrieve.
     * @return the <T> instalce for the systemName system.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractSystem> T get(Class<T> systemName) {
        return (T) systems.get(systemName);
    }

    /**
     * Add a System to the manager.
     *
     * @param s the system to be managed.
     */
    public void add(AbstractSystem s) {
        Class<? extends AbstractSystem> systemtype = s.getClass();
        systems.put(systemtype, s);
        s.initialize(game);
        log.debug("Add system {}", s.getName());
    }

    /**
     * Retrieve a System on its class name.
     *
     * @param <T>        the System implmentation type
     * @param systemName the name of the system to be retrieved.
     * @return the <T> system instance.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractSystem> T getSystem(Class<T> systemName) {
        return (T) systems.get(systemName);
    }

}
