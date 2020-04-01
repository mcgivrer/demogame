package samples.camera;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import samples.input.MouseCursor;
import samples.input.SampleInputHandler;
import samples.object.GameObject;
import samples.object.GameObject.GameObjectType;

@Slf4j
public class SampleCameraZoom extends SampleInputHandler {

    Map<Integer, Layer> layers = new HashMap<>();

    public class Layer {
        public Layer(int priority) {
            this.priority = priority;
        }

        public int priority;
        public boolean fixed = false;
        List<GameObject> objects = new ArrayList<>();

        public void add(GameObject go) {
            objects.add(go);
            fixed = !go.fixed && !fixed;
            objects.sort(new Comparator<GameObject>() {
                public int compare(GameObject o1, GameObject o2) {
                    return (o1.priority > o2.priority ? 1 : -1);
                }
            });
        }
    }

    protected SampleCameraZoom() {

    }

    public SampleCameraZoom(String title, int w, int h, int s) {
        super(title, w, h, s);
        log.info("Sample System Manager ready...");
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
            player.x = (screenBuffer.getWidth() - player.image.getWidth()) / 2;
            player.y = (screenBuffer.getHeight() - player.image.getHeight()) / 2;
            player.dx = 0;
            player.dy = 0;
            player.attributes.put("elasticity", 0.0);
            player.layer = 1;
            addObject(player);

            MouseCursor mCursor = new MouseCursor("mouse_cursor");
            mCursor.layer = 2;
            mCursor.fixed = true;
            addObject(mCursor);

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }

        camera = new Camera("cam1", objects.get("player"), 0.005f,
                new Rectangle(screenBuffer.getWidth(), screenBuffer.getHeight()));
        camera.zoomFactor = 1.0;
        addObject(camera);
    }

    public void addObject(GameObject go) {
        if (!objects.containsKey(go.name)) {
            objects.put(go.name, go);
            dispatchToLayers(go);
        } else {
            log.error("the GameObject named {} already exists", go.name);
        }

    }

    private void dispatchToLayers(GameObject go) {
        if (!layers.containsKey(go.layer)) {
            layers.put(go.layer, new Layer(go.layer));
        }
        layers.get(go.layer).add(go);
    }

    @Override
    public void render(long realFps) {
        Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        for (Layer l : layers.values()) {
            if (camera != null && !l.fixed) {
                g.translate(-camera.x, -camera.y);
                //g.scale(camera.zoomFactor, camera.zoomFactor);
            }
            // loop objects
            for (GameObject go : l.objects) {
                go.draw(this, g);
            }
            g.setColor(Color.GRAY);
            g.drawRect(0, 0, camera.viewport.width, camera.viewport.height);

            if (camera != null && !l.fixed) {
                //g.scale(1 / camera.zoomFactor, 1 / camera.zoomFactor);
                g.translate(camera.x, camera.y);
            }
        }
        drawToScreen(camera, realFps);
    }

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

    /**
     * Entry point for our SampleCameraZoom demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleCameraZoom sgl = new SampleCameraZoom("Sample With a Zooming Camera", 320, 240, 2);
        sgl.run();
    }
}