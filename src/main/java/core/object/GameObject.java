package core.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Game;
import core.behaviors.Behavior;
import core.collision.MapTileCollision;
import core.gfx.Animation;
import core.gfx.Renderer;
import core.map.MapObject;
import core.math.Material;
import core.math.PhysicEngineSystem.PhysicType;
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
	public List<Vector2D> forces;

	public double mass;
	public Material material;
	public PhysicType physicType = PhysicType.STATIC;

	public Vector2D size;

	public int direction = 1;

	public int layer = 0;
	public int priority = 0;

	public int debugLevel = 0;

	public boolean canCollect;

	public boolean collidable = true;

	public boolean fixed = false;

	public GameObjectType type;

	public BBox bbox;

	public double duration = -1;

	public boolean isContact = false;

	public Map<GameAction, Animation> animations = new HashMap<>();

	public Color foregroundColor;
	public Color backgroundColor;

	public Map<String, Object> attributes = new HashMap<>();

	public List<MapObject> items = new ArrayList<>();

	public Map<String, GameObject> child = new HashMap<>();

	public List<MapTileCollision> collidingZone = new ArrayList<>();

	public List<Behavior> behaviors = new ArrayList<>();

	public MapObject tileCollisionObject;

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
		this.newPos = new Vector2D();
		this.vel = new Vector2D();
		this.acc = new Vector2D();
		this.size = new Vector2D();
		this.forces = new ArrayList<>();
		this.type = GameObjectType.RECTANGLE;
		this.mass = 1;
		this.material = Material.NONE;
		this.bbox = new BBox(this);
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
		this.newPos.x = x;
		this.newPos.y = y;
		this.size.x = width;
		this.size.y = height;

		bbox.fromGameObject(this);
	}

	/**
	 * update the object (on all its characteristics, not only position if needed)
	 *
	 * @param dg      the core.Game containing the object.
	 * @param elapsed the elapsed time since previous call.
	 * @see core.state.Scene#update(Game, double)
	 */
	public void update(Game dg, double elapsed) {
		pos.x = newPos.x;
		pos.y = newPos.y;
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

	/*------- Getters & Setters ---------------*/
	public Vector2D getPOsition() {
		return this.pos;
	}

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
