package samples.collision;

import java.awt.Color;
import java.util.List;

/**
 * Interface to managed Collision with ColliderSystem ans QuadTree.
 * 
 * @author Frédéric Delorme
 *
 */
public interface Collidable {

	String getName();

	BoundingBox getBoundingBox();

	void addCollider(Collidable c);

	List<Collidable> getColliders();

	void setCollidingColor(Color c);

	String getCollidableList();

	BoundingBox getCollisionBox();
}