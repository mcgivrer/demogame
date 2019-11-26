package core.state;

import core.Game;
import core.Renderer;
import core.object.Camera;
import core.object.GameObject;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * The `State` interface define all steps to manage a game play loop.
 * The `StateManager` wil call all the steps in this order :
 * <p>
 * ```Text
 * - load(Game)
 * - initialize(Game)
 * - loop: (main looping from Game class)
 * - input(Game)
 * - update(Game, float)
 * - render(Game, Renderer)
 * - dispose(Game)
 * ```
 *
 * @author Frédéric Delorme<frederic.delorme@gmailcom>
 * @since 2019
 */
public interface State {

    String getName();

    void load(Game g);

    boolean isLoaded();

    void initialize(Game g);

    void input(Game g);

    void update(Game g, float elapsed);

    void render(Game g, Renderer r);

    void dispose(Game g);

    void onFocus(Game g);

    void focusLost(Game g);

    Camera getActiveCamera();

    public Map<String, GameObject> getObjects();

    void drawHUD(Game ga, Renderer r, Graphics2D g);

    void addObject(GameObject go);

    void addAllObject(Collection<GameObject> objects);

    void removeObject(GameObject go);

    void removeObject(String name);

    void removeFilteredObjects(String nameFilter);

    void setGame(Game g);

}
