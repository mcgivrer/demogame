package samples.render;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import samples.camera.entity.Camera;
import samples.collision.CollisionEvent;
import samples.collision.CollisionSystem;
import samples.collision.SampleCollision;
import samples.input.InputHandler;
import samples.input.entity.MouseCursor;
import samples.object.entity.GameObject;
import samples.object.entity.GameObject.GameObjectType;
import samples.render.collision.ColEvent;
import samples.system.AbstractGameSystem;
import samples.system.GameSystemManager;

@Slf4j
public class SampleRendererSystem extends SampleCollision {
	IRenderer rs;
	BufferedImage sprites;

	public SampleRendererSystem(String title, String[] args) {
		this.title = title;
		configureCliArguments();
		parseArgs(args);
	}

	/*---- Initialize the Game and all its resources ----*/

	public int initializeGame() {
		// Re-implement initialization() to split initialization() and a load()
		gsm = GameSystemManager.initialize(this);

		// add this InputHandler to the manager
		final InputHandler ih = new InputHandler(this);
		gsm.add(ih);
		ih.register(this);

		// add this Renderer to the manager
		rs = new Renderer(this);
		gsm.add((AbstractGameSystem) rs);

		// initialize Key listener in the rendering frame.
		rs.addKeyListener(gsm.getSystem(InputHandler.class));

		/**
		 * Add the CollisionSystem with new Collision event triggering Mouse Collision
		 */
		cs = new CollisionSystem<ColEvent>(this, 500) {
			@Override
			public ColEvent createEvent(GameObject o1, GameObject o2) {
				return new ColEvent(o1, o2);
			}
		};
		gsm.add(cs);

		try {
			sprites = ImageIO.read(this.getClass().getResourceAsStream("/res/images/tileset-1.png"));

		} catch (IOException ioe) {
			log.error("unable to read the tileset image");
			return -1;
		}
		return 0;
	}

	/**
	 * Add a GameObject go.
	 * 
	 * @param go
	 */
	public void addObject(GameObject go) {
		objects.put(go.name, go);
		cs.add(go);
		rs.addObject(go);
	}

	/*---- Load game objects and all needed things to be manage in the game ----*/
	@Override
	public void load() {
		collidingColor = Color.WHITE;
		squareColor = Color.RED;

		// Add Player
		GameObject player = new GameObject("player");
		player.type = GameObjectType.IMAGE;
		player.image = sprites.getSubimage(0, 48, 32, 32);
		player.width = player.image.getWidth();
		player.height = player.image.getHeight();
		player.maxD = 4;
		player.x = (getWidth() - player.image.getWidth()) / 2;
		player.y = (getHeight() - player.image.getHeight()) / 2;
		player.dx = 0;
		player.dy = 0;
		player.attributes.put("elasticity", 0.0);
		player.layer = 1;
		addObject(player);

		// Add a mouse cursor
		MouseCursor mCursor = new MouseCursor("mouse_cursor");
		mCursor.layer = 0;
		addObject(mCursor);

		// Add some other objects
		createObjects(5);

		Camera camera = new Camera("cam1", objects.get("player"), 0.005f, rs.getViewport());
		addObject(camera);

	}

	@Override
	protected void createObjects(int max) {
		for (int i = 0; i < max; i++) {
			GameObject go = new GameObject();
			go.x = (Math.random() * getWidth()) - 16;
			go.y = (Math.random() * getHeight()) - 16;
			go.width = 16;
			go.height = 16;
			go.maxD = 4;
			go.dx = (Math.random() * 8);
			go.dy = (Math.random() * 8);
			go.color = squareColor;
			go.layer = 2;

			go.attributes.put("elasticity", 1.0);

			go.type = randomType();

			addObject(go);

			log.info("Add a new GameObject named {}", go.name);
		}
	}

	/*---- the main run to start this Sample implmentation with a renderer ----*/
	@Override
	public void run() {
		// now initialize and load are separated actions.
		if (initializeGame() == 0) {
			load();
			loop();
			gsm.dispose();
		}
		System.exit(0);
	}

	/*---- Manage input from player ----*/

	@Override
	public void input(InputHandler ih) {
		setMousePosition(ih);
		GameObject go = objects.get("player");
		if (go != null) {
			movePlayerVerticaly(ih, go);
			movePlayerHorizontaly(ih, go);
			stopPlayer(ih, go);
			reshuffleObjects(ih);
		}
	}

	private void setMousePosition(InputHandler ih) {

		MouseCursor m = (MouseCursor) objects.get("mouse_cursor");
		if (m != null) {
			m.x = ih.getMouseX() / scale;
			m.y = ih.getMouseY() / scale;
			if (m.getBoundingBox().intersect(rs.getViewport())) {
				m.color = Color.WHITE;
			} else {
				m.color = Color.GRAY;
			}
		}
	}

	private void reshuffleObjects(InputHandler ih) {
		final List<String> excludedObjects = Arrays.asList("player", "mouse_cursor");
		if (ih.getKey(KeyEvent.VK_R)) {
			reshuffleVelocity(excludedObjects);
		}
	}

	private void stopPlayer(InputHandler ih, GameObject go) {
		if (ih.getKey(KeyEvent.VK_SPACE)) {
			go.dx = 0;
			go.dy = 0;
			go.color = Color.BLUE;
		}
	}

	private void movePlayerHorizontaly(InputHandler ih, GameObject go) {
		if (ih.getKey(KeyEvent.VK_LEFT)) {
			go.dx = (go.dx > -go.maxD ? go.dx - 1 : go.dx);
		}
		if (ih.getKey(KeyEvent.VK_RIGHT)) {
			go.dx = (go.dx < go.maxD ? go.dx + 1 : go.dx);
		}
	}

	private void movePlayerVerticaly(InputHandler ih, GameObject go) {
		if (ih.getKey(KeyEvent.VK_UP)) {
			go.dy = (go.dy > -go.maxD ? go.dy - 1 : go.dy);
		}
		if (ih.getKey(KeyEvent.VK_DOWN)) {
			go.dy = (go.dy < go.maxD ? go.dy + 1 : go.dy);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		switch (e.getKeyCode()) {
			case KeyEvent.VK_D:
				rs.setDebug(this.debug);
				break;
			default:
				break;
		}
	}

	@Override
	protected void constrainGameObject(GameObject go) {
		double elasticity = (go.attributes.containsKey("elasticity") ? (double) go.attributes.get("elasticity") : 0.0);
		if (go.x > getWidth() - go.width) {
			go.x = getWidth() - go.width;
			go.dx = -go.dx * elasticity;
			go.color = collidingColor;
		}
		if (go.y >= getHeight() - go.height) {
			go.y = getHeight() - go.height;
			go.dy = -go.dy * elasticity;
			go.color = collidingColor;
		}
		if (go.x <= 0) {
			go.x = 0;
			go.dx = -go.dx * elasticity;
			go.color = collidingColor;
		}
		if (go.y <= 0) {
			go.y = 0;
			go.dy = -go.dy * elasticity;
			go.color = collidingColor;
		}
	}

	@Override
	public void update(double elapsed) {

		// reset collision system
		cs.clearEvents();

		// loop objects
		for (GameObject go : objects.values()) {

			go.update(this, elapsed);

			// verify collision for this object.
			cs.update(go, elapsed);

			if (!(go.name.equals("cam1") || go.name.equals("mouse_cursor"))) {
				constrainGameObject(go);
			}
		}
		cs.processEvents(this);
	}

	/*---- Render the game display through this new Renderer ----*/

	@Override
	public void render(long realFps) {
		IRenderer render = gsm.getSystem(Renderer.class);
		render.render(this, realFps);
	}

	@Override
	public void collide(CollisionEvent e) {
		switch (e.type) {
			case COLLISION_MOUSE:
				e.b.debug = true;
				e.b.color = Color.ORANGE;
				break;
			case COLLISION_OBJECT:
				e.a.dx = 0;
				e.a.dy = 0;
				e.a.collidingColor = Color.ORANGE;
				e.b.dx = -e.b.dx;
				e.b.dy = -e.b.dy;
				e.b.collidingColor = Color.ORANGE;
				break;
			default:
				break;
		}

	}

	/*---- The main method for this sample using the new Rendering System ----*/

	public static void main(String[] args) {
		SampleRendererSystem g = new SampleRendererSystem("Sample Render System", args);
		g.run();
	}

}