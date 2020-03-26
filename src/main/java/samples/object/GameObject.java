package samples.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The GameObject to animate, display and process all game entities.
 */
public class GameObject {

    public enum GameObjectType {
        POINT, LINE, RECT, ELLIPSE, IMAGE;
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
    public GameObjectType type;
    public BufferedImage image;

    public double timeFactor = 0.1;

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
    public void update(SampleGameObject ga, double elapsed) {
        x += dx * (elapsed * timeFactor);
        y += dy * (elapsed * timeFactor);
    }

    /**
     * render the GameObject.
     * 
     * @param ga
     * @param g
     */
    public void draw(SampleGameObject ga, Graphics2D g) {
        g.setColor(this.color);
        switch (type) {
            case POINT:
                g.drawLine((int) x, (int) y, (int) x, (int) y);
                break;
            case LINE:
                g.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
                break;
            case RECT:
                g.fillRect((int) x, (int) y, (int) width, (int) height);
                break;
            case ELLIPSE:
                g.fillOval((int) x, (int) y, (int) width, (int) height);
                break;
            case IMAGE:
                g.drawImage(image, (int) x, (int) y, null);
                break;
            default:
                break;
        }
    }
}
