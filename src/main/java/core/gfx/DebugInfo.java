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
	public static final Color backgroundColor = new Color(0.3f, 0.3f, 0.3f,0.45f);
	public static final Color borderColor = Color.BLACK;
	public static double scale = 1.0;

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
		int maxWidth = 70;
		int maxLinePerColumn = 5;
		int fontHeight = fm.getHeight();
		double offsetX = (go.pos.x + go.size.x + 2)*DebugInfo.scale;
		double offsetY = (go.pos.y)*DebugInfo.scale;

		List<String> debugInfo = prepareDebugInfo(go);
		int width = (debugInfo.size() % maxLinePerColumn) * (maxWidth);
		int height = (debugInfo.size() - 1) * (fontHeight - 3);

		drawBackgroundPanel(g, offsetX, offsetY - fontHeight, width, height, borderColor,
				backgroundColor);

		drawAttributesText(g, debugInfo, offsetX, offsetY, maxWidth, maxLinePerColumn, fontHeight, Color.WHITE);

		// draw object size
		g.setColor(Color.BLUE);
		g.drawRect(
			(int) (go.pos.x*DebugInfo.scale), (int) (go.pos.y*DebugInfo.scale), 
			(int) (go.size.x*DebugInfo.scale), (int) (go.size.y*DebugInfo.scale));
		// draw bounding box
		g.setColor(Color.RED);
		g.drawRect(
			(int) (go.bbox.pos.x*DebugInfo.scale), (int) (go.bbox.pos.y*DebugInfo.scale), 
			(int) (go.bbox.size.x*DebugInfo.scale), (int) (go.bbox.size.y*DebugInfo.scale));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private static List<String> prepareDebugInfo(GameObject go) {
		List<String> debugInfo = new ArrayList<>();
		debugInfo.add(String.format("name:%s", go.name));
		debugInfo.add(String.format("acc:(%03.0f,%03.0f)", go.acc.x, go.acc.y));
		debugInfo.add(String.format("vel:(%03.0f,%03.0f)", go.vel.x, go.vel.y));
		debugInfo.add(String.format("pos:(%03.0f,%03.0f)", go.pos.x, go.pos.y));
		debugInfo.add(String.format("npo:(%03.0f,%03.0f)", go.newPos.x, go.newPos.y));
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
			g.drawString(String.format("%s", line), 
				(int) ((x + offsetX)*DebugInfo.scale), 
				(int) (((y * (fontHeight)) + offsetY + 4)*DebugInfo.scale));
			y += 1;
			if (y > maxLinePerColumn) {
				y = 0;
				x += maxWidth + 2;
			}
		}
	}

	private static void drawBackgroundPanel(Graphics2D g, double offsetX, double offsetY, int width, int height,
			Color borderColor, Color backgroundColor) {
		int dx = (int) ((offsetX - 4)*DebugInfo.scale);
		int dy =(int) (offsetY*DebugInfo.scale);
		g.setColor(backgroundColor);
		g.fillRect(dx, dy , width, height);
		g.setColor(borderColor);
		g.drawRect(dx, dy, width, height);
	}

	public static void displayCollisionTest(Graphics2D g, GameObject go) {
		int ox = (int) (go.bbox.pos.x*DebugInfo.scale / 16);
		int ow = (int) (go.bbox.size.x*DebugInfo.scale / 16);
		int oy = (int) (go.bbox.pos.y*DebugInfo.scale / 16);
		int oh = (int) (go.bbox.size.y*DebugInfo.scale / 16);
		// draw GameObject in the Map Tiles coordinates
		g.setColor(Color.ORANGE);
		g.drawRect(ox * 16, oy * 16, ow * 16, oh * 16);
		// draw the bounding box
		g.setColor(Color.RED);
		g.drawRect(
				(int) ((go.bbox.pos.x + go.bbox.left)*DebugInfo.scale), 
				(int) ((go.bbox.pos.y + go.bbox.top)*DebugInfo.scale),
				(int) ((go.bbox.size.x - go.bbox.left - go.bbox.right)*DebugInfo.scale),
				(int) ((go.bbox.size.y - go.bbox.top - go.bbox.bottom)*DebugInfo.scale));
		// draw the tested Tiles to detect Fall action.
		g.setColor(Color.BLUE);
		if (!go.collidingZone.isEmpty()) {
			for (MapTileCollision mtc : go.collidingZone) {
				if (mtc.mo != null) {
					Font d = g.getFont();
					g.setFont(d.deriveFont(9.5f));
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					g.setColor(Color.WHITE);
					g.drawString(
						mtc.mo.type.toString(), 
						(int)((mtc.rX + 2)*DebugInfo.scale), 
						(int)((((mtc.rY + (mtc.h / 2) + 4))*DebugInfo.scale)));
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setFont(d);
					switch (mtc.mo.type) {
						case TILE:
							g.setColor(Color.ORANGE);
							break;
						case OBJECT:
							g.setColor(Color.YELLOW);
							break;
						default:
							g.setColor(Color.GREEN);
							break;
					}
				} else {
					g.setColor(Color.BLUE);
				}
				g.drawRect(
					(int)(mtc.rX*DebugInfo.scale), (int)(mtc.rY*DebugInfo.scale), 
					(int)(mtc.w*DebugInfo.scale), (int)(mtc.h*DebugInfo.scale));
			}
		}
	}

}
