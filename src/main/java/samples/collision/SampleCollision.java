package samples.collision;

import samples.cli.SampleCliManager;

public class SampleCollision extends SampleCliManager{

    public SampleCollision(String title, String[] args) {
        super(title, args);
    }
    
    public static void main(String[] args) {
        SampleCollision g = new SampleCollision("Sample Collision", args);
        g.run();
    }


}