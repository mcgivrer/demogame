package core.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.collision.MapTileCollision;
import core.object.GameObject;

/**
 * A Debug object to draw debug information on a specific GameObject.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @since 2019
 */
public class DebugInfo {
	public static Font debugFont;

	/**
	 * display information to an information panel on right of GameObject
	 *
	 * @param g
	 * @param go
	 */
	public static void display(Graphics2D g, GameObject go) {
		g.setFont(debugFont);
		FontMetrics fm = g.getFontMetrics(debugFont);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		int maxWidth = 60;
		int maxLinePerColumn = 4;
		int fontHeight = fm.getHeight();
		double offsetX = go.x + go.width + 2;
		double offsetY = go.y;

		List<String> debugInfo = prepareDebugInfo(go);
		int width = (debugInfo.size() % maxLinePerColumn) * (maxWidth);
		int height = (debugInfo.size() - 1) * (fontHeight - 3);

		drawBackgroundPanel(g, offsetX, offsetY - fontHeight, width, height, Color.DARK_GRAY,
				new Color(0.6f, 0.6f, 0.6f, 0.9f));

		drawAttributesText(g, debugInfo, offsetX, offsetY, maxWidth, maxLinePerColumn, fontHeight, Color.WHITE);

		// draw object size
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, (int) go.width, (int) go.height);

	}

	private static List<String> prepareDebugInfo(GameObject go) {
		List<String> debugInfo = new ArrayList<>();
		debugInfo.add(String.format("name:%s", go.name));
		debugInfo.add(String.format("pos:(%03.1f,%03.1f)", go.x, go.y));
		debugInfo.add(String.format("vel:(%03.1f,%03.1f)", go.dx, go.dy));
		debugInfo.add(String.format("debug:%d", go.debugLevel));
		debugInfo.add(String.format("action:%s", go.action.toString()));

		for (Map.Entry<String, Object> e : go.attributes.entrySet()) {
			String debugInfoLine = String.format("%s:%s", e.getKey(), e.getValue().toString());
			debugInfo.add(debugInfoLine);
		}
		return debugInfo;
	}

	private static void drawAttributesText(Graphics2D g, List<String> debugInfo, double offsetX, double offsetY,
			int maxWidth, int maxLinePerColumn, int fontHeight, Color textColor) {
		g.setColor(textColor);
		int x = 0, y = 0;
		for (String line : debugInfo) {
			g.drawString(String.format("%s", line), (int) (x + offsetX), (int) ((y * (fontHeight + 2)) + offsetY + 4));
			y += 1;
			if (y > maxLinePerColumn) {
				y = 0;
				x += maxWidth + 2;
			}
		}
	}

	private static void drawBackgroundPanel(Graphics2D g, double offsetX, double offsetY, int width, int height,
			Color borderColor, Color backgroundColor) {
		g.setColor(backgroundColor);
		g.fillRect((int) offsetX - 4, (int) (offsetY), width, height);
		g.setColor(borderColor);
		g.drawRect((int) offsetX - 4, (int) (offsetY), width, height);
	}

	public static void displayCollisionTest(Graphics2D g, GameObject go) {
		int ox = (int) (go.bbox.x / 16);
		int ow = (int) (go.bbox.width / 16);
		int oy = (int) (go.bbox.y / 16);
		int oh = (int) (go.bbox.height / 16);
		// draw GameObject in the Map Tiles coordinates
		g.setColor(Color.ORANGE);
		g.drawRect(ox * 16, oy * 16, ow * 16, oh * 16);
		// draw the bounding box
		g.setColor(Color.RED);
		g.drawRect((int) (go.bbox.x + go.bbox.left), (int) (go.bbox.y + go.bbox.top),
				(int) (go.bbox.width - go.bbox.left - go.bbox.right),
				(int) (go.bbox.height - go.bbox.top - go.bbox.bottom));
		// draw the tested Tiles to detect Fall action.
		g.setColor(Color.BLUE);
		if (!go.collidingZone.isEmpty()) {
			for (MapTileCollision mo : go.collidingZone) {
				if (mo.mo != null) {
					Font d = g.getFont();
					g.setFont(d.deriveFont(9.5f));
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					g.setColor(Color.WHITE);
					g.drawString(mo.mo.type, mo.rX + 2, mo.rY + (mo.h / 2) + 4);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setFont(d);
					switch (mo.mo.type) {
					case "tile":
						g.setColor(Color.ORANGE);
						break;
					case "object":
						g.setColor(Color.YELLOW);
						break;
					default:
						g.setColor(Color.GREEN);
						break;
					}
				} else {
					g.setColor(Color.BLUE);
				}
				g.drawRect(mo.rX, mo.rY, mo.w, mo.h);
			}
		}
	}

}
