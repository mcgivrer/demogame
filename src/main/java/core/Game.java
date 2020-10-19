package core;

import core.audio.SoundSystem;
import core.collision.CollidingSystem;
import core.collision.MapCollidingSystem;
import core.gfx.Counter;
import core.gfx.Renderer;
import core.io.InputHandler;
import core.math.PhysicEngineSystem;
import core.object.ObjectManager;
import core.object.World;
import core.resource.ResourceManager;
import core.scene.Scene;
import core.scene.SceneManager;
import core.scripts.LuaScriptSystem;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

/**
 * An extra class to demonstrate some basics to create a simple java game.
 *
 * @author Frédéric Delorme
 * @since 2019
 */
@Slf4j
public class Game {

	public static long goIndex = 0;
	public Config config;
	public boolean exitRequest = false;

	/**
	 * System Manager and Systems.
	 */
	public SystemManager sysMan;
	public InputHandler inputHandler;
	public Renderer renderer;
	public CollidingSystem collidingSystem;
	public PhysicEngineSystem physicEngine;
	public SceneManager sceneManager;

	/**
	 * Create the Game container.
	 *
	 * @param argc
	 *                 list of arguments.
	 * @see Config#analyzeArgc(String[])
	 */
	public Game(String[] argc) {
		super();
		config = Config.analyzeArgc(this, argc);
	}

	/**
	 * Start the Game and proceed all initialization.
	 */
	public void run() {
		log.info("Run game");
		initialize();
		loop();
		log.info("Game stopped");
		dispose();
		System.exit(0);
	}

	/**
	 * Initialization of the game.
	 */
	public void initialize() {

		// start System Manager
		sysMan = SystemManager.initialize(this);
		sysMan.add(new ResourceManager(this));

		ResourceManager.add(new String[] { "/res/game.json", "/res/bgf-icon.png" });

		// add basic systems
		inputHandler = new InputHandler(this);
		sysMan.add(inputHandler);

		// GameObject manager system
		ObjectManager objectManager = new ObjectManager(this);
		sysMan.add(objectManager);

		// Renderer pipeline system
		renderer = new Renderer(this);
		sysMan.add(renderer);

		// Colliding System
		collidingSystem = new CollidingSystem(this);
		sysMan.add(collidingSystem);

		// Physic Engine system
		physicEngine = new PhysicEngineSystem(this, new World(this));
		sysMan.add(physicEngine);

		// Massive Sound system
		SoundSystem soundSystem = new SoundSystem(this);
		sysMan.add(soundSystem);

		// Start some more advanced systems.
		MapCollidingSystem mapCollider = new MapCollidingSystem(this);
		sysMan.add(mapCollider);

		// start State manager system
		sceneManager = new SceneManager(this);
		sysMan.add(sceneManager);

		LuaScriptSystem luaSystem = new LuaScriptSystem(this);
		sysMan.add(luaSystem);

	}

	/**
	 * Main loop for the game.
	 */
	private void loop() {

		long startTime = System.currentTimeMillis();
		long previousTime = startTime;
		double waitFrameDuration = config.fps * 0.000001f;
		double waitUpdateDuration = config.fps * 3 * 0.000001f;
		Counter realUPS = new Counter("UPS", 0, waitUpdateDuration);
		Counter realFPS = new Counter("FPS", 0, waitFrameDuration);

		renderer.setRealFPS(realFPS);
		renderer.setRealUPS(realUPS);

		sceneManager.startState(this);

		while (!exitRequest) {
			startTime = System.currentTimeMillis();

			double elapsed = startTime - previousTime;

			Scene current = sceneManager.getCurrent();

			physicEngine.update(this, current, elapsed);

			sceneManager.input(this);
			sceneManager.update(this, elapsed);

			if (realFPS.isReached()) {
				sceneManager.render(this, renderer, elapsed);
			}
			double wait = (waitUpdateDuration - elapsed);

			waitNextFrame(waitUpdateDuration, wait);

			realUPS.tick(elapsed);
			realFPS.tick(elapsed);

			previousTime = startTime;
		}
	}

	private void waitNextFrame(double waitFrameDuration, double wait) {
		if (wait > 0 && wait < waitFrameDuration) {
			log.debug("wait for {}ms", wait);
			try {
				Thread.sleep((int) wait);
			} catch (InterruptedException e) {
				log.error("Unable to wait {} wait ms", wait, e);
				Thread.currentThread().interrupt();
				System.exit(-1);
			}
		}
	}

	/**
	 * Dispose all systems.
	 */
	private void dispose() {
		sysMan.dispose();
	}

	/**
	 * The famous java Execution entry point.
	 *
	 * @param argc
	 *                 list of arguments from command lines
	 */
	public static void main(String[] argc) {
		Game dg = new Game(argc);
		dg.run();
	}
}