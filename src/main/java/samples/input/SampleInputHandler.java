package samples.input;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import lombok.extern.slf4j.Slf4j;
import samples.camera.Camera;
import samples.camera.SampleGameSystemManagerCamera;
import samples.object.GameObject;
import samples.object.GameObject.GameObjectType;
import samples.system.GameSystemManager;

/**
 * A Sample Game with an external InputHandler.
 */
@Slf4j
public class SampleInputHandler extends SampleGameSystemManagerCamera implements InputHandlerListener {

    public SampleInputHandler(String title, int w, int h, int s) {
        super(title, w, h, s);
        FPS = 60;
    }

    protected void createWindow(String title, int width, int height, int scale) {
        screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        Insets ins = frame.getInsets();
        Dimension dim = new Dimension((width * scale) - (ins.left + ins.right),
                (height * scale) - (ins.top + ins.bottom));
        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.pack();
        frame.addKeyListener(this);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB), 
                new Point(0,0), 
                "transparent_cursor"));
        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(4);
        }
    }


    @Override
    public void initialize() {
        gsm = GameSystemManager.initialize(this);

        InputHandler ih = new InputHandler(this);
        // add this new GameSystem to the manager
        gsm.add(ih);
        ih.register(this);

        frame.addKeyListener(ih);
        frame.addMouseListener(ih);
        frame.addMouseMotionListener(ih);
        frame.addMouseWheelListener(ih);

        load();
    }

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
            player.attributes.put("elasticity", 0.0);
            objects.put(player.name, player);

            MouseCursor mCursor = new MouseCursor("mouse_cursor");
            objects.put(mCursor.name, mCursor);

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }

        camera = new Camera("cam1", objects.get("player"), 0.018f,
                new Rectangle(screenBuffer.getWidth(), screenBuffer.getHeight()));
        objects.put(camera.name, camera);
    }

    private void createObjects(int max) {
        for (int i = 0; i < max; i++) {
            GameObject go = new GameObject();
            go.x = (int) Math.random() * (screenBuffer.getWidth() - 16);
            go.y = (int) Math.random() * (screenBuffer.getHeight() - 16);
            go.width = 16;
            go.height = 16;
            go.maxD = 4;
            go.dx = (int) (Math.random() * 8);
            go.dy = (int) (Math.random() * 8);
            go.color = squareColor;

            go.attributes.put("elasticity", 1.0);

            go.type = randomType();

            objects.put(go.name, go);
            log.info("Add e new GameObject named {}", go.name);
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

    public void input(InputHandler ih) {
        final List<String> excludedObjects = Arrays.asList("player","mouse_cursor");

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

    /**
     * The main loop or our Game Loop !
     */
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
        // loop objects
        for (GameObject go : objects.values()) {
            if (!go.name.equals("player")) {
                go.color = squareColor;
            }
            go.update(this, elapsed);
            if (!(go.name.equals("camera") || go.name.equals("mouse_cursor"))) {
                constrainGameObject(go);
            }
        }
    }

    protected void constrainGameObject(GameObject go) {
        double elasticity = (go.attributes.containsKey("elasticity") ? (double) go.attributes.get("elasticity") : 0.0);
        if (go.x > screenBuffer.getWidth() - go.width) {
            go.x = screenBuffer.getWidth() - go.width;
            go.dx = -go.dx * elasticity;
            go.color = collidingColor;
        }
        if (go.y >= screenBuffer.getHeight() - go.height) {
            go.y = screenBuffer.getHeight() - go.height;
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

    public void render(long realFps) {
        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        if (camera != null) {
            g.translate(-camera.x, -camera.y);
        }

        // loop objects
        for (GameObject go : objects.values()) {
            go.draw(this, g);
        }
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, camera.viewport.width, camera.viewport.height);

        if (camera != null) {
            g.translate(camera.x, camera.y);
        }
        drawToScreen(camera, realFps);
    }

    protected void displayGlobalDebug(Graphics2D sg,long realFps) {
        sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
        sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
        sg.setColor(Color.ORANGE);
        sg.drawString(String.format(
                "FPS: %d | debug:%d | pause:%s | cam:%s (zoom:%f)", 
                realFps, 
                debug, 
                (pause ? "on" : "off"),
                camera.name,
                (camera!=null?camera.zoomFactor:1)), 
                10, frame.getHeight() - 4);
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleInputHandler sgl = new SampleInputHandler("Sample With InputHandler", 320, 240, 2);
        sgl.run();
    }
}