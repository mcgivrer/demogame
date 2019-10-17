import java.awt.*;

/**
 * A Renderer for the MapLevel.
 *
 * @year 2019
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 */
public class MapRenderer {
    /**
     * Rendering the MapLevel according to the camera position.
     *
     * @param dg     the DemoGame container
     * @param g      The graphics API to be used
     * @param map    The MapLevel to be rendered
     * @param camera the camera to be used as a point of view.
     */
    void render(DemoGame dg, Graphics2D g, MapLevel map, Camera camera) {
        int mWidth = map.map.get(0).length();
        int mHeight = map.map.size();
        if(map.backgroundImage!=null){
            g.drawImage(map.backgroundImage,(int)camera.x,(int)camera.y,null);
        }
        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                MapObject mo = map.tiles[x][y];
                if (mo != null) {
                    g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
                } else {
                    //g.clearRect(x * map.asset.tileWidth, y * map.asset.tileHeight, map.asset.tileWidth, map.asset.tileHeight);
                }
            }
        }
    }
}