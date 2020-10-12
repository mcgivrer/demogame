package core.scene;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import core.Game;
import core.gfx.Renderer;
import core.resource.ResourceManager;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * The class SceneManager manege all states for the parent game.
 *
 * @author Frédéric Delorme<fredeic.delorme@gmail.com>
 * @see Scene
 * @see AbstractScene
 */
@Slf4j
public class SceneManager extends AbstractSystem {

    /**
     * List of all states managed for the game.
     */
    private final Map<String, Scene> states = new HashMap<>();

    /**
     * Current active State.
     */
    private Scene current;

    /**
     * Create the SceneManager for the Game.
     *
     * @param g the parent game.
     */
    public SceneManager(final Game g) {
        super(g);
        loadFromFile(g.config.statesPath);
    }

    /**
     * Load all states configuration and definition from <code>game.json</code>
     * file.
     *
     * @param path path to the game configuration JSON file.
     */
    public void loadFromFile(final String path) {
        try {
            final String gameScenes = ResourceManager.getString(path);
            final Gson gs = new Gson();
            final ScenesMap scenesMap = gs.fromJson(gameScenes, ScenesMap.class);
            for (final Entry<String, String> stateItem : scenesMap.scenes.entrySet()) {
                final Class<?> cs = Class.forName(stateItem.getValue());
                final Constructor<?> sceneConstructor = cs.getConstructor(new Class[] { Game.class });
                final Scene s = (Scene) sceneConstructor.newInstance(game);
                states.put(stateItem.getKey(), s);
                log.info("load state {}", stateItem.getKey());
            }
            activate(scenesMap.defaultScene);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.info("Unable to create class", e);
        }
    }

    public void activate(final String s) {
        if (current != null) {
            current.lostFocus(game);
        }
        current = states.get(s);
        if (!current.isLoaded()) {
            current.load(game);
            log.debug("activate state {}", s);
        }
        current.onFocus(game);
    }

    @Override
    public String getName() {
        return SceneManager.class.getCanonicalName();
    }

    public int initialize(final Game g) {
        log.debug("SceneManager system initialized");
        return 0;
    }

    public void startState(final Game g) {
        if (current != null && current.isLoaded()) {
            current.initialize(g);
            log.debug("{} state started", this.current.getName());
        }
    }

    @Override
    public void dispose() {

    }

    public void load(final Game g) {
        current.load(g);
    }

    public void input(final Game g) {
        current.input(g);
    }

    public void update(final Game g, final double elapsed) {
        current.update(g, elapsed);
    }

    public void render(final Game g, final Renderer r, final double elapsed) {
        current.render(g, r, elapsed);
    }

    public void dispose(final Game g) {
        current.dispose(g);
    }

    public void release(final Game g) {
        for (final Scene s : states.values()) {
            s.dispose(g);
        }
    }

    public Scene getCurrent() {
        return current;
    }

}