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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The ResourceManager is the resource store where to load all needed resources.
 * <p>
 * Sample usage : ```Java // in a next version you will be able to // provide a
 * listener to implement for example // a Gauge to track loading status.
 * ResourceManager.AddListener(new MyListener()); // at initialization time :
 * ResourceManager.add("MyFile.json"); ResourceManager.add("image.png"); //
 * later to use resource : String str =
 * ResourceManager.getString("MyFile.json"); BufferedImage img =
 * ResourceManager.getString("image.png"); ```
 */
@Slf4j
public class ResourceManager implements System {

    private static ResourceManager instance = new ResourceManager();

    public Map<String, Object> resources = new ConcurrentHashMap<>();

    public static BufferedImage getImage(String path) {
        return (BufferedImage) instance.resources.get(path);
    }

    public static String getString(String path) {
        return (String) instance.resources.get(path);
    }

    public static SoundClip getSoundClip(String path) {
        return (SoundClip) instance.resources.get(path);
    }


    public static void add(String[] paths) {
        for (String path : paths) {
            add(path);
            log.info("Add resource {}", path);
        }
    }

    public static void add(String path) {
        try {
            if (path.endsWith(".jpg") || path.contains(".png")) {
                BufferedImage o;
                o = ImageIO.read(instance.getClass().getResourceAsStream(path));
                if (o != null) {
                    instance.resources.put(path, o);
                }
            }
            if (path.contains(".json")) {
                InputStream stream = ResourceManager.class.getResourceAsStream(path);
                String json = new BufferedReader(new InputStreamReader(stream)).lines().parallel()
                        .collect(Collectors.joining("\n"));
                if (json != null && !json.equals("")) {
                    instance.resources.put(path, json);
                }
            }
            if (path.contains(".wav") || path.contains(".mp3")) {
                InputStream sndStream = ResourceManager.class.getResourceAsStream(path);
                SoundClip sc = new SoundClip(path, sndStream);
                if (sc != null) {
                    instance.resources.put(path, sc);
                }
            }
        } catch (IOException e) {
            log.error("Unable to read the resource :{}", path, e);
        }
    }

    public static void remove(String path) {
        if (instance.resources.containsKey(path)) {
            instance.resources.remove(path);
        }
    }

    @Override
    public String getName() {
        return ResourceManager.class.getCanonicalName();
    }

    @Override
    public int initialize(Game game) {
        return 0;
    }

    public void dispose() {
        instance.resources.clear();
    }
}