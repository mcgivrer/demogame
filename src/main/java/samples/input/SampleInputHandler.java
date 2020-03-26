package samples.input;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
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
    }

    @Override
    public void initialize() {
        gsm = GameSystemManager.initialize(this);
        gsm.add(new InputHandler(this));

        frame.addKeyListener(gsm.getSystem(InputHandler.class));
        frame.addMouseListener(gsm.getSystem(InputHandler.class));

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
            objects.put(player.name,player);

        } catch (IOException ioe) {
            log.error("unable to read the tileset image");
        }

        camera = new Camera("cam1", 
                objects.get("player"), 
                0.018f,
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


    public void input(InputHandler ih){
        GameObject go = objects.get("player");
        if(ih.getKey(KeyEvent.VK_UP)){
            go.dy = (go.dy > -go.maxD ? go.dy - 1 : go.dy);
        }
        if(ih.getKey(KeyEvent.VK_DOWN)){
            go.dy = (go.dy < go.maxD ? go.dy + 1 : go.dy);
        }
        if(ih.getKey(KeyEvent.VK_LEFT)){
            go.dx = (go.dx > -go.maxD ? go.dx - 1 : go.dx);
        }
        if(ih.getKey(KeyEvent.VK_RIGHT)){
            go.dx = (go.dx < go.maxD ? go.dx + 1 : go.dx);
        }
        if(ih.getKey(KeyEvent.VK_SPACE)){
            // Break the first object of the objects map.
            go.dx = 0;
            go.dy = 0;
            go.x = screenBuffer.getWidth() / 2;
            go.y = screenBuffer.getHeight() / 2;
            go.color = Color.BLUE;
        }
        if(ih.getKey(KeyEvent.VK_R)){
            reshuffleVelocity();
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
        long timeFrame=0;
        long frames=0;
        long realFps=0;
        while (!exit) {
            nextTime = System.currentTimeMillis();

            if (!pause) {
                input(gsm.getSystem(InputHandler.class));
                update(elapsed);
            }
            render(realFps);

            timeFrame+=elapsed;
            frames++;
            if(timeFrame>1000){
                realFps=frames;
                frames=0;
                timeFrame=0;
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
            if(!go.name.equals("camera")){
                constrainGameObject(go);
            }
        }
    }


    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleInputHandler sgl = new SampleInputHandler("Sample With Camera", 320, 240, 2);
        sgl.run();
    }
}