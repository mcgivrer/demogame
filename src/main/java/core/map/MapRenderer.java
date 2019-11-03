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
        int mWidth = map.map.get(0).length();
        int mHeight = map.map.size();
        if (map.backgroundImage != null) {
            double bx = camera.x * map.backgroundImage.getWidth() / (mWidth * map.asset.tileWidth);
            double by = camera.y;
            for (int x = (int) (bx - map.backgroundImage.getWidth());
                 x <= (bx + map.backgroundImage.getWidth());
                 x += map.backgroundImage.getWidth()) {
                g.drawImage(map.backgroundImage, (int) x, (int) by, null);
            }
        }

        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                MapObject mo = getTile(map, x, y);
                if (mo != null) {
                    g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
                }
            }
        }
    }

    private MapObject getTile(MapLevel map, int x, int y) {
        if (x >= 0
                && x < map.tiles[0].length
                && y >= 0
                && y < map.tiles.length) {
            return map.tiles[x][y];
        }
        return null;
    }
}