package demo.map;

import demo.map.MapObject;

import java.awt.image.BufferedImage;
import java.util.Map;

public class MapObjectAsset {
    public String name;
    public String image;
    public BufferedImage imageBuffer;

    public int tileWidth, tileHeight;

    public Map<String, MapObject> objects;
}