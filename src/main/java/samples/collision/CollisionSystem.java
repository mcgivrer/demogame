package samples.collision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import core.Game;
import lombok.extern.slf4j.Slf4j;
import samples.Sample;
import samples.object.GameObject;
import samples.system.AbstractGameSystem;

/**
 * The `CollisionSystem` will detect any collision between eligible
 * `GameObject`s. A class inheriting the `OnCollision` will be called to solve
 * all those collisions.
 */
@Slf4j
public class CollisionSystem extends AbstractGameSystem {

    List<GameObject> objects = new ArrayList<>();
    private List<Collidable> colliders = new ArrayList<>();
    private Queue<CollisionEvent> events;

    protected CollisionSystem(Sample game, int maxCollisionEventQueueSize) {
        super(game);
        events = new ArrayBlockingQueue<>(maxCollisionEventQueueSize);
    }

    public void addObject(GameObject g) {
        if (!objects.contains(g)) {
            objects.add(g);
        }
    }

    /**
     * clear all nodes from the quadtree and all objects from list.
     */
    public void clear() {
        this.events.clear();
        this.colliders.clear();
    }

    public void clearEvents() {
        this.events.clear();
    }

    public synchronized void add(Collidable e) {
        colliders.add(e);
        log.debug("Add {} to CollidingSystem", e.getName());
    }

    public synchronized void remove(Collidable e) {
        colliders.remove(e);

        log.debug("Remove {} from CollidingSystem", e.getName());
    }

    public synchronized void remove(String name) {
        List<Collidable> toBeRemoved = new ArrayList<>();
        for (Collidable c : colliders) {
            GameObject e = (GameObject) c;
            if (e.getName().equals(name)) {
                toBeRemoved.add(e);
                log.debug("object {} marked as remove into CollidingSystem", e.getName());
            }
        }
        colliders.removeAll(toBeRemoved);
    }

    /**
     * Manage collision from Player to other objects.
     * 
     * @param o       GameObject to be tested
     * @param elapsed the elapsed time since previous call
     */
    public void update(GameObject o, double elapsed) {
        List<Collidable> collisionList = colliders.stream().filter(c->!getName().equals(o.getName())).collect(Collectors.toList());
        if (collisionList != null && !collisionList.isEmpty()) {
            o.getColliders().clear();
            o.setCollidingColor(null);
            for (Collidable s : collisionList) {
                GameObject ago = (GameObject) s;
                if (isCollidableWith(o, ago) && !o.getName().equals(ago.getName())
                        && o.getBoundingBox().intersect(ago.getBoundingBox())) {
                    if (o.isDisplayed() && ago.isDisplayed()) {
                        o.addCollider(ago);
                        ago.addCollider(o);
                        o.setCollidingColor(Color.WHITE);

                        events.add(new CollisionEvent(o, ago));
                        log.debug("object {} collide object {}", o.getName(), ago.getName());
                    }
                }
            }
        }
    }

    private boolean isCollidableWith(GameObject o, GameObject ago) {
        for (String name : o.getCollidableList().split(":")) {
            if (ago.getName().contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Process events on all identified and filtered objects.
     */
    public void processEvents(OnCollision oc) {
        for (CollisionEvent ce : events) {
            oc.collide(ce);
        }
    }

    public String resolveCollision(GameObject a, GameObject b) {
        // get the vectors to check against
        double vX = (a.x + (a.getCollisionBox().size.x / 2)) - (b.x + (a.getCollisionBox().size.x / 2));
        double vY = (a.y + (a.getCollisionBox().size.y / 2)) - (b.y + (a.getCollisionBox().size.y / 2));
        // Half widths and half heights of the objects
        double ww2 = (a.getCollisionBox().size.x / 2) + (b.getCollisionBox().size.x / 2);
        double hh2 = (a.getCollisionBox().size.y / 2) + (b.getCollisionBox().size.y / 2);
        String colDir = "";

        // if the x and y vector are less than the half width or half height,
        // they we must be inside the object, causing a collision
        if (Math.abs(vX) < ww2 && Math.abs(vY) < hh2) {
            // figures out on which side we are colliding (top, bottom, left, or right)
            double oX = ww2 - Math.abs(vX), oY = hh2 - Math.abs(vY);
            if (oX >= oY) {
                if (vY > 0) {
                    colDir += "TOP";
                    a.y += (oY + 1);
                } else {
                    colDir += "BOTTOM";
                    a.y -= (oY + 1);
                }
            } else {
                if (vX > 0) {
                    colDir += "LEFT";
                    a.x += (oX + 1);
                } else {
                    colDir += "RIGHT";
                    a.x -= (oX + 1);
                }
            }
        }
        return colDir;
    }

    public void draw(Game game, Graphics2D g) {
        for (CollisionEvent ce : events) {
            g.setColor(Color.RED);
            g.drawLine((int) ce.a.x, (int) ce.a.y, (int) (ce.a.x + ce.vx),
                    (int) (ce.a.y + ce.vy));
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public String getName() {
        return CollisionSystem.class.getSimpleName();
    }

}