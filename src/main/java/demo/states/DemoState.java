package demo.states;

import core.Game;
import core.ProgressListener;
import core.Renderer;
import core.ResourceManager;
import core.audio.SoundClip;
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
import core.state.AbstractState;
import core.state.State;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@Slf4j
public class DemoState extends AbstractState implements State {


    SoundClip playCoin;

    public MapLevel mapLevel;
    public MapCollidingService mapCollider;

    public int score = 0;
    public int life = 4;
    private int maxItemsOnScreen = 2;

    private BufferedImage energyImg;
    private BufferedImage manaImg;
    private BufferedImage coinsImg;
    private BufferedImage lifeImg;
    private BufferedImage itemHolderImg;
    private InputHandler inputHandler;

    Font scoreFont;
    Font infoFont;

    public DemoState() {
        this.name = "DemoState";
    }

    public DemoState(Game g) {
        super(g);
    }

    @Override
    public void initialize(Game g) {
        inputHandler = g.sysMan.getSystem(InputHandler.class);
        inputHandler.addListener(this);
        mapCollider = g.sysMan.getSystem(MapCollidingService.class);

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
                GameObject player = map.mapObjects.get("player");
                player.items.add(mo);
                map.tiles[x][y] = null;
            }

            private void collectCoin(MapLevel map, GameObject go, MapObject mo, int x, int y) {
                if (mo.money > 0) {
                    go.attributes.put("coins", (double) (go.attributes.get("coins")) + mo.money);
                    map.tiles[x][y] = null;
                    if (playCoin != null) {
                        playCoin.play();
                    }
                }
            }
        });

        if (mapLevel != null) {
            mapLevel.priority = 1;
            mapLevel.layer = 3;
            addObject(mapLevel);
            addAllObject(mapLevel.mapObjects.values());

            // Create camera
            Camera cam = new Camera("camera", mapLevel.mapObjects.get("player"), 0.017f,
                    new Dimension(
                            g.config.screenWidth,
                            g.config.screenHeight));
            addObject(cam);
        }
    }

    @Override
    public void load(Game g) {

        ResourceManager.addListener(new ProgressListener() {
            @Override
            public void update(float value, String path) {
                log.info("reading resources: {} : {}", value * 100.0f, path);
            }
        });

        ResourceManager.add(new String[]{
                "/res/maps/map_1.json",
                "/res/assets/asset-1.json",
                "/res/images/background-1.jpg",
                "/res/images/tileset-1.png",
                "/res/audio/sounds/collect-coin.wav"});

        mapLevel = MapReader.readFromFile("/res/maps/map_1.json");

        BufferedImage sprites = ResourceManager.getImage("/res/images/tileset-1.png");

        energyImg = sprites.getSubimage(0, 0, 41, 9);
        manaImg = sprites.getSubimage(0, 22, 41, 5);
        lifeImg = sprites.getSubimage(8 * 16, 2 * 16, 16, 16);
        coinsImg = sprites.getSubimage(10 * 16, 1 * 16, 16, 16);
        itemHolderImg = sprites.getSubimage((5 * 16) + 1, 16, 18, 18);
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
        GameObject player = mapLevel.mapObjects.get("player");

        player.setSpeed(0.0f, 0.0f);

        // reset horizontal speed if falling.
        if (player.action == GameAction.FALL) {
            player.dx = 0.0f;
            player.dy = 0.25f;
        }
        if (inputHandler.keys[KeyEvent.VK_UP]) {
            player.dy = -0.2f;
            player.action = GameAction.JUMP;
        }
        if (inputHandler.keys[KeyEvent.VK_DOWN]) {
            player.dy = 0.1f;
            player.action = GameAction.DOWN;
        }

        if (inputHandler.keys[KeyEvent.VK_LEFT]) {
            player.dx = -0.2f;
            player.direction = -1;
            player.action = GameAction.WALK;
        }
        if (inputHandler.keys[KeyEvent.VK_RIGHT]) {
            player.dx = 0.2f;
            player.direction = 1;
            player.action = GameAction.WALK;
        }
    }

    @Override
    public void update(Game g, float elapsed) {

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

        g.renderer.render(g);
    }

    @Override
    public void dispose(Game g) {
        //ResourceManager.remove();
    }

    public void drawHUD(Game ga, Renderer r, Graphics2D g) {
        int offsetX = 12, offsetY = 30;
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

        GameObject player = mapLevel.mapObjects.get("player");

        // draw Coins
        g.drawImage(coinsImg, offsetX, offsetY, null);
        g.setFont(infoFont);
        double coins = (double) (player.attributes.get("coins"));
        r.drawOutLinedText(g, String.format("%d", (int) coins), offsetX + 8, offsetY + 16, Color.WHITE, Color.BLACK);

        // draw Mana
        float nrjRatio = (energyImg.getWidth() / 100.0f);
        double nrj = nrjRatio * ((double) (player.attributes.get("energy")));
        g.drawImage(energyImg, offsetX + 24, offsetY - 8, (int) nrj, energyImg.getHeight(), null);

        // draw Energy
        float manaRatio = (manaImg.getWidth() / 100.0f);
        double mana = manaRatio * ((double) (player.attributes.get("mana")));
        g.drawImage(manaImg, offsetX + 24, offsetY + 2, (int) mana, manaImg.getHeight(), null);

        // draw Items
        for (int itmNb = 1; itmNb <= maxItemsOnScreen; itmNb++) {
            g.drawImage(itemHolderImg,
                    ga.config.screenWidth - offsetX - (itmNb * (itemHolderImg.getWidth() - 1)),
                    ga.config.screenHeight - (itemHolderImg.getHeight() + 12),
                    itemHolderImg.getWidth(),
                    itemHolderImg.getHeight(),
                    null);
            if (itmNb - 1 < player.items.size()
                    && player.items.get(itmNb - 1) != null) {
                r.renderMapObject(g,
                        player.items.get(itmNb - 1),
                        ga.config.screenWidth - offsetX - (itmNb * (itemHolderImg.getWidth() - 1)),
                        ga.config.screenHeight - (itemHolderImg.getHeight() + 12)
                );
            }
        }
    }
}
