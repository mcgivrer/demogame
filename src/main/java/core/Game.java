package core;

import core.audio.SoundSystem;
import core.collision.MapCollidingService;
import core.gfx.Renderer;
import core.io.InputHandler;
import core.object.ObjectManager;
import core.resource.ResourceManager;
import core.state.StateManager;
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
	public StateManager stateManager;
	private SoundSystem soundSystem;
	private MapCollidingService mapCollider;
	private ObjectManager objectManager;

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
		objectManager = new ObjectManager(this);
		sysMan.add(objectManager);

		// rendering pipeline
		renderer = new Renderer(this);
		sysMan.add(renderer);

		// Massive Sound system
		soundSystem = new SoundSystem(this);
		sysMan.add(soundSystem);

		// Start some more advanced systems.
		mapCollider = new MapCollidingService(this);
		sysMan.add(mapCollider);

		// start State manager system
		stateManager = new StateManager(this);
		sysMan.add(stateManager);
	}

	/**
	 * Main loop for the game.
	 */
	private void loop() {
		stateManager.startState(this);

		long startTime = System.currentTimeMillis();
		long previousTime = startTime;

		while (!exitRequest) {
			startTime = System.currentTimeMillis();

			float elapsed = startTime - previousTime;

			stateManager.input(this);
			stateManager.update(this, elapsed);
			stateManager.render(this, renderer, elapsed);

			float wait = ((config.fps * 0.001f));

			if (wait > 0) {
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