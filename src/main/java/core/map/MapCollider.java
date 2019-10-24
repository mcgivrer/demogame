package core.map;

import core.object.GameObject;

public class MapCollider {

    public enum Direction {
        NONE, TOP, BOTTOM, LEFT, RIGHT;
    }

    public void checkCollision(MapLevel map, GameObject go) {
        int ox = (int) (go.bbox.x / map.asset.tileWidth);
        int oy = (int) (go.bbox.y / map.asset.tileHeight);
        int ow = (int) (go.bbox.width / map.asset.tileWidth);
        int oh = (int) (go.bbox.height / map.asset.tileHeight);

        MapObject mo;
        if (go.dx < 0) {
            mo = getTile(map, ox - 1, oy);
            resolveCollision(go, map, mo, ox - 1, oy, Direction.LEFT);
        }
        if (go.dx > 0) {
            mo = getTile(map, ox + 1, oy);
            resolveCollision(go, map, mo, ox + +ow + 1, oy, Direction.RIGHT);
        }
        if (go.dy < 0) {
            mo = getTile(map, ox, oy - 1);
            resolveCollision(go, map, mo, ox, oy - 1, Direction.TOP);
        }
        if (go.dy > 0) {
            mo = getTile(map, ox, oy + 1);
            resolveCollision(go, map, mo, ox, oy + oh + 1, Direction.BOTTOM);
        }

    }

    public MapObject getTile(MapLevel map, int x, int y) {

        if (x >= 0 && x < map.tiles.length && y >= 0 && y < map.tiles[0].length) {
            return map.tiles[x][y];
        } else {
            return null;
        }
    }

    public void resolveCollision(GameObject go, MapLevel map, MapObject mo, int tx, int ty, Direction dir) {
        if (mo != null) {
            if (mo.block) {
                switch (dir) {
                    case LEFT:
                        go.x = (tx + 1) * (map.asset.tileWidth - 1);
                        go.dx = 0.0f;
                        break;
                    case RIGHT:
                        go.x = 1 + ((tx - 1) * map.asset.tileWidth) - mo.width;
                        go.dx = 0.0f;
                        break;
                    case TOP:
                        go.y = ((ty - 1) * map.asset.tileHeight) - 1;
                        go.dy = 0.0f;
                        break;
                    case BOTTOM:
                        go.y = 1 + ((ty + 1) * map.asset.tileHeight) - mo.height;
                        go.dy = 0.0f;
                        break;
                }
            }
            if (mo.collectible) {
                if (mo.money > 0) {
                    double coins = (double) (go.attributes.get("coins"));
                    go.attributes.put("coins", coins + mo.money);
                    map.tiles[tx][ty] = null;
                }
            }
        }
    }
}
