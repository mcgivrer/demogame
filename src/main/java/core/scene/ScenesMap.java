package core.scene;

import java.util.Map;

/**
 * Class corresponding to the JSON Structure game.json.
 *
 * <pre>
 * {
 *   "defaultscene": "game",
 *   "scenes": {
 *     "game": "demo.scenes.Demoscene"
 *   }
 * }
 * </pre>
 *
 * @author Frédéric Delorm<frederic.delorme@gmail.com>
 */
public class ScenesMap {
    /**
     * The default scene to be activated at start.
     */
    public String defaultScene = "";
    /**
     * List of all existing scene for the game.
     */
    public Map<String, String> scenes;
}
