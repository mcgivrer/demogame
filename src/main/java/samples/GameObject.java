package samples;

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
    String name;
    double x;
    double y;
    double dx;
    double dy;
    double maxD;
    double width;
    double height;
    Color color;
    GameObjectType type;
    BufferedImage image;

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
     * @param elpased
     */
    public void update(SampleGameObject ga, long elpased) {
        x += dx;
        y += dy;
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
                g.drawLine((int)x, (int)y, (int)x, (int)y);
                break;
            case LINE:
                g.drawLine((int)x, (int)y, (int)(x + dx),(int)(y + dy));
                break;
            case RECT:
                g.fillRect((int)x, (int)y, (int)width, (int)height);
                break;
            case ELLIPSE:
                g.fillOval((int)x, (int)y, (int)width, (int)height);
                break;
            case IMAGE:
                g.drawImage(image, (int)x, (int)y, null);
                break;
            default:
                break;
        }
    }
}
