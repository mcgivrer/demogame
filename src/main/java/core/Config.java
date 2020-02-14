package core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A configuration component to manage and use easily parameters. This simple
 * POJO class will support all command line parameter values and there default
 * values.
 * </p>
 * <p>
 * It parses the argc arguments list directly coming from Java main method.
 * </p>
 * <p>
 * <strong>Note:</strong> Not the sexiest way to maintain thing, but surely one
 * of the easiest.
 * </p>
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 */
@Slf4j
public class Config {
    public int screenWidth;
    public int screenHeight;
    public float screenScale;
    public int fps;
    public String title;
    public int debug;
    public String statesPath;
    public boolean mute;
    public float soundVolume;

    public Map<String, Object> attributes = new HashMap<>();

    /**
     * Initialization of default values for configuraiton.
     */
    public Config() {
        this.title = "notitle";
        this.screenWidth = 360;
        this.screenHeight = 200;
        this.screenScale = 2.0f;
        this.fps = 60;
        this.debug = 0;
        this.statesPath = "/res/game.json";
        this.mute = true;
    }

	/**
	 * Parse all arguments form the main methods,and set the corresponding values.
	 * if no attribute is set, it uses a default value.
	 *
	 * @param argc list of arguments from command line.
	 * @return the Config object initialized with right values.
	 */
	public static Config analyzeArgc(String[] argc) {
		Config config = new Config();
		config.title = "DemoGame";
		config.screenWidth = 360;
		config.screenHeight = 200;
		config.screenScale = 2.0f;
		config.debug = 0;
        config.fps = 60;
        config.mute = true;

		for (String arg : argc) {
			System.out.println(String.format("arg: %s", arg));
			String[] parts = arg.split("=");
			switch (parts[0]) {
                case "f":
                case "fps":
                    config.fps = Integer.parseInt(parts[1]);
                    log.debug("fps request:{}", config.fps);

                    break;
                case "t":
                case "title":
                    config.title = parts[1];
                    log.debug("window title:{}", config.title);

                    break;
                case "h":
                case "height":
                    config.screenHeight = Integer.parseInt(parts[1]);
                    log.debug("Screen height:{}", config.screenHeight);

                    break;
                case "w":
                case "width":
                    config.screenWidth = Integer.parseInt(parts[1]);
                    log.debug("Screen width:{}", config.screenWidth);

                    break;
                case "s":
                case "scale":
                    config.screenScale = Float.parseFloat(parts[1]);
                    log.debug("screen scale:{}", config.screenScale);
                    break;
                case "d":
                case "debug":
                    config.debug = Integer.parseInt(parts[1]);
                    log.debug("debug mode:{}", config.debug);
                    break;
                case "m":
                case "mute":
                    config.mute = Boolean.parseBoolean(parts[1]);
                    log.debug("sound mute:{}", config.mute);
                    break;
                case "sv":
                case "soundVolume":
                    config.soundVolume = Float.parseFloat(parts[1]);
                    break;
                default:
                    System.out.println(String.format("Unknown arguments '%s'", arg));
                    break;
			}
		}
		return config;
	}
}