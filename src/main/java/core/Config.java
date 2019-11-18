package core;

/**
 * <p>A configuration component to manage and use easily parameters.
 * This simple POJO class will support all command line parameter values
 * and there default values.</p>
 * <p>It parses the argc arguments list directly coming from Java main method.</p>
 * <p><strong>Note:</strong> Not the sexiest way to maintain thing, but surely one of the easiest.</p>
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 */
public class Config {
    public int screenWidth;
    public int screenHeight;
    public float screenScale;
    public int fps;
    public String title;
    public int debug;
    public String statesPath;

    /**
     * Initialization of default values for configuraiton.
     */
    public Config() {
        this.title = "notitle";
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.screenScale = 0f;
        this.fps = 0;
        this.debug = 0;
        this.statesPath = "/res/game.json";
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

        for (String arg : argc) {
            System.out.println(String.format("arg: %s", arg));
            String[] parts = arg.split("=");
            switch (parts[0]) {
                case "f":
                case "fps":
                    config.fps = Integer.parseInt(parts[1]);
                    break;
                case "t":
                case "title":
                    config.title = parts[1];
                    break;
                case "h":
                case "height":
                    config.screenHeight = Integer.parseInt(parts[1]);
                    break;
                case "w":
                case "width":
                    config.screenWidth = Integer.parseInt(parts[1]);
                    break;
                case "s":
                case "scale":
                    config.screenScale = Float.parseFloat(parts[1]);
                    break;
                case "d":
                case "debug":
                    config.debug = Integer.parseInt(parts[1]);
                default:
                    System.out.println(String.format("Unknown arguments '%s'", arg));
                    break;
            }
        }
        return config;
    }
}