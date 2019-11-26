package core.collision;

/**
 * The OnCollision interface must be implemented in your own AbstractGameState
 * to manage the CollisionEvent.
 * 
 * 
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 * 
 * @see MapCollidingService
 * @see CollisionEvent
 **/
public interface OnCollision {

	public void collide(CollisionEvent e);
}
