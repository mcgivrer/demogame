package core.map;

import core.object.GameObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapLevel extends GameObject {
    public String description;

    public String objects;
    public MapObjectAsset asset;

    public String background;
    public BufferedImage backgroundImage;
    public List<String> map = new ArrayList<>();

    public MapObject[][] tiles;

    public String nextLevel;
    public Map<String, GameObject> mapObjects = new ConcurrentHashMap<>();
    public Map<String, Integer> counters = new HashMap<>();

    public void constrainToMapLevel(GameObject go) {
        if (go.x + go.width > width * asset.tileWidth) {
            go.x = width * asset.tileWidth - go.width;
            go.dx = -go.dx;
        }
        if (go.y + go.height > height * asset.tileHeight) {
            go.y = height * asset.tileHeight - go.height;
            go.dy = -go.dy;
        }

        if (go.x < 0.0f) {
            go.x = 0.0f;
            go.dx = -go.dx;
        }
        if (go.y < 0.0f) {
            go.y = 0.0f;
            go.dy = -go.dy;
        }
    }
}