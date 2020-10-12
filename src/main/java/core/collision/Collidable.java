package core.collision;

import java.util.List;
import java.awt.Color;

import core.object.BBox;

/**
 * Interface to managed Collision with ColliderSystem ans QuadTree.
 * 
 * @author Frédéric Delorme
 *
 */
public interface Collidable {

	BBox getBoundingBox();

	void addCollider(Collidable c);

	List<Collidable> getColliders();

	void setCollidingColor(Color c);

	String getCollidableList();

	BBox getCollisionBox();
}
