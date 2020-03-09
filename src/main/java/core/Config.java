package core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
	public int ups;
	public String title;
	public int debug;
	public String statesPath;
	public boolean mute;
	public float soundVolume;
	public float musicVolume;

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
		this.ups = 120;
		this.debug = 0;
		this.statesPath = "/res/game.json";
		this.mute = false;
		this.soundVolume = 0.0f;
		this.musicVolume = 0.0f;
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
		config.load();

		for (String arg : argc) {
			System.out.println(String.format("arg: %s", arg));
			String[] parts = arg.split("=");
			switch (parts[0]) {
				case "f":
				case "fps":
					config.fps = Integer.parseInt(parts[1]);
					log.info("fps request:{}", config.fps);

					break;
				case "u":
				case "ups":
					config.ups = Integer.parseInt(parts[1]);
					log.info("ups request:{}", config.ups);

					break;
				case "t":
				case "title":
					config.title = parts[1];
					log.info("window title:{}", config.title);

					break;
				case "h":
				case "height":
					config.screenHeight = Integer.parseInt(parts[1]);
					log.info("Screen height:{}", config.screenHeight);

					break;
				case "w":
				case "width":
					config.screenWidth = Integer.parseInt(parts[1]);
					log.info("Screen width:{}", config.screenWidth);

					break;
				case "s":
				case "scale":
					config.screenScale = Float.parseFloat(parts[1]);
					log.info("screen scale:{}", config.screenScale);
					break;
				case "d":
				case "debug":
					config.debug = Integer.parseInt(parts[1]);
					log.info("debug mode:{}", config.debug);
					break;
				case "m":
				case "mute":
					config.mute = Boolean.parseBoolean(parts[1]);
					log.info("sound mute:{}", config.mute);
					break;
				case "sv":
				case "soundVolume":
					config.soundVolume = Float.parseFloat(parts[1]);
					log.info("sound volume:{}", config.soundVolume);
					break;
				case "mv":
				case "usicVolume":
					config.musicVolume = Float.parseFloat(parts[1]);
					log.info("music volume:{}", config.musicVolume);
					break;
				default:
					System.out.println(String.format("Unknown arguments '%s'", arg));
					break;
			}
		}
		return config;
	}

	private void load() {
		ResourceBundle cfgFromFile = ResourceBundle.getBundle("res.config");

		this.title = cfgFromFile.getString("game.win.title");
		this.screenWidth = Integer.parseInt(cfgFromFile.getString("screen.width"));
		this.screenWidth = Integer.parseInt(cfgFromFile.getString("screen.height"));
		this.screenScale = Float.parseFloat(cfgFromFile.getString("screen.scale"));
		this.debug = Integer.parseInt(cfgFromFile.getString("debug.mode"));
		this.fps = Integer.parseInt(cfgFromFile.getString("screen.fps"));
		this.ups = Integer.parseInt(cfgFromFile.getString("physic.ups"));
		this.mute = Boolean.parseBoolean(cfgFromFile.getString("audio.mute"));
		this.soundVolume = Float.parseFloat(cfgFromFile.getString("audio.volume.sound"));
		this.musicVolume = Float.parseFloat(cfgFromFile.getString("audio.volume.music"));
		this.statesPath = cfgFromFile.getString("game.states.path");
	}

}
