package core.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import core.Config;
import core.Game;
import core.map.MapObject;
import core.object.GameObject;
import core.system.System;

public interface IRenderer extends System{

	/**
	 * Render all objects !
	 */
	void render(Game dg, double elapsed);

	void setRealFPS(Counter realFPS);

	void setRealUPS(Counter realUPS);

	void add(GameObject go);

	/**
	 * Clear all rendering pipeline and objects.
	 */
	public void clear();

	/**
	 * Add all object from collection to the rendering pipeline.
	 * 
	 * @param objects
	 */
	void addAll(Collection<GameObject> objects);

	/**
	 * Save a screenshot of the current buffer.
	 */
	void saveScreenshot(Config config);

	/**
	 * Set Rendering process on pause mode if true.
	 * 
	 * @param pause
	 */
	void setPause(boolean pause);

	void drawImage(BufferedImage holder, int x, int y, int width, int height);

	void renderMapObject(MapObject item, float x, float y);

	void drawImage(BufferedImage lifeImg, int offsetX, int i);

	void drawOutLinedText(Graphics2D g, String format, int x, int y, Color white, Color black, Font infoFont);

	void remove(GameObject go);

	void removeAll(List<GameObject> objectsToBeRemoved);

	BufferedImage getScreenBuffer();

}