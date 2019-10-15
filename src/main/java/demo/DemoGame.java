package demo;

import demo.map.MapLevel;
import demo.object.Camera;
import demo.object.GameObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An extra class to demonstrate some basics to create a simple java game.
 *
 * @author Frédéric Delorme
 * @since 2019
 */
public abstract class DemoGame implements KeyListener {

    private static long goIndex = 0;
    public Config config;
    public boolean exitRequest = false;
    private String[] argc;
    protected boolean[] keys = new boolean[65536];
    protected boolean[] previousKeys = new boolean[65536];

    public Renderer renderer;

    public Camera camera;
    public Map<String, GameObject> objects = new ConcurrentHashMap<>();


    /**
     * Create the Game container.
     *
     * @param argc list of arguments.
     * @see Config#analyzeArgc(String[])
     */
    public DemoGame(String[] argc) {
        super();
        config = Config.analyzeArgc(argc);
    }


    public void initialize() {
        renderer = new Renderer(this);

        loadState();
    }

    public abstract void loadState();


    public void loop() {

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (!exitRequest) {
            startTime = System.currentTimeMillis();
            float elapsed = startTime - previousTime;
            input();
            update(elapsed);
            renderer.render(this);
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

    public void constrainToMapLevel(MapLevel bi, GameObject go) {
        if (go.x + go.width > bi.width * bi.asset.tileWidth) {
            go.x = bi.width * bi.asset.tileWidth - go.width;
            go.dx = -go.dx;
        }
        if (go.y + go.height > bi.height * bi.asset.tileHeight) {
            go.y = bi.height * bi.asset.tileHeight - go.height;
            go.dy = -go.dy;
        }

        if (go.x < 0.0f) {
            go.x = 0.0f;
            go.dx = -go.dx;
        }
        if (go.y < 0.0f) {
            go.y = 0.0f;
            go.dy = -go.dy;
        }
    }

    /**
     * Add a Game object to the managed objects list.
     * If the <code>go</code> demo.object.GameObject is a demo.object.Camera instance, it will be set as the default camera.
     *
     * @param go the demo.object.GameObject to be added to the demo.DemoGame#objects list.
     */
    public void addObject(GameObject go) {
        if (go instanceof Camera) {
            this.camera = (Camera) go;
        } else if (!objects.containsKey(go.name)) {

            objects.put(go.name, go);
            renderer.add(go);

        }
    }

    /**
     * Add a bunch of object to the game !
     *
     * @param objects the list of demo.object.GameObject to be added to the demo.DemoGame#objects list.
     */
    public void addAllObject(List<GameObject> objects) {
        for (GameObject o : objects) {
            addObject(o);
        }
    }

    public void removeObject(GameObject go) {
        objects.remove(go.name);
        renderer.remove(go);
    }

    public void removeObject(String name) {
        if (objects.containsKey(name)) {
            GameObject go = objects.get(name);
            renderer.remove(go);
            objects.remove(go);
        }
    }

    public void removeFilteredObjects(String nameFilter) {
        List<GameObject> toBeRemoved = new ArrayList<>();
        for (GameObject go : objects.values()) {
            if (go.name.contains(nameFilter)) {
                toBeRemoved.add(go);
            }
        }
        if (!toBeRemoved.isEmpty()) {
            renderer.removeAll(toBeRemoved);
            objects.values().removeAll(toBeRemoved);
            toBeRemoved.clear();
        }
    }


    public void run() {
        System.out.println("Run game");
        initialize();
        loop();
        System.out.println("Game stopped");
        System.exit(0);
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
            default:
                break;
        }
    }

    public abstract void drawHUD(Renderer r, Graphics2D g);
}