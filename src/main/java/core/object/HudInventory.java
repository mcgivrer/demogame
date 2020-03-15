package core.object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.Game;
import core.gfx.Renderer;
import core.map.MapObject;

public class HudInventory extends GameObject {

    private GameObject player;

    private BufferedImage itemHolderImg;
    private BufferedImage itemHolderSelectedImg;

    private int offsetX = 0;
    private int offsetY = 0;
    private double maxItems;
    private double selectedItem;

    /**
     * initialiez the HUD inventory display.
     * 
     * @param player  the parent player object to take data from.
     * @param offsetX horizontal offset on screen.
     * @param offsetY vertical offsen on screen.
     */
    public HudInventory(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Load specific image resources from imageAsset.
     * 
     * @param imageAsset the buffered image source of all display asset
     */
    public void load(BufferedImage imageAsset) {
        itemHolderSelectedImg = imageAsset.getSubimage((4 * 16), 16, 18, 18);
        itemHolderImg = imageAsset.getSubimage((5 * 16) + 1, 16, 18, 18);
    }

    /**
     * <p>
     * Render the object according to player inventory information.
     * <p>
     * Read the MapObject from the player's inventory attribute, and display the
     * corrsponding items.
     * 
     * @param dg the parent Game
     * @param g  the Graphics API to render the object.
     */
    public void render(Game dg, Renderer r, Graphics2D g) {
        // draw Items
        for (int itmNb = 1; itmNb <= maxItems; itmNb++) {

            int posX = (int) (maxItems - itmNb) * (itemHolderImg.getWidth() - 1);
            MapObject item = null;
            if (player.items.size() > 0 && itmNb - 1 < player.items.size()) {
                item = player.items.get(itmNb - 1);
            }
            BufferedImage holder = switchItem(itmNb, item, selectedItem);
            r.drawImage(g, holder, dg.config.screenWidth - offsetX - posX,
                    dg.config.screenHeight - (holder.getHeight() + offsetY), holder.getWidth(), holder.getHeight());

            if (itmNb - 1 < player.items.size() && item != null) {
                r.renderMapObject(g, item, dg.config.screenWidth + 1 - offsetX - posX,
                        dg.config.screenHeight - (holder.getHeight() + offsetY));
            }
        }
    }

    /**
     * update data according to player data.
     */
    @Override
    public void update(Game dg, double elapsed) {
        maxItems = (double) player.attributes.get("maxItems");
        selectedItem = (double) player.attributes.get("selectedItem");
    }

    private BufferedImage switchItem(int itmNb, MapObject item, double selectedItem) {
        BufferedImage holder;
        if (((double) itmNb) == selectedItem && (item != null)) {
            holder = itemHolderSelectedImg;
        } else {
            holder = itemHolderImg;
        }
        return holder;
    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

}