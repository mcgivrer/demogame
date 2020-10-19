package core.map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.collision.CollisionPoint;
import core.gfx.Animation;
import core.map.MapReader.TileType;
import core.object.BBox;
import core.object.Light.LightType;
import lombok.ToString;

/**
 * The MapObject is one of the objects in an assets to populate MapLayer tiles.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
@ToString
public class MapObject {

    public String id;
    public String image;
    public TileType type;
    public String name;
    public String clazz;
    public String color;

    public String offset;
    public String size;
    public int priority;
    public int layer;

    public double friction;

    public LightType lightType;
    public double intensity;
    public double radius;

    public MapObjectAsset asset;

    public Animation animation;

    public List<String> frameSet = new ArrayList<>();

    public int mapX, mapY;

    public BBox bbox;

    public String description;
    public String usage;

    public int offsetX, offsetY, width, height;

    public BufferedImage imageBuffer;

    public boolean collectible;
    public boolean hit;
    public boolean block;
    public boolean canCollect;
    public boolean can;
    public boolean climbable;

    public int money;
    public int damage;
    public int energy;

    public Map<String, Object> attributes = new HashMap<>();
    public Map<String,CollisionPoint> collisionPoints = new HashMap<>();

    public boolean levelOutput;
    public String nextLevel;
}
