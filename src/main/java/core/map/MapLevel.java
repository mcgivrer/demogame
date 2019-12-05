package core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.object.GameObject;
import core.object.Light;
import lombok.ToString;

/**
 * A AmpLevel is a full Tilemap level loaded through the MapReader where all
 * level is computed and GameObjects are instantiated.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @see MapObject
 * @see MapObjectAsset
 * @see GameObject
 * @see MapReader
 * @see MapRenderer
 * @since 2019
 */
@ToString
public class MapLevel extends GameObject {
	public String description;

	// raw text format for the map.
	public Map<String, MapLayer> layers = new HashMap<>();

	// name of the output level
	public String nextLevel;

	// the initial position of the GameObject player.
	public double playerInitialX = 0;
	public double playerInitialY = 0;

	// Lights in the level.
	public List<Light> lights = new ArrayList<>();

	/**
	 * This method is used to constrain GameObject in the MapLevel bounding box.
	 *
	 * @param go the GameObject to be evaluated and constrained if necessary.
	 */
	public void constrainToMapLevel(MapLayer ml, int index, GameObject go) {
		if (go.x + go.width > ml.width * ml.assetsObjects.get(index).tileWidth) {
			go.x = ml.width * ml.assetsObjects.get(index).tileWidth - go.width;
			go.dx = -go.dx;
		}
		if (go.y + go.height > ml.height * ml.assetsObjects.get(index).tileHeight) {
			go.y = ml.height * ml.assetsObjects.get(index).tileHeight - go.height;
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