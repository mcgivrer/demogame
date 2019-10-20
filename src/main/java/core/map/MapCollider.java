package core.map;

import core.object.BBox;
import core.object.GameObject;

public class MapCollider {

    public void checkCollision(MapLevel map, GameObject go) {

        int ox = (int) (go.bbox.x / map.asset.tileWidth);
        int oy = (int) (go.bbox.y / map.asset.tileHeight);

        MapObject mo;
        if (Math.signum(go.dx) > 0.0f) {
            mo = getTile(map, ox + 1, oy);
            if (mo.block) {
                go.dx = 0.0f;
                go.x = mo.bbox.x - go.width;
            }
        }
        if (Math.signum(go.dx) < 0.0f) {
            mo = getTile(map, ox - 1, oy);
            if (mo.block) {
                go.dx = 0.0f;
                go.x = mo.bbox.x;
            }
        }


    }


    public MapObject getTile(MapLevel map, int x, int y) {
        BBox bbox = null;
        MapObject mo = map.tiles[x][y];
        if (mo != null) {
            bbox = new BBox(x * map.asset.tileWidth, y * map.asset.tileHeight, map.asset.tileWidth, map.asset.tileHeight);
            mo.bbox = bbox;
        }
        return mo;
    }
}
