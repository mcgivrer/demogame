package core.resource;

/**
 * the ProgressListener interface is used to track  Resource loading progression,
 * for example, from a UI.
 */
public interface ProgressListener {
    /**
     * This methods is called from the  ResourceManager during resource loading.
     *
     * @param value the value is the ratio from 0.0f to 1.0f covering the percentage of loaded resources
     * @param path  the path for the loaded resource.
     */
    void update(float value, String path);
}
