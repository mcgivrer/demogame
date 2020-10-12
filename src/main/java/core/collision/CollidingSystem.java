/**
 * SnapGames
 * 
 * @year 2019
 */
package core.collision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import core.Game;
import core.object.GameObject;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * Some utilities to assist rendering tasks.
 * 
 * @author Frederic Delorme
 *
 */
@Slf4j
public class CollidingSystem extends AbstractSystem {

	private List<Collidable> colliders = new ArrayList<>();
	private Queue<CollisionEvent> events = new ArrayBlockingQueue<>(500);

	/**
	 * The QuadTree to manage objects collision and visibility.
	 */
	private QuadTreeNode quadTree;

	/**
	 * Defin the colliding system on the gam play area
	 * 
	 * @param playArea
	 */
	public CollidingSystem(Game game) {
		super(game);
	}

	/**
	 * Define the play area for the QuadTree partitioning.
	 * 
	 * @param playArea the Dimension of the play area (in pixels) where all objects
	 *                 are located.
	 */
	public void setPlayArea(Dimension playArea) {
		quadTree = new QuadTreeNode(playArea.width, playArea.height);
		quadTree.setMaxObjects(4);
		quadTree.setMaxLevels(5);
	}

	/**
	 * clear all nodes from the quadtree and all objects from list.
	 */
	public void clear() {
		this.events.clear();
		this.colliders.clear();
		this.quadTree.clear();
	}

	public void clearEvents() {
		this.events.clear();
	}

	/**
	 * @param game
	 * @param dt
	 */
	public synchronized void cullingProcess(Game game, float dt) {
		quadTree.clear();
		for (Collidable e : colliders) {
			// inert object into QuadTree for collision detection.
			quadTree.insert(e);
		}
	}

	public synchronized void add(GameObject e) {
		colliders.add(e);
		log.debug("Add {} to CollidingSystem", e.getName());
	}

	public synchronized void remove(GameObject e) {
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
		List<Collidable> collisionList = new CopyOnWriteArrayList<>();
		quadTree.retrieve(collisionList, o);
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
						o.setCollidingColor(Color.RED);

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
		double vX = (a.pos.x + (a.getCollisionBox().size.x / 2)) - (b.pos.x + (a.getCollisionBox().size.x / 2));
		double vY = (a.pos.y + (a.getCollisionBox().size.y / 2)) - (b.pos.y + (a.getCollisionBox().size.y / 2));
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
					colDir = "TOP";
					a.pos.y += (oY + 1);
				} else {
					colDir = "BOTTOM";
					a.pos.y -= (oY + 1);
				}
			} else {
				if (vX > 0) {
					colDir = "LEFT";
					a.pos.x += (oX + 1);
				} else {
					colDir = "RIGHT";
					a.pos.x -= (oX + 1);
				}
			}
		}
		return colDir;
	}

	public void draw(Game game, Graphics2D g) {
		quadTree.draw(game, g);
		for (CollisionEvent ce : events) {
			g.setColor(Color.RED);
			g.drawLine((int) ce.a.pos.x, (int) ce.a.pos.y, (int) (ce.a.pos.x + ce.penetrationVector.x),
					(int) (ce.a.pos.y + ce.penetrationVector.y));
		}
	}

	@Override
	public String getName() {
		return CollidingSystem.class.getCanonicalName();
	}

	public int getCollisionEventQueueSize() {
		return events.size();
	}

	@Override
	public int initialize(Game game) {
		this.colliders.clear();
		if (this.quadTree != null) {
			this.quadTree.clear();
		}
		return 0;
	}

	@Override
	public void dispose() {

	}

}
