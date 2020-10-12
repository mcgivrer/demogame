package core.object;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import core.Game;

/**
 * A 2D core.object.Camera to render scene from a constrained point of view.
 */
public class Camera extends GameObject {

    public GameObject target;
    public float tween;
    public Dimension viewport;
    public float zoom = 1.0f;

    /**
     * Create a core.object.Camera <code>name</code> focusing on
     * <code>target</code>, with a <code>tween</code> factor to manage camera
     * sensitivity, and in a <code>viewport</code> size.
     *
     * @param name     name of the new camera.
     * @param target   the core.object.GameObject to be followed by the camera.
     * @param tween    the tween factor to manage camera sensitivity.
     * @param viewPort the size of the display window.
     */
    public Camera(String name, float tween, Dimension viewPort) {
        super(name, 0, 0, viewPort.width, viewPort.height);
        this.tween = tween;
        this.viewport = viewPort;
    }

    /**
     * Update the camera according to the <code>elapsed</code> time. Position is
     * relative to the <code>target</code> object and the camera speed is computed
     * through the <code>tween</code> factor.
     *
     * @param dg      the core.Game container for this camera
     * @param elapsed the elapsed time since previous update.
     */
    public void update(Game dg, double elapsed) {
        this.pos.x += Math.round((target.pos.x + (target.size.x) - ((double) (viewport.width) * 0.5f) - this.pos.x)
                * tween * Math.min(elapsed, 10));
        this.pos.y += Math.round((target.pos.y + (target.size.y) - ((double) (viewport.height) * 0.5f) - this.pos.y)
                * tween * Math.min(elapsed, 10));
        viewport.height *= zoom;
        viewport.width *= zoom;
        // log.debug("elapsed: {}, camera position : {},{}",elapsed, this.x,this.y);
    }

    /**
     * rendering of some (only) debug information.
     *
     * @param dg the containing game
     * @param g  the graphics API.
     */
    public void render(Game dg, Graphics2D g) {
        if (dg.config.debug > 1) {
            g.setColor(Color.YELLOW);
            g.drawRect((int) this.pos.x, (int) this.pos.y, viewport.width, viewport.height);
        }
    }

    public void setTarget(GameObject target) {
        this.target = target;
    }
}