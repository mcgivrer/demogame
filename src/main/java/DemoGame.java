import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An extra class to demonstrate some basics to create a simple java game.
 *
 * @author Frédéric Delorme
 * @since 2019
 */
public class DemoGame implements KeyListener {

    private static long goIndex = 0;
    public Config config;
    private JFrame jf;
    private boolean exitRequest = false;
    private String[] argc;
    private boolean[] keys = new boolean[65536];
    private boolean[] previousKeys = new boolean[65536];

    private BufferedImage screenBuffer;
    private Camera camera;

    private Map<String, GameObject> objects = new ConcurrentHashMap<>();
    private List<GameObject> renderingObjectpipelines = new ArrayList<>();

    private GameObject player;

    private int score = 0;
    private int lifes = 4;


    public DemoGame(String[] argc) {
        super();
        config = analyzeArgc(argc);
        jf = createWindow(config);
        screenBuffer = new BufferedImage(config.screenWidth, config.screenHeight, BufferedImage.TYPE_INT_ARGB);
    }

    public static void main(String[] argc) {
        DemoGame dg = new DemoGame(argc);
        dg.run();
    }

    public void initialize() {

        // Create the player object
        player = new GameObject("player", config.screenWidth / 2, config.screenHeight / 2, 16, 16);
        player.foregroundColor = Color.BLUE;
        player.priority = 10;
        player.layer = 1;
        addObject(player);

        // Create enemies
        for (int i = 0; i < 20; i++) {

            float x = (float) (Math.random() * config.screenWidth);
            float y = (float) (Math.random() * config.screenHeight);
            float dx = (float) (Math.random() * 0.05f);
            float dy = (float) (Math.random() * 0.05f);

            GameObject go = new GameObject("enemy_" + i, x, y, 8, 8);
            go.setSpeed(dx, dy);
            go.layer = 2;
            go.priority = 100;
            go.type = GameObjectType.CIRCLE;
            go.foregroundColor = Color.ORANGE;
            addObject(go);
        }

        // Create camera
        Camera cam = new Camera("camera", player, 0.002f, new Dimension(config.screenWidth, config.screenHeight));
        addObject(cam);
    }

    /**
     * create a WXindow to host the game display according to Config object.
     *
     * @param config the Config object to be used as configuration reference.
     * @return a JFrame initialized conforming to config attributes.
     */
    public JFrame createWindow(Config config) {
        jf = new JFrame(config.title);
        Insets ins = jf.getInsets();
        Dimension dim = new Dimension((int) (config.screenWidth * config.screenScale) - (ins.left + ins.right),
                (int) (config.screenHeight * config.screenScale) - (ins.top + ins.bottom));
        jf.setSize(dim);
        jf.setPreferredSize(dim);
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.addKeyListener(this);

        jf.setLocationByPlatform(true);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        return jf;
    }

    public void loop() {

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (!exitRequest) {
            startTime = System.currentTimeMillis();
            float elapsed = startTime - previousTime;
            input();
            update(elapsed);
            render();
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
        if (keys[KeyEvent.VK_ESCAPE]) {
            exitRequest = true;
        }

        player.setSpeed(0.0f, 0.0f);

        if (keys[KeyEvent.VK_UP]) {
            player.dy = -0.2f;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            player.dy = 0.2f;
        }
        if (keys[KeyEvent.VK_LEFT]) {
            player.dx = -0.2f;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            player.dx = 0.2f;
        }
        if (keys[KeyEvent.VK_SPACE]) {
            // Todo implement Jump
        }
    }

    /**
     * Update all the object according to elapsed time.
     *
     * @param elapsed
     */
    public void update(float elapsed) {

        // update all objects
        for (GameObject go : objects.values()) {
            if (!(go instanceof Camera)) {
                go.update(this, elapsed);
                constrainToViewport(screenBuffer, go);
            }
        }
        // active Camera update
        if (this.camera != null) {
            camera.update(this, elapsed);
        }
    }

    public void constrainToViewport(BufferedImage bi, GameObject go) {
        if (go.x + go.width > bi.getWidth()) {
            go.x = bi.getWidth() - go.width;
            go.dx = -go.dx;
        }
        if (go.y + go.height > bi.getHeight()) {
            go.y = bi.getHeight() - go.height;
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
     * Render all objects !
     */
    public void render() {
        Graphics2D g = screenBuffer.createGraphics();

        // activate Antialiasing for image and text rendering.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        // clear image
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, config.screenWidth, config.screenHeight);

        // if a camera is set, use it.
        if (camera != null) {
            g.translate(-camera.x, -camera.y);
        }

        // draw all objects
        for (GameObject go : renderingObjectpipelines) {
            if (!(go instanceof Camera)) {
                go.render(this, g);
            }
        }
        // if required, display debug info on GameObjects
        if (config.debug > 3) {
            for (GameObject go : renderingObjectpipelines) {
                if (!(go instanceof Camera)) {
                    displayDebugInfo(g, go);
                }
            }
        }

        // if a camera is set, use it.
        if (camera != null) {
            g.translate(camera.x, camera.y);
        }

        // draw score
        int offsetX = 4, offsetY = 30;
        Font f = g.getFont();
        g.setFont(f.deriveFont(8));
        drawOutLinedText(g, String.format("%05d", score), offsetX, offsetY, Color.WHITE, Color.BLACK);
        // draw Lifes
        String lifeStr = "[x]";
        drawOutLinedText(g, String.format("%s", String.format("%0" + lifes + "d", 0).replace("0", lifeStr)), config.screenWidth - (60 + offsetX), offsetY, Color.GREEN, Color.BLACK);
        g.setFont(f);
        g.dispose();

        // render image to real screen (applying scale factor)
        renderToScreen();
    }

    public void renderToScreen() {
        if (jf != null) {
            Graphics2D g = (Graphics2D) jf.getGraphics();
            if (g != null) {
                g.drawImage(screenBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, config.screenWidth, config.screenHeight,
                        Color.BLACK, null);
                if (config.debug > 1) {
                    g.setColor(Color.ORANGE);
                    g.drawString(
                            String.format("debug:%d | cam:(%f,%f) | player:(%f,%f)", config.debug, camera.x, camera.y, player.x, player.y),
                            4, jf.getHeight() - 20);
                }
                g.dispose();
            }
        }
    }

    public void displayDebugInfo(Graphics2D g, GameObject go) {
        // TODO implement debug data
    }

    /**
     * Add a Game object to the managed objects list.
     * If the <code>go</code> GameObject is a Camera instance, it will be set as the default camera.
     *
     * @param go the GameObject to be added to the DemoGame#objects list.
     */
    public void addObject(GameObject go) {
        if (go instanceof Camera) {
            this.camera = (Camera) go;
        } else if (!objects.containsKey(go.name)) {

            objects.put(go.name, go);
            renderingObjectpipelines.add(go);

            Collections.sort(renderingObjectpipelines, new Comparator<GameObject>() {
                public int compare(GameObject g1, GameObject g2) {
                    return (g1.priority < g2.priority ? (g1.layer < g2.layer ? 1 : -1) : -1);
                }
            });
        }
    }

    public void removeObject(GameObject go) {
        objects.remove(go.name);
        renderingObjectpipelines.remove(go);
    }

    public void removeObject(String name) {
        if (objects.containsKey(name)) {
            GameObject go = objects.get(name);
            renderingObjectpipelines.remove(go);
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
            renderingObjectpipelines.removeAll(toBeRemoved);
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

    /**
     * draw an outline text at (x,y) with textColor and a borderColor.
     *
     * @param g
     * @param text
     * @param x
     * @param y
     * @param textColor
     * @param borderColor
     */
    public void drawOutLinedText(Graphics2D g, String text, int x, int y, Color textColor, Color borderColor) {
        g.setColor(borderColor);
        g.drawString(text, x - 1, y);
        g.drawString(text, x, y - 1);
        g.drawString(text, x + 1, y);
        g.drawString(text, x, y + 1);

        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    public Config analyzeArgc(String[] argc) {
        Config config = new Config();
        config.title = "DemoGame";
        config.screenWidth = 320;
        config.screenHeight = 200;
        config.screenScale = 2.0f;
        config.debug = 0;
        config.fps = 60;

        for (String arg : argc) {
            System.out.println(String.format("arg: %s", arg));
            String[] parts = arg.split("=");
            switch (parts[0]) {
                case "f":
                case "fps":
                    config.fps = Integer.parseInt(parts[1]);
                    break;
                case "t":
                case "title":
                    config.title = parts[1];
                    break;
                case "h":
                case "height":
                    config.screenHeight = Integer.parseInt(parts[1]);
                    break;
                case "w":
                case "width":
                    config.screenWidth = Integer.parseInt(parts[1]);
                    break;
                case "s":
                case "scale":
                    config.screenScale = Float.parseFloat(parts[1]);
                    break;
                case "d":
                case "debug":
                    config.debug = Integer.parseInt(parts[1]);
                default:
                    System.out.println(String.format("Unkonwn arguments '%s'", arg));
                    break;
            }
        }
        return config;
    }

    /**
     * Game object type definition.
     */
    public enum GameObjectType {
        RECTANGLE, CIRCLE, IMAGE
    }

    public class MapObject {
        public String id;
        public BufferedImage image;

        public boolean collectable;
        public boolean hit;
        public boolean block;

        public int money;
        public int damage;
        public int energy;

        public boolean levelOutput;
        public String nextLevel;
    }

    public class MapObjectAsset {
        public String name;
        public String imageName;
        public BufferedImage image;
        public int tileWidth, tileHeight;
        public Map<String, MapObject> objects;
    }

    public class MapLevel {

        public String levelName;
        public String description;

        public String objectSet;
        public MapObjectAsset objects;

        public int width;
        public int height;

        public String background;
        public BufferedImage backgroundImage;
        public List<String> map = new ArrayList<>();

        public String nextLevel;
        GameObject player;
        List<GameObject> enemies;

        public void readFromFile(String filemap) throws IOException {

        }
    }

    /**
     * Class defining any object displayed by the game.
     */
    public class GameObject {

        private final int id = (int) goIndex++;

        public String name = "noname_" + id;

        public BufferedImage image;

        public float x, y;
        public float width, height;

        public float dx = 0, dy = 0;

        public int layer = 0;
        public int priority = 0;

        public GameObjectType type;

        public Color foregroundColor = Color.RED;
        public Color backgroundColor = Color.BLACK;

        public Map<String, Object> attributes = new HashMap<>();

        public GameObject(String name, float x, float y, float width, float height) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = GameObjectType.RECTANGLE;
        }

        public void update(DemoGame dg, float elapsed) {
            x += (dx * elapsed);
            y += (dy * elapsed);
        }

        public void render(DemoGame dg, Graphics2D g) {
            switch (type) {
                case RECTANGLE:
                    g.setColor(this.foregroundColor);
                    g.fillRect((int) x, (int) y, (int) width, (int) height);
                    break;
                case CIRCLE:
                    g.setColor(this.foregroundColor);
                    g.fillOval((int) x, (int) y, (int) width, (int) height);
                    break;
                case IMAGE:
                    g.drawImage(image, (int) x, (int) y, null);
                    break;

            }
        }

        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void setSpeed(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public void setSize(float width, float height) {
            this.width = width;
            this.height = height;
        }

    }

    public class Camera extends GameObject {

        public GameObject target;
        public float tween;
        Dimension viewport;

        public Camera(String name, GameObject target, float tween, Dimension viewPort) {
            super(name, target.x, target.y, viewPort.width, viewPort.height);
            this.target = target;
            this.tween = tween;
            this.viewport = viewPort;
        }

        public void update(DemoGame dg, float elapsed) {
            this.x += (target.x - ((float) viewport.width * 0.5f) - this.x) * tween * elapsed;
            this.y += (target.y - ((float) viewport.height * 0.5f) - this.y) * tween * elapsed;
        }

        public void render(DemoGame dg, Graphics2D g) {
            if (dg.config.debug > 1) {
                g.setColor(Color.YELLOW);
                g.drawRect((int) this.x, (int) this.y, viewport.width, viewport.height);
            }
        }
    }

    public class Config {
        public int screenWidth;
        public int screenHeight;
        public float screenScale;
        public int fps;
        public String title;
        public int debug;

        public Config() {
            this.title = "notitle";
            this.screenWidth = 0;
            this.screenHeight = 0;
            this.screenScale = 0f;
            this.fps = 0;
            this.debug = 0;
        }
    }

}
