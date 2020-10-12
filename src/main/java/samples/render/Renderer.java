package samples.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import samples.Sample;
import samples.camera.entity.Camera;
import samples.input.InputHandler;
import samples.object.entity.GameObject;
import samples.system.AbstractGameSystem;

/**
 * Create a Rendering pipeline as a System, to be used in the main game class.
 * 
 * Add this System to the GameSystemManager, and when needed, just call the
 * {@link Renderer#render(SampleRendererSystem, long)}.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 2020
 */
public class Renderer extends AbstractGameSystem implements IRenderer {

	private int debug = 0;

	// Internal Rendering buffer
	protected BufferedImage screenBuffer;
	// the Java Window to contains the game
	protected JFrame frame;

	protected Camera camera;
	protected Rectangle viewport;

	private List<GameObject> objects = new ArrayList<>();

	protected Renderer(Sample game) {
		super(game);
	}

	@Override
	public String getName() {
		return Renderer.class.getSimpleName();
	}

	private void createWindow(String title, int width, int height, double scale) {

		viewport = new Rectangle(width, height);

		screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.BLACK);
		Insets ins = frame.getInsets();
		Dimension dim = new Dimension((int) ((width * scale) - (ins.left + ins.right)),
				(int) ((height * scale) - (ins.top + ins.bottom)));
		frame.setSize(dim);
		frame.setPreferredSize(dim);
		frame.pack();

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.requestFocus();
		frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "transparent_cursor"));
		BufferStrategy bs = frame.getBufferStrategy();
		if (bs == null) {
			frame.createBufferStrategy(4);
		}
	}

	@Override
	public int initialize(Sample game) {
		createWindow(game.getTitle(), game.getWidth(), game.getHeight(), game.getScale());
		return 0;
	}

	@Override
	public void dispose() {
		objects.clear();
		objects = null;
		screenBuffer = null;
		frame = null;
	}

	/**
	 * Add an object to the render pipeline.
	 * 
	 * @param object the GameObject to be added to the renderering pipeline. It will
	 *               be sorted regarding its layer and priority.
	 */
	@Override
	public void addObject(GameObject object) {
		objects.add(object);
		if (object instanceof Camera) {
			this.camera = (Camera) object;
			this.camera.viewport = this.getViewport();
		}
		// Sort object list according to there belonging layer and their own priority.
		objects.sort((o1, o2) -> (o1.layer < o2.layer ? (o1.priority < o2.priority ? -1 : 1) : -1));
	}

	/**
	 * Render the image according to already updated objects.
	 * 
	 * @param game    the parent Game running this service.
	 * @param realFps the real frame per seconds value to be rendered in debug mode
	 *                (if requested)
	 */
	@Override
	public void render(Sample game, long realFps) {

		Graphics2D g = (Graphics2D) screenBuffer.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());

		if (camera != null) {
			g.translate(-camera.x, -camera.y);
		}

		// loop objects
		objects.stream().forEach(go -> {
			drawObject(game, g, go);
		});

		if (camera != null) {
			g.translate(camera.x, camera.y);
		}
		drawToScreen(game, camera, realFps);
	}

	private void drawObject(Sample game, Graphics2D g, GameObject go) {
		g.setColor(go.color);
		int ox = (int) (go.x + go.offsetX);
		int oy = (int) (go.y + go.offsetY);
		switch (go.getClass().getSimpleName()) {
			case "GameObject":
				drawGameObject(g, go, ox, oy);
				break;
			case "MouseCursor":
				drawMouseCursor(g, go, ox, oy);
				break;
			default:
				break;
		}
	}

	private void drawMouseCursor(Graphics2D g, GameObject go, int x, int y) {
		g.setColor(go.color);
		g.drawLine((int) (x - (go.width / 2)), y, (int) (x + (go.width / 2)), y);
		g.drawLine(x, (int) (y - (go.height / 2)), x, (int) (y + (go.height / 2)));
		g.setColor(Color.GRAY);
		g.drawLine(x, y, x, y);
	}

	private void drawGameObject(Graphics2D g, GameObject go, int ox, int oy) {
		switch (go.type) {
			case POINT:
				g.drawLine(ox, oy, ox, oy);
				break;
			case LINE:
				g.drawLine(ox, oy, ox + (int) (go.dx), oy + (int) (go.dy));
				break;
			case RECT:
				g.fillRect(ox, oy, (int) go.width, (int) go.height);
				break;
			case ELLIPSE:
				g.fillOval(ox, oy, (int) go.width, (int) go.height);
				break;
			case IMAGE:
				if (Math.signum(go.direction) == -1) {
					g.drawImage(go.image, (int) (ox + go.width), oy, (int) (-go.width), (int) go.height, null);
				} else {
					g.drawImage(go.image, ox, oy, (int) go.width, (int) go.height, null);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Draw the screen buffer to the real frame according to the requested game
	 * scale.
	 * 
	 * @param realFps the frames per second rate to be renderd with the debug mode.
	 */
	protected void drawToScreen(Sample game, Camera camera, long realFps) {
		// render to screen
		BufferStrategy bs = frame.getBufferStrategy();
		Graphics2D sg = (Graphics2D) bs.getDrawGraphics();

		sg.drawImage(screenBuffer, 0, 0, (int) (game.getWidth() * game.getScale()),
				(int) (game.getHeight() * game.getScale()), 0, 0, game.getWidth(), game.getHeight(), null);
		// Add some debug information
		if (debug > 0) {
			sg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			if (debug > 1) {
				if (camera != null) {
					sg.translate(-camera.x * game.getScale(), -camera.y * game.getScale());
				}
				sg.setColor(Color.GRAY);
				sg.drawRect(0, 0, (int) (getViewport().getWidth() * game.getScale()),
						(int) (getViewport().getHeight() * game.getScale()));

				if (debug > 3) {
					objects.stream().forEach(go -> {
							displayDebug(sg, go);

					});
				}
				if (camera != null) {
					sg.translate(camera.x * game.getScale(), camera.y * game.getScale());
				}
				if (debug > 2) {
					sg.setColor(Color.ORANGE);
					sg.drawRect((int) (10 * game.getScale()), (int) (10 * game.getScale()),
							(int) ((game.getWidth() - 20) * game.getScale()),
							(int) ((game.getHeight() - 20) * game.getScale()));
				}
			}
			displayGlobalDebug(sg, realFps);
		}
		bs.show();
	}

	/**
	 * Display global debug information at bottom screen.
	 * 
	 * @param sg      the graphics interface to be used to draw the debug info.
	 * @param realFps the frames per second rate.
	 */
	private void displayGlobalDebug(Graphics2D sg, long realFps) {
		sg.setColor(new Color(0.6f, 0.3f, 0.0f, 0.7f));
		sg.fillRect(0, frame.getHeight() - 20, frame.getWidth(), 20);
		sg.setColor(Color.ORANGE);
		sg.drawString(String.format("FPS: %d | debug:%d | pause:%s ", realFps, debug, (game.getPause() ? "on" : "off")),
				10, frame.getHeight() - 4);
	}

	/**
	 * Display debug information for the GameObject.
	 * 
	 * @param sg the Graphics2D API to be used
	 * @param go the GameObject to dsplay debug for.
	 */
	private void displayDebug(Graphics2D sg, GameObject go) {

		Font f = sg.getFont().deriveFont(9);
		sg.setFont(f);
		FontMetrics fm = sg.getFontMetrics();
		int lineHeight = fm.getHeight();
		int xOffset = (int) ((go.x + go.width + 8) * game.getScale());
		int yOffset = (int) (go.y * game.getScale());
		if (go.debug) {
			displayBoundingBox(sg,go,xOffset, yOffset);
		}
		go.prepareDebugInfo();
		sg.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
		sg.fillRect(xOffset - 2, yOffset, 150, (go.debugInfo.size()) * lineHeight);
		sg.setColor(Color.ORANGE);
		int i = 0;
		for (String di : go.debugInfo) {
			i++;
			drawString(sg, xOffset, yOffset, lineHeight, i, di);
		}
	}
	
	private void displayBoundingBox(Graphics2D sg, GameObject go, int ox, int oy) {
		sg.setColor(Color.ORANGE);
		sg.drawRect(ox,oy, (int)(go.width*game.getScale()), (int)(go.height*game.getScale()));
	}

	/**
	 * A helper to draw quickly a debug string on screen.
	 * 
	 */
	private void drawString(Graphics2D sg, int xOffset, int yOffset, int lineHeight, int line, String message) {
		sg.drawString(message, xOffset, yOffset + (line * lineHeight));
	}

	/**
	 * Add a KeyListener to the java JFrame.
	 * 
	 * @param ih the InputHandler to support.
	 */
	@Override
	public void addKeyListener(InputHandler ih) {
		frame.addKeyListener(ih);
		frame.addMouseListener(ih);
		frame.addMouseMotionListener(ih);
		frame.addMouseWheelListener(ih);
	}

	@Override
	public Rectangle getViewport() {
		return viewport;
	}

	public Camera getActiveCamera() {
		return camera;
	}

	public void setDebug(int debug) {
		this.debug = debug;
	}
}
