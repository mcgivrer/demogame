package core.gfx.soft;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import core.Config;
import core.Game;
import core.gfx.Counter;
import core.gfx.DebugInfo;
import core.gfx.IRenderer;
import core.io.InputHandler;
import core.map.MapLevel;
import core.map.MapObject;
import core.map.MapRenderer;
import core.object.Camera;
import core.object.GameObject;
import core.object.Light;
import core.object.TextObject;
import core.resource.ResourceManager;
import core.system.AbstractSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * This Renderer class is the main rendering component for all objects managed
 * by its parent core.Game instance.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
@Slf4j
public class Renderer extends AbstractSystem implements IRenderer {

	private static int screenShotIndex = 0;
	public BufferedImage screenBuffer;
	private JFrame jf;

	private Map<Integer, Layer> layers = new HashMap<>();
	private List<GameObject> renderingObjectPipeline = new ArrayList<>();
	private MapRenderer mapRenderer;
	private boolean renderingPause = false;

	private Counter realFPS;
	private Counter realUPS;

	private Graphics2D g;

	/**
	 * Create the Game renderer.
	 *
	 * @param dg the core.Game instance parent for this core.gfx.Renderer.
	 */
	public Renderer(Game dg) {
		super(dg);
		jf = createWindow(dg);
		screenBuffer = new BufferedImage(dg.config.screenWidth, dg.config.screenHeight, BufferedImage.TYPE_INT_ARGB);
		g = screenBuffer.createGraphics();
	}

	@Override
	public int initialize(Game game) {
		mapRenderer = new MapRenderer();
		return 0;
	}

	/**
	 * create a WXindow to host the game display according to core.Config object.
	 *
	 * @param dg the core.Game object to access the configuration instance.
	 * @return a JFrame initialized conforming to config attributes.
	 */
	private JFrame createWindow(Game dg) {
		// log.info("Java Library Path: {}", System.getProperty("java.library.path"));
		log.info(getMonitorSizes());

		jf = new JFrame(dg.config.title);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setBackground(Color.BLACK);
		Insets ins = jf.getInsets();
		Dimension dim = new Dimension((int) (dg.config.screenWidth * dg.config.screenScale) - (ins.left + ins.right),
				(int) (dg.config.screenHeight * dg.config.screenScale) - (ins.top + ins.bottom));
		jf.setSize(dim);
		jf.setPreferredSize(dim);
		jf.pack();
		InputHandler kih = dg.sysMan.getSystem(InputHandler.class.getSimpleName());
		jf.addKeyListener(kih);
		jf.setIconImage(ResourceManager.getImage("/res/bgf-icon.png"));
		jf.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				float ratio = (float) dg.config.screenWidth / (float) dg.config.screenHeight;
				float w = componentEvent.getComponent().getWidth();
				Dimension dim = new Dimension((int) w, (int) (w / ratio));
				jf.setSize(dim);
				jf.setMaximumSize(dim);
				jf.setMinimumSize(dim);
				jf.setPreferredSize(dim);
				jf.pack();
			}
		});

		jf.setIgnoreRepaint(true);
		jf.enableInputMethods(true);
		jf.setLocationByPlatform(true);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		BufferStrategy bs = jf.getBufferStrategy();
		if (bs == null) {
			jf.createBufferStrategy(4);
		}
		return jf;
	}

	private String getMonitorSizes() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			sb.append(i + ", width: " + dm.getWidth() + ", height: " + dm.getHeight() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Render all objects !
	 */
	@Override
	public void render(Game dg, double elapsed) {
		if (!renderingPause) {
			DebugInfo.debugFont = g.getFont().deriveFont(8.0f);

			Camera camera = dg.sceneManager.getCurrent().getActiveCamera();

			// activate Anti-aliasing for image and text rendering.
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			// clear image
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dg.config.screenWidth, dg.config.screenHeight);
			for (Layer layer : layers.values()) {
				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(-camera.pos.x, -camera.pos.y);
				}
				renderObjects(dg, elapsed, g, camera, layer);
				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(camera.pos.x, camera.pos.y);
				}
			}

			// draw HUD
			dg.sceneManager.getCurrent().drawHUD(dg, this, g);
			// render image to real screen (applying scale factor)
			renderToScreen(dg, realFPS, realUPS);
		}
	}

	private void renderObjects(Game dg, double elapsed, Graphics2D g, Camera camera, Layer layer) {
		// draw all objects
		for (GameObject go : layer.objects) {
			if (go.enable && go.displayed) {
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (go instanceof MapLevel) {

					// if MapLevel, delegates rendering operation to the MapRenderer.

					mapRenderer.render(dg, g, (MapLevel) go, camera, elapsed);

				} else if (go instanceof TextObject) {
					TextObject to = (TextObject) go;
					drawText(g, to);

				} else if (go instanceof Light) {
					Light l = (Light) go;
					drawLight(dg, g, l);

				} else if (go instanceof GameObject) {
					drawObject(dg, g, go);
				}

				// if debug mode activated, draw debug info
				if (dg.config.debug > 2) {
					DebugInfo.displayCollisionTest(g, go);
					DebugInfo.display(g, go);
				}
			}
		}
	}

	/**
	 * @param g
	 * @param to
	 */
	private void drawText(Graphics2D g, TextObject to) {
		if (to.font != null && to.text != null) {
			Font b = g.getFont();
			g.setFont(to.font);
			FontMetrics fm = g.getFontMetrics(to.font);
			to.size.x = fm.stringWidth(to.text);
			to.size.y = fm.getHeight();

			double ox = to.pos.x;
			double oy = to.pos.y;
			switch (to.align) {
				case CENTER:
					ox = to.pos.x - (to.size.x / 2);
					break;
				case RIGHT:
					ox = to.pos.x - to.size.x;
					break;
				case LEFT:
				default:
					ox = to.pos.x;
					break;
			}
			int boxPadding = 4;

			// draw Background rectangle.
			drawBackgroundBox(g, to, fm, ox, oy, boxPadding);

			if (to.shadowColor != null) {
				g.setColor(to.shadowColor);
				g.drawString(to.text, (int) ox + 1, (int) oy + 1);
				g.drawString(to.text, (int) ox + 2, (int) oy + 2);
			}

			if (to.outlinedColor != null) {
				g.setColor(to.outlinedColor);
				g.drawString(to.text, (int) ox + 1, (int) oy);
				g.drawString(to.text, (int) ox, (int) oy + 1);
				g.drawString(to.text, (int) ox - 1, (int) oy);
				g.drawString(to.text, (int) ox, (int) oy - 1);
			}

			g.setColor(to.foregroundColor);
			g.drawString(to.text, (int) ox, (int) oy);
			g.setFont(b);
		}
	}

	/**
	 * @param g
	 * @param to
	 * @param fm
	 * @param ox
	 * @param oy
	 * @param boxPadding
	 */
	public void drawBackgroundBox(Graphics2D g, TextObject to, FontMetrics fm, double ox, double oy, int boxPadding) {
		if (to.backgroundColor != null) {
			g.setColor(to.shadowColor);
			g.fillRect((int) (ox), (int) (oy - (fm.getMaxAscent())), (int) (to.size.x + boxPadding * 2),
					(int) (to.size.y + boxPadding * 2));
			g.setColor(to.backgroundColor);
			g.fillRect((int) (ox - boxPadding), (int) (oy - boxPadding - (fm.getMaxAscent())),
					(int) (to.size.x + boxPadding * 2), (int) (to.size.y + boxPadding * 2));
		}
		if (to.borderColor != null) {
			g.setColor(to.borderColor);
			g.drawRect((int) (ox - boxPadding), (int) (oy - boxPadding - (fm.getMaxAscent())),
					(int) (to.size.x + boxPadding * 2), (int) (to.size.y + boxPadding * 2));

			g.setColor(to.shadowColor);
			g.drawRect((int) (ox - boxPadding + 1), (int) (oy - boxPadding - (fm.getMaxAscent()) + 1),
					(int) (to.size.x - 2 + boxPadding * 2), (int) (to.size.y - 2 + boxPadding * 2));

			g.setColor(Color.BLACK);
			g.drawRect((int) (ox - (boxPadding + 1)), (int) (oy - (boxPadding + 1) - (fm.getMaxAscent())),
					(int) (to.size.x + (boxPadding * 2) + 2), (int) (to.size.y + (boxPadding * 2) + 2));
		}
	}

	/**
	 * rendering of a Light object.
	 *
	 * @param dg the core.Game containing the object.
	 * @param g  the graphics API.
	 * @param l  the Light to be rendered.
	 */
	private void drawLight(Game dg, Graphics2D g, Light l) {
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) l.intensity));
		switch (l.lightType) {
			case LIGHT_SPHERE:
				drawLightSphere(g, l);
				break;
			case LIGHT_AMBIANT:
				drawLightAmbient(dg, g, l);
				break;
			case LIGHT_CONE:
				drawLightCone(dg,g,l);
				break;
			default:
				break;
		}

		g.setComposite(c);
	}

	private void drawLightSphere(Graphics2D g, Light l) {
		l.foregroundColor = brighten(l.foregroundColor, l.intensity);
		l.colors = new Color[] { 
				l.foregroundColor,
				new Color(	
						l.foregroundColor.getRed() / 2, 
						l.foregroundColor.getGreen() / 2,
						l.foregroundColor.getBlue() / 2, 
						l.foregroundColor.getAlpha() / 2),
				new Color(0.0f, 0.0f, 0.0f, 0.0f) };
		l.rgp = new RadialGradientPaint(
				new Point(
						(int) (l.pos.x + (20 * Math.random() * l.glitterEffect)),
						(int) (l.pos.y + (20 * Math.random() * l.glitterEffect))),
						(int) (l.size.x * 2), 
						l.dist, 
						l.colors);
		g.setPaint(l.rgp);
		g.fill(new Ellipse2D.Double(l.pos.x, l.pos.y, l.size.x, l.size.y));
	}

	private void drawLightAmbient(Game dg, Graphics2D g, Light l) {
		final Area ambientArea = new Area(
			new Rectangle2D.Double(
				dg.sceneManager.getCurrent().getActiveCamera().pos.x,
				dg.sceneManager.getCurrent().getActiveCamera().pos.y, 
				dg.config.screenWidth, 
				dg.config.screenHeight));
		g.setColor(l.foregroundColor);
		g.fill(ambientArea);

	}

	/**
	 * TODO implements the Conical Light drawing.
	 */
	private void drawLightCone(Game dg, Graphics2D g, Light l){
		log.debug("draw Light cone {}",l.name);
	}

	/**
	 * Rendering of the object (will be delegated to another component in a next
	 * version.
	 *
	 * @param dg the core.Game containing the object.
	 * @param g  the graphics API.
	 * @param go the GameObject to be rendered.
	 */
	private void drawObject(Game dg, Graphics2D g, GameObject go) {
		switch (go.type) {
			case RECTANGLE:
				g.setColor(go.foregroundColor);
				g.fillRect((int) go.pos.x, (int) go.pos.y, (int) go.size.x, (int) go.size.y);
				break;
			case CIRCLE:
				g.setColor(go.foregroundColor);
				g.fillOval((int) go.pos.x, (int) go.pos.y, (int) go.size.x, (int) go.size.y);
				break;
			case IMAGE:
				if (go.direction < 0) {
					g.drawImage(go.image, (int) (go.pos.x + go.size.x), (int) go.pos.y, (int) (-go.size.x),
							(int) go.size.y, null);
				} else {
					g.drawImage(go.image, (int) go.pos.x, (int) go.pos.y, (int) go.size.x, (int) go.size.y, null);
				}
				break;
		}
	}

	@Override
	public void setRealFPS(Counter realFPS) {
		this.realFPS = realFPS;
	}

	@Override
	public void setRealUPS(Counter realUPS) {
		this.realUPS = realUPS;
	}

	public class Layer {
		int index;
		boolean fixed;
		List<GameObject> objects = new ArrayList<>();
	}

	private void renderToScreen(Game dg, Counter realFPS, Counter realUPS) {
		BufferStrategy bs = jf.getBufferStrategy();
		Camera camera = dg.sceneManager.getCurrent().getActiveCamera();
		if (bs != null) {
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			float sX = jf.getWidth() / dg.config.screenWidth;
			float sY = jf.getHeight() / dg.config.screenHeight;

			if (g != null) {
				g.drawImage(screenBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, dg.config.screenWidth,
						dg.config.screenHeight, Color.BLACK, null);

				if (dg.config.debug > 0) {
					g.setColor(Color.ORANGE);
					g.drawString(
							String.format("debug:%01d | FPS: %03f | UPS: %03f | cam:(%03.1f,%03.1f)", dg.config.debug,
									realFPS.getCounter(), realUPS.getCounter(), camera.pos.x, camera.pos.y),
							4, jf.getHeight() - 20);

					if (dg.config.debug > 2) {
						g.setColor(Color.ORANGE);
						g.drawString("cam:" + camera.name, (int) (20 + sX), (int) (20 * sY));
						g.drawRect((int) ((10) * sX), (int) ((10) * sY), (int) ((dg.config.screenWidth - 20) * sX),
								(int) ((dg.config.screenHeight - 20) * sY));
					}
				}
			}
			bs.show();

		}
	}

	/**
	 * draw an outline text at (x,y) with textColor and a borderColor.
	 *
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param textColor
	 * @param borderColor
	 */
	public void drawOutLinedText(Graphics2D g, String text, int x, int y, Color textColor, Color borderColor) {
		g.setColor(borderColor);
		g.drawString(text, x - 1, y);
		g.drawString(text, x, y - 1);
		g.drawString(text, x + 1, y);
		g.drawString(text, x, y + 1);

		g.setColor(textColor);
		g.drawString(text, x, y);
	}

	/**
	 * draw an outline text at (x,y) with textColor and a borderColor.
	 *
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param textColor
	 * @param borderColor
	 * @param font
	 */
	public void drawOutLinedText(Graphics2D g, String text, int x, int y, Color textColor, Color borderColor,
			Font font) {
		g.setFont(font);
		drawOutLinedText(g, text, x, y, textColor, borderColor);
	}

	@Override
	public void add(GameObject go) {
		disptachToLayer(go);
		if (!go.child.isEmpty()) {
			putAll(go.child);
		}
	}

	/**
	 * Dispatch the GameObject to the right layer and sort its rendering pipeline.
	 * 
	 * @param go
	 */
	private void disptachToLayer(GameObject go) {
		Layer l;
		if (layers.get(go.layer) == null) {
			l = new Layer();
			l.index = go.layer;
			if (go.fixed) {
				l.fixed = true;
			}
			;
			layers.put(go.layer, l);
		}
		l = layers.get(go.layer);
		l.objects.add(go);
		Collections.sort(l.objects, new Comparator<GameObject>() {
			public int compare(GameObject g1, GameObject g2) {
				return g1.layer < g2.layer ? -1 : (g1.priority < g2.priority ? -1 : 1);
			}
		});
	}

	/**
	 * Add all object from collection to the rendering pipeline.
	 * 
	 * @param objects
	 */
	@Override
	public void addAll(Collection<GameObject> objects) {
		for (GameObject go : objects) {
			add(go);
		}
	}

	/**
	 * Put all object from collection to the rendering pipeline.
	 * 
	 * @param objects
	 */
	public void putAll(Map<String, GameObject> objects) {
		for (GameObject go : objects.values()) {
			add(go);
		}
	}

	public void remove(GameObject go) {
		renderingObjectPipeline.remove(go);
	}

	public void removeAll(List<GameObject> toBeRemoved) {
		renderingObjectPipeline.removeAll(toBeRemoved);
	}

	/**
	 * Save a screenshot of the current buffer.
	 */
	@Override
	public void saveScreenshot(Config config) {
		final String path = this.getClass().getResource("/").getFile();
		Path targetDir = Paths.get(path + File.separator);
		String filename = path + File.separator + config.title + "-screenshot-" + java.lang.System.nanoTime() + "-"
				+ (screenShotIndex++) + ".png";

		try {
			if (!Files.exists(targetDir)) {
				Files.createDirectory(targetDir);
			}
			File out = new File(filename);
			renderingPause = true;
			ImageIO.write(screenBuffer, "PNG", out);
			renderingPause = false;
		} catch (IOException e) {
			log.error("Unable to write screenshot to {}:{}", filename, e.getMessage());
		}
	}

	public void renderMapObject(MapObject mo, float x, float y) {
		g.drawImage(mo.imageBuffer, (int) x, (int) y, null);
	}

	@Override
	public String getName() {
		return "renderer";
	}

	@Override
	public void dispose() {

	}

	/**
	 * Make a color brighten.
	 *
	 * @param color    Color to make brighten.
	 * @param fraction Darkness fraction.
	 * @return Lighter color.
	 */
	public Color brighten(Color color, double fraction) {

		int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
		int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
		int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);

	}

	/**
	 * Set Rendering process on pause mode if true.
	 * 
	 * @param pause
	 */
	@Override
	public void setPause(boolean pause) {
		this.renderingPause = pause;

	}

	/**
	 * Clear all rendering pipeline and objects.
	 */
	public void clear() {
		layers.clear();
		renderingObjectPipeline.clear();
	}

	public void drawImage(BufferedImage image, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}

	public void drawImage(BufferedImage image, int x, int y) {
		g.drawImage(image, x, y, null);

	}

	@Override
	public BufferedImage getScreenBuffer() {
		return screenBuffer;
	}

}
