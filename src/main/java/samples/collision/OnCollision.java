package samples.collision;

/**
 * The OnCollision interface must be implemented in your own Sample to manage
 * the CollisionEvent.
 * 
 * 
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2020
 * 
 * @see CollisionEvent
 **/
public interface OnCollision {

    public void collide(CollisionEvent e);

}