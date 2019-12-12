package core.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Game;
import core.collision.MapTileCollision;
import core.gfx.Animation;
import core.gfx.Renderer;
import core.map.MapObject;
import lombok.Data;
import lombok.ToString;

/**
 * Any object displayed by the game. the GameObject will provide most operations
 * of the game loop steps:
 * <ul>
 * <li><code>update(Game,double)</code> will update the Object status</li>
 * <li><code>render(Game,Graphics2D)</code> draw all about this GameObject</li>
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 */
@Data
@ToString
public class GameObject {

    public GameAction action = GameAction.IDLE;

    private static int goIndex = 0;
    private final int id = (int) goIndex++;

    public String name = "noname_" + id;

    public BufferedImage image;

    public boolean enable = true;

    public double x;
    public double y;
    public double oldX;
    public double oldY;
    public double width;
    public double height;

    public double dx = 0;
    public double dy = 0;

    public int direction = 1;

    public int layer = 0;
    public int priority = 0;

    public int debugLevel = 0;

    public boolean canCollect;

    public boolean fixed = false;

    public GameObjectType type;

    public BBox bbox;

    public double duration = -1;

    public Map<GameAction, Animation> animations = new HashMap<>();

    public Color foregroundColor = Color.RED;
    public Color backgroundColor = Color.BLACK;

    public Map<String, Object> attributes = new HashMap<>();

    public List<MapObject> items = new ArrayList<>();

    public Map<String, GameObject> child = new HashMap<>();

    public List<MapTileCollision> collidingZone = new ArrayList<>();

    /**
     * If the object is active it will be processed as other, but not rendered.
     */
    public boolean active = true;

    /**
     * Create a new GameObject with some default values.
     */
    public GameObject() {
        this.name = "gameObjectName";
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.type = GameObjectType.RECTANGLE;
        bbox = new BBox(x, y, width, height);
    }

    /**
     * Create a new object in the game with its position <code>(x,y)</code> and size
     * <code>(width,height)</code>.
     *
     * @param name   Name of this object.
     * @param x      horizontal position
     * @param y      vertical position
     * @param width  width of the object (if no image set)
     * @param height height of the object (if no image set)
     */
    public GameObject(String name, double x, double y, double width, double height) {
        this();
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = GameObjectType.RECTANGLE;
        bbox.fromGameObject(this);
    }

    /**
     * update the object (on all its characteristics, not only position if needed)
     *
     * @param dg      the core.Game containing the object.
     * @param elapsed the elapsed time since previous call.
     * @see core.state.State#update(Game, double)
     */
    public void update(Game dg, double elapsed) {
        oldX = x;
        oldY = y;
        // compute action and move to be performed.
        switch (action) {
            case IDLE:
            case IDLE2:
                dy = 0.0f;
                dx = 0.0f;
                break;
            case WALK:
                x += (dx * elapsed);
                break;
            case RUN:
                x += (dx * 2.0f * elapsed);
                break;
            case FALL:
            case DOWN:
                y += (dy * elapsed);
                break;
            case JUMP:
                y += (dy*3 * elapsed);
            case UP:
            	
                break;
			default:
				break;
        }
        // update the bounding box for this GameObject
        if (bbox != null) {
            bbox.fromGameObject(this);
        }
        // Compute Life duration for this GameObject.
        if (duration > -1) {
            duration -= elapsed;
            if (duration < 0) {
                duration = 0;
            }
        }
    }

    /**
     * Rendering of the object (will be delegated to another component in a next
     * version. this method will be called by the Renderer.
     *
     * @param dg the core.Game containing the object.
     * @param g  the graphics API.
     * @see Renderer#render(Game, double)
     */
    public void render(Game dg, Graphics2D g) {

    }

    public enum GameAction {
        IDLE, IDLE2, WALK, RUN, FALL, JUMP, UP, DOWN, DEAD1, DEAD2;
    }

    /*------- Setters ---------------*/
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        bbox.fromGameObject(this);
    }

    public void setSpeed(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        bbox.fromGameObject(this);
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        bbox.fromGameObject(this);
    }

}