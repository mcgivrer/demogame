package core.collision;

import static core.collision.CollisionEvent.CollisionType.COLLISION_MAP;

import java.util.HashMap;
import java.util.Map;

import core.Game;
import core.map.MapLayer;
import core.map.MapObject;
import core.map.MapObjectAsset;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.system.AbstractSystem;

/**
 * The MapColliding service is dedicated to check GameObject vs. MapObject from
 * the map tiles.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
public class MapCollidingService extends AbstractSystem {

    Map<Class<?>, OnCollision> listeners = new HashMap<>();

    public MapCollidingService(Game g) {
        super(g);
    }

    @Override
    public String getName() {
        return MapCollidingService.class.getCanonicalName();
    }

    @Override
    public int initialize(Game game) {
        return 0;
    }

    @Override
    public void dispose() {

    }

    public void addListener(Class<?> clazz, OnCollision oc) {
        if (!listeners.containsKey(clazz)) {
            listeners.put(clazz, oc);
        }
    }

    /**
     * Check the collision between the MapLevel tiles and a GameObject
     *
     * @param map the map to check against
     * @param go  the GameObject to be verified.
     */
    public void checkCollision(MapLayer frontLayer, int indexAsset, GameObject go) {
        MapObjectAsset asset = frontLayer.assetsObjects.get(indexAsset);
        int ox = (int) (go.bbox.pos.x / asset.tileWidth);
        int oy = (int) ((go.newPos.y + go.bbox.size.y) / asset.tileHeight);
        int oy2 = (int) ((go.bbox.pos.y + go.bbox.size.y) / asset.tileHeight);

        int ow = (int) (go.bbox.size.x / asset.tileWidth);
        int oh = (int) (go.bbox.size.y / asset.tileHeight);

        go.collidingZone.clear();

        if (go.vel.x > 0) {
            testMoveRight(frontLayer, go, ox, ow, oy, oh);
        }
        if (go.vel.x < 0) {
            testMoveLeft(frontLayer, go, ox, oy, oh);
        }
        if (go.vel.y < 0) {
            testMoveUp(frontLayer, go);
        }
        if (go.vel.y > 0) {
            testIfMoveDown(frontLayer, go);
        }
        testIfFall(frontLayer, go, true);
    }

    private void testIfMoveDown(MapLayer layer, GameObject go) {
        testIfFall(layer, go, false);
    }

    public void testIfFall(MapLayer layer, GameObject go, boolean falling) {
        int dy = +1;

        /**
         * Compute bottom coordinate of bottom corners tiles.
         */
        int y0 = (int) ((go.pos.y + go.bbox.size.y) / layer.assetsObjects.get(0).tileHeight) + dy;

        int x1 = (int) (go.bbox.pos.x / layer.assetsObjects.get(0).tileWidth);
        int y1 = (int) ((go.bbox.pos.y + go.bbox.size.y) / layer.assetsObjects.get(0).tileHeight) + dy;

        int x2 = (int) ((go.bbox.pos.x + go.bbox.size.x) / layer.assetsObjects.get(0).tileWidth);
        int y2 = (int) ((go.bbox.pos.y + go.bbox.size.y) / layer.assetsObjects.get(0).tileHeight) + dy;

        // test all tiles from old to new position
        for (int y = y0; y <= y1; y += 1) {
            // get Tile at bottom corners
            MapObject m1 = getTileInMap(layer, x1, y);
            MapObject m2 = getTileInMap(layer, x2, y);
            // if no tile on both bottom corners, fall !
            if (m1 == null && m2 == null) {
                go.action = GameAction.FALL;
            }
            // add some debugging information on detected tiles
            createDebugInfo(go, layer, m1, x1, y);
            createDebugInfo(go, layer, m2, x2, y);
        }
        // if Go is not falling and not on a tile, recompute right Y value according to
        // tile height.
        if (go.action != GameAction.FALL && (go.pos.y % layer.assetsObjects.get(0).tileHeight) > 0) {
            go.pos.y = (int) (go.pos.y / layer.assetsObjects.get(0).tileHeight) * layer.assetsObjects.get(0).tileHeight;
            go.bbox.fromGameObject(go);
        }
    }

    public void testMoveUp(MapLayer map, GameObject go) {
        int x1 = (int) (go.bbox.pos.x / map.assetsObjects.get(0).tileWidth);
        int y1 = (int) ((go.bbox.pos.y) / map.assetsObjects.get(0).tileHeight);

        int x2 = (int) ((go.bbox.pos.x + go.bbox.size.x) / map.assetsObjects.get(0).tileWidth);
        int y2 = (int) ((go.bbox.pos.y) / map.assetsObjects.get(0).tileHeight);

        MapObject m1 = getTileInMap(map, x1, y1);
        MapObject m2 = getTileInMap(map, x2, y2);

        if (m1 != null) {
            collide(go, map, m1, x1, y1);
            createDebugInfo(go, map, m1, x1, y1);
        }
        if (m2 != null) {
            collide(go, map, m2, x2, y2);
            createDebugInfo(go, map, m2, x2, y2);
        }
        createDebugInfo(go, map, m1, x1, y1);
        createDebugInfo(go, map, m2, x2, y2);
    }

    public void testMoveLeft(MapLayer map, GameObject go, int ox, int oy, int oh) {
        MapObject mo;
        for (int iy = oy; iy < oy + oh; iy++) {
            mo = getTileInMap(map, ox, iy);
            createDebugInfo(go, map, mo, ox, iy);
            if (mo != null) {
                collide(go, map, mo, ox, iy);
            }
        }
    }

    public void testMoveRight(MapLayer map, GameObject go, int ox, int ow, int oy, int oh) {
        testMoveLeft(map, go, ox + ow, oy, oh);
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
        if (game.config.debug > 3) {
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
     * As the `MapObject` is not null and is not a blocking one, we try to collect
     * it, and test if the `MapObject` type is an item or an object.
     *
     * @param go  the `GameObject` that `canCollect`
     * @param map the map where to search for
     * @param mo  the MapObject to be tested with
     * @param x   the horizontal position in the tiles map
     * @param y   the vertical position in the tiles map
     */
    private void collide(GameObject go, MapLayer map, MapObject mo, int x, int y) {
        listeners.get(go.getClass()).collide(new CollisionEvent(COLLISION_MAP, go, null, mo, map, x, y));
    }

    /**
     * Retrieve the MapObject from the tiles map. it's also checking that (x,y) is
     * not out pf the map. return null elsewhere.
     *
     * @param map the map to find
     * @param x   the horizontal position to test
     * @param y   the vertical position to test
     * @return
     */
    private MapObject getTileInMap(MapLayer map, int x, int y) {
        if (x < 0 || y < 0 || x > map.tiles.length - 1 || y > map.tiles[0].length - 1) {
            return null;
        }
        return map.tiles[x][y];
    }
}
