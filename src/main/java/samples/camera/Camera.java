package samples.camera;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import samples.object.GameObject;
import samples.object.SampleGameObject;

/**
 * Camera to follow a
 */
public class Camera extends GameObject {
    public GameObject target;
    public Rectangle viewport;
    public double tween = 1.0;
    public double zoomFactor = 1.0;
    public double offsetX = 0.0;
    public double offsetY = 0.0;

    public Camera(String name, GameObject target, double tween, Rectangle viewport) {
        super(name);
        this.target = target;
        this.tween = tween;
        this.viewport = viewport;
        this.offsetX = (viewport.width * 0.5f);
        this.offsetY = (viewport.height * 0.5f);
    }

    @Override
    public void update(SampleGameObject ga, double elapsed) {
        if(target==null){
            return;
        }
        x += (target.x + target.width - offsetX - x) * tween * Math.max(elapsed,10);
        y += (target.y + target.height - offsetY - y) * tween * Math.max(elapsed,10);
    }

    @Override
    public void draw(SampleGameObject ga, Graphics2D g) {
    }

}