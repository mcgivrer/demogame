package core.object;

import core.Game;
import core.map.MapObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Any object displayed by the game.
 */
public class GameObject {

    public enum GameAction {
        IDLE,
        IDLE2,
        WALK,
        RUN,
        FALL,
        JUMP,
        UP,
        DOWN,
        DEAD1,
        DEAD2;
    }

    private static int goIndex = 0;
    private final int id = (int) goIndex++;

    public String name = "noname_" + id;

    public BufferedImage image;

    public boolean enable = true;

    public float x, y;
    public float oldX, oldY;
    public float width, height;

    public float dx = 0, dy = 0;

    public int direction = 1;
    public GameAction action = GameAction.IDLE;

    public int layer = 0;
    public int priority = 0;

    public int debugLevel = 0;

    public boolean canCollect;

    public GameObjectType type;

    public BBox bbox;

    public Color foregroundColor = Color.RED;
    public Color backgroundColor = Color.BLACK;

    public Map<String, Object> attributes = new HashMap<>();
    public List<MapObject> items = new ArrayList<>();

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
    public GameObject(String name, float x, float y, float width, float height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = GameObjectType.RECTANGLE;
    }

    /**
     * update the object (on all its characteristics, not only position if needed)
     *
     * @param dg      the core.Game containing the object.
     * @param elapsed the elapsed time since previous call.
     */
    public void update(Game dg, float elapsed) {
        oldX = x;
        oldY = y;
        switch (action) {
            case IDLE:
            case IDLE2:
                dy = 0.0f;
                dx = 0.0f;
            case WALK:
                x += (dx * elapsed);
                break;
            case RUN:
                x += (dx * 2.0f * elapsed);
                break;
            case FALL:
                y += (0.2f * elapsed);
                break;
            case DOWN:
                y += (dy * elapsed);
                break;
            case JUMP:
                y += dy*elapsed;
                break;
        }
        bbox.x = x;
        bbox.y = y;
    }

    /**
     * Rendering of the object (will be delegated to another component in a next
     * version.
     *
     * @param dg the core.Game containing the object.
     * @param g  the graphics API.
     */
    public void render(Game dg, Graphics2D g) {
        switch (type) {
            case RECTANGLE:
                g.setColor(this.foregroundColor);
                g.fillRect((int) x, (int) y, (int) width, (int) height);
                break;
            case CIRCLE:
                g.setColor(this.foregroundColor);
                g.fillOval((int) x, (int) y, (int) width, (int) height);
                break;
            case IMAGE:
                if (direction < 0) {
                    g.drawImage(image, (int) (x + width)-4, (int) y, (int) (-width), (int) height, null);
                } else {
                    g.drawImage(image, (int) x-4, (int) y, (int) width, (int) height, null);
                }
                break;

        }
    }

    /*------- Setters ---------------*/
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bbox.fromGameObject(this);
    }

    public void setSpeed(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
        bbox.fromGameObject(this);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        bbox.fromGameObject(this);
    }

}