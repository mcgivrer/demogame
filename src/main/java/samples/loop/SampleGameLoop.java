package samples.loop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics2D;
import javax.swing.JFrame;

import samples.DefaultSample;

/**
 * project : DemoGame SampleGameLoop is a demonstration of a simple GameLoop the
 * core hearth of every game.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
public class SampleGameLoop extends DefaultSample implements KeyListener {
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

    // precious data about square's position, speed and color
    int x = 0;
    int y = 0;
    int dx = 0;
    int dy = 0;
    int maxD = 2;
    Color color;

    // internal rendering information.
    Color collidingColor;
    Color squareColor;

    /**
     * Let's create a SampleGameLoop process.
     * 
     * @param title  title for the window.
     * @param width  width for this window.
     * @param height height for this window.
     */
    public SampleGameLoop(String title, int width, int height, int s) {
        super(title,width,height,s);
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
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
    }

    /*----- the KeyListener interface corresponding implementation -----*/
    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                dy = (dy < maxD ? dy - 1 : dy);
                break;
            case KeyEvent.VK_DOWN:
                dy = (dx >= -maxD ? dy + 1 : dy);
                break;
            case KeyEvent.VK_LEFT:
                dx = (dx >= -maxD ? dx - 1 : dx);
                break;
            case KeyEvent.VK_RIGHT:
                dx = (dx < maxD ? dx + 1 : dx);
                break;
            case KeyEvent.VK_ESCAPE:
                exit = true;
                break;
            case KeyEvent.VK_D:
                debug = (debug < 5 ? debug + 1 : 0);
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
     * In a near futur, I am quite sure we will have some vaiables or objects to be
     * initialized.
     */
    public void initialize() {
        frame.addKeyListener(this);
        x = (screenBuffer.getWidth() - 16) / 2;
        y = (screenBuffer.getHeight() - 16) / 2;
        collidingColor = Color.WHITE;
        squareColor = Color.RED;
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
            update(elapsed);
            render(0);
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
        this.color = squareColor;
        x += dx;
        y += dy;
        if (x > screenBuffer.getWidth() - 16) {
            x = screenBuffer.getWidth() - 16;
            dx = -dx;
            this.color = collidingColor;
        }
        if (y >= screenBuffer.getHeight() - 16) {
            y = screenBuffer.getHeight() - 16;
            dy = -dy;
            this.color = collidingColor;
        }
        if (x <= 0) {
            x = 0;
            dx = -dx;
            this.color = collidingColor;
        }
        if (y <= 0) {
            y = 0;
            dy = -dy;
            this.color = collidingColor;
        }
    }

    /**
     * Render the image according to already updated objects.
     */
    public void render(long realFps) {
        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        g.setColor(this.color);
        g.fillRect(x, y, 16, 16);

        // render to screen
        Graphics2D sg = (Graphics2D) frame.getContentPane().getGraphics();
        sg.drawImage(screenBuffer, 0, 0, screenBuffer.getWidth() * scale, screenBuffer.getHeight() * scale, 0, 0,
                screenBuffer.getWidth(), screenBuffer.getHeight(), null);

        // Add some debug information
        if (debug > 1) {
            sg.setColor(Color.ORANGE);
            sg.drawString(String.format("debug:%d", debug), 10, 16);
            if (debug > 2) {
                sg.drawString(String.format("pos:%03d,%03d", x, y), (x + 18) * scale, y * scale);
            }
        }

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
        SampleGameLoop sgl = new SampleGameLoop("Sample Game Loop", 320, 240, 2);
        sgl.run();
    }

}