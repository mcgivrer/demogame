package samples;

import lombok.extern.slf4j.Slf4j;

/**
 * project : DemoGame
 * <p>
 * SampleGameObject is a demonstration of a using GameObject to animate things.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
@Slf4j
public class SampleSystemManager extends SampleGameObject {

    public SampleSystemManager(String title, int w, int h, int s) {
        super(title, w, h, s);
        log.info("Sample System Manager ready...");
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleSystemManager sgl = new SampleSystemManager("Sample System Manager", 320, 240, 2);
        sgl.run();
    }

}