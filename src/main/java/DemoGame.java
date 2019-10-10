import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
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
    private List<GameObject> renderingObjectPipeline = new ArrayList<>();

    private MapLevel mapLevel;
    private MapRenderer mapRenderer = new MapRenderer();

    private int score = 0;
    private int lifes = 4;

    /**
     * Create the Game container.
     *
     * @param argc list of arguments.
     * @see DemoGame#analyzeArgc(String[])
     */
    public DemoGame(String[] argc) {
        super();
        config = analyzeArgc(argc);
        jf = createWindow(config);
        screenBuffer = new BufferedImage(config.screenWidth, config.screenHeight, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * The famous java Execution entry point.
     *
     * @param argc
     */
    public static void main(String[] argc) {
        DemoGame dg = new DemoGame(argc);
        dg.run();
    }

    public void initialize() {

        mapLevel = MapReader.readFromFile("res/maps/map_1.json");

        addObject(mapLevel.player);
        addAllObject(mapLevel.enemies);

        // Create enemies
        /*for (int i = 0; i < 20; i++) {

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
        }*/

        // read a map


        // Create camera
        Camera cam = new Camera("camera", mapLevel.player, 0.002f, new Dimension(mapLevel.width, mapLevel.height));
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

        mapLevel.player.setSpeed(0.0f, 0.0f);

        if (keys[KeyEvent.VK_UP]) {
            mapLevel.player.dy = -0.2f;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            mapLevel.player.dy = 0.2f;
        }
        if (keys[KeyEvent.VK_LEFT]) {
            mapLevel.player.dx = -0.2f;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            mapLevel.player.dx = 0.2f;
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
                constrainToMapLevel(mapLevel, go);
            }
        }
        // active Camera update
        if (this.camera != null) {
            camera.update(this, elapsed);
        }
    }

    public void constrainToMapLevel(MapLevel bi, GameObject go) {
        if (go.x + go.width > bi.width*bi.asset.tileWidth) {
            go.x = bi.width*bi.asset.tileWidth - go.width;
            go.dx = -go.dx;
        }
        if (go.y + go.height > bi.height*bi.asset.tileHeight) {
            go.y = bi.height*bi.asset.tileHeight - go.height;
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

        mapRenderer.render(this, g, mapLevel, camera);

        // draw all objects
        for (GameObject go : renderingObjectPipeline) {
            if (!(go instanceof Camera)) {
                go.render(this, g);
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
            float sX = jf.getWidth() / config.screenWidth;
            float sY = jf.getHeight() / config.screenHeight;

            if (g != null) {
                g.drawImage(screenBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, config.screenWidth, config.screenHeight,
                        Color.BLACK, null);
                if (config.debug > 0) {
                    g.setColor(Color.ORANGE);
                    g.drawString(
                            String.format("debug:%d | cam:(%03.1f,%03.1f) | player:(%03.1f,%03.1f)",
                                    config.debug,
                                    camera.x, camera.y,
                                    mapLevel.player.x, mapLevel.player.y),
                            4, jf.getHeight() - 20);

                    for (GameObject go : renderingObjectPipeline) {
                        displayDebugInfo(g, go, camera, sX, sY);
                    }
                }
                g.dispose();
            }
        }
    }

    public void displayDebugInfo(Graphics2D g, GameObject go, Camera cam, float sX, float sY) {
        Font debugFont = g.getFont().deriveFont(5.0f);
        if (config.debug > 1) {
            float offsetX = go.x + go.width + 2 - cam.x;
            float offsetY = go.y - cam.y;

            g.setColor(Color.LIGHT_GRAY);
            g.drawString(
                    String.format("name:%s", go.name),
                    (offsetX * sX), offsetY * sY);
            g.drawString(
                    String.format("pos:(%03.1f,%03.1f)",
                            go.x, go.y),
                    (offsetX * sX), (offsetY + 10) * sY);
            g.drawString(
                    String.format("vel:(%03.1f,%03.1f)",
                            go.dx, go.dy),
                    (offsetX * sX), (offsetY + 20) * sY);
            g.drawString(
                    String.format("debug:%d",
                            config.debug),
                    (offsetX) * sX, (offsetY + 30) * sY);
        }
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
            renderingObjectPipeline.add(go);

            Collections.sort(renderingObjectPipeline, new Comparator<GameObject>() {
                public int compare(GameObject g1, GameObject g2) {
                    return (g1.priority < g2.priority ? (g1.layer < g2.layer ? 1 : -1) : -1);
                }
            });
        }
    }

    /**
     * Add a bunch of object to the game !
     *
     * @param objects
     */
    public void addAllObject(List<GameObject> objects) {
        for (GameObject o : objects) {
            addObject(o);
        }
    }

    public void removeObject(GameObject go) {
        objects.remove(go.name);
        renderingObjectPipeline.remove(go);
    }

    public void removeObject(String name) {
        if (objects.containsKey(name)) {
            GameObject go = objects.get(name);
            renderingObjectPipeline.remove(go);
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
            renderingObjectPipeline.removeAll(toBeRemoved);
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
                    System.out.println(String.format("Unknown arguments '%s'", arg));
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
        public String image;
        public String type;
        public String clazz;

        public String offset;
        public String size;
        public int offsetX, offsetY, width, height;

        public BufferedImage imageBuffer;

        public boolean collectible;
        public boolean hit;
        public boolean block;

        public int money;
        public int damage;
        public int energy;

        public Map<String, Object> attributes = new HashMap<>();

        public boolean levelOutput;
        public String nextLevel;
    }

    public class MapObjectAsset {
        public String name;
        public String image;
        public BufferedImage imageBuffer;

        public int tileWidth, tileHeight;

        public Map<String, MapObject> objects;
    }

    public class MapLevel {

        public String levelName;
        public String description;

        public String objects;
        public MapObjectAsset asset;

        public int width;
        public int height;

        public String background;
        public BufferedImage backgroundImage;
        public List<String> map = new ArrayList<>();

        public MapObject[][] tiles;

        public String nextLevel;
        public GameObject player;
        public List<GameObject> enemies = new ArrayList<>();
    }

    public static class MapReader {
        private static int idxEnemy = 0;

        public static MapLevel readFromFile(String fileMap) {
            MapLevel mapLevel = null;
            try {
                // load level from json file
                String jsonDataString = new String(Files.readAllBytes(Paths.get(MapReader.class.getResource(fileMap).toURI())));
                if (!jsonDataString.equals("")) {
                    Gson gson = new Gson();
                    mapLevel = gson.fromJson(jsonDataString, MapLevel.class);
                    mapLevel.width = mapLevel.map.get(0).length();
                    mapLevel.height = mapLevel.map.size();

                    // load asset from json file.
                    String jsonAssetString = new String(Files.readAllBytes(Paths.get(MapReader.class.getResource("res/assets/" + mapLevel.objects + ".json").toURI())));
                    if (!jsonAssetString.equals("")) {
                        MapObjectAsset mop = gson.fromJson(jsonAssetString, MapObjectAsset.class);
                        mapLevel.asset = mop;

                        // generate all objects.
                        mapLevel = generateObject(mapLevel);

                        // generate tiles
                        mapLevel.tiles = new MapObject[mapLevel.width][mapLevel.height];
                        for (int y = 0; y < mapLevel.height; y++) {
                            String line = mapLevel.map.get(y);
                            for (int x = 0; x < mapLevel.width; x++) {
                                String code = "" + line.charAt(x);
                                if (mapLevel.asset.objects.containsKey(code)) {
                                    mapLevel.tiles[x][y] = mapLevel.asset.objects.get(code);
                                } else {
                                    mapLevel.tiles[x][y] = null;
                                }
                            }
                        }
                    }
                }

            } catch (IOException | URISyntaxException e) {
                System.out.println("Unable to create MapLevel from Json");
            }
            return mapLevel;
        }

        private static MapLevel generateObject(MapLevel mapLevel) {
            try {
                mapLevel.asset.imageBuffer = ImageIO.read(MapReader.class.getResource(mapLevel.asset.image).openStream());
                for (Entry<String, MapObject> emo : mapLevel.asset.objects.entrySet()) {
                    MapObject mo = emo.getValue();
                    if (mo != null) {
                        switch (mo.type) {
                            case "player":
                                Class<?> classO = Class.forName(mo.clazz);
                                GameObject player = (GameObject) classO.newInstance();
                                player = populateGo(mapLevel, player, mo);
                                mapLevel.player = player;
                                break;
                            case "enemy_":
                                Class<?> class1 = Class.forName(mo.clazz);
                                GameObject enemy = (GameObject) class1.newInstance();
                                enemy = populateGo(mapLevel, enemy, mo);
                                if (mapLevel.enemies == null) {
                                    mapLevel.enemies = new ArrayList<>();
                                }
                                mapLevel.enemies.add(enemy);
                                break;
                            case "tile":
                            case "object":
                            default:
                                if (mo.offset != null && mo.imageBuffer == null && mo.offset.equals("")) {
                                    String[] offsetValue = mo.offset.split(",");
                                    mo.offsetX = Integer.parseInt(offsetValue[0]);
                                    mo.offsetY = Integer.parseInt(offsetValue[1]);
                                    if (mo.size != null && !mo.size.equals("")) {
                                        String[] sizeValue = mo.offset.split(",");
                                        mo.width = Integer.parseInt(offsetValue[0]);
                                        mo.height = Integer.parseInt(offsetValue[1]);
                                    } else {
                                        mo.width = mapLevel.asset.tileWidth;
                                        mo.height = mapLevel.asset.tileHeight;
                                    }
                                    mo.imageBuffer = mapLevel.asset.imageBuffer.getSubimage(
                                            mo.offsetX * mapLevel.asset.tileWidth,
                                            mo.offsetY * mapLevel.asset.tileHeight,
                                            mo.width,
                                            mo.height);
                                }
                                mapLevel.asset.objects.put(emo.getKey(), mo);
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.out.println("unable to intantiate " + e.getMessage() +
                        "Stack:" + e.getStackTrace());
            }
            return mapLevel;
        }

        private static GameObject populateGo(MapLevel mapLevel, GameObject go, MapObject mo) {
            if (!mo.offset.equals("")) {
                String[] values = mo.offset.split(",");
                int ox = Integer.parseInt(values[0]);
                int oy = Integer.parseInt(values[1]);

                values = mo.size.split(",");
                go.width = Integer.parseInt(values[0]);
                go.height = Integer.parseInt(values[1]);
                //get image
                go.image = mapLevel.asset.imageBuffer.getSubimage(ox * mapLevel.asset.tileWidth, oy * mapLevel.asset.tileHeight, (int) go.width, (int) go.height);
            }
            go.name = mo.type.replace("_", "_" + (++idxEnemy));
            // initialize attributes
            go.attributes.putAll(mo.attributes);
            return go;
        }
    }


    /**
     * A Renderer for the MapLevel.
     */
    public class MapRenderer {
        /**
         * Rendering the MapLevel according to the camera position.
         *
         * @param dg     the DemoGame container
         * @param g      The graphics API to be used
         * @param map    The MapLevel to be rendered
         * @param camera the camera to be used as a point of view.
         */
        void render(DemoGame dg, Graphics2D g, MapLevel map, Camera camera) {
            int mWidth = map.map.get(0).length();
            int mHeight = map.map.size();

            for (int y = 0; y < mHeight; y++) {
                for (int x = 0; x < mWidth; x++) {
                    MapObject mo = map.tiles[x][y];
                    if (mo != null) {
                        g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
                    } else {
                        g.clearRect(x * map.asset.tileWidth, y * map.asset.tileHeight, map.asset.tileWidth, map.asset.tileHeight);
                    }
                }
            }
        }
    }


    /**
     * A 2D Camera to render scene from a constrained point of view.
     */
    public class Camera extends GameObject {

        public GameObject target;
        public float tween;
        public Dimension viewport;
        public float zoom = 1.0f;

        /**
         * Create a Camera <code>name</code> focusing on <code>target</code>, with a <code>tween</code> factor to
         * manage camera sensitivity, and in a <code>viewport</code> size.
         *
         * @param name     name of the new camera.
         * @param target   the GameObject to be followed by the camera.
         * @param tween    the tween factor to manage camera sensitivity.
         * @param viewPort the size of the display window.
         */
        public Camera(String name, GameObject target, float tween, Dimension viewPort) {
            super(name, target.x, target.y, viewPort.width, viewPort.height);
            this.target = target;
            this.tween = tween;
            this.viewport = viewPort;
        }

        /**
         * Update the camera according to the <code>elapsed</code> time.
         * Position is relative to the <code>target</code> object and the camera speed is computed through the <code>tween</code> factor.
         *
         * @param dg      the DemoGame container for this camera
         * @param elapsed the elapsed time since previous update.
         */
        public void update(DemoGame dg, float elapsed) {
            this.x += (target.x - ((float) (viewport.width+dg.screenBuffer.getWidth()) * 0.5f) - this.x) * tween * elapsed;
            this.y += (target.y - ((float) (viewport.height+dg.screenBuffer.getHeight()) * 0.5f) - this.y) * tween * elapsed;
            viewport.height *= zoom;
            viewport.width *= zoom;
        }

        /**
         * rendering of some (only) debug information.
         *
         * @param dg the containing game
         * @param g  the graphics API.
         */
        public void render(DemoGame dg, Graphics2D g) {
            if (dg.config.debug > 1) {
                g.setColor(Color.YELLOW);
                g.drawRect((int) this.x, (int) this.y, viewport.width, viewport.height);
            }
        }
    }

    /**
     * A configuration component to manage and use easily parameters.
     */
    public class Config {
        public int screenWidth;
        public int screenHeight;
        public float screenScale;
        public int fps;
        public String title;
        public int debug;

        /**
         * Initialization of default values for configuraiton.
         */
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
