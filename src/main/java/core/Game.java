package core;

import core.audio.SoundSystem;
import core.collision.MapCollidingService;
import core.gfx.Renderer;
import core.io.InputHandler;
import core.math.PhysicEngineSystem;
import core.object.ObjectManager;
import core.object.World;
import core.resource.ResourceManager;
import core.scene.Scene;
import core.scripts.LuaScriptSystem;
import core.scene.AbstractScene;
import core.scene.SceneManager;
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
	public PhysicEngineSystem physicEngine;
	public SceneManager sceneManager;

	/**
	 * Create the Game container.
	 *
	 * @param argc list of arguments.
	 * @see Config#analyzeArgc(String[])
	 */
	public Game(String[] argc) {
		super();
		config = Config.analyzeArgc(argc);
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
		ResourceManager.add(new String[] { "/res/game.json", "/res/bgf-icon.png" });

		// start System Manager
		sysMan = SystemManager.initialize(this);

		// add basic systems
		inputHandler = new InputHandler(this);
		sysMan.add(inputHandler);

		// GameObject manager system
		ObjectManager objectManager = new ObjectManager(this);
		sysMan.add(objectManager);

		// Renderer pipeline system
		renderer = new Renderer(this);
		sysMan.add(renderer);

		// Physic Engine system
		physicEngine = new PhysicEngineSystem(this, new World(this));
		sysMan.add(physicEngine);

		// Massive Sound system
		SoundSystem soundSystem = new SoundSystem(this);
		sysMan.add(soundSystem);

		// Start some more advanced systems.
		MapCollidingService mapCollider = new MapCollidingService(this);
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
		sceneManager.startState(this);

		long startTime = System.currentTimeMillis();
		long previousTime = startTime;
		int frames = 0;
		int realFPS = 0;
		int elapsedFrameTime = 0;
		double waitFrameDuration = config.fps * 0.001f;

		while (!exitRequest) {
			startTime = System.currentTimeMillis();

			double elapsed = startTime - previousTime;

			Scene current = sceneManager.getCurrent();

			physicEngine.update(this, (AbstractScene) current, elapsed);

			sceneManager.input(this);
			sceneManager.update(this, elapsed);
			renderer.setRealFPS(realFPS);
			sceneManager.render(this, renderer, elapsed);

			double wait = (elapsed - waitFrameDuration);

			frames++;
			elapsedFrameTime += elapsed;
			if (elapsedFrameTime > 1000) {
				frames = 0;
				elapsedFrameTime = 0;
				realFPS = frames;
				log.debug("elapsed:{}, wait:{}", elapsed, wait);
			}

			if (wait > 0 && wait < waitFrameDuration) {
				try {
					Thread.sleep((int) wait);
				} catch (InterruptedException e) {
					log.error("Unable to wait {} wait ms", wait, e);
				}
			}

			previousTime = startTime;
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
	 * @param argc list of arguments from command lines
	 */
	public static void main(String[] argc) {
		Game dg = new Game(argc);
		dg.run();
	}
}