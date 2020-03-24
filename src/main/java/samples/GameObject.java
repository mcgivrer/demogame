package samples;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

/**
 * The GameObject to animate, display and process all game entities.
 */
public class GameObject {

    public enum GameObjectType {
        POINT, LINE, RECT, ELLIPSE, IMAGE;
    }

    public static int index = 0;
    String name;
    int x;
    int y;
    int dx;
    int dy;
    int maxD;
    int width;
    int height;
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
                g.drawLine(x, y, x, y);
                break;
            case LINE:
                g.drawLine(x, y, x + dx, y + dy);
                break;
            case RECT:
                g.fillRect(x,y,width,height);
                break;
            case ELLIPSE:
                g.fillOval(x, y, width, height);
                break;
            case IMAGE:
                g.drawImage(image,x,y,null);
                break;
            default:
                break;
        }
    }
}
