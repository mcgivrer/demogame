package core.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import core.behaviors.Behavior;
import core.gfx.Animation;
import core.math.Material;
import core.math.PhysicEngineSystem.PhysicType;
import core.object.GameObject;
import core.object.GameObjectType;
import core.object.Light;
import core.resource.ResourceManager;
import lombok.extern.slf4j.Slf4j;

/**
 * The class read the map file from a fileMap path and generate/load all needed
 * resources. - It will build the assets for map rendering, - It will load the
 * background image is provided, - It will create the entities for this level.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
@Slf4j
public class MapReader {

	private static List<String> resources = new ArrayList<>();

	public enum TileType {
		PLAYER("player"), ENEMY("enemy"), LIGHT("light"), OBJECT("object"), ITEM("item"), TILE("tile");

		private String value;

		TileType(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}

	}

	private static int idxEnemy = 0;

	/**
	 * Return the list of resources to be loaded.
	 * 
	 * @return list of string correspong to path to resources.
	 */
	public static List<String> detectResourcesToLoad() {
		return resources;
	}

	/**
	 * Read the json file fileMap to renegare al tiles and object for a level map.
	 *
	 * @param fileMap the json file to ne read.
	 * @return a fully ready to play a MapLevel
	 */
	public static MapLevel readFromFile(String fileMap) {
		MapLevel mapLevel = null;
		// load level from json file
		String jsonDataString = ResourceManager.getString(fileMap);

		if (jsonDataString != null && !jsonDataString.equals("")) {

			log.debug("parse the {} json file as e map level", fileMap);

			Gson gson = new Gson();
			mapLevel = gson.fromJson(jsonDataString, MapLevel.class);

			for (MapLayer ml : mapLevel.layers.values()) {

				switch (ml.type) {

				case LAYER_BACKGROUND_IMAGE:
					if (ml.background != null && !ml.background.equals("")) {
						ml.backgroundImage = ResourceManager.getImage(ml.background);
						log.debug("Load a specific background image {}", ml.background);
					}
					break;

				case LAYER_TILEMAP:
					ml.width = ml.map.get(0).length();
					ml.height = ml.map.size();
					// load asset from json file.
					for (String assetStr : ml.assets) {
						createAsset(gson, ml, assetStr);
					}
					// generate tiles
					mapLevel = generateTilesAndObject(mapLevel, ml);
					break;

				default:
					break;
				}
			}

		}
		return mapLevel;
	}

	/**
	 * @param gson
	 * @param ml
	 * @param assetStr
	 */
	private static void createAsset(Gson gson, MapLayer ml, String assetStr) {
		String jsonAssetString = ResourceManager.getString(assetStr);
		if (jsonAssetString != null && !jsonAssetString.equals("")) {
			MapObjectAsset mop = gson.fromJson(jsonAssetString, MapObjectAsset.class);
			ml.assetsObjects.add(mop);
		}
	}

	/**
	 * Generate Tiles and Objects in the map.
	 * 
	 * @param mapLevel the MapLevel object to be created.
	 * @param ml       the MapLevel where to create the tiles and objects.
	 */
	public static MapLevel generateTilesAndObject(MapLevel mapLevel, MapLayer ml) {
		ml.tiles = new MapObject[(int) ml.width][(int) ml.height];

		// generate all objects.
		ml = createAssetMapObjects(ml);

		// build Map
		for (int y = 0; y < ml.height; y++) {
			String line = ml.map.get(y);
			for (int x = 0; x < ml.width; x++) {
				String code = "" + line.charAt(x);
				if (ml.assetsObjects.get(0).objects.containsKey(code)) {
					MapObject mo = ml.assetsObjects.get(0).objects.get(code);
					// those MapObject is tile
					switch (mo.type) {
					case PLAYER:
					case LIGHT:
					case ENEMY:
						createGameObject(mapLevel, ml, x, y, mo);
						break;
					default:
						ml.tiles[x][y] = mo;
						break;
					}
				} else {
					// no tile or object on tile place.
					ml.tiles[x][y] = null;
				}
			}
		}
		return mapLevel;
	}

	/**
	 * Create a GameObject from a MapObject int the MapLevel at (x,y)
	 * 
	 * @param mapLevel the map where to generate object from
	 * @param ml       the MapLayer object to extract the MapObject from
	 * @param x        the x coordinate in MapLayer
	 * @param y        the y coordinate in MapLayer
	 * @param mo       the MapObject containing the MapObject definition.
	 */
	public static void createGameObject(MapLevel mapLevel, MapLayer ml, int x, int y, MapObject mo) {
		// Initialize MapLevel.child attribute if not.
		if (mapLevel.child == null) {
			mapLevel.child = new HashMap<String, GameObject>();
		}

		try {
			// create GameObject from MapOpbject
			GameObject go = createObjectFromClass(ml, mo, x, y);
			mapLevel.initialPosition.put(go.name,go.pos);
			switch (mo.type) {
			case ENEMY:
				// add the object to the MapLevel object.
				mapLevel.child.put(go.name, go);
				go.physicType = PhysicType.DYNAMIC;
				break;
			case PLAYER:
				mapLevel.playerInitialX = go.pos.x;
				mapLevel.playerInitialY = go.pos.y;
				//go.physicType = PhysicType.DYNAMIC;
				// add the object to the MapLevel object.
				mapLevel.child.put(go.name, go);
				break;
			case LIGHT:
				mapLevel.lights.add((Light) go);
				go.physicType = PhysicType.STATIC;
				break;
			default:
				log.error(String.format("Unknown object type %s", mo.type));
				break;
			}

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Unable to instantiate the {}  object.", mo.clazz);
		}
	}

	/**
	 * Instantiate an object based on the MapObject clazz attribute at a specific
	 * position x,y and in a defined priority and layer.
	 *
	 * @param mapLevel the MapLevel to browse
	 * @param mo       the MapObject to be interpreted to create a GameObject
	 * @param x        the horizontal position
	 * @param y        the vertical position
	 * @return an initialized GameObject
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static GameObject createObjectFromClass(MapLayer layer, MapObject mo, int x, int y)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		GameObject go;

		Class<?> class1 = Class.forName(mo.clazz);

		go = (GameObject) class1.newInstance();
		go = populateGameObjectAttributes(layer.assetsObjects.get(0), go, mo);
		go.pos.x = (x - 1) * layer.assetsObjects.get(0).tileWidth;
		go.pos.y = (y - 1) * layer.assetsObjects.get(0).tileHeight;
		go.newPos = go.pos;
		go.bbox.fromGameObject(go);
		return go;
	}

	/**
	 * Read the asset file to populate MapObject asset into the mapLevel.
	 *
	 * @param mapLevel the map level where to load the MapObject asset.
	 * @return the MapLevel with its asset initialized.
	 */
	private static MapLayer createAssetMapObjects(MapLayer mapLayer) {
		try {
			for (MapObjectAsset asset : mapLayer.assetsObjects) {
				asset.imageBuffer = ImageIO.read(MapReader.class.getResourceAsStream(asset.image));
				for (Entry<String, MapObject> emo : asset.objects.entrySet()) {
					MapObject mo = emo.getValue();
					mo.asset = asset;
					if (mo != null) {
						switch (mo.type) {
						case TILE:
						case OBJECT:
						default:
							if (mo.size != null && !mo.size.equals("")) {
								String[] sizeValue = mo.offset.split(",");
								mo.width = Integer.parseInt(sizeValue[0]);
								mo.height = Integer.parseInt(sizeValue[1]);
							} else {
								mo.width = asset.tileWidth;
								mo.height = asset.tileHeight;
							}
							if (mo.offset != null && !mo.offset.equals("")) {
								String[] offsetValue = mo.offset.split(",");
								mo.offsetX = Integer.parseInt(offsetValue[0]);
								mo.offsetY = Integer.parseInt(offsetValue[1]);
								mo = getImageBufferFromAsset(asset, mo, mo.offsetX, mo.offsetY);
							}
							if (mo.frameSet.size() > 0) {
								mo = createAnimation(asset, mo);
							}

							asset.objects.put(emo.getKey(), mo);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("unable to intantiate " + e.getMessage() + "Stack:" + e.getStackTrace());
		}
		return mapLayer;
	}

	/**
	 * Create frames from a list of offset(frameSet) and build list of frameImages
	 * 
	 * @param asset
	 * @param mo
	 */
	private static MapObject createAnimation(MapObjectAsset asset, MapObject mo) {
		mo.animation = new Animation();
		for (String frame : mo.frameSet) {
			String[] frameItem = frame.split(",");
			int ox = Integer.parseInt(frameItem[0]);
			int oy = Integer.parseInt(frameItem[1]);
			int timeFrame = Integer.parseInt(frameItem[2]);
			BufferedImage img = asset.imageBuffer.getSubimage((ox - 1) * mo.width, (oy - 1) * mo.height, mo.width,
					mo.height);
			mo.animation.frameImages.add(img);
			mo.animation.frameTime.add(timeFrame);
		}
		mo.animation.reset();
		return mo;
	}

	public static MapObject getImageBufferFromAsset(MapObjectAsset asset, MapObject mo, int x, int y) {
		int ix = (x - 1) * asset.tileWidth;
		int iy = (y - 1) * asset.tileHeight;
		mo.imageBuffer = asset.imageBuffer.getSubimage(ix, iy, mo.width, mo.height);
		return mo;
	}

	private static GameObject populateGameObjectAttributes(MapObjectAsset moa, GameObject go, MapObject mo) {
		if (mo.offset != null && !mo.offset.equals("") && mo.size != null && !mo.size.equals("")) {
			String[] values = mo.offset.split(",");
			int ox = Integer.parseInt(values[1]);
			int oy = Integer.parseInt(values[0]);
			values = mo.size.split(",");
			go.size.x = Integer.parseInt(values[0]);
			go.size.y = Integer.parseInt(values[1]);
			// get image

			go.image = moa.imageBuffer.getSubimage((ox - 1) * moa.tileWidth, (oy - 1) * moa.tileHeight, (int) go.size.x,
					(int) go.size.y);
			go.type = GameObjectType.IMAGE;
			// go.bbox = mo.bbox;
			go.bbox.fromGameObject(go);
		}
		go.priority = mo.priority;
		go.layer = mo.layer;
		// the GameObject can collect items (or not !)
		go.canCollect = mo.canCollect;

		if (!mo.color.equals("")) {
			if (mo.color.startsWith("[")) {
				String[] color = mo.color.substring(1, mo.color.length() - 1).split(",");
				float[] v = new float[4];
				int i = 0;
				for (String c : color) {
					v[i++] = Float.parseFloat(c);
				}
				go.foregroundColor = new Color(v[0], v[1], v[2], v[3]);
			} else {
				switch (mo.color) {
				case "RED":
					go.foregroundColor = Color.RED;
					break;
				case "YELLOW":
					go.foregroundColor = Color.YELLOW;
					break;
				case "BLUE":
					go.foregroundColor = Color.BLUE;
					break;
				case "GREEN":
					go.foregroundColor = Color.GREEN;
					break;
				case "WHITE":
					go.foregroundColor = Color.WHITE;
					break;
				case "BLACK":
					go.foregroundColor = Color.BLACK;
					break;
				default:
					go.foregroundColor = null;
					break;

				}
			}
		}
		if (mo.name != null && !mo.name.equals("")) {
			go.name = mo.name.replace("#", "" + (++idxEnemy));
		}
		// initialize attributes
		go.attributes.putAll(mo.attributes);

		// Convert behaviors list into real Behaviors
		addBehaviors(go);
		// add material (if defined)
		addPhysicAttributes(go);

		// Specific processing for Light object
		if (go instanceof Light) {
			Light l = (Light) go;
			if (mo.lightType != null) {
				l.lightType = mo.lightType;
			}
			switch (l.lightType) {
			case LIGHT_CONE:
				l.size.x = (double) mo.attributes.get("radius");
				l.size.y = (double) mo.attributes.get("size");
				break;
			case LIGHT_SPHERE:
				l.size.x = (double) mo.attributes.get("radius");
				l.size.y = (double) mo.attributes.get("radius");
				break;
			case LIGHT_AMBIANT:
				break;
			}
			l.intensity = (double) mo.attributes.get("intensity");
			l.size.y += (3 * moa.tileHeight);
			if (mo.attributes.containsKey("glittering")) {
				l.glitterEffect = (double) mo.attributes.get("glittering");
			}
			return l;
		}
		return go;
	}

	private static void addPhysicAttributes(GameObject go) {
		if (go.attributes.containsKey("physic")) {
			Map<String, Object> physicAttributes = (Map<String, Object>) go.attributes.get("physic");
			// retrieve physic engine computation type to use for this entity.
			if (physicAttributes.containsKey("physicType")) {
				go.physicType = PhysicType.valueOf((String) physicAttributes.get("physicType"));
			}
			// retrieve material to use for this entity.
			if (physicAttributes.containsKey("material")) {
				Map<String, Object> materialAttributes = (Map<String, Object>) physicAttributes.get("material");
				Material material = Material.builder((String) materialAttributes.get("name"));
				if (materialAttributes.containsKey("elasticity")) {
					material.elasticity = (double) materialAttributes.get("elasticity");
				}
				if (materialAttributes.containsKey("friction")) {
					material.friction = (double) materialAttributes.get("friction");
				}
				if (materialAttributes.containsKey("magnetism")) {
					material.magnetism = (double) materialAttributes.get("magnetism");
				}
				if (materialAttributes.containsKey("density")) {
					material.density = (double) materialAttributes.get("density");
				}
				go.material = material;
			}
		}
	}

	/**
	 * Parse the GameObject <code>go</code> "behaviors" attribute list to add and
	 * instantiate the neede Behaviors.
	 * 
	 * @param go the GameObject to add behaviors to.
	 */
	private static void addBehaviors(GameObject go) {
		if (go.attributes.containsKey("behaviors")) {
			List<String> behaviors = (List<String>) go.attributes.get("behaviors");
			for (String sb : behaviors) {
				try {
					Class<?> classBehavior = Class.forName(sb);
					Behavior b = (Behavior) classBehavior.newInstance();
					if (b != null) {
						go.behaviors.add(b);
					}
				} catch (Exception e) {
					log.error("Unable to add behavior {} to GameObject named {}", sb, go.name, e);
				}
			}
		}

	}

}