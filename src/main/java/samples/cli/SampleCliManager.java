package samples.cli;

import lombok.extern.slf4j.Slf4j;
import samples.input.SampleInputHandler;

@Slf4j
public class SampleCliManager extends SampleInputHandler {

    CliManager clm;

    public SampleCliManager(String title, String[] args) {
        this.title = title;
        configuraCliArguments();
        parseArgs(args);
        createWindow(title, width, height, (int) scale);
    }

    private void configuraCliArguments() {
        clm = new CliManager(this);
        // Define title attribute.
        clm.add(new StringArgParser("WindowTitle", "t", "title", "BGF", "Title of the displayed game window",
                "the title must be a simple character's string"));
        // Add debug attribute
        clm.add(new IntArgParser("Debug", "d", "debug", 0, 0, 5, "Define the Debug level to on screen display.",
                "%s set to %s is wrong, default value is %d and can be between %d and %d"));
        // Add the Width attribute
        clm.add(new IntArgParser("Width", "w", "width", 320, 120, 640, "Define the Width of the game window.",
                "%s set to %s is wrong, default value is %d and can be between %d and %d"));
        // add the Height attribute
        clm.add(new IntArgParser("Height", "h", "height", 200, 80, 480, "Define the height of the game window.",
                "%s set to %s is wrong, default value is %d and can be between %d and %d"));
        // Add the scale factor.
        clm.add(new FloatArgParser("Scale", "s", "scale", 2.0f, 1, 4, "Define the factor to be apply to pixel scale.",
                "%s set to %s is wrong, default value is %d and can be between %d and %d"));
        // Add the scale factor.
        clm.add(new IntArgParser("FPS", "f", "fps", 60, 25, 30, "Define the frames per second ratio.",
                "%s set to %s is wrong, default value is %d and can be between %d and %d"));
    }

    private void parseArgs(String[] args) {
        clm.parse(args);
        try {
            debug = (Integer) (clm.getValue("Debug"));
            width = (Integer) (clm.getValue("Width"));
            height = (Integer) (clm.getValue("Height"));
            scale = (Float) (clm.getValue("Scale"));
            title = ((String) clm.getValue("WindowTitle"));
            if (clm.isExists("FPS")) {
                Integer argFps = (Integer) clm.getValue("FPS");
                FPS = argFps;
            }
        } catch (ArgumentUnknownException aue) {
            log.error("error on command line argument", aue);
        }
    }

    @Override
    public void initialize() {
        super.initialize();

    }

    public static void main(String[] args) {
        SampleCliManager g = new SampleCliManager("Sample Command Line", args);
        g.run();
    }

}