package demo.states;

import core.Game;
import core.ProgressListener;
import core.ResourceManager;
import core.audio.SoundSystem;
import core.collision.CollisionEvent;
import core.collision.MapCollidingService;
import core.collision.OnCollision;
import core.gfx.Renderer;
import core.io.InputHandler;
import core.map.MapLayer;
import core.map.MapLevel;
import core.map.MapObject;
import core.map.MapReader;
import core.object.Camera;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.TextObject;
import core.state.AbstractState;
import core.state.State;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * The <code>DemoState</code> is an implementation for a Game <code>State<code>
 * to demonstrate how to use this small framework to produce a PLatform 2D game.
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @see State
 * @see AbstractState
 * @since 2019
 */
@Slf4j
public class DemoState extends AbstractState implements State {

	public MapLevel mapLevel;
	public MapCollidingService mapCollider;

	public int score = 0;
	public int life = 4;

	private BufferedImage energyImg;
	private BufferedImage manaImg;
	private BufferedImage coinsImg;
	private BufferedImage lifeImg;
	private BufferedImage itemHolderImg;
	private BufferedImage itemHolderSelectedImg;
	private InputHandler inputHandler;
	private SoundSystem soundSystem;

	private static int lastIdleChange = 0;
	private static int lastIdleChangePace = 120;
	private static GameAction idleAction = GameAction.IDLE;

	private TextObject scoreObject;

	private Font scoreFont;
	private Font infoFont;

	public DemoState() {
		this.name = "DemoState";
	}

	public DemoState(Game g) {
		super(g);
	}

	@Override
	public void load(Game g) {
		g.config.attributes.put("sound_volume",0.8f);
		g.config.attributes.put("music_volume",0.4f);

		ResourceManager.clear();
		ResourceManager.addListener(new ProgressListener() {
			@Override
			public void update(float value, String path) {
				log.info("reading resources: {} : {}", value * 100.0f, path);
			}
		});
		ResourceManager.add(new String[] { 
				"/res/maps/map_2.json", 
				"/res/assets/asset-2.json",
				"/res/images/background-1.jpg", 
				"/res/images/tileset-1.png", 
				"/res/audio/sounds/collect-coin.wav",
				"/res/audio/sounds/collect-item-1.wav", 
				"/res/audio/sounds/collect-item-2.wav",
				"/res/audio/musics/once-around-the-kingdom.mp3" });

		objects.clear();
		mapLevel = MapReader.readFromFile("/res/maps/map_2.json");

		BufferedImage sprites = ResourceManager.getImage("/res/images/tileset-1.png");

		energyImg = sprites.getSubimage(0, 0, 41, 9);
		manaImg = sprites.getSubimage(0, 22, 41, 5);
		lifeImg = sprites.getSubimage(8 * 16, 2 * 16, 16, 16);
		coinsImg = sprites.getSubimage(10 * 16, 1 * 16, 16, 16);
		itemHolderSelectedImg = sprites.getSubimage((4 * 16), 16, 18, 18);
		itemHolderImg = sprites.getSubimage((5 * 16)+1, 16, 18, 18);
	}

	@Override
	public void initialize(Game g) {
		// prepare user input handler
		inputHandler = g.sysMan.getSystem(InputHandler.class);
		inputHandler.addListener(this);
		mapCollider = g.sysMan.getSystem(MapCollidingService.class);
		// load Sounds
		soundSystem = g.sysMan.getSystem(SoundSystem.class);
		soundSystem.load("coins", "/res/audio/sounds/collect-coin.wav");
		soundSystem.load("item-1", "/res/audio/sounds/collect-item-1.wav");
		soundSystem.load("item-2", "/res/audio/sounds/collect-item-2.wav");
		soundSystem.load("music", "/res/audio/musics/once-around-the-kingdom.mp3");

		soundSystem.setMute(false);

		// define the OnCollision listener
		mapCollider.addListener(GameObject.class, new OnCollision() {
			/**
			 * Collision Listener
			 *
			 * @param e Collision Event to manage.
			 */
			public void collide(CollisionEvent e) {
				if (e.m2.collectible && e.o1.canCollect) {
					switch (e.m2.type) {
					case "object":
						collectCoin(e.map, e.o1, e.m2, e.mapX, e.mapY);
						break;
					case "item":
						collectItem(e.map, e.o1, e.m2, e.mapX, e.mapY);
						break;
					default:
						break;
					}
				}
			}

			/**
			 * A GameObject <code>go</code> collects a MapObject <code>mo</code> item
			 *
			 * @param map the MapLayer where the GameObject is moving
			 * @param go  the GameObject having collision
			 * @param mo  the Item to be collected by the GameObject
			 * @param x   tilemap horizontal position
			 * @param y   tilemap vertical position
			 */
			private void collectItem(MapLayer map, GameObject go, MapObject mo, int x, int y) {
				if (go.attributes.containsKey("maxItems")) {
					double maxItems = (Double) go.attributes.get("maxItems");
					if (go.items.size() <= maxItems) {
						go.items.add(mo);
						map.tiles[x][y] = null;
						soundSystem.play("item-1", (float)game.config.attributes.get("sound_volume"));
						log.debug("Collect {}:{} at {},{}", mo.type, mo.name, x, y);
					}
				}
			}

			/**
			 * A GameObject <code>go</code> collect a MapObject <code>mo</code> as Coins.
			 *
			 * @param map the MapLayer where the GameObject is moving
			 * @param go  the GameObject having collision
			 * @param mo  the coins to be collected by the GameObject
			 * @param x   tilemap horizontal position
			 * @param y   tilemap vertical position
			 */
			private void collectCoin(MapLayer map, GameObject go, MapObject mo, int x, int y) {

				if (mo.money > 0) {
					double value = (double) (go.attributes.get("coins"));
					go.attributes.put("coins", (double) mo.money + value);
					map.tiles[x][y] = null;
					soundSystem.play("coins", (float)game.config.attributes.get("sound_volume"));
					log.debug("Collect {}:{} at {},{}", mo.type, mo.money, x, y);
				}
			}
		});

		if (mapLevel != null) {
			// add the MapLevel
			mapLevel.priority = 1;
			mapLevel.layer = 1;
			// MapLevel and all its child GameObjects will be added.
			addObject(mapLevel);

			// Add Score text on H.U.D. (fixed = true)

			/*
			 * TODO add score
			 */
			scoreObject = new TextObject();
			scoreObject.name = "score";
			scoreObject.fixed = true;
			scoreObject.layer = 0;
			scoreObject.foregroundColor = Color.WHITE;
			scoreObject.shadowColor = Color.BLACK;
			scoreObject.borderColor = new Color(0.1f, 0.1f, 0.1f, 0.8f);
			scoreObject.setPosition(100, 100);
			addObject(scoreObject);

			GameObject player = objects.get("player");
			// Create camera
			Camera cam = new Camera("camera", player, 0.017f,
					new Dimension((int) g.config.screenWidth, (int) g.config.screenHeight));
			addObject(cam);
		}
	}

	@Override
	public void onFocus(Game g) {
		super.onFocus(g);
		// start game music background
		soundSystem = g.sysMan.getSystem(SoundSystem.class);
		soundSystem.loop("music", (float)g.config.attributes.get("music_volume"));
	}

	@Override
	public boolean isLoaded() {
		return mapLevel != null;
	}

	@Override
	public void input(Game g) {

		GameObject player = objects.get("player");

		if (inputHandler.keys[KeyEvent.VK_ESCAPE]) {
			g.exitRequest = true;
		}

		player.setSpeed(0.0f, 0.0f);
		if (player.action == GameAction.FALL) {
			player.dx = 0.0f;
			player.dy = 0.25f;
		} else {
			player.action = idleAction;
			randomNextIdleAction();
		}

		// reset horizontal speed if falling.
		if (inputHandler.keys[KeyEvent.VK_UP]) {
			player.dy = -0.2f;
			player.action = GameAction.JUMP;
		}
		if (inputHandler.keys[KeyEvent.VK_DOWN]) {
			player.dy = 0.1f;
			player.action = GameAction.DOWN;
		}

		if (inputHandler.keys[KeyEvent.VK_LEFT]) {
			player.dx = -0.2f;
			player.direction = -1;
			player.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
		} else if (inputHandler.keys[KeyEvent.VK_RIGHT]) {
			player.dx = 0.2f;
			player.direction = 1;
			player.action = (!inputHandler.shift ? GameAction.WALK : GameAction.RUN);
		}

		int itemsNb = player.items.size();
		if (inputHandler.keys[KeyEvent.VK_1] && itemsNb <= 1) {
			player.attributes.put("selectedItem", 1.0);

		}
		if (inputHandler.keys[KeyEvent.VK_2] && itemsNb <= 2) {
			player.attributes.put("selectedItem", 2.0);
		}
		if (inputHandler.keys[KeyEvent.VK_3] && itemsNb <= 3) {
			player.attributes.put("selectedItem", 3.0);
		}
		if (inputHandler.keys[KeyEvent.VK_4] && itemsNb <= 4) {
			player.attributes.put("selectedItem", 4.0);
		}
		if (inputHandler.keys[KeyEvent.VK_5] && itemsNb <= 5) {
			player.attributes.put("selectedItem", 5.0);
		}
	}

	private void randomNextIdleAction() {

		GameObject player = objects.get("player");
		// compute next value for Idle
		lastIdleChange++;
		if (lastIdleChange > lastIdleChangePace) {
			double rndAction = (Math.random() * 1.0) + 0.5;
			idleAction = player.action = (rndAction > 1.0 ? GameAction.IDLE : GameAction.IDLE2);
			lastIdleChange = 0;
			lastIdleChangePace = (int) ((Math.random() * 100.0) + 100.0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		boolean control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_R:
			if (control) {

				GameObject player = objects.get("player");
				player.action = GameAction.IDLE2;
				player.setSpeed(0.0f, 0.0f);
				player.setPosition(mapLevel.playerInitialX, mapLevel.playerInitialY);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Game g, float elapsed) {

		// TODO activate score TextObject update
		TextObject s = (TextObject) objects.get("score");
		if (s != null) {
			s.text = String.format("%05d", this.score);
		}
		MapLayer frontLayer = mapLevel.layers.get("front");
		// update all objects
		for (GameObject go : objects.values()) {
			if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
				go.update(g, elapsed);
				mapCollider.checkCollision(frontLayer, 0, go);
				mapLevel.constrainToMapLevel(frontLayer, 0, go);
			}
		}
		// active core.object.Camera update
		if (this.camera != null) {
			camera.update(g, elapsed);
		}
	}

	@Override
	public void render(Game g, Renderer r, double elapsed) {

		g.renderer.render(g, elapsed);
	}

	@Override
	public void dispose(Game g) {
		ResourceManager.clear();
	}

	public void drawHUD(Game ga, Renderer r, Graphics2D g) {
		GameObject player = objects.get("player");
		// super.drawHUD(ga, r, g);

		int offsetX = 24;
		int offsetY = 30;
		// prepare font.
		if (scoreFont == null) {
			infoFont = g.getFont().deriveFont(10.0f);
			scoreFont = infoFont.deriveFont(AffineTransform.getScaleInstance(1.4, 2.0));
		}

		// draw Score
		g.setFont(scoreFont);
		r.drawOutLinedText(g, String.format("%05d", score), ga.config.screenWidth - (46 + offsetX), offsetY + 8,
				Color.WHITE, Color.BLACK);

		// draw Life
		g.drawImage(lifeImg, offsetX, offsetY - 16, null);
		g.setFont(infoFont);
		r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY + 1, Color.WHITE, Color.BLACK);

		// draw Coins
		g.drawImage(coinsImg, offsetX, offsetY, null);
		g.setFont(infoFont);
		double coins = (double) (player.attributes.get("coins"));
		r.drawOutLinedText(g, String.format("%d", (int) coins), offsetX + 8, offsetY + 16, Color.WHITE, Color.BLACK);

		// draw Mana
		float nrjRatio = (energyImg.getWidth() / 100.0f);
		double nrj = nrjRatio * ((double) (player.attributes.get("energy")));
		g.drawImage(energyImg, offsetX + 24, offsetY - 8, (int) nrj, energyImg.getHeight(), null);

		// draw Energy
		float manaRatio = (manaImg.getWidth() / 100.0f);
		double mana = manaRatio * ((double) (player.attributes.get("mana")));
		g.drawImage(manaImg, offsetX + 24, offsetY + 2, (int) mana, manaImg.getHeight(), null);

		// draw Items
		double maxItems = (double) player.attributes.get("maxItems");
		double selectedItem = (double) player.attributes.get("selectedItem");
		for (int itmNb = 1; itmNb <= maxItems; itmNb++) {

			int posX = (int) (maxItems - itmNb) * (itemHolderImg.getWidth() - 1);
			MapObject item = null;
			if (player.items.size() > 0 && itmNb - 1 < player.items.size()) {
				item = player.items.get(itmNb - 1);
			}
			BufferedImage holder;
			if(((double)itmNb) == selectedItem && (item != null)){
				holder = itemHolderSelectedImg ;
				
			}else{
				holder = itemHolderImg;
			}
			g.drawImage(holder, ga.config.screenWidth - offsetX - posX,
					ga.config.screenHeight - (holder.getHeight() + 12), holder.getWidth(),
					holder.getHeight(), null);

			if (itmNb - 1 < player.items.size() && item != null) {
				r.renderMapObject(g, item, ga.config.screenWidth + 1 - offsetX - posX,
						ga.config.screenHeight - (holder.getHeight() + 12));
			}
		}
	}

}
