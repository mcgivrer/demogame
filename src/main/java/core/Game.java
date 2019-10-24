package core;

import core.state.StateManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An extra class to demonstrate some basics to create a simple java game.
 *
 * @author Frédéric Delorme
 * @since 2019
 */
public class Game implements KeyListener {

    public static long goIndex = 0;
    public Config config;
    public boolean exitRequest = false;
    private String[] argc;
    public boolean[] keys = new boolean[65536];
    public boolean[] previousKeys = new boolean[65536];

    public Renderer renderer;
    public StateManager stateManager;


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

    public void run() {
        System.out.println("Run game");
        initialize();
        loop();
        System.out.println("Game stopped");
        System.exit(0);
    }

    public void initialize() {
        ResourceManager.add("/res/game.json");
        renderer = new Renderer(this);
        stateManager = new StateManager(this);
    }

    public void loop() {
        stateManager.initialize(this);

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (!exitRequest) {
            startTime = System.currentTimeMillis();

            float elapsed = startTime - previousTime;

            stateManager.input(this);
            stateManager.update(this, elapsed);
            stateManager.render(this, renderer);

            float wait = ((config.fps * 0.001f));

            if (wait > 0) {
                try {
                    Thread.sleep((int) wait);
                } catch (InterruptedException e) {
                    System.out.println(String.format("Unable to wait %d wait ms", wait));
                }
            }
            previousTime = startTime;
        }
    }

    public void input() {

    }

    /**
     * Update all the object according to elapsed time.
     *
     * @param elapsed
     */
    public void update(float elapsed) {

    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = true;
        onKeyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        this.previousKeys[e.getKeyCode()] = this.keys[e.getKeyCode()];
        this.keys[e.getKeyCode()] = false;
        onKeyReleased(e);
    }

    /**
     * Process some keypressed events.
     *
     * @param e
     */
    public void onKeyPressed(KeyEvent e) {

    }

    /**
     * Process some KeyReleased events.
     *
     * @param e
     */
    public void onKeyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_D:
                // roll the debug level.
                config.debug = (config.debug < 6 ? config.debug + 1 : 0);
                break;
            case KeyEvent.VK_F3:
                renderer.saveScreenshot(config);
            default:
                break;
        }
    }

    /**
     * The famous java Execution entry point.
     *
     * @param argc
     */
    public static void main(String[] argc) {
        Game dg = new Game(argc);
        dg.run();
    }
}