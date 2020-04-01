package samples.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import samples.Sample;

/**
 * The GameObject to animate, display and process all game entities.
 */
public class GameObject {

    public enum GameObjectType {
        POINT, LINE, RECT, ELLIPSE, IMAGE, OTHER;
    }

    public static int index = 0;
    public String name;
    public double x;
    public double y;
    public double dx;
    public double dy;
    public double maxD;
    public double width;
    public double height;
    public Color color;
    public int direction = 1;
    public GameObjectType type;
    public BufferedImage image;
    public double offsetX = 0;
    public double offsetY = 0;

    public int layer = 0;
    public int priority = 0;
    public boolean fixed = false;

    public Map<String, Object> attributes = new HashMap<>();

    public double timeFactor = 0.05;

    /**
     * Default constructor initializing all main attribtues.
     */
    public GameObject() {
        name = "gameobject_" + (index++);
        x = y = 0;
        dx = dy = 0;
        width = height = 0;
        type = GameObjectType.RECT;
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
}
