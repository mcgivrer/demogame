package samples;

import java.util.HashMap;
import java.util.Map;

import samples.object.GameObject;

public abstract class DefaultSample implements Sample {
    protected String title;
    protected int width;
    protected int height;
    protected double scale;
    protected Camera camera;

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

    public Camera getActiveCamera(){
        return camera;
    }
    @Override
    public Map<String, GameObject> getObjects() {
        return objects;
    }

}