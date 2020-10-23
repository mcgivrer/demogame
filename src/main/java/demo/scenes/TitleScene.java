package demo.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Game;
import core.gfx.Renderer;
import core.object.TextObject;
import core.resource.ProgressListener;
import core.resource.ResourceManager;
import core.scene.AbstractScene;
import core.scene.SceneManager;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TitleScene extends AbstractScene {

    private Renderer renderer;
    private Font textFont;
    private Font titleFont;

    public TitleScene() {
        this.name = "title";
    }

    @Override
    public void input(Game g) {
        // nothing realtime for input management in this part.
    }

    @Override
    public void initialize(Game g) {
        titleFont = ResourceManager.getFont("/res/fonts/Prince Valiant.ttf");
        textFont = ResourceManager.getFont("/res/fonts/lilliput steps.ttf");
        renderer = SystemManager.get(Renderer.class);
    }

    @Override
    public void load(Game g) {
        ResourceManager.clear();
        ResourceManager.addListener(new ProgressListener() {
            @Override
            public void update(float value, String path) {
                log.info("reading resources: {} : {}", value * 100.0f, path);
            }
        });
        // TODO add resources to be loaded.
        ResourceManager.add(new String[] { "/res/fonts/Prince Valiant.ttf", "/res/fonts/lilliput steps.ttf" });

        TextObject title = new TextObject("title", "DemoGame", g.config.screenWidth / 2, g.config.screenHeight / 3,
                titleFont, new Color(0.3f, 0.3f, 0.3f, 0.6f), Color.BLACK, Color.WHITE);
        addObject(title);
        TextObject copyright = new TextObject("copyright", "(c) 2019 FDE / MIT license", g.config.screenWidth / 2,
                (g.config.screenHeight / 3) * 2, textFont, new Color(0.3f, 0.3f, 0.3f, 0.6f), Color.BLACK, Color.WHITE);
        addObject(title);
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public void render(Game g, Renderer r, double elapsed) {
        renderer.render(g, elapsed);
    }

    @Override
    public void dispose(Game g) {
        // nothing to dispose.
    }

    @Override
    public void drawHUD(Game ga, Renderer r, Graphics2D g) {
        // no hud to be displayed.
    }

    @Override
    public void keyReleased(KeyEvent e) {

        super.keyReleased(e);
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
        case KeyEvent.VK_SPACE:
            loadDemo();
            break;
        default:
            break;

        }
    }

    private void loadDemo() {
        SceneManager stm = SystemManager.get(SceneManager.class);

        stm.activate("game");
    }
}