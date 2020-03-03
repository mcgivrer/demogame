package demo.scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import core.Game;
import core.audio.SoundSystem;
import core.behaviors.PlayerInputBehavior;
import core.collision.CollisionEvent;
import core.collision.MapCollidingService;
import core.collision.OnCollision;
import core.gfx.Renderer;
import core.map.MapLayer;
import core.map.MapLevel;
import core.map.MapObject;
import core.map.MapReader;
import core.math.PhysicEngineSystem;
import core.object.Camera;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.TextObject;
import core.object.TextObject.TextAlign;
import core.resource.ProgressListener;
import core.resource.ResourceManager;
import core.scene.AbstractScene;
import core.scene.Scene;
import core.scripts.LuaScriptSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * The <code>DemoState</code> is an implementation for a Game <code>State<code>
 * to demonstrate how to use this small framework to produce a PLatform 2D game
 * .
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 * @see Scene
 * @see AbstractScene
 * @since 2019
 */
@Slf4j
public class DemoScene extends AbstractScene {

	public MapLevel mapLevel;
	public MapCollidingService mapCollider;
	public LuaScriptSystem luas;
	public PhysicEngineSystem physicEngine;

	public int score = 0;
	public int life = 4;

	private BufferedImage energyImg;
	private BufferedImage manaImg;
	private BufferedImage coinsImg;
	private BufferedImage lifeImg;
	private BufferedImage itemHolderImg;
	private BufferedImage itemHolderSelectedImg;

	private TextObject scoreObject;
	private TextObject welcomeText;

	private Font scoreFont;
	private Font infoFont;
	private Font messageFont;

	public DemoScene() {
		this.name = "DemoState";
	}

	public DemoScene(Game g) {
		super(g);
	}

	@Override
	public void load(Game g) {
		g.config.attributes.put("sound_volume", 0.8f);
		g.config.attributes.put("music_volume", 0.4f);

		ResourceManager.clear();
		ResourceManager.addListener(new ProgressListener() {
			@Override
			public void update(float value, String path) {
				log.info("reading resources: {} : {}", value * 100.0f, path);
			}
		});

		ResourceManager.add(new String[] {
				// level game
				"/res/maps/map_2.json", "/res/assets/asset-2.json",
				// graphics
				"/res/images/background-1.jpg", "/res/images/tileset-1.png",
				// audio
				"/res/audio/sounds/collect-coin.ogg", "/res/audio/sounds/collect-item-1.ogg",
				"/res/audio/sounds/collect-item-2.ogg", "/res/audio/musics/once-around-the-kingdom.ogg",
				// fonts
				"/res/fonts/Prince Valiant.ttf", "/res/fonts/lilliput steps.ttf",
				// scripts
				"/res/scripts/enemy_update.lua" });

		mapLevel = MapReader.readFromFile("/res/maps/map_2.json");
		BufferedImage sprites = ResourceManager.getImage("/res/images/tileset-1.png");

		energyImg = sprites.getSubimage(0, 0, 41, 9);
		manaImg = sprites.getSubimage(0, 22, 41, 5);
		lifeImg = sprites.getSubimage(8 * 16, 2 * 16, 16, 16);
		coinsImg = sprites.getSubimage(10 * 16, 1 * 16, 16, 16);
		itemHolderSelectedImg = sprites.getSubimage((4 * 16), 16, 18, 18);
		itemHolderImg = sprites.getSubimage((5 * 16) + 1, 16, 18, 18);
	}

	@Override
	public void initialize(Game g) {
		super.initialize(g);

		objectManager.clear();
		g.sysMan.getSystem(Renderer.class).clear();

		messageFont = ResourceManager.getFont("/res/fonts/Prince Valiant.ttf").deriveFont(16.0f);
		scoreFont = messageFont.deriveFont(24.0f);
		infoFont = ResourceManager.getFont("/res/fonts/lilliput steps.ttf").deriveFont(10.0f);

		inputHandler.addListener(this);
		mapCollider = g.sysMan.getSystem(MapCollidingService.class);
		soundSystem.load("coins", "/res/audio/sounds/collect-coin.ogg");
		soundSystem.load("item-1", "/res/audio/sounds/collect-item-1.ogg");
		soundSystem.load("item-2", "/res/audio/sounds/collect-item-2.ogg");
		soundSystem.load("music", "/res/audio/musics/once-around-the-kingdom.ogg");
		soundSystem.setMute(g.config.mute);

		luas = g.sysMan.getSystem(LuaScriptSystem.class);
		luas.loadAll(new String[] { "/res/scripts/enemy_update.lua" });

		physicEngine = g.sysMan.getSystem(PhysicEngineSystem.class);

		// define the OnCollision listener
		mapCollider.addListener(GameObject.class, new OnCollision() {
			/**
			 * Collision Listener
			 *
			 * @param e Collision Event to manage.
			 */
			public void collide(CollisionEvent e) {
				switch (e.type) {
					case COLLISION_OBJECT:
						collectCoin(e);
						break;
					case COLLISION_ITEM:
						collectItem(e);
						break;
					case COLLISION_MAP:
						manageTileCollision(e);
						break;
					default:
						break;
				}
			}

			/**
			 * A GameObject <code>go</code> collects a MapObject <code>mo</code> item
			 *
			 * @param e the CollisionEvent when the GameObject is moving
			 */
			private void collectItem(CollisionEvent e) {
				if (e.o1.attributes.containsKey("maxItems") && e.m2.collectible && e.o1.canCollect) {
					double maxItems = (Double) e.o1.attributes.get("maxItems");
					if (e.o1.items.size() <= maxItems) {
						e.o1.items.add(e.m2);
						e.map.tiles[e.mapX][e.mapY] = null;
						soundSystem.play("item-1", (float) game.config.attributes.get("sound_volume"));
						log.debug("Collect {}:{} at {},{}", e.m2.type, e.m2.name, e.mapX, e.mapY);
					}
				}
			}

			/**
			 * A GameObject <code>go</code> collect a MapObject <code>mo</code> as Coins.
			 *
			 * @param e the CollisionEvent when the GameObject is moving
			 */
			private void collectCoin(CollisionEvent e) {

				if (e.m2.collectible && e.o1.canCollect && e.m2.money > 0) {
					double value = (double) (e.o1.attributes.get("coins"));
					e.o1.attributes.put("coins", (double) e.m2.money + value);
					e.map.tiles[e.mapX][e.mapY] = null;
					soundSystem.play("coins", (float) game.config.attributes.get("sound_volume"));
					log.debug("Collect {}:{} at {},{}", e.m2.type, e.m2.money, e.mapX, e.mapY);
				}
			}

			private void manageTileCollision(CollisionEvent e) {
				if (e.m2.block) {
					if (Math.abs(e.o1.vel.x) > 0) {
						e.o1.vel.x = 0;
					}
					if (Math.abs(e.o1.vel.y) > 0) {
						e.o1.vel.y = 0;
						e.o1.pos.y = (int) (e.o1.pos.y / e.map.assetsObjects.get(0).tileHeight)
								* e.map.assetsObjects.get(0).tileHeight;
						e.o1.bbox.fromGameObject(e.o1);
					}

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
			scoreObject = new TextObject("score", g.config.screenWidth - 80, 40, Color.WHITE, Color.BLACK,
					new Color(0.1f, 0.1f, 0.1f, 0.8f), scoreFont, true, 10, TextAlign.LEFT);
			scoreObject.setText("%06d", this.score);
			addObject(scoreObject);

			// add a Welcome message for 3s.
			welcomeText = new TextObject("welcome", g.config.screenWidth / 2, g.config.screenHeight * 2 / 3,
					Color.WHITE, Color.BLACK, new Color(0.1f, 0.1f, 0.1f, 0.8f), messageFont, true, 10,
					TextAlign.CENTER);
			welcomeText.duration = 5000;
			welcomeText.backgroundColor = new Color(0.3f, 0.3f, 0.3f, 0.8f);
			welcomeText.borderColor = Color.GRAY;
			welcomeText.setText("Welcome to this Basic Game Demonstration");
			addObject(welcomeText);

			// Create camera
			GameObject player = objectManager.get("player");
			objectManager.addBehavior(player, new PlayerInputBehavior());

			Camera cam = new Camera("camera", player, 0.017f,
					new Dimension((int) g.config.screenWidth, (int) g.config.screenHeight));

			addObject(cam);
			// start game music background
			soundSystem = g.sysMan.getSystem(SoundSystem.class);
			soundSystem.loop("music", (float) g.config.attributes.get("music_volume"));

		}

	}

	@Override
	public void onFocus(Game g) {
		super.onFocus(g);
	}

	@Override
	public boolean isLoaded() {
		return mapLevel != null;
	}

	@Override
	public void input(Game g) {

		if (inputHandler.keys[KeyEvent.VK_ESCAPE]) {
			g.exitRequest = true;
		}
		objectManager.objects.values().forEach(go -> {
			objectManager.inputObject(game, go);
		});
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		boolean control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_R:
				if (control) {
					resetState();
				}
				break;
			case KeyEvent.VK_Z:
				if (control) {
					resetState();
				}
				break;
			default:
				break;
		}
	}

	public void resetState() {
		GameObject player = objectManager.get("player");
		player.action = GameAction.IDLE2;
		player.setSpeed(0.0f, 0.0f);
		player.setPosition(mapLevel.playerInitialX, mapLevel.playerInitialY);
		TextObject welcome = (TextObject) objectManager.get("welcome");
		welcome.duration = 5000;
		welcome.displayed = true;
	}

	/**
	 * Update the DemoGame state.
	 *
	 * @param g
	 * @param elapsed
	 */
	@Override
	public void update(Game g, double elapsed) {

		TextObject s = (TextObject) objectManager.get("score");
		if (s != null) {
			s.setText("%06d", this.score);
		}

		MapLayer frontLayer = mapLevel.layers.get("front");

		game.physicEngine.update(game, this, elapsed);
		// update all objects
		for (GameObject go : objectManager.getAll()) {
			if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
				objectManager.updateObject(game, go, elapsed);
				mapCollider.checkCollision(frontLayer, 0, go);
				mapLevel.constrainToMapLevel(frontLayer, 0, go);
				executeScriptUpdate(g, go);
			}
		}

		// active core.object.Camera update
		if (this.camera != null) {
			((Camera) camera).update(g, elapsed);
		}
	}

	/**
	 * Parse object's attribute "scripts", and if exists, execute all defined lua
	 * scripts.
	 * 
	 * @param g  the parent Game
	 * @param go the GameObject to be updated by its own scripts.
	 */
	private void executeScriptUpdate(Game g, GameObject go) {
		Map<String, GameObject> objects = objectManager.objects;
		if (go.attributes.containsKey("scripts")) {
			List<String> scripts = (List<String>) (go.attributes.get("scripts"));
			for (String script : scripts) {
				try {
					luas.execute(g, physicEngine.getWorld(), script, go, objects);

				} catch (ScriptException e) {
					log.error("unable to update game object {} with its own LUA scripts : {}", go.name, e.getMessage());
				}
			}

		}
	}

	@Override
	public void render(Game g, Renderer r, double elapsed) {
		r.render(g, elapsed);
	}

	@Override
	public void dispose(Game g) {
		ResourceManager.clear();
	}

	public void drawHUD(Game ga, Renderer r, Graphics2D g) {
		GameObject player = objectManager.get("player");

		int offsetX = 24;
		int offsetY = 30;

		// draw Life
		g.drawImage(lifeImg, offsetX, offsetY - 16, null);
		r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY + 1, Color.WHITE, Color.BLACK, infoFont);

		// draw Coins
		g.drawImage(coinsImg, offsetX, offsetY, null);
		double coins = (double) (player.attributes.get("coins"));
		r.drawOutLinedText(g, String.format("%d", (int) coins), offsetX + 8, offsetY + 16, Color.WHITE, Color.BLACK,
				infoFont);

		// draw Mana
		float nrjRatio = (energyImg.getWidth() / 100.0f);
		double nrj = nrjRatio * ((double) (player.attributes.get("energy")));
		g.drawImage(energyImg, offsetX + 24, offsetY - 12, (int) nrj, energyImg.getHeight(), null);

		// draw Energy
		float manaRatio = (manaImg.getWidth() / 100.0f);
		double mana = manaRatio * ((double) (player.attributes.get("mana")));
		g.drawImage(manaImg, offsetX + 24, offsetY - 2, (int) mana, manaImg.getHeight(), null);

		// draw Items
		double maxItems = (double) player.attributes.get("maxItems");
		double selectedItem = (double) player.attributes.get("selectedItem");
		for (int itmNb = 1; itmNb <= maxItems; itmNb++) {

			int posX = (int) (maxItems - itmNb) * (itemHolderImg.getWidth() - 1);
			MapObject item = null;
			if (player.items.size() > 0 && itmNb - 1 < player.items.size()) {
				item = player.items.get(itmNb - 1);
			}
			BufferedImage holder = switchItem(itmNb, item, selectedItem);
			g.drawImage(holder, ga.config.screenWidth - offsetX - posX,
					ga.config.screenHeight - (holder.getHeight() + 12), holder.getWidth(), holder.getHeight(), null);

			if (itmNb - 1 < player.items.size() && item != null) {
				r.renderMapObject(g, item, ga.config.screenWidth + 1 - offsetX - posX,
						ga.config.screenHeight - (holder.getHeight() + 12));
			}
		}
	}

	private BufferedImage switchItem(int itmNb, MapObject item, double selectedItem) {
		BufferedImage holder;
		if (((double) itmNb) == selectedItem && (item != null)) {
			holder = itemHolderSelectedImg;
		} else {
			holder = itemHolderImg;
		}
		return holder;
	}

}
