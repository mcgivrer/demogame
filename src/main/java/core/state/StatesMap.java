package core.state;

import java.util.Map;

/**
 * Class corresponding to the JSON Structure game.json.
 *
 * <pre>
 * {
 *   "defaultState": "game",
 *   "states": {
 *     "game": "demo.states.DemoState"
 *   }
 * }
 * </pre>
 *
 * @author Frédéric Delorm<frederic.delorme@gmail.com>
 */
public class StatesMap {
    /**
     * The default state to be activated at start.
     */
    public String defaultState = "";
    /**
     * List of all existing state for the game.
     */
    public Map<String, String> states;
}
