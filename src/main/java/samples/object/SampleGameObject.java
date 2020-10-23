package samples.object;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import lombok.extern.slf4j.Slf4j;
import samples.DefaultSample;
import samples.object.GameObject.GameObjectType;

/**
 * project : DemoGame
 * <p>
 * SampleGameObject is a demonstration of a using GameObject to animate things.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
@Slf4j
public class SampleGameObject extends DefaultSample implements KeyListener {

    // Internal Renderinf buffer
    protected BufferedImage screenBuffer;
    // the Java Window to contains the game
    protected JFrame frame;
    // a flag to request to exit from this sample
    protected boolean exit = false;
    // how many frames per second on this screen ?
    protected int FPS = 30;
    // debug display mode
    protected int debug = 0;


    // internal rendering information.
    protected Color collidingColor;
    protected Color squareColor;

    public SampleGameObject(){
        
    }

    /**
     * Let's create a SampleGameLoop process.
     * 
     * @param title  title for the window.
     * @param width  width for this window.
     * @param height height for this window.
     */
    public SampleGameObject(String title, int width, int height, int s) {
        super(title, width, height, s);
        createWindow(title, width, height, s);
        log.info("JFrame created with height={}, width={}, with a BufferedStrategy of {} buffers", height, width, 4);
    }

    protected void createWindow(String title, int width, int height, double scale) {
        screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        Insets ins = frame.getInsets();
        Dimension dim = new Dimension((int)(width * scale) - (ins.left + ins.right),
                (int)(height * scale) - (ins.top + ins.bottom));
        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.pack();
        frame.addKeyListener(this);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(4);
        }
        Cursor transparent = frame.getToolkit().createCustomCursor(frame.getToolkit().getImage(""), new Point(),
                "trans");
        frame.setCursor(transparent);
    }

    /*----- the KeyListener interface corresponding implementation -----*/
    @Override
    public void keyPressed(KeyEvent e) {
        // nothing to do there.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        GameObject go = objects.get("gameobject_1");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                go.dy = (go.dy > -go.maxD ? go.dy - 1 : go.dy);
                break;
            case KeyEvent.VK_DOWN:
                go.dy = (go.dy < go.maxD ? go.dy + 1 : go.dy);
                break;
            case KeyEvent.VK_LEFT:
                go.dx = (go.dx > -go.maxD ? go.dx - 1 : go.dx);
                break;
            case KeyEvent.VK_RIGHT:
                go.dx = (go.dx < go.maxD ? go.dx + 1 : go.dx);
                break;
            case KeyEvent.VK_ESCAPE:
                exit = true;
                break;
            case KeyEvent.VK_SPACE:
                // Break the first object of the objects map.
                go.dx = 0;
                go.dy = 0;
                go.x = screenBuffer.getWidth() / 2;
                go.y = screenBuffer.getHeight() / 2;
                go.color = Color.BLUE;
                break;

            case KeyEvent.VK_D:
                debug = (debug < 5 ? debug + 1 : 0);
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_PAUSE:
                pause = !pause;
                break;
            case KeyEvent.VK_R:
                reshuffleVelocity(Arrays.asList("gameobject_1"));
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // nothing to do there.
    }

    /*----- the sample gameloop processing -----*/

    public void run() {
        initialize();
        loop();
        frame.dispose();
    }

    /**
     * generate randomly new velocity for all GameObject except for 'gameobject_1'.
     */
    protected void reshuffleVelocity(List<String> excludedObjects) {
        for (GameObject go : objects.values()) {
            if (!excludedObjects.contains(go.name)) {
                go.dx = (int) (Math.random() * 8) - 4;
                go.dy = (int) (Math.random() * 8) - 4;
                go.type = randomType();
            }
        }
    }

    /**
     * In a near futur, I am quite sure we will have some vaiables or objects to be
     * initialized.
     */
    public void initialize() {
        collidingColor = Color.WHITE;
        squareColor = Color.RED;
        createObjects(20);
        try {
            BufferedImage sprites = ImageIO.read(this.getClass().getResourceAsStream("/res/images/tileset-1.png"));

            GameObject player = objects.get("gameobject_1");
            player.type = GameObjectType.IMAGE;
            player.image = sprites.getSubimage(0, 48, 32, 32);
            player.width = player.image.getWidth();
            player.height = player.image.getHeight();

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }
    }

    protected void createObjects(int maxNbObjects) {
        for (int i = 0; i < maxNbObjects; i++) {
            GameObject go = new GameObject();
            go.x = (int) Math.random() * (screenBuffer.getWidth() - 16);
            go.y = (int) Math.random() * (screenBuffer.getHeight() - 16);
            go.width = 16;
            go.height = 16;
            go.maxD = 4;
            go.dx = (int) (Math.random() * 8);
            go.dy = (int) (Math.random() * 8);
            go.color = squareColor;

            go.type = randomType();

            objects.put(go.name, go);
            log.info("Add e new GameObject named {}", go.name);
        }
    }

    protected GameObjectType randomType() {
        // all type but NOT IMAGE => max 4
        int vt = (int) (Math.random() * 4);
        return GameObjectType.values()[vt];
    }

    /**
     * The main loop or our Game Loop !
     */
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

    /**
     * Compute updates for the frame.
     * 
     * @param elapsed
     */
    public void update(double elapsed) {
        // loop objects
        for (GameObject go : objects.values()) {
            if (!go.name.equals("gameobject_1")) {
                go.color = squareColor;
            } else {
                go.color = Color.BLUE;
            }
            go.update(this, elapsed);
            constrainGameObject(go);
        }
    }

    protected void constrainGameObject(GameObject go) {
        if (go.x > screenBuffer.getWidth() - go.width) {
            go.x = screenBuffer.getWidth() - go.width;
            go.dx = -go.dx;
            go.color = collidingColor;
        }
        if (go.y >= screenBuffer.getHeight() - go.height) {
            go.y = screenBuffer.getHeight() - go.height;
            go.dy = -go.dy;
            go.color = collidingColor;
        }
        if (go.x <= 0) {
            go.x = 0;
            go.dx = -go.dx;
            go.color = collidingColor;
        }
        if (go.y <= 0) {
            go.y = 0;
            go.dy = -go.dy;
            go.color = collidingColor;
        }
    }

    /**
     * Render the image according to already updated objects.
     */
    public void render(long realFps) {

        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        // loop objects
        for (GameObject go : objects.values()) {
            go.draw(this, g);
        }

        drawToScreen(realFps);
    }

    protected void drawToScreen(long realFps) {
        // render to screen
        BufferStrategy bs = frame.getBufferStrategy();
        Graphics2D sg = (Graphics2D) bs.getDrawGraphics();

        sg.drawImage(screenBuffer, 0, 0, (int) (width * scale), (int) (height * scale), 0, 0, width, height, null);
        // Add some debug information
        if (debug > 1) {
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (GameObject go : objects.values()) {
                if (debug > 2) {
                    displayDebug(sg, go);
                }
            }
            displayGlobalDebug(sg, realFps);
        }
        bs.show();
    }

    protected void displayGlobalDebug(Graphics2D sg, long realFps) {
        sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
        sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
        sg.setColor(Color.ORANGE);
        sg.drawString(String.format("FPS: %d | debug:%d | pause:%s ", realFps, debug, (pause ? "on" : "off")), 10,
                frame.getHeight() - 4);
    }

    /**
     * Display debug information for the GameObject.
     * 
     * @param sg the Graphics2D API to be used
     * @param go the GameObject to dsplay debug for.
     */
    protected void displayDebug(Graphics2D sg, GameObject go) {
        Font f = sg.getFont().deriveFont(9);
        sg.setFont(f);
        FontMetrics fm = sg.getFontMetrics();
        int lineHeight = fm.getHeight();
        int xOffset = (int) ((go.x + go.width + 8) * scale);
        int yOffset = (int) (go.y * scale);

        sg.setColor(Color.BLUE);
        switch(go.type){
            case ELLIPSE:
                sg.drawArc(
                    (int)(go.bbox.x*scale),(int)(go.bbox.y*scale), 
                    (int)(go.bbox.w*scale), (int)(go.bbox.h*scale),
                    0,360);
                break;
            default:
                sg.drawRect(
                    (int)(go.bbox.x*scale),(int)(go.bbox.y*scale), 
                    (int)(go.bbox.w*scale), (int)(go.bbox.h*scale));
                break;
        }

        sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
        sg.fillRect(xOffset - 4, yOffset, 150, 6 * lineHeight);

        sg.setColor(Color.ORANGE);
        drawString(sg, xOffset, yOffset, lineHeight, 1, String.format("name:%s", go.name));
        drawString(sg, xOffset, yOffset, lineHeight, 2, String.format("pos:%03.2f,%03.2f", go.x, go.y));
        drawString(sg, xOffset, yOffset, lineHeight, 3, String.format("vel:%03.2f,%03.2f", go.dx, go.dy));
        drawString(sg, xOffset, yOffset, lineHeight, 4, String.format("type:%s", go.type.name()));
        drawString(sg, xOffset, yOffset, lineHeight, 5, String.format("siz:%03.2f,%03.2f", go.width, go.height));
        int i=0;
        for(String d : go.debugInfo){
            drawString(sg, xOffset, yOffset, lineHeight, 5+(i++), d);
        }
    }

    protected void drawString(Graphics2D sg, int xOffset, int yOffset, int lineHeight, int line, String message) {
        sg.drawString(message, xOffset, yOffset + (line * lineHeight));
    }

    /**
     * Witing for the FPS requested.
     * 
     * @param elapsed
     */
    public void waitNext(double elapsed) {
        long waitTime = (1000 / FPS) - (long) elapsed;
        try {
            Thread.sleep(waitTime > 0 ? waitTime : 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.exit(-1);
            e.printStackTrace();
        }
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleGameObject sgl = new SampleGameObject("Sample Game Object", 320, 240, 2);
        sgl.run();
    }
}