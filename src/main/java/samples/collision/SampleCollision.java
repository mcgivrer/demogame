package samples.collision;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import samples.camera.Camera;
import samples.cli.SampleCliManager;
import samples.input.InputHandler;
import samples.input.MouseCursor;
import samples.object.GameObject;
import samples.object.GameObject.GameObjectType;
import samples.system.GameSystemManager;

/**
 * The Sample Collision class is an example of how to use the CollisionSystem.
 * 
 * @author Frédéric Delorme
 * @since 2020
 */
@Slf4j
public class SampleCollision extends SampleCliManager implements OnCollision {

    CollisionSystem cs;

    public SampleCollision(final String title, final String[] args) {
        super(title, args);
    }

    @Override
    public void initialize() {
        gsm = GameSystemManager.initialize(this);

        final InputHandler ih = new InputHandler(this);
        // add this new GameSystem to the manager
        gsm.add(ih);
        ih.register(this);

        frame.addKeyListener(ih);
        frame.addMouseListener(ih);
        frame.addMouseMotionListener(ih);
        frame.addMouseWheelListener(ih);

        // Add the CollisionSystem
        cs = new CollisionSystem(this, 500);
        gsm.add(cs);

        load();
    }

    public void addObject(GameObject go) {
        objects.put(go.name, go);
        cs.add(go);
    }

    public GameObject getObject(String name) {
        return objects.get(name);
    }

    @Override
    public void load() {
        collidingColor = Color.WHITE;
        squareColor = Color.RED;
        createObjects(5);
        try {
            BufferedImage sprites = ImageIO.read(this.getClass().getResourceAsStream("/res/images/tileset-1.png"));

            GameObject player = new GameObject("player");
            player.type = GameObjectType.IMAGE;
            player.image = sprites.getSubimage(0, 48, 32, 32);
            player.width = player.image.getWidth();
            player.height = player.image.getHeight();

            player.maxD = 4;
            player.x = (screenBuffer.getWidth() - player.image.getWidth()) / 2;
            player.y = (screenBuffer.getHeight() - player.image.getHeight()) / 2;
            player.dx = 0;
            player.dy = 0;

            player.attributes.put("elasticity", 0.0);
            player.debugInfo.add(String.format("collidable:%s", (player.collidable ? "true" : "false")));
            player.debugInfo.add(String.format("colliders:%d", player.colliders.size()));

            player.collidableList = "enemy";
            addObject(player);

            MouseCursor mCursor = new MouseCursor("mouse_cursor");
            addObject(mCursor);

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }

        camera = new Camera("cam1", getObject("player"), 0.005f,
                new Rectangle(screenBuffer.getWidth(), screenBuffer.getHeight()));

        addObject(camera);
    }

    protected void createObjects(int max) {
        for (int i = 0; i < max; i++) {
            GameObject go = new GameObject();
            go.name = "enemy_" + go.id;
            go.x = (int) Math.random() * (screenBuffer.getWidth() - 16);
            go.y = (int) Math.random() * (screenBuffer.getHeight() - 16);
            go.type = randomType();
            switch (go.type) {
                case POINT:
                    go.width = 1;
                    go.height = 1;
                    break;
                default:
                    go.width = 16;
                    go.height = 16;
                    break;
            }
            go.maxD = 4;
            go.dx = (int) (Math.random() * 2) - 1;
            go.dy = (int) (Math.random() * 2) - 1;
            go.color = squareColor;

            go.attributes.put("elasticity", 1.0);

            go.debugInfo.add(String.format("collidable:%s", (go.collidable ? "true" : "false")));
            go.debugInfo.add(String.format("colliders:%d", go.colliders.size()));

            addObject(go);
            log.info("Add e new GameObject named {}", go.name);
        }
    }

    @Override
    public void input(InputHandler ih) {
        final List<String> excludedObjects = Arrays.asList("player", "mouse_cursor");

        MouseCursor m = (MouseCursor) objects.get("mouse_cursor");
        m.x = ih.getMouseX() / scale;
        m.y = ih.getMouseY() / scale;

        GameObject go = objects.get("player");

        if (ih.getKey(KeyEvent.VK_UP)) {
            go.dy = (go.dy > -go.maxD ? go.dy - 1 : go.dy);
        }
        if (ih.getKey(KeyEvent.VK_DOWN)) {
            go.dy = (go.dy < go.maxD ? go.dy + 1 : go.dy);
        }
        if (ih.getKey(KeyEvent.VK_LEFT)) {
            go.dx = (go.dx > -go.maxD ? go.dx - 1 : go.dx);
        }
        if (ih.getKey(KeyEvent.VK_RIGHT)) {
            go.dx = (go.dx < go.maxD ? go.dx + 1 : go.dx);
        }
        if (ih.getKey(KeyEvent.VK_SPACE)) {
            // Break the first object of the objects map.
            go.dx = 0;
            go.dy = 0;
            go.color = Color.BLUE;
        }
        if (ih.getKey(KeyEvent.VK_R)) {
            reshuffleVelocity(excludedObjects);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                exit = true;
                break;
            case KeyEvent.VK_D:
                debug = (debug < 5 ? debug + 1 : 0);
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_PAUSE:
                pause = !pause;
                break;
            default:
                break;
        }
    }

    @Override
    public void loop() {
        long nextTime = System.currentTimeMillis();
        long prevTime = nextTime;
        double elapsed = 0;
        long timeFrame = 0;
        long frames = 0;
        long realFps = 0;
        while (!exit) {
            nextTime = System.currentTimeMillis();

            if (!pause) {
                input(gsm.getSystem(InputHandler.class));
                update(elapsed);
            }
            render(realFps);

            timeFrame += elapsed;
            frames++;
            if (timeFrame > 1000) {
                realFps = frames;
                frames = 0;
                timeFrame = 0;
            }

            elapsed = nextTime - prevTime;

            waitNext(elapsed);
            prevTime = nextTime;
        }
    }

    @Override
    public void update(double elapsed) {

        // reset collision system
        cs.clearEvents();

        // loop objects
        for (GameObject go : objects.values()) {
            if (!go.name.equals("player")) {
                go.color = squareColor;
            }
            go.update(this, elapsed);

            // verify collision for this object.
            cs.update(camera, elapsed);

            if (!(go.name.equals("cam1") || go.name.equals("mouse_cursor"))) {
                constrainGameObject(go);
            }
        }
        cs.processEvents(this);
    }

    @Override
    public void render(long realFps) {
        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        if (camera != null) {
            g.translate(-camera.x, -camera.y);
        }

        // draw play area
        g.setColor(new Color(0.0f, 0.0f, 0.3f));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0.6f, 0.6f, 0.6f));
        g.drawRect(0, 0, width, height);

        // loop objects
        for (GameObject go : objects.values()) {
            go.draw(this, g);
        }

        if (camera != null) {
            g.translate(camera.x, camera.y);
        }
        drawToScreen(camera, realFps);
    }

    @Override
    protected void drawToScreen(Camera camera, long realFps) {
        // render to screen
        BufferStrategy bs = frame.getBufferStrategy();
        Graphics2D sg = (Graphics2D) bs.getDrawGraphics();

        sg.drawImage(screenBuffer, 0, 0, (int) (width * scale), (int) (height * scale), 0, 0, (int) width, (int) height,
                null);
        // Add some debug information
        if (debug > 1) {
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (camera != null) {
                sg.translate(-camera.x * scale, -camera.y * scale);
            }
            for (GameObject go : objects.values()) {
                if (debug > 2) {
                    displayDebug(sg, go);
                }
            }
            if (camera != null) {
                sg.translate(camera.x * scale, camera.y * scale);

            }
            displayGlobalDebug(sg, realFps);
        }
        bs.show();
    }

    @Override
    protected void displayDebug(Graphics2D sg, GameObject go) {
        Font f = sg.getFont().deriveFont(9);
        sg.setFont(f);
        FontMetrics fm = sg.getFontMetrics();
        int lineHeight = fm.getHeight();
        int xOffset;
        int yOffset;

        Class<?> clazz = go.getClass();
        switch (clazz.getSimpleName()) {
            case "MouseCursor":
                xOffset = (int) (width * scale - 150);
                yOffset = (int) (height * scale - (4 * lineHeight));
                sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
                sg.fillRect(xOffset - 4, yOffset, 150, 3 * lineHeight);
                sg.setColor(Color.ORANGE);
                drawString(sg, xOffset, yOffset, lineHeight, 1, String.format("name:%s", go.name));
                drawString(sg, xOffset, yOffset, lineHeight, 2, String.format("pos:%03.2f,%03.2f", go.x, go.y));
                break;
            case "Camera":
                xOffset = (int) (1 * lineHeight);
                yOffset = (int) (3 * lineHeight);
                // draw camera debug information
                sg.setColor(Color.ORANGE);
                sg.drawRect((int) (go.x * scale) + 10, (int) (go.y * scale), (int) (width * scale) - 40,
                        (int) (height * scale) - 40);
                sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
                sg.fillRect((int) (go.x * scale) + 10, (int) (go.y * scale) + yOffset, 150, 3 * lineHeight);
                sg.setColor(Color.ORANGE);
                drawString(sg, (int) (go.x * scale) + 10 + xOffset, (int) (go.y * scale) + yOffset, lineHeight, 1,
                        String.format("name:%s", go.name));
                drawString(sg, (int) (go.x * scale) + 10 + xOffset, (int) (go.y * scale) + yOffset, lineHeight, 2,
                        String.format("pos:%03.2f,%03.2f", go.x, go.y));
                break;
            default:
                xOffset = (int) ((go.x + go.width + 8) * scale);
                yOffset = (int) (go.y * scale);

                sg.setColor(Color.BLUE);
                switch (go.type) {
                    case ELLIPSE:
                        sg.drawArc((int) (go.bbox.x * scale), (int) (go.bbox.y * scale), (int) (go.bbox.w * scale),
                                (int) (go.bbox.h * scale), 0, 360);
                        break;
                    default:
                        sg.drawRect((int) (go.bbox.x * scale), (int) (go.bbox.y * scale), (int) (go.bbox.w * scale),
                                (int) (go.bbox.h * scale));
                        break;
                }
                sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
                sg.fillRect(xOffset - 4, yOffset, 150, 6 * lineHeight);

                sg.setColor(Color.ORANGE);
                drawString(sg, xOffset, yOffset, lineHeight, 1, String.format("name:%s", go.name));
                drawString(sg, xOffset, yOffset, lineHeight, 2, String.format("pos:%03.2f,%03.2f", go.x, go.y));
                drawString(sg, xOffset, yOffset, lineHeight, 3, String.format("vel:%03.2f,%03.2f", go.dx, go.dy));
                drawString(sg, xOffset, yOffset, lineHeight, 4, String.format("type:%s", go.type.name()));
                drawString(sg, xOffset, yOffset, lineHeight, 5,
                        String.format("siz:%03.2f,%03.2f", go.width, go.height));
                int i = 0;
                for (String d : go.debugInfo) {
                    drawString(sg, xOffset, yOffset, lineHeight, 5 + (i++), d);
                }
                break;
        }
    }

    @Override
    protected void displayGlobalDebug(Graphics2D sg, long realFps) {
        sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
        sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
        sg.setColor(Color.ORANGE);
        sg.drawString(
                String.format("FPS: %d | debug:%d | pause:%s | cam:%s (zoom:%f)", realFps, debug,
                        (pause ? "on" : "off"), camera.name, (camera != null ? camera.zoomFactor : 1)),
                10, frame.getHeight() - 4);
    }

    public static void main(final String[] args) {
        final SampleCollision g = new SampleCollision("Sample Collision", args);
        g.run();
    }

    /**
     * Implementation for the Collision processing.
     */
    @Override
    public void collide(CollisionEvent e) {
        switch (e.type) {
            case COLLISION_OBJECT:
                e.a.dx = 0;
                e.a.dy = 0;
                e.a.collidingColor = Color.WHITE;
                e.b.dx = -e.b.dx;
                e.b.dy = -e.b.dy;
                e.b.collidingColor = Color.WHITE;
                break;
            default:
                break;
        }

    }

}