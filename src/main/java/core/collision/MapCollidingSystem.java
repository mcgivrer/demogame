package core.collision;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import core.Game;
import core.map.MapLayer;
import core.map.MapObject;
import core.map.MapObjectAsset;
import core.object.GameObject;
import core.system.AbstractSystem;

/**
 * The MapColliding service is dedicated to check GameObject vs. MapObject from
 * the map tiles.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
public class MapCollidingSystem extends AbstractSystem {

    Map<Class<?>, OnCollision> listeners = new HashMap<>();

    public MapCollidingSystem(Game g) {
        super(g);
    }

    @Override
    public String getName() {
        return MapCollidingSystem.class.getCanonicalName();
    }

    @Override
    public int initialize(Game game) {
        this.game = game;
        return 0;
    }

    @Override
    public void dispose() {
        listeners.clear();
        listeners = null;
    }

    public void addListener(Class<?> clazz, OnCollision oc) {
        if (!listeners.containsKey(clazz)) {
            listeners.put(clazz, oc);
        }
    }

    /**
     * Check the collision between the MapLevel tiles and a GameObject
     *
     * @param map
     *                the map to check against
     * @param go
     *                the GameObject to be verified.
     */
    public void checkCollision(MapLayer mLayer, int indexAsset, GameObject go) {

        if (go.collidable) {
            MapObjectAsset asset = mLayer.assetsObjects.get(indexAsset);
            go.collidingZone.clear();
            go.setContact(false);
            for (Entry<String, CollisionPoint> cpe : go.collisionPoints.entrySet()) {
                checkPoint(mLayer, asset, go, cpe);
            }
        }
    }

    private void checkPoint(MapLayer mLayer, MapObjectAsset asset, GameObject go, Entry<String, CollisionPoint> cpe) {
        int px = (int) ((go.pos.x + cpe.getValue().dx) / asset.tileWidth);
        int py = (int) ((go.pos.y + cpe.getValue().dy) / asset.tileWidth)+1;
        MapObject m = getTileInMap(mLayer, px, py);
        if (m != null) {
            collide(go, mLayer, m, px, py, cpe);
        }
    }

    /**
     * As the `MapObject` is not null and is not a blocking one, we try to collect
     * it, and test if the `MapObject` type is an item or an object.
     *
     * @param go
     *                the `GameObject` that `canCollect`
     * @param map
     *                the map where to search for
     * @param mo
     *                the MapObject to be tested with
     * @param x
     *                the horizontal position in the tiles map
     * @param y
     *                the vertical position in the tiles map
     */
    private void collide(GameObject go, MapLayer map, MapObject mo, int x, int y, Entry<String, CollisionPoint> cpe) {
        go.setContact(true);
        createDebugInfo(go, map, mo, x, y);
        listeners.get(go.getClass()).collide(new CollisionEvent(mo.type, go, null, mo, map, cpe.getKey(), x, y));
    }

    /**
     * Add debug information (if debug mode activated)
     *
     * @param go
     * @param map
     * @param m1
     * @param ox
     * @param oy
     */
    private void createDebugInfo(GameObject go, MapLayer map, MapObject m1, int ox, int oy) {
        if (game.config.debug > 1) {
            MapTileCollision mtc = new MapTileCollision();
            mtc.x = ox;
            mtc.y = oy;
            mtc.w = map.assetsObjects.get(0).tileWidth;
            mtc.h = map.assetsObjects.get(0).tileHeight;
            mtc.rX = mtc.x * mtc.w;
            mtc.rY = mtc.y * mtc.h;
            mtc.mo = m1;
            go.collidingZone.add(mtc);
        }
    }

    /**
     * Retrieve the MapObject from the tiles map. it's also checking that (x,y) is
     * not out pf the map. return null elsewhere.
     *
     * @param map
     *                the map to find
     * @param x
     *                the horizontal position to test
     * @param y
     *                the vertical position to test
     * @return
     */
    private MapObject getTileInMap(MapLayer map, int x, int y) {
        if (x < 0 || y < 0 || x > map.tiles.length - 1 || y > map.tiles[0].length - 1) {
            return null;
        }
        return map.tiles[x][y];
    }
}
