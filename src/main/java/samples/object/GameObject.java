package samples.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import samples.Sample;
import samples.collision.BoundingBox;
import samples.collision.Collidable;

/**
 * The GameObject to animate, display and process all game entities.
 */
public class GameObject implements Collidable {

    public enum GameObjectType {
        POINT, LINE, RECT, ELLIPSE, IMAGE, OTHER;
    }

    public static int index = 0;
    public int id;
    public String name;
    public double x = 0;
    public double y = 0;
    public double dx = 0;
    public double dy = 0;
    public double maxD = 0;
    public double width = 0;
    public double height = 0;
    public Color color = Color.WHITE;
    public int direction = 1;
    public GameObjectType type = GameObjectType.RECT;
    public BufferedImage image;
    public double offsetX = 0;
    public double offsetY = 0;
    public int layer = 0;
    public int priority = 1;

    public BoundingBox bbox;
    public boolean collidable = true;
    public boolean displayed = true;

    public List<Collidable> colliders = new ArrayList<>();
    public Color collidingColor;
    public String collidableList = "";

    public Map<String, Object> attributes = new HashMap<>();

    public double timeFactor = 0.05;

    public List<String> debugInfo = new ArrayList<>();

    /**
     * Default constructor initializing all main attribtues.
     */
    public GameObject() {
        id = index++;
        name = "gameobject_" + id;
        x = y = 0;
        dx = dy = 0;
        width = height = 0;
        type = GameObjectType.RECT;
        bbox = new BoundingBox();
        bbox.update(this);
    }

    public GameObject(String name) {
        this();
        this.name = name;
    }

    /**
     * Update the game object
     * 
     * @param ga
     * @param elapsed
     */
    public void update(Sample ga, double elapsed) {
        x += dx * (elapsed * timeFactor);
        y += dy * (elapsed * timeFactor);
        direction = (dx > 0 ? 1 : -1);
        bbox.update(this);
    }

    /**
     * render the GameObject.
     * 
     * @param ga
     * @param g
     */
    public void draw(Sample ga, Graphics2D g) {
        g.setColor(this.color);
        int ox = (int) (x + offsetX);
        int oy = (int) (y + offsetY);

        switch (type) {
            case POINT:
                g.drawLine(ox, oy, ox, oy);
                break;
            case LINE:
                g.drawLine(ox, oy, ox + (int) (dx), oy + (int) (dy));
                break;
            case RECT:
                g.fillRect(ox, oy, (int) width, (int) height);
                break;
            case ELLIPSE:
                g.fillOval(ox, oy, (int) width, (int) height);
                break;
            case IMAGE:
                if (direction < 0) {
                    g.drawImage(image, (int) (x + width), (int) y, (int) (-width), (int) height, null);
                } else {
                    g.drawImage(image, (int) x, (int) y, (int) width, (int) height, null);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public BoundingBox getBoundingBox() {
        return bbox;
    }

    @Override
    public void addCollider(Collidable c) {
        this.colliders.add(c);
    }

    @Override
    public List<Collidable> getColliders() {
        return this.colliders;
    }

    @Override
    public void setCollidingColor(Color c) {
        this.collidingColor = c;
    }

    @Override
    public String getCollidableList() {
        return this.collidableList;
    }

    @Override
    public BoundingBox getCollisionBox() {
        return null;
    }

	public boolean isDisplayed() {
		return displayed;
	}

    @Override
    public String getName() {
        return name;
    }
}
