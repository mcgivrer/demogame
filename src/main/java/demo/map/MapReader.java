package demo.map;

import com.google.gson.Gson;
import demo.object.GameObject;
import demo.object.GameObjectType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

/**
 * The class read the map file from a fileMap path and generate/load all needed resources.
 * - It will build the assets for map rendering,
 * - It will load the background image is provided,
 * - It will create the entities for this level.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
public class MapReader {
    private static int idxEnemy = 0;

    public static MapLevel readFromFile(String fileMap) {
        MapLevel mapLevel = null;
        try {
            // load level from json file
            String jsonDataString = new String(Files.readAllBytes(Paths.get(MapReader.class.getResource(fileMap).toURI())));
            if (!jsonDataString.equals("")) {
                Gson gson = new Gson();
                mapLevel = gson.fromJson(jsonDataString, MapLevel.class);
                mapLevel.width = mapLevel.map.get(0).length();
                mapLevel.height = mapLevel.map.size();

                if (mapLevel != null && mapLevel.background != null && !mapLevel.background.equals("")) {
                    mapLevel.backgroundImage = ImageIO.read(MapReader.class.getResourceAsStream(mapLevel.background));
                }
                // load asset from json file.
                String jsonAssetString = new String(Files.readAllBytes(Paths.get(MapReader.class.getResource("/res/assets/" + mapLevel.objects + ".json").toURI())));
                if (!jsonAssetString.equals("")) {
                    MapObjectAsset mop = gson.fromJson(jsonAssetString, MapObjectAsset.class);
                    mapLevel.asset = mop;

                    // generate tiles
                    mapLevel.tiles = new MapObject[(int)mapLevel.width][(int)mapLevel.height];
                    // generate all objects.
                    mapLevel = createAssetMapObjects(mapLevel);
                    // build Map
                    for (int y = 0; y < mapLevel.height; y++) {
                        String line = mapLevel.map.get(y);
                        for (int x = 0; x < mapLevel.width; x++) {
                            String code = "" + line.charAt(x);
                            if (mapLevel.asset.objects.containsKey(code)) {
                                MapObject mo = mapLevel.asset.objects.get(code);
                                if (!mo.type.equals("player") && !mo.type.equals("enemy_")) {
                                    mapLevel.tiles[x][y] = mo;
                                } else {
                                    GameObject go = null;
                                    try {
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
                                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                mapLevel.tiles[x][y] = null;
                            }
                        }
                    }
                }
            }

        } catch (IOException | URISyntaxException e) {
            System.out.println("Unable to create demo.map.MapLevel from Json");
        }
        return mapLevel;
    }

    private static GameObject generateGameObject(MapLevel mapLevel, MapObject mo, int x, int y) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        GameObject go = null;
        switch (mo.type) {
            case "player":
                Class<?> classO = Class.forName(mo.clazz);
                go = (GameObject) classO.newInstance();
                go = populateGo(mapLevel, go, mo);
                go.layer = 2;
                go.priority = 1;
                go.x = (x - 1) * mapLevel.asset.tileWidth;
                go.y = (y - 1) * mapLevel.asset.tileHeight;
                break;
            case "enemy_":
                Class<?> class1 = Class.forName(mo.clazz);
                go = (GameObject) class1.newInstance();
                go = populateGo(mapLevel, go, mo);
                go.layer = 2;
                go.priority = 10;
                go.x = (x - 1) * mapLevel.asset.tileWidth;
                go.y = (y - 1) * mapLevel.asset.tileHeight;
                break;
        }
        return go;
    }

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
                                    mo.width = Integer.parseInt(offsetValue[0]);
                                    mo.height = Integer.parseInt(offsetValue[1]);
                                } else {
                                    mo.width = mapLevel.asset.tileWidth;
                                    mo.height = mapLevel.asset.tileHeight;
                                }
                                int ix = (mo.offsetX - 1) * mapLevel.asset.tileWidth;
                                int iy = (mo.offsetY - 1) * mapLevel.asset.tileHeight;
                                mo.imageBuffer = mapLevel.asset.imageBuffer.getSubimage(
                                        ix,
                                        iy,
                                        mo.width,
                                        mo.height);
                            }
                            mapLevel.asset.objects.put(emo.getKey(), mo);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("unable to intantiate " + e.getMessage() +
                    "Stack:" + e.getStackTrace());
        }
        return mapLevel;
    }

    private static GameObject populateGo(MapLevel mapLevel, GameObject go, MapObject mo) {
        if (!mo.offset.equals("")) {
            String[] values = mo.offset.split(",");
            int ox = Integer.parseInt(values[1]);
            int oy = Integer.parseInt(values[0]);

            values = mo.size.split(",");
            go.width = Integer.parseInt(values[0]);
            go.height = Integer.parseInt(values[1]);
            //get image
            go.image = mapLevel.asset.imageBuffer.getSubimage(
                    (ox - 1) * mapLevel.asset.tileWidth,
                    (oy - 1) * mapLevel.asset.tileHeight,
                    (int) go.width,
                    (int) go.height);
            go.type = GameObjectType.IMAGE;
        }
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