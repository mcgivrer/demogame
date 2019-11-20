package demo.states;

import core.Game;
import core.ProgressListener;
import core.Renderer;
import core.ResourceManager;
import core.audio.SoundSystem;
import core.collision.CollisionEvent;
import core.collision.MapCollidingService;
import core.collision.OnCollision;
import core.io.InputHandler;
import core.map.MapLevel;
import core.map.MapObject;
import core.map.MapReader;
import core.object.Camera;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.TextObject;
import core.state.AbstractState;
import core.state.State;
import core.system.SystemManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@Slf4j
public class DemoState extends AbstractState implements State {

    public MapLevel mapLevel;
    public MapCollidingService mapCollider;

    public int score = 0;
    public int life = 4;

    private BufferedImage energyImg;
    private BufferedImage manaImg;
    private BufferedImage coinsImg;
    private BufferedImage lifeImg;
    private BufferedImage itemHolderImg;
    private InputHandler inputHandler;
    private SoundSystem soundSystem;

    private static int lastIdleChange = 0;
    private static int lastIdleChangePace = 120;
    private static GameAction idleAction = GameAction.IDLE;

    private TextObject scoreObject;

    private Font scoreFont;
    private Font infoFont;

    private Renderer renderer;

    public DemoState() {
        this.name = "DemoState";
        renderer = SystemManager.get(Renderer.class);
    }

    public DemoState(Game g) {
        super(g);
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
        ResourceManager.add(new String[]{"/res/maps/map_1.json"
                , "/res/assets/asset-1.json"
                , "/res/images/background-1.jpg"
                , "/res/images/tileset-1.png"
                , "/res/audio/sounds/collect-coin.wav"
                //, "/res/audio/musics/once-around-the-kingdom.mp3"
        });

        objects.clear();
        mapLevel = MapReader.readFromFile("/res/maps/map_1.json");

        BufferedImage sprites = ResourceManager.getImage("/res/images/tileset-1.png");

        energyImg = sprites.getSubimage(0, 0, 41, 9);
        manaImg = sprites.getSubimage(0, 22, 41, 5);
        lifeImg = sprites.getSubimage(8 * 16, 2 * 16, 16, 16);
        coinsImg = sprites.getSubimage(10 * 16, 1 * 16, 16, 16);
        itemHolderImg = sprites.getSubimage((5 * 16) + 1, 16, 18, 18);
    }

    @Override
    public void initialize(Game g) {
        inputHandler = g.sysMan.getSystem(InputHandler.class);
        inputHandler.addListener(this);
        mapCollider = g.sysMan.getSystem(MapCollidingService.class);
        soundSystem = g.sysMan.getSystem(SoundSystem.class);
        soundSystem.load("coins", "/res/audio/sounds/collect-coin.wav");
        soundSystem.load("music", "/res/audio/musics/once-around-the-kingdom.mp3");

        soundSystem.setMute(false);

        mapCollider.addListener(GameObject.class, new OnCollision() {
            public void collide(CollisionEvent e) {
                if (e.m2.collectible && e.o1.canCollect) {
                    switch (e.m2.type) {
                        case "object":
                            collectCoin(e.map, e.o1, e.m2, e.mapX, e.mapY);
                            break;
                        case "item":
                            collectItem(e.map, e.m2, e.mapX, e.mapY);
                            break;
                        default:
                            break;
                    }
                }
            }

            private void collectItem(MapLevel map, MapObject mo, int x, int y) {

                double maxItems = (double) mapLevel.player.attributes.get("maxItems");
                if (map.player.items.size() <= maxItems) {
                    map.player.items.add(mo);
                    map.tiles[x][y] = null;
                }
            }

            private void collectCoin(MapLevel map, GameObject go, MapObject mo, int x, int y) {

                if (mo.money > 0) {
                    go.attributes.put("coins", (double) (go.attributes.get("coins")) + mo.money);
                    map.tiles[x][y] = null;
                    soundSystem.play("coins", 1.0f);
                }
            }
        });

        if (mapLevel != null) {
            // add the MapLevel
            mapLevel.priority = 1;
            mapLevel.layer = 3;
            addObject(mapLevel);
            addObject(mapLevel.player);
            addAllObject(mapLevel.enemies);

            // Add Score text on H.U.D. (fixed = true)
//            scoreObject = new TextObject();
//            scoreObject.name = "score";
//            scoreObject.fixed = true;
//            scoreObject.foregroundColor = Color.WHITE;
//            scoreObject.shadowColor = Color.BLACK;
//            scoreObject.borderColor = new Color(0.1f, 0.1f, 0.1f, 0.8f);
//            scoreObject.setPosition((g.config.screenWidth / 6) * 5, 24);
//            addObject(scoreObject);

            // Create camera
            Camera cam = new Camera("camera", mapLevel.player, 0.017f,
                    new Dimension(
                            g.config.screenWidth,
                            g.config.screenHeight));
            addObject(cam);


        }
    }


    @Override
    public void onFocus(Game g) {
        super.onFocus(g);
        // start game music background
        //soundSystem.loop("music", 0.4f);
    }

    @Override
    public boolean isLoaded() {
        return mapLevel != null;
    }

    @Override
    public void input(Game g) {
        if (inputHandler.keys[KeyEvent.VK_ESCAPE]) {
            g.exitRequest = true;
        }

        mapLevel.player.setSpeed(0.0f, 0.0f);
        if (mapLevel.player.action == GameAction.FALL) {
            mapLevel.player.dx = 0.0f;
            mapLevel.player.dy = 0.25f;
        } else {
            mapLevel.player.action = idleAction;
            randomNextIdleAction();
        }

        // reset horizontal speed if falling.
        if (inputHandler.keys[KeyEvent.VK_UP]) {
            mapLevel.player.dy = -0.2f;
            mapLevel.player.action = GameAction.JUMP;
        }
        if (inputHandler.keys[KeyEvent.VK_DOWN]) {
            mapLevel.player.dy = 0.1f;
            mapLevel.player.action = GameAction.DOWN;
        }

        if (inputHandler.keys[KeyEvent.VK_LEFT]) {
            mapLevel.player.dx = -0.2f;
            mapLevel.player.direction = -1;
            mapLevel.player.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
        } else if (inputHandler.keys[KeyEvent.VK_RIGHT]) {
            mapLevel.player.dx = 0.2f;
            mapLevel.player.direction = 1;
            mapLevel.player.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
        }
    }

    private void randomNextIdleAction() {
        // compute next value for Idle
        lastIdleChange++;
        if (lastIdleChange > lastIdleChangePace) {
            double rndAction = (Math.random() * 1.0) + 0.5;
            idleAction = mapLevel.player.action = (rndAction > 1.0 ? GameAction.IDLE : GameAction.IDLE2);
            lastIdleChange = 0;
            lastIdleChangePace = (int) ((Math.random() * 100.0) + 100.0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        boolean control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                if (control) {
                    mapLevel.player.action = GameAction.IDLE2;
                    mapLevel.player.setSpeed(0.0f, 0.0f);
                    mapLevel.player.setPosition(mapLevel.playerInitialX, mapLevel.playerInitialY);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Game g, float elapsed) {
        //scoreObject.text = String.format("%05d", this.score);

        // update all objects
        for (GameObject go : objects.values()) {
            if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
                go.update(g, elapsed);
                mapCollider.checkCollision(mapLevel, go);
                mapLevel.constrainToMapLevel(go);
            }
        }
        // active core.object.Camera update
        if (this.camera != null) {
            camera.update(g, elapsed);
        }
    }

    @Override
    public void render(Game g, Renderer r) {
        renderer.render(g);
    }

    @Override
    public void dispose(Game g) {
        ResourceManager.clear();
    }

    public void drawHUD(Game ga, Renderer r, Graphics2D g) {
        //super.drawHUD(ga, r, g);

        int offsetX = 24;
        int offsetY = 30;
        // prepare font.
        if (scoreFont == null) {
            infoFont = g.getFont().deriveFont(10.0f);
            scoreFont = infoFont.deriveFont(AffineTransform.getScaleInstance(1.4, 2.0));
        }

        // draw Score
        g.setFont(scoreFont);
        r.drawOutLinedText(g, String.format("%05d", score), ga.config.screenWidth - (46 + offsetX), offsetY + 8, Color.WHITE, Color.BLACK);


        // draw Life
        g.drawImage(lifeImg, offsetX, offsetY - 16, null);
        g.setFont(infoFont);
        r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY + 1, Color.WHITE, Color.BLACK);

        // draw Coins
        g.drawImage(coinsImg, offsetX, offsetY, null);
        g.setFont(infoFont);
        double coins = (double) (mapLevel.player.attributes.get("coins"));
        r.drawOutLinedText(g, String.format("%d", (int) coins), offsetX + 8, offsetY + 16, Color.WHITE, Color.BLACK);

        // draw Mana
        float nrjRatio = (energyImg.getWidth() / 100.0f);
        double nrj = nrjRatio * ((double) (mapLevel.player.attributes.get("energy")));
        g.drawImage(energyImg, offsetX + 24, offsetY - 8, (int) nrj, energyImg.getHeight(), null);

        // draw Energy
        float manaRatio = (manaImg.getWidth() / 100.0f);
        double mana = manaRatio * ((double) (mapLevel.player.attributes.get("mana")));
        g.drawImage(manaImg, offsetX + 24, offsetY + 2, (int) mana, manaImg.getHeight(), null);

        // draw Items
        double maxItems = (double) mapLevel.player.attributes.get("maxItems");
        for (int itmNb = 1; itmNb <= maxItems; itmNb++) {

            int posX = (int) (maxItems - itmNb) * (itemHolderImg.getWidth() - 1);

            g.drawImage(itemHolderImg,
                    ga.config.screenWidth - offsetX - posX,
                    ga.config.screenHeight - (itemHolderImg.getHeight() + 12),
                    itemHolderImg.getWidth(),
                    itemHolderImg.getHeight(),
                    null);

            if (itmNb - 1 < mapLevel.player.items.size()
                    && mapLevel.player.items.get(itmNb - 1) != null) {
                r.renderMapObject(g,
                        mapLevel.player.items.get(itmNb - 1),
                        ga.config.screenWidth - offsetX - posX,
                        ga.config.screenHeight - (itemHolderImg.getHeight() + 12)
                );
            }
        }
    }

}
