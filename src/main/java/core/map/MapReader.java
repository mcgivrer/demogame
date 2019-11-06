package core.map;

import com.google.gson.Gson;
import core.ResourceManager;
import core.object.GameObject;
import core.object.GameObjectType;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public static final String SUFFIX = "_#";
    private static int idxMapEntity = 0;
    private static Map<String, Integer> itemCounters = new HashMap<>();

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
            mapLevel.width = mapLevel.map.get(0).length();
            mapLevel.height = mapLevel.map.size();

            if (mapLevel != null && mapLevel.background != null && !mapLevel.background.equals("")) {
                mapLevel.backgroundImage = ResourceManager.getImage(mapLevel.background);
                log.debug("Lod a specific background image {}", mapLevel.background);
            }
            // load asset from json file.
            String jsonAssetString = ResourceManager.getString(mapLevel.objects);
            if (jsonAssetString != null && !jsonAssetString.equals("")) {
                MapObjectAsset mop = gson.fromJson(jsonAssetString, MapObjectAsset.class);
                mapLevel.asset = mop;

                // generate tiles
                mapLevel = generateTilesAndObject(mapLevel);
            }
        }
        mapLevel.counters = itemCounters;
        return mapLevel;
    }

    /**
     * Parse all map lines and create corresponding MapObject entries in the tiles attribute.
     *
     * @param mapLevel the MapLevel to be parsed and fill with tiles and objects.
     * @return
     */
    public static MapLevel generateTilesAndObject(MapLevel mapLevel) {
        mapLevel.tiles = new MapObject[(int) mapLevel.width][(int) mapLevel.height];
        // generate all objects.
        mapLevel = createAssetMapObjects(mapLevel);
        // build Map
        for (int y = 0; y < mapLevel.height; y++) {
            String line = mapLevel.map.get(y);
            for (int x = 0; x < mapLevel.width; x++) {
                String code = "" + line.charAt(x);
                if (mapLevel.asset.objects.containsKey(code)) {
                    MapObject mo = mapLevel.asset.objects.get(code);
                    updateCounterForObjectType(mo);
                    switch (mo.type) {
                        // those MapObject is tile
                        case "item":
                        case "object":
                        case "tile":
                            mapLevel.tiles[x][y] = mo;
                            break;
                        // those MapObject is an Entity
                        case "entity":
                            createGameObject(mapLevel, y, x, mo);
                            break;
                        default:
                            break;
                    }
                } else {
                    // no tile or object on tile place.
                    mapLevel.tiles[x][y] = null;
                }
            }
        }
        return mapLevel;
    }

    /**
     * Update counter statistics for map elements.
     *
     * @param mo the MapObject to be added to statistic computation.
     */
    private static void updateCounterForObjectType(MapObject mo) {
        if (!itemCounters.containsKey(mo.type)) {
            itemCounters.put(mo.type, new Integer(0));
        }
        int i = itemCounters.get(mo.type);
        i++;
        itemCounters.put(mo.type, i);
    }

    /**
     * Based on described MapObject, create an Entity GameObject in the MapLevel.mapObjects map.
     *
     * @param mapLevel the MapLevel to be parsed and fill with tiles and objects.
     * @param y        map vertical position
     * @param x        map horizontal position
     * @param mo       the MapObject to be converted to GameObject.
     */
    public static void createGameObject(MapLevel mapLevel, int y, int x, MapObject mo) {
        generateGameObject(mapLevel, mo, x, y);
    }

    /**
     * <p>Add a GameObject to the MapLevel.mapObjects</p>
     * <p>The name of the game object is generated according to its suffix.</p>
     * <p>If the suffix format <code>xxxxx_#</code>, a new name will be generated as <code>xxxxx_9999</code> where
     * <code>9999</code> is an internal auto-incremented counter.</p>
     *
     * @param mapLevel
     * @param go
     * @return the modified GameObject.
     */
    private static GameObject addObjectToMap(MapLevel mapLevel, GameObject go) {
        if (go.name.endsWith(SUFFIX)) {
            idxMapEntity++;
            String suffix = String.format("_%04d", idxMapEntity);
            go.name = go.name.replace(SUFFIX, suffix);
        }
        mapLevel.mapObjects.put(go.name, go);
        return go;
    }

    /**
     * Generate a GameObject into the mapLevel map from a MapObject mo at a specific position x,y.
     *
     * @param mapLevel the MapLevel reading
     * @param mo       the MapObject to be translated to a GameObject
     * @param x        the horizontal position
     * @param y        the vertical position
     * @return a well fitted GameObject.
     */
    private static GameObject generateGameObject(MapLevel mapLevel, MapObject mo, int x, int y) {
        GameObject go = null;
        try {
            go = createObjectFromClass(mapLevel, mo, x, y);
            go = addObjectToMap(mapLevel, go);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Unable to instantiate the " + mo.clazz + " object.");
        }
        return go;
    }

    /**
     * Instantiate an object based on the MapObject clazz attribute at a specific position x,y
     * and in a defined priority and layer.
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
    private static GameObject createObjectFromClass(MapLevel mapLevel, MapObject mo, int x, int y) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        GameObject go;
        Class<?> class1 = Class.forName(mo.clazz);

        go = (GameObject) class1.newInstance();
        go = populateGameObjectAttributes(mapLevel, go, mo);

        go.x = (x - 1) * mapLevel.asset.tileWidth;
        go.y = (y - 1) * mapLevel.asset.tileHeight;
        go.bbox.fromGameObject(go);
        return go;
    }

    /**
     * Read the asset file to populate MapObject asset into the mapLevel.
     *
     * @param mapLevel the map level where to load the MapObject asset.
     * @return the MapLevel with its asset initialized.
     */
    private static MapLevel createAssetMapObjects(MapLevel mapLevel) {
        try {
            mapLevel.asset.imageBuffer = ImageIO.read(MapReader.class.getResourceAsStream(mapLevel.asset.image));
            for (Map.Entry<String, MapObject> emo : mapLevel.asset.objects.entrySet()) {
                MapObject mo = emo.getValue();
                if (mo != null) {
                    switch (mo.type) {
                        case "tile":
                        case "object":
                        default:
                            if (mo.offset != null && !mo.offset.equals("")) {
                                String[] offsetValue = mo.offset.split(",");
                                mo.offsetX = Integer.parseInt(offsetValue[0]);
                                mo.offsetY = Integer.parseInt(offsetValue[1]);
                                if (mo.size != null && !mo.size.equals("")) {
                                    String[] sizeValue = mo.offset.split(",");
                                    mo.width = Integer.parseInt(sizeValue[0]);
                                    mo.height = Integer.parseInt(sizeValue[1]);
                                } else {
                                    mo.width = mapLevel.asset.tileWidth;
                                    mo.height = mapLevel.asset.tileHeight;
                                }
                                int ix = (mo.offsetX - 1) * mapLevel.asset.tileWidth;
                                int iy = (mo.offsetY - 1) * mapLevel.asset.tileHeight;
                                mo.imageBuffer = mapLevel.asset.imageBuffer.getSubimage(ix, iy, mo.width, mo.height);
                            }
                            mapLevel.asset.objects.put(emo.getKey(), mo);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("unable to instantiate " + e.getMessage() + "Stack:" + e.getStackTrace());
        }
        return mapLevel;
    }

    /**
     * Copy MapObject attribute to the GameObject with some translation.
     *
     * @param mapLevel
     * @param go       the destination GameObject
     * @param mo       the source MapObject
     * @return the updated GameObject
     */
    private static GameObject populateGameObjectAttributes(MapLevel mapLevel, GameObject go, MapObject mo) {
        go.name = mo.name;
        if (!mo.offset.equals("")) {
            String[] values = mo.offset.split(",");
            int ox = Integer.parseInt(values[1]);
            int oy = Integer.parseInt(values[0]);

            values = mo.size.split(",");
            go.width = Integer.parseInt(values[0]);
            go.height = Integer.parseInt(values[1]);
            go.priority = mo.priority;
            go.layer = mo.layer;
            // get image
            go.image = mapLevel.asset.imageBuffer.getSubimage((ox - 1) * mapLevel.asset.tileWidth,
                    (oy - 1) * mapLevel.asset.tileHeight, (int) go.width, (int) go.height);
            go.type = GameObjectType.IMAGE;
            go.bbox = mo.bbox;
            go.bbox.fromGameObject(go);
        }

        // the GameObject can collect items (or not !)
        go.canCollect = mo.canCollect;

        if (!mo.color.equals("")) {
            switch (mo.color) {
                case "RED":
                    go.foregroundColor = Color.RED;
                    break;
                case "BLUE":
                    go.foregroundColor = Color.BLUE;
                    break;
                case "GREEN":
                    go.foregroundColor = Color.GREEN;
                    break;

            }
        }
        // initialize attributes
        go.attributes.putAll(mo.attributes);
        return go;
    }
}