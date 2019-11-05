package core.collision;

import core.Game;
import core.map.MapLevel;
import core.map.MapObject;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.system.AbstractSystem;
import core.system.System;

import java.util.HashMap;
import java.util.Map;

import static core.collision.CollisionEvent.CollisionType.COLLISION_MAP;

/**
 * The MapColliding service is dedicated to check GameObject vs. MapObject from the map tiles.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
public class MapCollidingService extends AbstractSystem implements System {


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
    public void checkCollision(MapLevel map, GameObject go) {
        int ox = (int) (go.bbox.x / map.asset.tileWidth);
        int ow = (int) (go.bbox.width / map.asset.tileWidth);
        int oy = (int) ((go.oldY + go.bbox.height) / map.asset.tileHeight);
        int oy2 = (int) ((go.bbox.y + go.bbox.height) / map.asset.tileHeight);
        int oh = (int) (go.bbox.height / map.asset.tileHeight);

        go.collidingZone.clear();

        if (go.dx > 0) {
            testMoveRight(map, go, ox, ow, oy - 1, oh);
        }
        if (go.dx < 0) {
            testMoveLeft(map, go, ox, oy - 1, oh);
        }
        if (go.dy < 0) {
            testMoveUp(map, go);
        } else {
            testIfFall(map, go, true);
            testIfMoveDown(map, go);
        }
    }

    private void testIfMoveDown(MapLevel map, GameObject go) {
        testIfFall(map, go, false);
    }

    public void testIfFall(MapLevel map, GameObject go, boolean falling) {
        int dy = 0;

        int y0 = (int) ((go.oldY + go.bbox.height) / map.asset.tileWidth) + dy;
        int x1 = (int) (go.bbox.x / map.asset.tileWidth);
        int y1 = (int) ((go.bbox.y + go.bbox.height) / map.asset.tileWidth) + dy;
        int x2 = (int) ((go.bbox.x + go.bbox.width) / map.asset.tileWidth);
        int y2 = (int) ((go.bbox.y + go.bbox.height) / map.asset.tileWidth) + dy;

        for (int y = y0; y <= y1; y += 1) {
            MapObject m1 = getTileInMap(map, x1, y);
            MapObject m2 = getTileInMap(map, x2, y);
            if (((m1 != null && m1.block) || (m2 != null && m2.block)) && go.action == GameAction.FALL) {
                go.action = GameAction.IDLE;
                //go.y =(int)(go.y/map.asset.tileHeight)*map.asset.tileHeight;
                break;
            }
            if (m1 == null && m2 == null && falling) {
                go.action = GameAction.FALL;
            }
            createDebugInfo(go, map, m1, x1, y);
            createDebugInfo(go, map, m2, x2, y);
        }
    }

    public void testMoveUp(MapLevel map, GameObject go) {
        int x1 = (int) (go.bbox.x / map.asset.tileWidth);
        int y1 = (int) ((go.bbox.y) / map.asset.tileWidth);
        int x2 = (int) ((go.bbox.x + go.bbox.width) / map.asset.tileWidth);
        int y2 = (int) ((go.bbox.y) / map.asset.tileWidth);
        MapObject m1 = getTileInMap(map, x1, y1);
        MapObject m2 = getTileInMap(map, x2, y2);


        if (go.action == GameAction.JUMP
                && ((m1 != null && m1.block)
                || (m2 != null && m2.block)
        )
        ) {
            go.y = go.oldY;
            createDebugInfo(go, map, m1, x1, y1);
        }
        createDebugInfo(go, map, m1, x1, y1);
        createDebugInfo(go, map, m2, x2, y2);
    }


    public void testMoveLeft(MapLevel map, GameObject go, int ox, int oy, int oh) {
        MapObject mo;
        for (int iy = oy; iy < oy + oh; iy++) {
            mo = getTileInMap(map, ox, iy);
            createDebugInfo(go, map, mo, ox, iy);
            if (mo != null) {
                if (mo.block) {
                    go.dx = 0.0f;
                    go.x = go.oldX;
                    break;
                } else
                    collide(go, map, mo, ox, iy);
            }
        }
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
    private void createDebugInfo(GameObject go, MapLevel map, MapObject m1, int ox, int oy) {
        if (game.config.debug > 3) {
            MapTileCollision mtc = new MapTileCollision();
            mtc.x = ox;
            mtc.y = oy;
            mtc.w = map.asset.tileWidth;
            mtc.h = map.asset.tileHeight;
            mtc.rX = mtc.x * mtc.w;
            mtc.rY = mtc.y * mtc.h;
            mtc.mo = m1;
            go.collidingZone.add(mtc);
        }
    }

    public void testMoveRight(MapLevel map, GameObject go, int ox, int ow, int oy, int oh) {
        testMoveLeft(map, go, ox + ow, oy, oh);
    }

    /**
     * As the `MapObject` is not null and is not a blocking one, we try to collect it,
     * and test if the `MapObject` type is an item or an object.
     *
     * @param go  the `GameObject` that `canCollect`
     * @param map the map where to search for
     * @param mo  the MapObject to be tested with
     * @param x   the horizontal position in the tiles map
     * @param y   the vertical position in the tiles map
     */
    private void collide(GameObject go, MapLevel map, MapObject mo, int x, int y) {
        listeners.get(go.getClass()).collide(new CollisionEvent(COLLISION_MAP, go, null, mo, map, x, y));
    }

    /**
     * Retrieve the MapObject from the tiles map. it's also checking that (x,y) is not out pf the map.
     * return null elsewhere.
     *
     * @param map the map to find
     * @param x   the horizontal position to test
     * @param y   the vertical position to test
     * @return
     */
    private MapObject getTileInMap(MapLevel map, int x, int y) {
        if (x < 0 || y < 0 || x > map.tiles.length - 1 || y > map.tiles[0].length - 1) {
            return null;
        }
        return map.tiles[x][y];
    }
}
