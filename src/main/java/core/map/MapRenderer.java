package core.map;

import core.Game;
import core.object.Camera;

import java.awt.*;

/**
 * A core.Renderer for the core.map.MapLevel.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
public class MapRenderer {
    Color backTransparent = new Color(0.0f, 0.0f, 0.0f, 0.0f);

    /**
     * Rendering the core.map.MapLevel according to the camera position.
     *
     * @param dg     the core.Game container
     * @param g      The graphics API to be used
     * @param map    The core.map.MapLevel to be rendered
     * @param camera the camera to be used as a point of view.
     */
    public void render(Game dg, Graphics2D g, MapLevel map, Camera camera) {

        int mHeight = map.map.size();
        int mWidth = map.map.get(0).length();

        int cw = (camera.viewport.width / map.asset.tileWidth) + 2;
        int ch = (camera.viewport.height / map.asset.tileHeight) + 2;
        int cx = (int) (camera.x / map.asset.tileWidth) - 1;
        int cy = (int) (camera.y / map.asset.tileHeight) - 1;

        if (map.backgroundImage != null) {
            double bx = camera.x * map.backgroundImage.getWidth() / (mWidth * map.asset.tileWidth);
            double by = camera.y;
            for (int x = -1; x <= 2; x += 1) {
                g.drawImage(map.backgroundImage, (int) (bx + (map.backgroundImage.getWidth() * x)), (int) by, null);
            }
        }

        //for (int y = cy; y < cy + ch; y++) {
        //   for (int x = cx; x < cx + cw; x++) {
        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                MapObject mo = getTile(map, x, y);
                if (mo != null) {
                    g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
                }
                if (dg.config.debug > 4) {
                    if (mo != null) {
                        g.setColor(Color.GRAY);
                    } else {
                        g.setColor(Color.BLUE);
                    }
                    g.drawRect(x * map.asset.tileWidth, y * map.asset.tileHeight, map.asset.tileWidth, map.asset.tileHeight);
                }
            }
        }
    }

    private MapObject getTile(MapLevel map, int x, int y) {
        if (x >= 0
                && x < map.width
                && y >= 0
                && y < map.height) {
            return map.tiles[x][y];
        }
        return null;
    }
}