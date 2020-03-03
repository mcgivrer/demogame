package core.scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Game;
import core.audio.SoundSystem;
import core.gfx.Renderer;
import core.io.InputHandler;
import core.object.Camera;
import core.object.GameObject;
import core.object.ObjectManager;
import lombok.extern.slf4j.Slf4j;

/**
 * the AbstractState is the default implementation for a State interface. It
 * will provide all necessary default behaviors and processing for a State. All
 * specifics would be implemented in the inheriting class.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @see core.state.Scene
 * @since 2019
 */
@Slf4j
public abstract class AbstractScene implements Scene, KeyListener {

	// the parent game.
	protected Game game;
	// the name of this state.
	protected String name;
	// a State must have a Camera.
	public Camera camera;
	public Map<String, Camera> cameras = new ConcurrentHashMap<>();
	protected ObjectManager objectManager;
	protected InputHandler inputHandler;
	protected SoundSystem soundSystem;

	/**
	 * the default constructor.
	 */
	public AbstractScene() {

	}

	/**
	 * Initialize the AbstractState with the parent game
	 *
	 * @param g the parent game.
	 */
	public AbstractScene(Game g) {
		this.game = g;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract void input(Game g);

	public void initialize(Game g) {
		objectManager = g.sysMan.getSystem(ObjectManager.class);
		// prepare user input handler
		inputHandler = g.sysMan.getSystem(InputHandler.class);
		// load Sounds
		soundSystem = g.sysMan.getSystem(SoundSystem.class);

	};

	@Override
	public abstract void load(Game g);

	@Override
	public abstract void update(Game g, double elapsed);

	@Override
	public abstract void render(Game g, Renderer r, double elpased);

	@Override
	public void onFocus(Game g) {
		log.debug("{} state get focus", this.getName());
	}

	@Override
	public void lostFocus(Game g) {
		log.debug("{} state lost focus", this.getName());
	}

	/**
	 * Add a Game object to the managed objects list. If the <code>go</code>
	 * core.object.GameObject is a core.object.Camera instance, it will be set as
	 * the default camera.
	 *
	 * @param go the core.object.GameObject to be added to the core.Game#objects
	 *           list.
	 */
	public void addObject(GameObject go) {
		// If camera, add it to the camera list
		if (go instanceof Camera) {
			if (!cameras.containsKey(go.name)) {
				cameras.put(go.name, (Camera) go);
			}
			// if no defaut caera, set it as default one.
			if (this.camera == null) {
				this.camera = (Camera) go;
			}
		} else {
			// this is a simple GameObject, just add-it.
			objectManager.add(go);
			game.renderer.add(go);
			game.physicEngine.add(go);
		}
	}

	public void switchCamera(String camName) {
		if (cameras.containsKey(camName)) {
			this.camera = cameras.get(camName);
		}
	}

	/**
	 * return the current active camera.
	 *
	 * @return
	 */
	public Camera getActiveCamera() {
		return camera;
	}

	@Override
	public ObjectManager getObjectManager() {
		return objectManager;
	}

	/**
	 * Define the parent Game.
	 *
	 * @param g the parent game for this state.
	 */
	public void setGame(Game g) {
		this.game = g;
	}

	/**
	 * A Unicode key has been pressed.
	 *
	 * @param e
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * Process some keypressed events.
	 *
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e) {

	}

	/**
	 * Process some KeyReleased events.
	 *
	 * @param e
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		Renderer r = game.sysMan.getSystem(Renderer.class);
		switch (e.getKeyCode()) {
			case KeyEvent.VK_D:
				// roll the debug level.
				game.config.debug = (game.config.debug < 6 ? game.config.debug + 1 : 0);
				break;
			case KeyEvent.VK_F3:
				r.saveScreenshot(game.config);
			default:
				break;
		}
	}
}
