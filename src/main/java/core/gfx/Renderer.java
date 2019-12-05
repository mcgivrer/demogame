package core.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import core.ResourceManager;
import core.io.InputHandler;
import core.map.MapLevel;
import core.map.MapObject;
import core.map.MapRenderer;
import core.object.Camera;
import core.object.GameObject;
import core.object.Light;
import core.system.AbstractSystem;
import core.system.System;
import lombok.extern.slf4j.Slf4j;

/**
 * This Renderer class is the main rendering component for all objects managed
 * by its parent core.Game instance.
 *
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @year 2019
 */
@Slf4j
public class Renderer extends AbstractSystem implements System {

	public class Layer {
		int index;
		boolean fixed;
		List<GameObject> objects = new ArrayList<>();
	}

	private static int screenShotIndex = 0;
	public BufferedImage screenBuffer;
	public BufferedImage lightBuffer;
	private JFrame jf;

	private Map<Integer, Layer> layers = new HashMap<>();
	private List<GameObject> renderingObjectPipeline = new ArrayList<>();

	private List<Light> lights = new ArrayList<>();

	private MapRenderer mapRenderer = new MapRenderer();
	private boolean renderingPause = false;

	/**
	 * Create the Game renderer.
	 *
	 * @param dg the core.Game instance parent for this core.gfx.Renderer.
	 */
	public Renderer(Game dg) {
		super(dg);
		jf = createWindow(dg);
		screenBuffer = new BufferedImage(dg.config.screenWidth, dg.config.screenHeight, BufferedImage.TYPE_INT_ARGB);
		lightBuffer = new BufferedImage(dg.config.screenWidth, dg.config.screenHeight, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * create a WXindow to host the game display according to core.Config object.
	 *
	 * @param dg the core.Game object to access the configuration instance.
	 * @return a JFrame initialized conforming to config attributes.
	 */
	public JFrame createWindow(Game dg) {
		jf = new JFrame(dg.config.title);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setBackground(Color.BLACK);
		Insets ins = jf.getInsets();
		Dimension dim = new Dimension((int) (dg.config.screenWidth * dg.config.screenScale) - (ins.left + ins.right),
				(int) (dg.config.screenHeight * dg.config.screenScale) - (ins.top + ins.bottom));
		jf.setSize(dim);
		jf.setPreferredSize(dim);
		jf.pack();
		InputHandler kih = dg.sysMan.getSystem(InputHandler.class);
		jf.addKeyListener(kih);
		jf.setIconImage(ResourceManager.getImage("/res/bgf-icon.png"));
		jf.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				float ratio = (float) dg.config.screenWidth / (float) dg.config.screenHeight;
				float w = componentEvent.getComponent().getWidth();
				Dimension d = new Dimension((int) w, (int) (w / ratio));
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
		return jf;
	}

	/**
	 * Render all objects !
	 */
	public void render(Game dg, double elapsed) {
		if (!renderingPause) {
			Graphics2D g = screenBuffer.createGraphics();

			Camera camera = dg.stateManager.getCurrent().getActiveCamera();

			// activate Antialiasing for image and text rendering.

			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			// clear image
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dg.config.screenWidth, dg.config.screenHeight);

			for (Layer layer : layers.values()) {
				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(-camera.x, -camera.y);
				}

				// draw all objects
				for (GameObject go : layer.objects) {
					if (go.enable) {
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						if (go instanceof MapLevel) {

							// if MapLevel, delegates rendering operation to the MapRenderer.
							if (dg.config.debug > 2) {
								g.setColor(Color.BLUE);
								g.fillRect(0, 0, (int) go.width, (int) go.height);
							}
							
							mapRenderer.render(dg, g, (MapLevel) go, camera, elapsed);

						} else if (go instanceof GameObject) {

							if (dg.config.debug > 2) {
								DebugInfo.displayCollisionTest(g, go);
								DebugInfo.display(g, go);
							}
							// if standard GameObject, render with the embedded render method.
							go.render(dg, g);
						}
					}
				}

				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(camera.x, camera.y);
				}
			}

			// rendering light
			Graphics2D lg = (Graphics2D) lightBuffer.getGraphics();
			lg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			lg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			// Clear Light buffer
			lg.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));

			lg.fillRect(0, 0, dg.config.screenWidth, dg.config.screenHeight);
			// draw all Lights
			for (Light l : lights) {
				l.render(dg, lg);
			}

			// draw HUD
			dg.stateManager.getCurrent().drawHUD(dg, this, g);

			lg.dispose();
			g.dispose();

			// render image to real screen (applying scale factor)
			renderToScreen(dg);
		}
	}

	/**
	 * Rendering of the object (will be delegated to another component in a next
	 * version.
	 *
	 * @param dg the core.Game containing the object.
	 * @param g  the graphics API.
	 */
	public void renderObject(Game dg, GameObject go, Graphics2D g) {
		switch (go.type) {
		case RECTANGLE:
			g.setColor(go.foregroundColor);
			g.fillRect((int) go.x, (int) go.y, (int) go.width, (int) go.height);
			break;
		case CIRCLE:
			g.setColor(go.foregroundColor);
			g.fillOval((int) go.x, (int) go.y, (int) go.width, (int) go.height);
			break;
		case IMAGE:
			if (go.direction < 0) {
				g.drawImage(go.image, (int) (go.x + go.width), (int) go.y, (int) (-go.width), (int) go.height, null);
			} else {
				g.drawImage(go.image, (int) go.x, (int) go.y, (int) go.width, (int) go.height, null);
			}
			break;
		}
	}

	public void renderToScreen(Game dg) {
		Camera camera = dg.stateManager.getCurrent().getActiveCamera();
		if (jf != null) {
			Graphics2D g = (Graphics2D) jf.getGraphics();
			float sX = jf.getWidth() / dg.config.screenWidth;
			float sY = jf.getHeight() / dg.config.screenHeight;

			if (g != null) {
				g.drawImage(screenBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, dg.config.screenWidth,
						dg.config.screenHeight, Color.BLACK, null);

				if (lights.size() > 0) {
					g.drawImage(lightBuffer, 0, 0, jf.getWidth(), jf.getHeight(), 0, 0, dg.config.screenWidth,
							dg.config.screenHeight, null);
				}

				if (dg.config.debug > 0) {
					g.setColor(Color.ORANGE);
					g.drawString(String.format("debug:%d | cam:(%03.1f,%03.1f)", dg.config.debug, camera.x, camera.y),
							4, jf.getHeight() - 20);

					if (dg.config.debug > 2) {
						g.setColor(Color.ORANGE);
						g.drawString("cam:" + camera.name, (int) (20 + sX), (int) (20 * sY));
						g.drawRect((int) ((10) * sX), (int) ((10) * sY), (int) ((dg.config.screenWidth - 20) * sX),
								(int) ((dg.config.screenHeight - 20) * sY));
					}
				}
				g.dispose();
			}
			if (jf.isDoubleBuffered()) {
				jf.getBufferStrategy().show();
			}
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

	public void add(GameObject go) {
		if (go instanceof GameObject) {
			disptachToLayer(go);
		} else if (go instanceof Light) {
			lights.add((Light) go);
		}
	}

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

	public void addAll(Collection<GameObject> objects) {
		for (GameObject go : objects) {
			add(go);
		}
	}

	public void addAll(Map<String, GameObject> objects) {
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
			java.lang.System.err.println("Unable to write screenshot to " + filename + ":" + e.getMessage());
		}
	}

	public void renderMapObject(Graphics2D g, MapObject mo, float x, float y) {
		g.drawImage(mo.imageBuffer, (int) x, (int) y, null);
	}

	@Override
	public String getName() {
		return Renderer.class.getCanonicalName();
	}

	@Override
	public int initialize(Game game) {
		return 0;
	}

	@Override
	public void dispose() {

	}
}
