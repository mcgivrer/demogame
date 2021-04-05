package demo.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Game;
import core.gfx.IRenderer;
import core.gfx.soft.Renderer;
import core.object.GameObject;
import core.object.GameObjectType;
import core.object.TextObject;
import core.resource.ProgressListener;
import core.resource.ResourceManager;
import core.scene.AbstractScene;
import core.scene.SceneManager;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TitleScene extends AbstractScene {

    public TitleScene() {
        this.name = "title";
    }

    public TitleScene(Game game) {
        super(game);
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

        ResourceManager.add(new String[] { "/res/images/background-1.jpg", "/res/fonts/Prince Valiant.ttf",
                "/res/fonts/lilliput steps.ttf" });

    }

    @Override
    public void initialize(Game g) {
        super.initialize(g);

        g.config.attributes.put("sound_volume", 0.8f);
        g.config.attributes.put("music_volume", 0.4f);

        inputHandler.addListener(this);

        objectManager.clear();
        IRenderer r = g.sysMan.getSystem(IRenderer.class.getSimpleName());
        r.clear();

        Font textFont = ResourceManager.getFont("/res/fonts/lilliput steps.ttf").deriveFont(9.0f);
        Font titleFont = ResourceManager.getFont("/res/fonts/Prince Valiant.ttf").deriveFont(20.0f);

        soundSystem.load("music", "/res/audio/musics/once-around-the-kingdom.ogg");
        soundSystem.setMute(g.config.mute);

        GameObject background = new GameObject("background", 0.0f, (g.config.screenHeight / 5.0f) * 1.0f, 0, 0);
        background.type = GameObjectType.IMAGE;
        background.setImage(ResourceManager.getImage("/res/images/background-1.jpg"));
        background.layer = 0;
        addObject(background);

        TextObject title = new TextObject("title", "DemoGame", g.config.screenWidth / 2.0f,
                (g.config.screenHeight / 5f) * 2.0f, titleFont, new Color(0.3f, 0.3f, 0.3f, 0.6f), Color.BLACK,
                Color.WHITE);
        title.layer = 1;
        addObject(title);

        TextObject copyright = new TextObject("copyright", "(c) 2020 FDE / MIT license", g.config.screenWidth / 2.0f,
                (g.config.screenHeight / 5.0f) * 4.0f, textFont, new Color(0.3f, 0.3f, 0.3f, 0.6f), Color.BLACK,
                Color.WHITE);
        copyright.layer = 1;
        addObject(copyright);

        // start game music background
        soundSystem.loop("music", (float) g.config.attributes.get("music_volume"));

    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void input(Game g) {
        if (inputHandler != null && inputHandler.keys[KeyEvent.VK_ESCAPE]) {
            g.exitRequest = true;
        }
    }

    @Override
    public void update(Game g, double elapsed) {
        // nothing to do there.
    }

    @Override
    public void render(Game g, IRenderer r, double elapsed) {
        r.render(g, elapsed);
    }

    @Override
    public void dispose(Game g) {
        // nothing to dispose.
    }

    @Override
    public void drawHUD(Game ga, IRenderer renderer, Graphics2D g) {
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
        SceneManager stm = SystemManager.get(SceneManager.class.getSimpleName());

        stm.activate("game");
    }

    @Override
    public void onFocus(Game g) {
        super.onFocus(g);
    }

    @Override
    public String getName() {
        return TitleScene.class.getSimpleName();
    }

}