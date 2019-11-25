package core.map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MapLayer {

	public enum LayerType {
		LAYER_BACKGROUND_IMAGE, LAYER_TILEMAP
	}

	public LayerType type;
	public int index;
	public String name;
	public MapLevel parent;
	
	public int width;
	public int height;

	public List<String> assets ;

	public List<MapObjectAsset> assetsObjects = new ArrayList<>();

	// the image used as background of the level.
	public String background;
	public BufferedImage backgroundImage;

	// raw text format for the map.
	public List<String> map;
	// All tiles of the level.
	public MapObject[][] tiles;

}
