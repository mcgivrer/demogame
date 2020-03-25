package samples;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * The <code>GameSystemManager</code> class will ensure the good management of all
 * game's systems.
 * <p>
 * Developer will be able to create `System` based interface to provide any
 * service to the game. It would extends the <code>AbstractSystem</code> and
 * extends the <code>System</code> interface.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @see System
 * @see @AbstractSystem
 */
@Slf4j
public class GameSystemManager {
    /**
     * list of systems to be managed.
     */
    private static Map<Class<?>, AbstractGameSystem> systems = new HashMap<>();
    /**
     * the parent game.
     */
    private Sample game;

    /**
     * Start the Game System Manager.
     *
     * @param game the parent game for all systems.
     */
    private GameSystemManager(Sample game) {
        this.game = game;
    }

    /**
     * Initialize the System Manager.
     *
     * @param game
     * @return
     */
    public static GameSystemManager initialize(Sample game) {
        return new GameSystemManager(game);
    }

    /**
     * retrieve a System on its implementation class Name.
     *
     * @param <T>        the Type of the system to be retrieved.
     * @param systemName The name of the system to retrieve.
     * @return the <T> instalce for the systemName system.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractGameSystem> T get(Class<T> systemName) {
        return (T) systems.get(systemName);
    }

    /**
     * Add a System to the manager.
     *
     * @param s the system to be managed.
     */
    public void add(AbstractGameSystem s) {
        if (s != null) {
            Class<? extends AbstractGameSystem> systemType = s.getClass();
            systems.put(systemType, s);
            log.debug("Add system {}", s.getName());
            s.initialize(game);
            log.debug("System {} initialized.", s.getName());
        }
    }

    /**
     * Retrieve a System on its class name.
     *
     * @param <T>        the System implementation type
     * @param systemName the name of the system to be retrieved.
     * @return the <T> system instance.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractGameSystem> T getSystem(Class<T> systemName) {
        log.debug("retrieve system {}", systemName.getCanonicalName());
        return (T) systems.get(systemName);
    }

    public void dispose() {
        for (GameSystem s : systems.values()) {
            log.debug("disposing system {}", s.getName());
            s.dispose();
        }
    }
}
