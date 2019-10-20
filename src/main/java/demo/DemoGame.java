package demo;

import core.Config;
import core.Game;
import core.Renderer;
import core.ResourceManager;
import core.map.MapCollider;
import core.map.MapLevel;
import core.map.MapReader;
import core.object.Camera;
import core.object.GameObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * An extra class to demonstrate some basics to create a simple java game.
 *
 * @author Frédéric Delorme
 * @since 2019
 */
public class DemoGame extends Game {

    public MapLevel mapLevel;
    public MapCollider mapCollider;

    public int score = 0;
    public int life = 4;
    private int maxItemsOnScreen = 2;

    private BufferedImage energyImg;
    private BufferedImage manaImg;
    private BufferedImage coinsImg;
    private BufferedImage lifeImg;
    private BufferedImage itemHolderImg;

    /**
     * Create the Game container.
     *
     * @param argc list of arguments.
     * @see Config#analyzeArgc(String[])
     */
    public DemoGame(String[] argc) {
        super(argc);
        config = Config.analyzeArgc(argc);
    }

    /**
     * The famous java Execution entry point.
     *
     * @param argc
     */
    public static void main(String[] argc) {
        DemoGame dg = new DemoGame(argc);
        dg.run();
    }

    @Override
    public void initialize() {
        super.initialize();
        mapCollider = new MapCollider();

        ResourceManager.add(new String[]{
                "/res/maps/map_1.json",
                "/res/assets/asset-1.json",
                "/res/images/background-1.jpg",
                "/res/images/tileset-1.png"});
        BufferedImage sprites = ResourceManager.getImage("/res/images/tileset-1.png");
        energyImg = sprites.getSubimage(0, 0, 41, 9);
        manaImg = sprites.getSubimage(0, 22, 41, 5);
        lifeImg = sprites.getSubimage(8 * 16, 2 * 16, 16, 16);
        coinsImg = sprites.getSubimage(10 * 16, 1 * 16, 16, 16);
        itemHolderImg = sprites.getSubimage((5 * 16)+1, 16, 18, 18);

        loadState();
    }

    public void loadState() {

        mapLevel = MapReader.readFromFile("/res/maps/map_1.json");
        if (mapLevel != null) {
            mapLevel.priority = 1;
            mapLevel.layer = 3;
            addObject(mapLevel);
            addObject(mapLevel.player);
            addAllObject(mapLevel.enemies);

            // Create camera
            Camera cam = new Camera("camera", mapLevel.player, 0.017f,
                    new Dimension((int) mapLevel.width, (int) mapLevel.height));
            addObject(cam);
        }
    }

    /**
     * Update all the object according to elapsed time.
     *
     * @param elapsed
     */
    public void update(float elapsed) {

        // update all objects
        for (GameObject go : objects.values()) {
            if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
                go.update(this, elapsed);
                constrainToMapLevel(mapLevel, go);
                // Direction ir = mapCollider.isColliding(mapLevel,go);

            }
        }
        // active core.object.Camera update
        if (this.camera != null) {
            camera.update(this, elapsed);
        }
    }

    public void input() {
        if (keys[KeyEvent.VK_ESCAPE]) {
            exitRequest = true;
        }

        mapLevel.player.setSpeed(0.0f, 0.0f);

        if (keys[KeyEvent.VK_UP]) {
            mapLevel.player.dy = -0.2f;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            mapLevel.player.dy = 0.2f;
        }
        if (keys[KeyEvent.VK_LEFT]) {
            mapLevel.player.dx = -0.2f;
            mapLevel.player.direction = -1;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            mapLevel.player.dx = 0.2f;
            mapLevel.player.direction = 1;
        }
        if (keys[KeyEvent.VK_SPACE]) {
            // Todo implement Jump
        }
    }

    public void drawHUD(Renderer r, Graphics2D g) {
        int offsetX = 12, offsetY = 30;
        Font f = g.getFont();
        g.setFont(f.deriveFont(12.0f));
        // draw Score
        r.drawOutLinedText(g, String.format("%05d", score), config.screenWidth - (46 + offsetX), offsetY, Color.WHITE, Color.BLACK);
        // draw Life
        g.drawImage(lifeImg, offsetX, offsetY - 16, null);
        g.setFont(f.deriveFont(10.0f));
        r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY+1, Color.WHITE, Color.BLACK);
        // draw Coins
        g.drawImage(coinsImg, offsetX, offsetY, null);
        g.setFont(f.deriveFont(10.0f));
        double coins = (double)(mapLevel.player.attributes.get("coins"));
        r.drawOutLinedText(g, String.format("%d", (int)coins), offsetX + 8, offsetY+16, Color.WHITE, Color.BLACK);
        // draw Mana
        float nrjRatio = (energyImg.getWidth() / 100.0f);
        double nrj = nrjRatio * ((double) (mapLevel.player.attributes.get("energy")));
        g.drawImage(energyImg, offsetX + 24, offsetY - 8, (int) nrj, energyImg.getHeight(), null);
        // draw Energy
        float manaRatio = (manaImg.getWidth() / 100.0f);
        double mana = manaRatio * ((double) (mapLevel.player.attributes.get("mana")));
        g.drawImage(manaImg, offsetX + 24, offsetY + 2, (int) mana, manaImg.getHeight(), null);

        // draw Items
        for (int itmNb = 1; itmNb <= maxItemsOnScreen; itmNb++) {
            g.drawImage(itemHolderImg,
                    config.screenWidth - offsetX - (itmNb * (itemHolderImg.getWidth()-1)),
                    config.screenHeight - (itemHolderImg.getHeight()+12),
                    itemHolderImg.getWidth(),
                    itemHolderImg.getHeight(),
                    null);
        }
        g.setFont(f);
    }
}