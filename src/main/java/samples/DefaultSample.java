package samples;

import java.util.HashMap;
import java.util.Map;

import samples.camera.entity.Camera;
import samples.object.entity.GameObject;

public abstract class DefaultSample implements Sample {
    protected String title;
    protected int width;
    protected int height;
    protected double scale;
    // pause flag
    protected boolean pause = false;
    // list of managed objects
    protected Map<String, GameObject> objects = new HashMap<>();

    public DefaultSample(){

    }


    public DefaultSample(String title, int width, int height, double scale) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Map<String, GameObject> getObjects() {
        return objects;
    }

    @Override
    public boolean getPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

}