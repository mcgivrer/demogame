package demo.states;

import core.Game;
import core.ProgressListener;
import core.Renderer;
import core.ResourceManager;
import core.object.TextObject;
import core.state.AbstractState;
import core.state.State;
import core.state.StateManager;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;

@Slf4j
public class TitleState extends AbstractState implements State {
    private Renderer renderer;
    private Font textFont;
    private Font titleFont;

    public TitleState() {
        this.name = "title";
    }


    @Override
    public void input(Game g) {

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
        ResourceManager.add(new String[]{"/res/fonts/Prince Valiant.ttf", "/res/fonts/lilliput steps.ttf"});

        TextObject title = new TextObject("title", "DemoGame",
                g.config.screenWidth / 2, g.config.screenHeight / 3,
                titleFont,
                new Color(0.3f, 0.3f, 0.3f, 0.6f),
                Color.BLACK,
                Color.WHITE);
        addObject(title);
        TextObject copyright = new TextObject("copyroght", "(c) 2019 FDE / MIT license",
                g.config.screenWidth / 2, (g.config.screenHeight / 3) * 2,
                textFont,
                new Color(0.3f, 0.3f, 0.3f, 0.6f),
                Color.BLACK,
                Color.WHITE);
        addObject(title);
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public void update(Game g, float elapsed) {

    }

    @Override
    public void render(Game g, Renderer r) {
        renderer.render(g);
    }

    @Override
    public void dispose(Game g) {

    }

    @Override
    public void drawHUD(Game ga, Renderer r, Graphics2D g) {

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
        StateManager stm = SystemManager.get(StateManager.class);

        stm.activate("game");
    }
}
