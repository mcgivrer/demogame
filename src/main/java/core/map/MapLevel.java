package core.map;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.math.PhysicEngineSystem.PhysicType;
import core.math.Vector2D;
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
	
	public Map<String,Vector2D> initialPosition = new HashMap<>();

	// name of the output level
	public String nextLevel;

	// the initial position of the GameObject player.
	public double playerInitialX = 0;
	public double playerInitialY = 0;

	// Max Size of the level
	public Dimension maxSize;

	// Lights in the level.
	public List<Light> lights = new ArrayList<>();

	public MapLevel() {
		super();
		this.physicType = PhysicType.STATIC;
	}

	/**
	 * This method is used to constrain GameObject in the MapLevel bounding box.
	 *
	 * @param go the GameObject to be evaluated and constrained if necessary.
	 */
	public void constrainToMapLevel(MapLayer ml, int index, GameObject go) {
		if (go.pos.x + go.size.x > ml.width * ml.assetsObjects.get(index).tileWidth) {
			go.pos.x = ml.width * ml.assetsObjects.get(index).tileWidth - go.size.x;
			go.vel.x = -go.vel.x;
		}
		if (go.pos.y + go.size.y > ml.height * ml.assetsObjects.get(index).tileHeight) {
			go.pos.y = ml.height * ml.assetsObjects.get(index).tileHeight - go.size.y;
			go.vel.y = -go.vel.y;
		}

		if (go.pos.x < 0.0f) {
			go.pos.x = 0.0f;
			go.vel.x = -go.vel.x;
		}
		if (go.pos.y < 0.0f) {
			go.pos.y = 0.0f;
			go.vel.y = -go.vel.y;
		}
	}

	public Dimension getMaxSize() {
		return maxSize;
	}
}