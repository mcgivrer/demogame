package samples.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

import samples.Sample;
import samples.input.InputHandler;
import samples.object.GameObject;
import samples.system.AbstractGameSystem;

/**
 * Create a Rendering pipeline as a System, to be used in the main game class.
 * 
 * Add this System to the GameSystemManager, and when neede, just call the
 * {@link Renderer#render(SampleRendererSystem, long)}.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2020
 */
public class Renderer extends AbstractGameSystem {

    private int debug = 0;

    // Internal Renderinf buffer
    protected BufferedImage screenBuffer;
    // the Java Window to contains the game
    protected JFrame frame;

    protected Rectangle viewport;

    private List<GameObject> objects = new ArrayList<>();

    protected Renderer(Sample game) {
        super(game);
    }

    @Override
    public String getName() {
        return Renderer.class.getSimpleName();
    }

    private void createWindow(String title, int width, int height, double scale) {

        viewport = new Rectangle(width, height);

        screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        Insets ins = frame.getInsets();
        Dimension dim = new Dimension((int) ((width * scale) - (ins.left + ins.right)),
                (int) ((height * scale) - (ins.top + ins.bottom)));
        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.pack();
        // frame.addKeyListener();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "transparent_cursor"));
        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(4);
        }
    }

    @Override
    public int initialize(Sample game) {
        createWindow(game.getTitle(), game.getWidth(), game.getHeight(), game.getScale());
        return 0;
    }

    @Override
    public void dispose() {
        objects.clear();
        objects = null;
        screenBuffer = null;
        frame = null;
    }

    /**
     * Add an object to the render pipeline.
     * 
     * @param object
     *                   the GameObject to be added to the renderering pipeline. It
     *                   will be sorted regarding its layer and priority.
     */
    public void addObject(GameObject object) {
        objects.add(object);
        // Sort object list according to there belonging layer and their own priority.
        objects.sort(new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                // compare layer then priority or each object.
                return (o1.layer > o2.layer ? (o1.priority > o2.priority ? -1 : 1) : -1);
            }
        });
    }

    /**
     * Render the image according to already updated objects.
     * 
     * @param game
     *                    the parent Game running this service.
     * @param realFps
     *                    the real frame per seconds value to be rendered in debug
     *                    mode (if requested)
     */
    public void render(SampleRendererSystem game, long realFps) {

        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

        // loop objects
        for (GameObject go : game.getObjects().values()) {
            go.draw((Sample) this, g);
        }

        drawToScreen(realFps);
    }

    /**
     * Draw the screen buffer to the real frame according to the requested game
     * scale.
     * 
     * @param realFps
     *                    the frames per second rate to be renderd with the debug
     *                    mode.
     */
    private void drawToScreen(long realFps) {
        // render to screen
        BufferStrategy bs = frame.getBufferStrategy();
        Graphics2D sg = (Graphics2D) bs.getDrawGraphics();

        sg.drawImage(screenBuffer, 0, 0, (int) (game.getWidth() * game.getScale()),
                (int) (game.getHeight() * game.getScale()), 0, 0, game.getWidth(), game.getHeight(), null);
        // Add some debug information
        if (debug > 1) {
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (GameObject go : objects) {
                if (debug > 2) {
                    displayDebug(sg, go);
                }
            }
            displayGlobalDebug(sg, realFps);
        }
        bs.show();
    }

    /**
     * Display global debug information at bottom screen.
     * @param sg the graphics iunterface to be used to draw the debug info.
     * @param realFps the frames per second rate.
     */
    private void displayGlobalDebug(Graphics2D sg, long realFps) {
        sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
        sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
        sg.setColor(Color.ORANGE);
        sg.drawString(String.format("FPS: %d | debug:%d | pause:%s ", realFps, debug, (game.getPause() ? "on" : "off")),
                10, frame.getHeight() - 4);
    }

    /**
     * Display debug information for the GameObject.
     * 
     * @param sg
     *               the Graphics2D API to be used
     * @param go
     *               the GameObject to dsplay debug for.
     */
    private void displayDebug(Graphics2D sg, GameObject go) {
        Font f = sg.getFont().deriveFont(9);
        sg.setFont(f);
        FontMetrics fm = sg.getFontMetrics();
        int lineHeight = fm.getHeight();
        int xOffset = (int) ((go.x + go.width + 8) * game.getScale());
        int yOffset = (int) (go.y * game.getScale());

        sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
        sg.fillRect(xOffset - 4, yOffset, 150, 6 * lineHeight);

        sg.setColor(Color.ORANGE);
        drawString(sg, xOffset, yOffset, lineHeight, 1, String.format("name:%s", go.name));
        drawString(sg, xOffset, yOffset, lineHeight, 2, String.format("pos:%03.2f,%03.2f", go.x, go.y));
        drawString(sg, xOffset, yOffset, lineHeight, 3, String.format("vel:%03.2f,%03.2f", go.dx, go.dy));
        drawString(sg, xOffset, yOffset, lineHeight, 4, String.format("type:%s", go.type.name()));
        drawString(sg, xOffset, yOffset, lineHeight, 5, String.format("siz:%03.2f,%03.2f", go.width, go.height));
    }

    /**
     * A helper to draw quickly a debug string on screen.
     * 
     */
    private void drawString(Graphics2D sg, int xOffset, int yOffset, int lineHeight, int line, String message) {
        sg.drawString(message, xOffset, yOffset + (line * lineHeight));
    }

    /**
     * Add a KeyListener to the java JFrame.
     * 
     * @param ih
     *               the InputHandler to support.
     */
    public void addKeyListener(InputHandler ih) {
        frame.addKeyListener(ih);
        frame.addMouseListener(ih);
        frame.addMouseMotionListener(ih);
        frame.addMouseWheelListener(ih);
    }

    public Rectangle getViewport() {
        return viewport;
    }

}
