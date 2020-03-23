package samples;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The GameObject to animate, display and process all game entities.
 */
public class GameObject {
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

    /**
     * Default constructor initializing all main attribtues.
     */
    public GameObject() {
        name = "gameobject_"+(index++);
        x = y = 0;
        dx = dy = 0;
        width = height = 0;
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
        g.fillRect(x, y, 16, 16);
    }
}
