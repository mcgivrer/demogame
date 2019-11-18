package core.map;

import core.object.GameObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A AmpLevel is a full Tilemap level loaded through the MapReader where all level is computed
 * and GameObjects are instantiated.
 *
 * @author Frdéric Delorme <frederic.delorme@gmai.com>
 * @see MapObject
 * @see GameObject
 * @see MapReader
 * @see MapRenderer
 * @since 2019
 */
public class MapLevel extends GameObject {
    public String description;

    public String objects;
    // the asset to be used to render/display the level.
    public MapObjectAsset asset;

    // the image used as background of the level.
    public String background;
    public BufferedImage backgroundImage;
    // raw text format for the map.
    public List<String> map = new ArrayList<>();

    // All tiles of the level.
    public MapObject[][] tiles;

    // name of the output level
    public String nextLevel;
    // the player
    public GameObject player;
    //the initial position fo the GameObject player.
    public float playerInitialX = 0;
    public float playerInitialY = 0;
    // enemies GameObject in the level.
    public List<GameObject> enemies = new ArrayList<>();

    /**
     * This method is used to constrain GameObject in the MapLevel bounding box.
     *
     * @param go the GameObject to be evaluated and constrained if necessary.
     */
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