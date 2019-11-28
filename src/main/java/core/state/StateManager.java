package core.state;

import com.google.gson.Gson;
import core.Game;
import core.ResourceManager;
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
    private Map<String, State> states = new HashMap<>();

    /**
     * Current active State.
     */
    private State current;

    /**
     * Create the StateManager for the Game.
     *
     * @param g the parent game.
     */
    public StateManager(Game g) {
        super(g);
        load(g.config.statesPath);
    }


    /**
     * Load all states configurationand defnition from <code>game.json</code> file.
     *
     * @param path path to the game configuration JSON file.
     */
    public void load(String path) {
        try {
            String gameStates = ResourceManager.getString("/res/game.json");
            Gson gs = new Gson();
            StatesMap statesMap = gs.fromJson(gameStates, StatesMap.class);
            for (Entry<String, String> stateItem : statesMap.states.entrySet()) {
                Class<State> cs = (Class<State>) Class.forName(stateItem.getValue());
                State s = cs.newInstance();
                s.setGame(game);
                states.put(stateItem.getKey(), s);
                log.info("load state {}", stateItem.getKey());
            }
            activate(statesMap.defaultState);

        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            log.info("Unable to create class ", e);
        }
    }


    public void activate(String s) {
        if(current!=null){
            current.focusLost(game);
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

    public int initialize(Game g) {
        log.debug("StateManager system initialized");
        return 0;
    }

    public void startState(Game g) {
        if (current != null && current.isLoaded()) {
            current.initialize(g);
            log.debug("{} state started", this.current.getName());
        }
    }

    @Override
    public void dispose() {

    }

    public void load(Game g) {
        current.load(g);
    }

    public void input(Game g) {
        current.input(g);
    }

    public void update(Game g, float elapsed) {
        current.update(g, elapsed);
    }

    public void render(Game g, Renderer r, double elapsed) {
        current.render(g, r, elapsed);
    }

    public void dispose(Game g) {
        current.dispose(g);
    }

    public void release(Game g) {
        for (State s : states.values()) {
            s.dispose(g);
        }
    }

    public State getCurrent() {
        return current;
    }

}
