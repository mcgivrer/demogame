import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MapLevel {

    public String levelName;
    public String description;

    public String objects;
    public MapObjectAsset asset;

    public int width;
    public int height;

    public String background;
    public BufferedImage backgroundImage;
    public List<String> map = new ArrayList<>();

    public MapObject[][] tiles;

    public String nextLevel;
    public GameObject player;
    public List<GameObject> enemies = new ArrayList<>();
}