package core.state;

import core.Game;
import core.gfx.Renderer;
import core.object.Camera;
import core.object.GameObject;

import java.awt.*;
import java.util.Collection;
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

    void update(Game g, double elapsed);

    void render(Game g, Renderer r,double elapsed);

    void dispose(Game g);

    void onFocus(Game g);

    void lostFocus(Game g);

    Camera getActiveCamera();

    void drawHUD(Game ga, Renderer r, Graphics2D g);

    void setGame(Game g);

}
