package core.map;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * A MapObjectAsset will contains many MapObject, the set is used by one or more
 * MapLayer for tiles resources.
 * 
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
public class MapObjectAsset {
	public String name;
	public String image;
	public BufferedImage imageBuffer;

	public int tileWidth, tileHeight;

	public Map<String, MapObject> objects;
}