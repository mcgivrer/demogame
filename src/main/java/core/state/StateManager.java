package core.state;

import com.google.gson.Gson;
import core.Game;
import core.resource.ResourceManager;
import core.gfx.Renderer;
import core.system.AbstractSystem;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The class StateManager manege all states for the parent game.
 *
 * @author Frédéric Delorme<fredeic.delorme@gmail.com>
 * @see State
 * @see AbstractState
 */
@Slf4j
public class StateManager extends AbstractSystem implements System {

    /**
     * List of all states managed for the game.
     */
    private final Map<String, State> states = new HashMap<>();

    /**
     * Current active State.
     */
    private State current;

    /**
     * Create the StateManager for the Game.
     *
     * @param g the parent game.
     */
    public StateManager(final Game g) {
        super(g);
        loadFromFile(g.config.statesPath);
    }


    /**
     * Load all states configuration and definition from <code>game.json</code> file.
     *
     * @param path path to the game configuration JSON file.
     */
    public void loadFromFile(final String path) {
        try {
            final String gameStates = ResourceManager.getString("/res/game.json");
            final Gson gs = new Gson();
            final StatesMap statesMap = gs.fromJson(gameStates, StatesMap.class);
            for (final Entry<String, String> stateItem : statesMap.states.entrySet()) {
                final Class<State> cs = (Class<State>) Class.forName(stateItem.getValue());
                final State s = cs.newInstance();
                s.setGame(game);
                states.put(stateItem.getKey(), s);
                log.info("load state {}", stateItem.getKey());
            }
            activate(statesMap.defaultState);
        } catch (IllegalAccessException 
                | InstantiationException 
                | ClassNotFoundException e) {
            log.info("Unable to create class", e);
        }
    }

    public void activate(final String s) {
        if(current!=null){
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
        return StateManager.class.getCanonicalName();
    }

    public int initialize(final Game g) {
        log.debug("StateManager system initialized");
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

    public void update(final Game g, final float elapsed) {
        current.update(g, elapsed);
    }

    public void render(final Game g, final Renderer r, final float elapsed) {
        current.render(g, r, elapsed);
    }

    public void dispose(final Game g) {
        current.dispose(g);
    }

    public void release(final Game g) {
        for (final State s : states.values()) {
            s.dispose(g);
        }
    }

    public State getCurrent() {
        return current;
    }

}