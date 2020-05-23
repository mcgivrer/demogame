package samples.render;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import samples.Sample;
import samples.camera.Camera;
import samples.collision.SampleCollision;
import samples.input.InputHandler;
import samples.input.MouseCursor;
import samples.object.GameObject;
import samples.object.GameObject.GameObjectType;
import samples.system.GameSystemManager;

@Slf4j
public class SampleRendererSystem extends SampleCollision {
    Renderer rs;
    InputHandler ih;
    public SampleRendererSystem(String title, String[] args) {
        super(title, args);
    }

    @Override
    public void initialize() {

        gsm = GameSystemManager.initialize(this);
        
        rs = new Renderer(this);
        gsm.add(rs);

        ih = new InputHandler((Sample)this);
        // add this new GameSystem to the manager
        gsm.add(ih);
        ih.register(this);
        rs.addKeyListener(ih);


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
            objects.put(player.name, player);

            MouseCursor mCursor = new MouseCursor("mouse_cursor");
            objects.put(mCursor.name, mCursor);

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }

        camera = new Camera("cam1", objects.get("player"), 0.005f,
                rs.getViewport());
        objects.put(camera.name, camera);
    }

    protected void createObjects(int max) {
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
            // Add the GameObject to the rendering pipeline.
            rs.addObject(go);

            log.info("Add a new GameObject named {}", go.name);
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
    public void loop() {
        super.loop();

    }

    @Override
    public void update(double elapsed) {
        super.update(elapsed);

    }

    @Override
    public void render(long realFps) {
        Renderer render = gsm.getSystem(Renderer.class);
        render.render(this, realFps);
    }

    public static void main(String[] args) {
        SampleRendererSystem g = new SampleRendererSystem("Sample Render System", args);
        g.run();
    }

}