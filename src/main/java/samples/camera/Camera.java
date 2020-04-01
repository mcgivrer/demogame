package samples.camera;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import samples.Sample;
import samples.object.GameObject;

/**
 * <p>
 * <code>Camera</code> to follow a targeted GameObject.
 * <p>
 * A tween factor gise some elasticity to the camera in the follwoing moves.
 */
public class Camera extends GameObject {
    public GameObject target;
    public Rectangle viewport;
    public double tween = 1.0;
    public double zoomFactor = 1.0;
    public double offsetX = 0.0;
    public double offsetY = 0.0;

    /**
     * Create a new Camera named <code>name</code> and follow the GameObject target,
     * with a <code>tween</code> factor. The <code>viewport</code> is the size of
     * the camera view.
     * 
     * @param name     name of the camera
     * @param target   the GameObject to be tracked by the camera.
     * @param tween    the elasticity factor
     * @param viewport the size of the camera view in pixels.
     */
    public Camera(String name, GameObject target, double tween, Rectangle viewport) {
        super(name);
        this.target = target;
        this.tween = tween;
        this.viewport = viewport;
        this.offsetX = (viewport.width * 0.5f);
        this.offsetY = (viewport.height * 0.5f);
    }

    @Override
    public void update(Sample ga, double elapsed) {
        if (target == null) {
            return;
        }
        x += (target.x + target.width - offsetX - x) * tween * Math.max(elapsed, 10);
        y += (target.y + target.height - offsetY - y) * tween * Math.max(elapsed, 10);
    }

    @Override
    public void draw(Sample ga, Graphics2D g) {
    }

}