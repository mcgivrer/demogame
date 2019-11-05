package core.map;

import com.google.gson.Gson;
import core.ResourceManager;
import core.object.GameObject;
import core.object.GameObjectType;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
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
    private static int idxEnemy = 0;

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
        return mapLevel;
    }

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
                    // those MapObject is tile
                    if (!mo.type.equals("player") && !mo.type.equals("enemy_")) {
                        mapLevel.tiles[x][y] = mo;
                    } else {
                        // those MapObject are GameObject !
                        createGameObject(mapLevel, y, x, mo);
                    }
                } else {
                    // no tile or object on tile place.
                    mapLevel.tiles[x][y] = null;
                }
            }
        }
        return mapLevel;
    }

    public static void createGameObject(MapLevel mapLevel, int y, int x, MapObject mo) {
        GameObject go = null;
        go = generateGameObject(mapLevel, mo, x, y);
        switch (mo.type) {
            case "player":
                mapLevel.player = go;
                break;
            case "enemy_":
                if (mapLevel.enemies == null) {
                    mapLevel.enemies = new ArrayList<>();
                }
                mapLevel.enemies.add(go);
                break;
            default:
                System.out.println(String.format("Unknown object type %s", mo.type));
                break;
        }
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
            switch (mo.type) {
                case "player":
                    go = createObjectFromClass(mapLevel, mo, x, y);
                    break;
                case "enemy_":
                    go = createObjectFromClass(mapLevel, mo, x, y);
                    break;
                default:
                    break;
            }
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
            System.out.println("unable to intantiate " + e.getMessage() + "Stack:" + e.getStackTrace());
        }
        return mapLevel;
    }

    private static GameObject populateGameObjectAttributes(MapLevel mapLevel, GameObject go, MapObject mo) {
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
        go.name = mo.type.replace("_", "_" + (++idxEnemy));
        // initialize attributes
        go.attributes.putAll(mo.attributes);
        return go;
    }
}