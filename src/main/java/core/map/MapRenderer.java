package core.map;

import core.Game;
import core.object.Camera;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * A core.gfx.Renderer for the core.map.MapLevel.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
@Slf4j
public class MapRenderer {
	Color backTransparent = new Color(0.0f, 0.0f, 0.0f, 0.0f);

	/**
	 * Rendering the core.map.MapLevel according to the camera position.
	 *
	 * @param dg      the core.Game container
	 * @param g       The graphics API to be used
	 * @param map     The core.map.MapLevel to be rendered
	 * @param camera  the camera to be used as a point of view.
	 * @param elapsed time elapsed since previous call.
	 */
	public void render(Game dg, Graphics2D g, MapLevel map, Camera camera, double elapsed) {

		MapLayer frontLayer = map.layers.get("front");
		int mHeight = frontLayer.map.size();
		int mWidth = frontLayer.map.get(0).length();
		int tileWidth = frontLayer.assetsObjects.get(0).tileWidth;

		for (MapLayer mapLayer : map.layers.values()) {

			switch (mapLayer.type) {

			case LAYER_BACKGROUND_IMAGE:
				if (mapLayer.backgroundImage != null) {
					double bx = camera.x * mapLayer.backgroundImage.getWidth() / (mWidth * tileWidth);
					double by = camera.y;
					for (int x = (int) (bx - (1 * mapLayer.backgroundImage.getWidth())); x <= (bx
							+ (2 * mapLayer.backgroundImage.getWidth())); x += mapLayer.backgroundImage.getWidth()) {
						g.drawImage(mapLayer.backgroundImage, (int) x, (int) by, null);
					}
				}
				break;

			case LAYER_TILEMAP:
				for (int y = 0; y < mHeight; y++) {
					for (int x = 0; x < mWidth; x++) {
						MapObject mo = getTile(mapLayer, x, y);
						if (mo != null) {
							if (mo.frameSet.size() > 0) {
								animateMapObject(mo, elapsed);
							}
							g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
						}
						if (dg.config.debug > 4) {
							if (mo != null) {
								g.setColor(Color.GRAY);
							} else {
								g.setColor(Color.BLUE);
							}
							g.drawRect((int) x * mapLayer.assetsObjects.get(0).tileWidth,
									(int) y * mapLayer.assetsObjects.get(0).tileHeight,
									mapLayer.assetsObjects.get(0).tileWidth, mapLayer.assetsObjects.get(0).tileHeight);
						}
					}
				}
				break;
			}

		}

	}

	/**
	 * Compute next frame of animation for the MapObject.
	 * 
	 * @param mo      the MapObject to be updated.
	 * @param elapsed the elapsed time since previous call.
	 * @return the MapObject with frame and imageBuffer updated.
	 */
	private MapObject animateMapObject(MapObject mo, double elapsed) {
		mo.imageBuffer = mo.animation.animate(elapsed);
		return mo;
	}

	private MapObject getTile(MapLayer layer, int x, int y) {
		if (x >= 0 && x < layer.width && y >= 0 && y < layer.height) {
			return layer.tiles[x][y];
		}
		return null;
	}
}