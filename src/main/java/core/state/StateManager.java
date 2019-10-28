package core.state;

import com.google.gson.Gson;
import core.Game;
import core.Renderer;
import core.ResourceManager;
import core.system.AbstractSystem;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class StateManager extends AbstractSystem implements System {


    private Map<String, State> states = new HashMap<>();

    private State current;


    public StateManager(Game g) {
        super(g);
        load(g.config.statesPath);
    }

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
        current = states.get(s);
        if (!current.isLoaded()) {
            current.load(game);
            log.debug("activate state {}", s);
        }
    }

    @Override
    public String getName() {
        return StateManager.class.getCanonicalName();
    }

    public int initialize(Game g) {
        current.initialize(g);
        return 0;
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

    public void render(Game g, Renderer r) {
        current.render(g, r);
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
