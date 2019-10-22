package demo.states;

import core.Game;
import core.Renderer;
import core.ResourceManager;
import core.map.MapCollider;
import core.map.MapLevel;
import core.map.MapReader;
import core.object.Camera;
import core.object.GameObject;
import core.state.AbstractState;
import core.state.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class DemoState extends AbstractState implements State {

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

    public DemoState() {
        this.name = "DemoState";
    }

    public DemoState(Game g) {
        super(g);
    }


    @Override
    public void initialize(Game g) {
        mapCollider = new MapCollider();

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

    @Override
    public void load(Game g) {

        ResourceManager.add(new String[]{
                "/res/maps/map_1.json",
                "/res/assets/asset-1.json",
                "/res/images/background-1.jpg",
                "/res/images/tileset-1.png"});

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
        return mapLevel!=null;
    }

    @Override
    public void input(Game g) {
        if (g.keys[KeyEvent.VK_ESCAPE]) {
            g.exitRequest = true;
        }

        mapLevel.player.setSpeed(0.0f, 0.0f);

        if (g.keys[KeyEvent.VK_UP]) {
            mapLevel.player.dy = -0.2f;
        }
        if (g.keys[KeyEvent.VK_DOWN]) {
            mapLevel.player.dy = 0.2f;
        }
        if (g.keys[KeyEvent.VK_LEFT]) {
            mapLevel.player.dx = -0.2f;
            mapLevel.player.direction = -1;
        }
        if (g.keys[KeyEvent.VK_RIGHT]) {
            mapLevel.player.dx = 0.2f;
            mapLevel.player.direction = 1;
        }
        if (g.keys[KeyEvent.VK_SPACE]) {
            // Todo implement Jump
        }
    }

    @Override
    public void update(Game g, float elapsed) {

        // update all objects
        for (GameObject go : objects.values()) {
            if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
                go.update(g, elapsed);
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
        Font f = g.getFont();
        g.setFont(f.deriveFont(12.0f));
        // draw Score
        r.drawOutLinedText(g, String.format("%05d", score), ga.config.screenWidth - (46 + offsetX), offsetY, Color.WHITE, Color.BLACK);
        // draw Life
        g.drawImage(lifeImg, offsetX, offsetY - 16, null);
        g.setFont(f.deriveFont(10.0f));
        r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY + 1, Color.WHITE, Color.BLACK);
        // draw Coins
        g.drawImage(coinsImg, offsetX, offsetY, null);
        g.setFont(f.deriveFont(10.0f));
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
        for (int itmNb = 1; itmNb <= maxItemsOnScreen; itmNb++) {
            g.drawImage(itemHolderImg,
                    ga.config.screenWidth - offsetX - (itmNb * (itemHolderImg.getWidth() - 1)),
                    ga.config.screenHeight - (itemHolderImg.getHeight() + 12),
                    itemHolderImg.getWidth(),
                    itemHolderImg.getHeight(),
                    null);
        }
        g.setFont(f);
    }
}
