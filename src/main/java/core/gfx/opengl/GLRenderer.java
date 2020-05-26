package core.gfx.opengl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import core.Config;
import core.Game;
import core.gfx.Counter;
import core.gfx.IRenderer;
import core.map.MapObject;
import core.object.GameObject;
import core.system.AbstractSystem;

public class GLRenderer extends AbstractSystem implements IRenderer {

	protected GLRenderer(Game game) {
		super(game);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Game dg, double elapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRealFPS(Counter realFPS) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRealUPS(Counter realUPS) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(GameObject go) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(Collection<GameObject> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveScreenshot(Config config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPause(boolean pause) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "renderer";
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(BufferedImage holder, int i, int j, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderMapObject(MapObject item, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(BufferedImage lifeImg, int offsetX, int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOutLinedText(Graphics2D g, String format, int i, int j, Color white, Color black, Font infoFont) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(GameObject go) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(List<GameObject> objectsToBeRemoved) {
		// TODO Auto-generated method stub

	}

	@Override
	public BufferedImage getScreenBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
    
}