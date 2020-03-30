package samples.system;

import lombok.extern.slf4j.Slf4j;
import samples.object.SampleGameObject;

/**
 * project : DemoGame
 * <p>
 * SampleGameObject is a demonstration of a using GameObject to animate things.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com
 * @since 0.1
 */
@Slf4j
public class SampleGameSystemManager extends SampleGameObject{

    protected GameSystemManager gsm;

    public SampleGameSystemManager(){
        
    }

    public SampleGameSystemManager(String title, int w, int h, int s) {
        super(title, w, h, s);
        log.info("Sample System Manager ready...");
    }


    @Override
    public void initialize() {
        super.initialize();
        frame.addKeyListener(this);
        gsm = GameSystemManager.initialize(this);
    }

    /**
     * Entry point for our SampleGameLoop demo.
     * 
     * @param argc
     */
    public static void main(String[] argc) {
        SampleGameSystemManager sgl = new SampleGameSystemManager("Sample System Manager", 320, 240, 2);
        sgl.run();
    }

}