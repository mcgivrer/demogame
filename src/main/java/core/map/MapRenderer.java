package core.map;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;
import core.object.Camera;

/**
 * A core.gfx.Renderer for the core.map.MapLevel.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2019
 */
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
				drawBackgroundLayer(g, camera, mWidth, tileWidth, mapLayer);
				break;

			case LAYER_TILEMAP:
				drawTilemapLayer(dg, g, elapsed, camera, mHeight, mWidth, mapLayer);
				break;
			}

		}

	}

	/**
	 * @param dg
	 * @param g
	 * @param elapsed
	 * @param mHeight
	 * @param mWidth
	 * @param mapLayer
	 */
	private void drawTilemapLayer(Game dg, Graphics2D g, double elapsed, Camera camera, int mHeight, int mWidth,
			MapLayer mapLayer) {

		int cx = (int) (camera.pos.x / mapLayer.assetsObjects.get(0).tileWidth);
		int cy = (int) (camera.pos.y / mapLayer.assetsObjects.get(0).tileHeight);
		int offCx = (int) (camera.viewport.width / (mapLayer.assetsObjects.get(0).tileWidth)) + 2;
		int offCy = (int) (camera.viewport.height / (mapLayer.assetsObjects.get(0).tileHeight)) + 2;

		int top = (cy < 0 ? 0 : cy);
		int bottom = (cy + offCy > mHeight ? mHeight : cy + offCy);
		int left = (cx < 0 ? 0 : cx);
		int right = (cx + offCx > mHeight ? mWidth : cx + offCx);

		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {
				MapObject mo = getTile(mapLayer, x, y);
				if (mo != null) {
					if (mo.frameSet.size() > 0) {
						animateMapObject(mo, elapsed);
					}
					g.drawImage(mo.imageBuffer, x * mo.width, y * mo.height, null);
				}

			}
		}
	}
	
	public void drawDebuginfo(Game dg, Graphics2D g, double elapsed, Camera camera, int mHeight, int mWidth,
	MapLayer mapLayer, double scale){
		int cx = (int) ((camera.pos.x / mapLayer.assetsObjects.get(0).tileWidth)*scale);
		int cy = (int) ((camera.pos.y / mapLayer.assetsObjects.get(0).tileHeight)*scale);
		int offCx = (int) (((camera.viewport.width / (mapLayer.assetsObjects.get(0).tileWidth)) + 2)*scale);
		int offCy = (int) (((camera.viewport.height / (mapLayer.assetsObjects.get(0).tileHeight)) + 2)*scale);

		int top = (cy < 0 ? 0 : cy);
		int bottom = (cy + offCy > mHeight ? mHeight : cy + offCy);
		int left = (cx < 0 ? 0 : cx);
		int right = (cx + offCx > mHeight ? mWidth : cx + offCx);
		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {
				MapObject mo = getTile(mapLayer, x, y);
				if (dg.config.debug > 4) {
					if (mo != null) {
						g.setColor(Color.GRAY);
					} else {
						g.setColor(Color.BLUE);
					}
					g.drawRect(
						(int)(x * mapLayer.assetsObjects.get(0).tileWidth*scale),
						(int)(y * mapLayer.assetsObjects.get(0).tileHeight*scale),
						(int)(mapLayer.assetsObjects.get(0).tileWidth*scale),
						(int)(mapLayer.assetsObjects.get(0).tileHeight*scale));
				}
			}
		}
	}

	/**
	 * @param g
	 * @param camera
	 * @param mWidth
	 * @param tileWidth
	 * @param mapLayer
	 */
	private void drawBackgroundLayer(Graphics2D g, Camera camera, int mWidth, int tileWidth, MapLayer mapLayer) {
		if (mapLayer.backgroundImage != null) {
			double bx = camera.pos.x * mapLayer.backgroundImage.getWidth() / (mWidth * tileWidth);
			double by = camera.pos.y;
			for (int x = (int) (bx - (1 * mapLayer.backgroundImage.getWidth())); x <= (bx
					+ (2 * mapLayer.backgroundImage.getWidth())); x += mapLayer.backgroundImage.getWidth()) {
				g.drawImage(mapLayer.backgroundImage, (int) x, (int) by, null);
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