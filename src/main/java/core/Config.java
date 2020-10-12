package core;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import core.cli.ArgumentUnknownException;
import core.cli.BooleanArgParser;
import core.cli.CliManager;
import core.cli.FloatArgParser;
import core.cli.IntArgParser;
import core.cli.StringArgParser;
import lombok.extern.slf4j.Slf4j;

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
	private final CliManager clm;

	/**
	 * Initialization of default values for configuraiton.
	 */
	public Config(final Game g) {
		this.title = "Kingdom of Asperia";
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
		clm = new CliManager(g);

		// Define title attribute.
		clm.add(new StringArgParser("WindowTitle", "t", "title", this.title, "Title of the displayed game window",
				"the title must be a simple character's string"));
		// Add debug attribute
		clm.add(new IntArgParser("Debug", "d", "debug", this.debug,0, 5, "Define the Debug level to on screen display.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the Width attribute
		clm.add(new IntArgParser("Width", "w", "width", this.screenWidth, 120, 640, "Define the Width of the game window.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// add the Height attribute
		clm.add(new IntArgParser("Height", "h", "height", this.screenHeight, 80, 480, "Define the height of the game window.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the scale factor.
		clm.add(new FloatArgParser("Scale", "s", "scale", this.screenScale, 1, 4, "Define the factor to be apply to pixel scale.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the frame per second.
		clm.add(new IntArgParser("FPS", "f", "fps", this.fps, 25, 60, "Define the frames per second ratio.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the update per second.
		clm.add(new IntArgParser("UPS", "u", "ups", this.ups, 25, 60, "Define the update per second ratio.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the soundVolume factor.
		clm.add(new BooleanArgParser("MuteMode", "m", "muteMode", this.mute,true, false, "set the mute mode.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the soundVolume factor.
		clm.add(new FloatArgParser("SoundVolume", "sm", "soundVolume", this.soundVolume, 0.0f, 1.0f,
				"Define the sound volume value.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the musicVolume factor.
		clm.add(new FloatArgParser("MusicVolume", "mm", "musicVolume", this.musicVolume, 0.0f, 1.0f,
				"Define the sound volume value.",
				"%s set to %s is wrong, default value is %d and can be between %d and %d"));
		// Add the states configuration path.
		clm.add(new StringArgParser("StatePath", "st", "statePath", this.statesPath, "Path where the game.json file exists",
		"the state path must be a simple path string"));
	}

	/**
	 * Parse all arguments form the main methods,and set the corresponding values.
	 * if no attribute is set, it uses a default value.
	 *
	 * @param argc list of arguments from command line.
	 * @return the Config object initialized with right values.
	 */
	public static Config analyzeArgc(final Game g, final String[] argc) {
		final Config config = new Config(g);
		config.load();
		config.clm.parse(argc);
		try {
			config.debug = (Integer) (config.clm.getValue("Debug"));
			config.screenWidth = (Integer) (config.clm.getValue("Width"));
			config.screenHeight = (Integer) (config.clm.getValue("Height"));
			config.screenScale = (Float) (config.clm.getValue("Scale"));
			config.title = ((String) config.clm.getValue("WindowTitle"));
			config.fps = (Integer) (config.clm.getValue("FPS"));
			config.ups = (Integer) (config.clm.getValue("UPS"));
			config.mute = (Boolean) (config.clm.getValue("MuteMode"));
			config.soundVolume = (Float) (config.clm.getValue("SoundVolume"));
			config.musicVolume = (Float) (config.clm.getValue("MusicVolume"));
			config.statesPath = ((String) config.clm.getValue("StatePath"));;
		} catch (ArgumentUnknownException e) {
			log.error(e.getMessage());
		}
		return config;
	}

	private void load() {
		final ResourceBundle cfgFromFile = ResourceBundle.getBundle("res.config");

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
