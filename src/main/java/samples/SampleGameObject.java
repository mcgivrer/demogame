package samples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import lombok.extern.slf4j.Slf4j;
import samples.GameObject.GameObjectType;

/**
 * project : DemoGame
 * <p>
 * SampleGameObject is a demonstration of a using GameObject to animate things.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
@Slf4j
public class SampleGameObject implements KeyListener {

    // Internal Renderinf buffer
    BufferedImage screenBuffer;
    // the Java Window to contains the game
    JFrame frame;
    // a flag to request to exit from this sample
    boolean exit = false;
    // how many frames per second on this screen ?
    int FPS = 30;
    // scaling factor
    int scale = 1;
    // debug display mode
    int debug = 0;
    // pause flag
    boolean pause = false;

    // internal rendering information.
    Color collidingColor;
    Color squareColor;

    // list of managed objects
    Map<String, GameObject> objects = new HashMap<>();

    /**
     * Let's create a SampleGameLoop process.
     * 
     * @param title  title for the window.
     * @param width  width for this window.
     * @param height height for this window.
     */
    public SampleGameObject(String title, int width, int height, int s) {
        scale = s;
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
        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(4);
        }
        log.info("JFrame created with height={}, width={}, with a BufferedStrategy of {} buffers", height, width, 4);
    }

    /*----- the KeyListener interface corresponding implementation -----*/
    @Override
    public void keyPressed(KeyEvent e) {

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
                reshuffleVelocity();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
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
    private void reshuffleVelocity() {
        for (GameObject go : objects.values()) {
            if (!go.name.equals("gameobject_1")) {
                go.dx = (int) (Math.random() * 8) - 4;
                go.dy = (int) (Math.random() * 8) - 4;
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
        for (int i = 0; i < 20; i++) {
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
        try{
            BufferedImage sprites = ImageIO.read(this.getClass().getResourceAsStream("/res/images/tileset-1.png"));
            
            GameObject player = objects.get("gameobject_1");
            player.type = GameObjectType.IMAGE;
            player.image = sprites.getSubimage(0,48,32,32);
            player.width = player.image.getWidth();
            player.height = player.image.getHeight();

        }catch(IOException ioe){
            log.error("unable to read the tileset image");
        }
    }

    private GameObjectType randomType(){
        // all type but not IMAGE => max 4
        int vt = (int)(Math.random()*4);
        return GameObjectType.values()[vt];
    }

    /**
     * The main loop or our Game Loop !
     */
    public void loop() {
        long nextTime = System.currentTimeMillis();
        long prevTime = nextTime;
        long elapsed = 0;
        while (!exit) {
            nextTime = System.currentTimeMillis();
            if (!pause) {
                update(elapsed);
            }
            render();
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
    public void update(long elapsed) {
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

    private void constrainGameObject(GameObject go) {
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
    public void render() {

        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        // loop objects
        for (GameObject go : objects.values()) {
            go.draw(this, g);
        }

        drawToScreen();
    }

    private void drawToScreen() {
        // render to screen
        BufferStrategy bs = frame.getBufferStrategy();
        Graphics2D sg = (Graphics2D) bs.getDrawGraphics();
        
        sg.drawImage(screenBuffer, 0, 0, screenBuffer.getWidth() * scale, screenBuffer.getHeight() * scale, 0, 0,
                screenBuffer.getWidth(), screenBuffer.getHeight(), null);
        // Add some debug information
        if (debug > 1) {
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                for (GameObject go : objects.values()) {
                if (debug > 2) {
                    displayDebug(sg, go);
                }
            }
            sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
            sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
            sg.setColor(Color.ORANGE);
            sg.drawString(String.format("debug:%d | pause:%s", debug, (pause ? "on" : "off")), 10,
                    frame.getHeight() - 4);
        }
        bs.show();
    }

    /**
     * Display debug information for the GameObject.
     * 
     * @param sg the Graphics2D API to be used
     * @param go the GameObject to dsplay debug for.
     */
    private void displayDebug(Graphics2D sg, GameObject go) {
        Font f = sg.getFont().deriveFont(9);
        sg.setFont(f);
        FontMetrics fm = sg.getFontMetrics();
        int lineHeight = fm.getHeight();
        int xOffset = (go.x + go.width + 8);

        sg.setColor(Color.DARK_GRAY);
        sg.fillRect((xOffset-4) * scale, go.y * scale, 150, 6*lineHeight);

        sg.setColor(Color.ORANGE);
        sg.drawString(
                String.format("name:%s", go.name), 
                xOffset * scale,
                (go.y * scale) + (1 * lineHeight));
        sg.drawString(
                String.format("pos:%03d,%03d", go.x, go.y), 
                xOffset * scale,
                (go.y * scale) + (2 * lineHeight));
        sg.drawString(
                String.format("vel:%03d,%03d", go.dx, go.dy), 
                xOffset * scale,
                (go.y * scale) + (3 * lineHeight));
        sg.drawString(
                String.format("type:%s", go.type.name()), 
                xOffset * scale,
                (go.y * scale) + (4 * lineHeight));
        sg.drawString(
                String.format("siz:%03d,%03d", go.width, go.height), 
                xOffset * scale,
                (go.y * scale) + (5 * lineHeight));
    }

    /**
     * Witing for the FPS requested.
     * 
     * @param elapsed
     */
    public void waitNext(long elapsed) {
        long waitTime = (1000 / FPS) - elapsed;
        try {
            Thread.sleep(waitTime > 0 ? waitTime : 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleGameObject sgl = new SampleGameObject("Sample Game Loop", 320, 240, 2);
        sgl.run();
    }

}