package core.map;

import core.object.BBox;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapObject {
    public String id;
    public String image;
    public String type;
    public String clazz;
    public String color;

    public String offset;
    public String size;

    public String description;
    public String usage;


    public int offsetX, offsetY, width, height;

    public BufferedImage imageBuffer;

    public boolean collectible;
    public boolean hit;
    public boolean block;
    public boolean canCollect;
    public boolean can;

    public int money;
    public int damage;
    public int energy;

    public Map<String, Object> attributes = new HashMap<>();

    public boolean levelOutput;
    public String nextLevel;
    public BBox bbox;
}
