package core;

/**
 * A configuration component to manage and use easily parameters.
 */
public class Config {
    public int screenWidth;
    public int screenHeight;
    public float screenScale;
    public int fps;
    public String title;
    public int debug;

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
    }

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