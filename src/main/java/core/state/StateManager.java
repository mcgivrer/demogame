package core.state;

import com.google.gson.Gson;
import core.Game;
import core.Renderer;
import core.ResourceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StateManager {

    private final Game game;
    private Map<String, State> states = new HashMap<>();

    private State current;


    public StateManager(Game g) {
        this.game = g;
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
                states.put(stateItem.getKey(), s);
            }
            activate(statesMap.defaultState);

        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.out.println("Unable to create class " + e.getMessage());
        }
    }


    public void activate(String s) {
        current = states.get(s);
        if (!current.isLoaded()) {
            current.load(game);
        }
    }

    public void initialize(Game g) {
        current.initialize(g);
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
