package core.resource;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import core.Game;
import core.audio.SoundClip;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * The ResourceManager is the resource store where to load all needed resources.
 * <p>
 * Sample usage :
 * 
 * <pre>
 * // in a next version you will be able to
 * // provide a listener to implement for example
 * // a Gauge to track loading status.
 * ResourceManager.addListener(new MyListener());
 * // at initialization time :
 * ResourceManager.add("MyFile.json");
 * ResourceManager.add("image.png");
 * // later to use resource :
 * String str = ResourceManager.getString("MyFile.json");
 * BufferedImage img = ResourceManager.getString("image.png");
 * </pre>
 * 
 * ```
 */
@Slf4j
public class ResourceManager extends AbstractSystem {

    private static Map<String, Object> resources = new ConcurrentHashMap<>();
    private static List<ProgressListener> listeners = new ArrayList<>();
    private static List<String> resourcesNotPreloaded = new ArrayList<>();

    public ResourceManager(Game game) {
        super(game);
    }

    /**
     * retrieve a resource as a BufferedImage
     *
     * @param path
     * @return
     */
    public static BufferedImage getImage(String path) {
        addResourceIfNotPreloaded(path);
        return (BufferedImage) resources.get(path);
    }

    private static void addResourceIfNotPreloaded(String path) {
        if (!resources.containsKey(path)) {
            log.info("Resource {} has not been preloaded", path);
            resourcesNotPreloaded.add(path);
            add(path);
        }
    }

    /**
     * Retrieve a resource as a String.
     *
     * @param path
     * @return
     */
    public static String getString(String path) {
        addResourceIfNotPreloaded(path);
        return (String) resources.get(path);
    }

    /**
     * Retrieve a resource as a SoundClip.
     *
     * @param path
     * @return
     */
    public static SoundClip getSoundClip(String path) {
        addResourceIfNotPreloaded(path);
        return (SoundClip) resources.get(path);
    }

    /**
     * Load a list of resources
     *
     * @param paths
     * @see ResourceManager#add(String)
     */
    public static void add(String[] paths) {
        float nbResources = paths.length, index = 0.0f;
        for (String path : paths) {
            add(path);
            index += 1.0;
            if (listeners != null && !listeners.isEmpty()) {
                for (ProgressListener pl : listeners) {
                    pl.update((float) (index / nbResources), path);
                }
            }
        }
    }

    /**
     * Add a resource to the resource the managed ones. Load as Image of JSON
     * reosurces according to their file extension.
     * <ul>
     * <li><code>jpg</code>, <code>png</code> are loaded as image resource,</li>
     * <li><code>json</code> is loaded as String resource.</li>
     * </ul>
     *
     * @param path the file path to the resource to be loaded and managed.
     */
    public static void add(String path) {
        log.debug("Add resource '{}'", path);
        String ext = path.substring(path.lastIndexOf("."));
        try (InputStream stream = ResourceManager.class.getResourceAsStream(path)) {

            switch (ext) {
                case ".jpg":
                case ".png":
                    loadImage(path, stream);
                    break;
                case ".json":
                    loadJson(path, stream);
                    break;
                case ".lua":
                    loadLuaScript(path, stream);
                    break;
                case ".wav":
                case ".mp3":
                case ".aiff":
                    loadSound(path, stream);
                    break;
                case ".ttf":
                    loadFont(path, stream);
                    break;
                default:
                    log.error("Unknown file type {} in {}", ext, path);
                    break;
            }
        } catch (IOException e) {
            log.error("Unable to read the resource : '{}'", path, e);
        }
    }

    private static void loadFont(String path, InputStream stream) {
        // load a Font resource
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            if (font != null) {
                resources.put(path, font);
            }
        } catch (FontFormatException | IOException e) {
            log.error("Unable to read font from " + path);
        }
    }

    private static void loadSound(String path, InputStream stream) {
        SoundClip sc = new SoundClip(path, stream);
        if (sc != null) {
            resources.put(path, sc);
        }
        log.debug("'{}' added as an audio resource", path);
    }

    private static void loadLuaScript(String path, InputStream stream) {
        String luas = new BufferedReader(new InputStreamReader(stream)).lines().parallel()
                .collect(Collectors.joining("\n"));
        if (luas != null && !luas.equals("")) {
            resources.put(path, luas);
        }
        log.debug("'{}' added as a LUA script resource", path);
    }

    private static void loadJson(String path, InputStream stream) {
        String json = new BufferedReader(new InputStreamReader(stream)).lines().parallel()
                .collect(Collectors.joining("\n"));
        if (json != null && !json.equals("")) {
            resources.put(path, json);
        }
        log.debug("'{}' added as a JSON resource", path);
    }

    private static void loadImage(String path, InputStream stream) throws IOException {
        BufferedImage o = ImageIO.read(stream);
        if (o != null) {
            resources.put(path, o);
        }
        log.debug("'{}' added as an image resource", path);
    }

    /**
     * Remove the resource path from the scope of the manager.
     *
     * @param path the resource to be removed.
     */
    public static void remove(String path) {
        if (resources.containsKey(path)) {
            resources.remove(path);
        }
    }

    public static void addListener(ProgressListener pl) {
        if (listeners != null) {
            listeners.add(pl);
        }
    }

    public static void clear() {
        resources.clear();
    }

    public static Font getFont(String s) {
        if (!resources.containsKey(s)) {
            add(s);
            log.warn("Resource loading time can be optimized by adding this '{}' resource to the preload time", s);
        }
        return (Font) resources.get(s);
    }

    /**
     * get the name of this service.
     *
     * @return
     */
    @Override
    public String getName() {
        return ResourceManager.class.getCanonicalName();
    }

    /**
     * Initialize the service.
     *
     * @param game
     * @return
     */
    @Override
    public int initialize(Game game) {
        resources = new ConcurrentHashMap<>();
        listeners = new ArrayList<>();
        return 0;
    }

    /**
     * Release all resources got by the service.
     */
    public void dispose() {
        log.info("Resource not preloaded:");
        for (String r : resourcesNotPreloaded) {
            log.info("- {}", r);
        }
        resources.clear();
        log.debug("All resources have been removed.");
    }
}