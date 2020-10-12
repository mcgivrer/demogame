package samples;

import java.util.Map;

import samples.camera.entity.Camera;
import samples.object.entity.GameObject;

/**
 * All Sample game will implements this interface, just to simplify
 * implementation between all example through inheritance.
 */
public interface Sample {
	public int getWidth();

	public int getHeight();

	public double getScale();

	public String getTitle();

	public Camera getActiveCamera();

	public Map<String, GameObject> getObjects();

	public void initialize();

	public void loop();

	public void update(double elapsed);

	public void render(long realFps);

	public boolean getPause();
}