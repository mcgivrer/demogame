package core.map;

import core.object.GameObject;

import java.awt.*;

public class MapCollider {

    public enum Direction{
        NONE,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT;
    }

    public Direction isColliding(MapLevel map, GameObject go) {
        int ox = (int) (go.bbox.x / map.asset.tileWidth);
        int oy = (int) (go.bbox.x / map.asset.tileHeight);
        int ow = (int) (go.bbox.width / map.asset.tileWidth);
        int oh = (int) (go.bbox.height / map.asset.tileHeight);

        MapObject mo = map.tiles[ox-1][oy];
        MapObject mo1 = map.tiles[ox+ow+1][oy];
        MapObject mo2 = map.tiles[ox][oy-1];
        MapObject mo3 = map.tiles[ox][oy+oh+1];
        if(mo!=null){
            return Direction.LEFT;
        }
        if(mo1!=null){
            return Direction.RIGHT;
        }
        if(mo2!=null){
            return Direction.TOP;
        }
        if(mo3!=null){
            return Direction.BOTTOM;
        }
        return Direction.NONE;
    }
}
