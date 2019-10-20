package core.map;

import core.Game;
import core.object.Camera;

import java.awt.*;

/**
 * A core.Renderer for the core.map.MapLevel.
 *
 * @year 2019
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
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
        if(map.backgroundImage!=null){
            double bx = camera.x * map.backgroundImage.getWidth() / (mWidth*map.asset.tileWidth);
            double by = camera.y;

            g.drawImage(map.backgroundImage,(int)bx,(int)by,null);
        }
        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                MapObject mo = map.tiles[x][y];
                if (mo != null) {
                    g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
                } else {
                    g.setBackground(backTransparent);
                    g.clearRect(x * map.asset.tileWidth, y * map.asset.tileHeight, map.asset.tileWidth, map.asset.tileHeight);
                }
            }
        }
    }
}