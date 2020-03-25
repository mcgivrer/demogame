package samples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import lombok.extern.slf4j.Slf4j;

/**
 * project : DemoGame
 * <p>
 * SampleGameSysemManagerCamera is a demonstration of a using : - some
 * GameObject to animate things. - A SystemMnager ready to use - a Camera
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
@Slf4j
public class SampleGameSystemManagerCamera extends SampleGameObject{

    private Camera camera;

    public SampleGameSystemManagerCamera(String title, int w, int h, int s) {
        super(title, w, h, s);
        log.info("Sample System Manager ready...");
    }

    @Override
    public void initialize() {
        super.initialize();
        GameSystemManager.initialize(this);
        camera = new Camera("cam1", objects.get("gameobject_1"), 0.018f,
                new Rectangle(screenBuffer.getWidth(), screenBuffer.getHeight()));
        objects.put(camera.name,camera);
    }

    @Override
    public void update(long elapsed) {
        // loop objects
        for (GameObject go : objects.values()) {
            if (!go.name.equals("gameobject_1")) {
                go.color = squareColor;
            }
            go.update(this, elapsed);
            constrainGameObject(go);
        }
        camera.update(this, elapsed);
    }

    @Override
    public void render() {
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
        g.drawRect(0,0,camera.viewport.width,camera.viewport.height);

        if (camera != null) {
            g.translate(camera.x, camera.y);

        }
        drawToScreen(camera);
    }

    protected void drawToScreen(Camera camera) {
        // render to screen
        BufferStrategy bs = frame.getBufferStrategy();
        Graphics2D sg = (Graphics2D) bs.getDrawGraphics();

        sg.drawImage(screenBuffer, 0, 0, screenBuffer.getWidth() * scale, screenBuffer.getHeight() * scale, 0, 0,
                screenBuffer.getWidth(), screenBuffer.getHeight(), null);
        // Add some debug information
        if (debug > 1) {
            sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (camera != null) {
                sg.translate(-camera.x*scale, -camera.y*scale);
            }

            for (GameObject go : objects.values()) {
                if (debug > 2) {
                    displayDebug(sg, go);
                }
            }

            if (camera != null) {
                sg.translate(camera.x*scale, camera.y*scale);
    
            }
            displayGlobalDebug(sg);
        }
        bs.show();
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleGameSystemManagerCamera sgl = new SampleGameSystemManagerCamera("Sample With Camera", 320, 240, 2);
        sgl.run();
    }

}