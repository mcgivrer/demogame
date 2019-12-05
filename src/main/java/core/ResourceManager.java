package core;

import core.audio.SoundClip;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
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

/**
 * The ResourceManager is the resource store where to load all needed resources.
 * <p>
 * Sample usage :
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
 * </pre> ```
 */
@Slf4j
public class ResourceManager implements System {

    private static ResourceManager instance = new ResourceManager();

    public Map<String, Object> resources = new ConcurrentHashMap<>();
    private List<ProgressListener> listeners = new ArrayList<>();

    /**
     * retrieve a resource as a BufferedImage
     *
     * @param path
     * @return
     */
    public static BufferedImage getImage(String path) {
        return (BufferedImage) instance.resources.get(path);
    }

    /**
     * Retrieve a resource as a String.
     *
     * @param path
     * @return
     */
    public static String getString(String path) {
        return (String) instance.resources.get(path);
    }

    /**
     * Retrieve a resource as a SoundClip.
     *
     * @param path
     * @return
     */
    public static SoundClip getSoundClip(String path) {
        return (SoundClip) instance.resources.get(path);
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
            if (instance != null
                    && instance.listeners != null
                    && !instance.listeners.isEmpty()) {
                for (ProgressListener pl : instance.listeners) {
                    pl.update((float) (index / nbResources), path);
                }
            }
        }
    }

    /**
     * Add a resource to the resource the managed ones. Load as Image of JSON reosurces according to their
     * file extension.
     * <ul>
     *     <li><code>jpg</code>, <code>png</code> are loaded as image resource,</li>
     *     <li><code>json</code> is loaded as String resource.</li>
     * </ul>
     *
     * @param path the file path to the resource to be loaded and managed.
     */
    public static void add(String path) {
        log.debug("Add resource '{}'", path);
        try {
            if (path.endsWith(".jpg") || path.contains(".png")) {
                BufferedImage o;
                o = ImageIO.read(instance.getClass().getResourceAsStream(path));
                if (o != null) {
                    instance.resources.put(path, o);
                }
                log.debug("'{}' added as an image resource", path);
            }
            if (path.contains(".json")) {
                InputStream stream = ResourceManager.class.getResourceAsStream(path);
                String json = new BufferedReader(new InputStreamReader(stream)).lines().parallel()
                        .collect(Collectors.joining("\n"));
                if (json != null && !json.equals("")) {
                    instance.resources.put(path, json);
                }
                log.debug("'{}' added as a JSON resource", path);
            }
            if (path.contains(".wav") || path.contains(".mp3") || path.contains(".aiff")) {
                InputStream sndStream = ResourceManager.class.getResourceAsStream(path);
                SoundClip sc = new SoundClip(path, sndStream);
                if (sc != null) {
                    instance.resources.put(path, sc);
                }
            }
        } catch (IOException e) {
            log.error("Unable to read the resource : '{}'", path, e);
        }
    }

    /**
     * Remove the resource path from the scope of the manager.
     *
     * @param path the resource to be removed.
     */
    public static void remove(String path) {
        if (instance.resources.containsKey(path)) {
            instance.resources.remove(path);
        }
    }

    public static void addListener(ProgressListener pl) {
        if (instance != null) {
            instance.listeners.add(pl);
        }
    }

    public static void clear(){
        if (instance != null) {
            instance.dispose();
        }

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


        instance = new ResourceManager();

        resources = new ConcurrentHashMap<>();
        listeners = new ArrayList<>();
        return 0;
    }

    /**
     * Release all resources got by the service.
     */
    public void dispose() {
        instance.resources.clear();
        log.debug("All resources have been removed.");
    }


}