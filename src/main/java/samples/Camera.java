package samples;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Camera to follow a
 */
public class Camera extends GameObject {
    public GameObject target;
    public Rectangle viewport;
    public double tween = 1.0;
    public double zoomFactor = 0.0;

    public Camera(String name, GameObject target, double tween, Rectangle viewport) {
        super(name);
        this.target = target;
        this.tween = tween;
        this.viewport = viewport;
    }

    @Override
    public void update(SampleGameObject ga, long elapsed) {
        x += ((target.x + target.width - viewport.width * 0.5f) - x) * tween * Math.max(elapsed,10);
        y += ((target.y + target.height - viewport.height * 0.5f) - y) * tween * Math.max(elapsed,10);
    }

    @Override
    public void draw(SampleGameObject ga, Graphics2D g) {
    }

}