package core.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Game;
import core.collision.MapTileCollision;
import core.gfx.Animation;
import core.gfx.Renderer;
import core.map.MapObject;
import core.math.Vector2D;
import lombok.Data;
import lombok.ToString;

/**
 * Any object displayed by the game. the GameObject will provide most operations
 * of the game loop steps:
 * <ul>
 * <li><code>update(Game,double)</code> will update the Object status</li>
 * <li><code>render(Game,Graphics2D)</code> draw all about this GameObject</li>
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 */
@Data
@ToString
public class GameObject {

	public GameAction action = GameAction.IDLE;

	private static int goIndex = 0;
	private final int id = (int) goIndex++;

	public String name = "noname_" + id;

	public BufferedImage image;

	public boolean enable = true;

	public Vector2D pos;
	public Vector2D newPos;
	public Vector2D vel;
	public Vector2D acc;
	public Vector2D forces;

	public Vector2D size;

	public int direction = 1;

	public int layer = 0;
	public int priority = 0;

	public int debugLevel = 0;

	public boolean canCollect;

	public boolean fixed = false;

	public GameObjectType type;

	public BBox bbox;

	public double duration = -1;

	public Map<GameAction, Animation> animations = new HashMap<>();

	public Color foregroundColor;
	public Color backgroundColor;

	public Map<String, Object> attributes = new HashMap<>();

	public List<MapObject> items = new ArrayList<>();

	public Map<String, GameObject> child = new HashMap<>();

	public List<MapTileCollision> collidingZone = new ArrayList<>();

	/**
	 * If the object is active it will be processed as other, but not rendered.
	 */
	public boolean displayed = true;

	/**
	 * Create a new GameObject with some default values.
	 */
	public GameObject() {
		this.name = "gameObjectName";
		this.pos = new Vector2D();
		this.vel = new Vector2D();
		this.acc = new Vector2D();
		this.forces = new Vector2D();
		this.type = GameObjectType.RECTANGLE;
		bbox = new BBox(this);
	}

	/**
	 * Create a new object in the game with its position <code>(x,y)</code> and size
	 * <code>(width,height)</code>.
	 *
	 * @param name   Name of this object.
	 * @param x      horizontal position
	 * @param y      vertical position
	 * @param width  width of the object (if no image set)
	 * @param height height of the object (if no image set)
	 */
	public GameObject(String name, double x, double y, double width, double height) {
		this();
		this.name = name;
		this.pos.x = x;
		this.pos.y = y;
		this.size.x = width;
		this.size.y = height;
		this.type = GameObjectType.RECTANGLE;
		bbox.fromGameObject(this);
	}

	/**
	 * update the object (on all its characteristics, not only position if needed)
	 *
	 * @param dg      the core.Game containing the object.
	 * @param elapsed the elapsed time since previous call.
	 * @see core.state.State#update(Game, double)
	 */
	public void update(Game dg, double elapsed) {
		pos.x = newPos.x;
		pos.y = newPos.y;
		// compute action and move to be performed.
		switch (action) {
		case IDLE:
		case IDLE2:
			vel.y = 0.0f;
			vel.x = 0.0f;
			break;
		case WALK:
			pos.x += (vel.x * elapsed);
			break;
		case RUN:
			pos.x += (vel.x * 2.0f * elapsed);
			break;
		case FALL:
		case DOWN:
			pos.y += (vel.y * elapsed);
			break;
		case JUMP:
			pos.y += (vel.y * 3 * elapsed);
		case UP:

			break;
		default:
			break;
		}
		// update the bounding box for this GameObject
		if (bbox != null) {
			bbox.fromGameObject(this);
		}
	}

	/**
	 * Rendering of the object (will be delegated to another component in a next
	 * version. this method will be called by the Renderer.
	 *
	 * @param dg the core.Game containing the object.
	 * @param g  the graphics API.
	 * @see Renderer#render(Game, double)
	 */
	public void render(Game dg, Graphics2D g) {

	}

	public enum GameAction {
		IDLE, IDLE2, WALK, RUN, FALL, JUMP, UP, DOWN, DEAD1, DEAD2;
	}

	/*------- Setters ---------------*/
	public void setPosition(double x, double y) {
		this.pos.x = x;
		this.pos.y = y;
		bbox.fromGameObject(this);
	}

	public void setSpeed(double dx, double dy) {
		this.vel.x = dx;
		this.vel.y = dy;
		bbox.fromGameObject(this);
	}

	public void setSize(double width, double height) {
		this.size.x = width;
		this.size.y = height;
		bbox.fromGameObject(this);
	}
}
