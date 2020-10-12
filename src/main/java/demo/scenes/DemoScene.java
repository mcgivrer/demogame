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
import core.collision.CollidingSystem;
import core.collision.MapCollidingSystem;
import core.gfx.Renderer;
import core.map.MapLayer;
import core.map.MapLevel;
import core.map.MapReader;
import core.math.PhysicEngineSystem;
import core.object.Camera;
import core.object.GameObject;
import core.object.GameObject.GameAction;
import core.object.HudInventory;
import core.object.TextObject;
import core.object.TextObject.TextAlign;
import core.resource.ProgressListener;
import core.resource.ResourceManager;
import core.scene.AbstractScene;
import core.scene.Scene;
import core.scripts.LuaScriptSystem;
import javazoom.jl.player.Player;
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

	public LuaScriptSystem luas;
	public PhysicEngineSystem physicEngine;
	private CollidingSystem collidingSystem;
	public MapCollidingSystem mapCollider;

	public MapLevel mapLevel;

	public int score = 0;
	public int life = 4;

	private BufferedImage energyImg;
	private BufferedImage manaImg;
	private BufferedImage coinsImg;
	private BufferedImage lifeImg;

	private TextObject scoreText;
	private TextObject welcomeText;
	HudInventory inventory;

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

		mapLevel = MapReader.readFromFile("/res/maps/map_2.json");
		BufferedImage imageAsset = ResourceManager.getImage("/res/images/tileset-1.png");

		energyImg = imageAsset.getSubimage(0, 0, 41, 9);
		manaImg = imageAsset.getSubimage(0, 22, 41, 5);
		lifeImg = imageAsset.getSubimage(8 * 16, 2 * 16, 16, 16);
		coinsImg = imageAsset.getSubimage(10 * 16, 1 * 16, 16, 16);

		inventory = new HudInventory(20, 12);
		inventory.load(imageAsset);
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
		mapCollider = g.sysMan.getSystem(MapCollidingSystem.class);
		soundSystem.load("coins", "/res/audio/sounds/collect-coin.ogg");
		soundSystem.load("item-1", "/res/audio/sounds/collect-item-1.ogg");
		soundSystem.load("item-2", "/res/audio/sounds/collect-item-2.ogg");
		soundSystem.load("music", "/res/audio/musics/once-around-the-kingdom.ogg");
		soundSystem.setMute(g.config.mute);

		luas = g.sysMan.getSystem(LuaScriptSystem.class);
		luas.loadAll(new String[] { "/res/scripts/enemy_update.lua" });

		physicEngine = g.sysMan.getSystem(PhysicEngineSystem.class);
		collidingSystem = g.sysMan.getSystem(CollidingSystem.class);

		// define the OnCollision listener
		mapCollider.addListener(GameObject.class, new ObjectCollisionResolver(game));

		Dimension d = new Dimension(mapLevel.getMaxSize());
		g.sysMan.getSystem(CollidingSystem.class).setPlayArea(d);

		if (mapLevel != null) {
			// add the MapLevel
			mapLevel.priority = 1;
			mapLevel.layer = 1;
			// MapLevel and all its child GameObjects will be added.
			addObject(mapLevel);

			// Add Score text on H.U.D. (fixed = true)
			scoreText = new TextObject("score", g.config.screenWidth - 80, 40, Color.WHITE, Color.BLACK,
					new Color(0.1f, 0.1f, 0.1f, 0.8f), scoreFont, true, 10, TextAlign.LEFT);
			scoreText.setText("%06d", this.score);
			scoreText.setCollidable(false);
			addObject(scoreText);

			// add a Welcome message for 3s.
			welcomeText = new TextObject("welcome", g.config.screenWidth / 2, g.config.screenHeight * 2 / 3,
					Color.WHITE, Color.BLACK, new Color(0.1f, 0.1f, 0.1f, 0.8f), messageFont, true, 10,
					TextAlign.CENTER);
			welcomeText.duration = 5000;
			welcomeText.backgroundColor = new Color(0.3f, 0.3f, 0.3f, 0.8f);
			welcomeText.borderColor = Color.GRAY;
			welcomeText.setText("Welcome to this Basic Game Demonstration");
			welcomeText.setCollidable(false);
			addObject(welcomeText);

			// Create camera
			GameObject player = objectManager.get("player");
			Camera cam = new Camera("camera", 0.017f,
					new Dimension((int) g.config.screenWidth, (int) g.config.screenHeight));
			cam.setTarget(player);
			addObject(cam);

			// Attache inventory object to the Player inventory.
			inventory.setPlayer(player);

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

	/**
	 * Manage Input processing on all objects.
	 */
	@Override
	public void input(Game g) {

		if (inputHandler.keys[KeyEvent.VK_ESCAPE]) {
			g.exitRequest = true;
		}
		objectManager.objects.values().forEach(go -> {
			objectManager.inputObject(game, go);
		});
	}

	/**
	 * Process Special Scene keys to reset things
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		boolean control = e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_R:
				if (control) {
					resetGameObjects();
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

	/**
	 * Reset GameObject and WelComeText on a CTRL+Z
	 */
	public void resetState() {
		resetGameObjects();
		TextObject welcome = (TextObject) objectManager.get("welcome");
		welcome.duration = 5000;
		welcome.displayed = true;

	}

	/**
	 * Reset GameObjects on a CTRL+R
	 */
	public void resetGameObjects() {
		for (GameObject go : objectManager.objects.values()) {
			if (mapLevel.initialPosition.containsKey(go.name)) {
				go.pos = mapLevel.initialPosition.get(go.name);
			}
			if (go.name.contentEquals("player")) {
				go.action = GameAction.IDLE2;
				go.setSpeed(0.0f, 0.0f);

			}
		}
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

		// update all objects
		for (GameObject go : objectManager.getAll()) {
			if (!(go instanceof Camera) && !(go instanceof MapLevel)) {
				physicEngine.update(g, go, elapsed);
				objectManager.updateObject(game, go, elapsed);

				mapCollider.checkCollision(frontLayer, 0, go);
				mapLevel.constrainToMapLevel(frontLayer, 0, go);
				collidingSystem.update(go, elapsed);
				// execute any lua script attached to this object
				executeScriptUpdate(g, go);
			}
		}

		// active core.object.Camera update
		if (this.camera != null) {
			((Camera) camera).update(g, elapsed);
		}
		inventory.update(g, elapsed);
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
			@SuppressWarnings("unchecked")
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
		r.drawImage(lifeImg, offsetX, offsetY - 16);
		r.drawOutLinedText(g, String.format("%d", life), offsetX + 9, offsetY + 1, Color.WHITE, Color.BLACK, infoFont);

		// draw Coins
		g.drawImage(coinsImg, offsetX, offsetY, null);
		double coins = (double) (player.attributes.get("coins"));
		r.drawOutLinedText(g, String.format("%d", (int) coins), offsetX + 8, offsetY + 16, Color.WHITE, Color.BLACK,
				infoFont);

		// draw Mana
		double nrjRatio = (energyImg.getWidth() / 100.0f);
		double nrj = nrjRatio * ((double) (player.attributes.get("energy")));
		g.drawImage(energyImg, offsetX + 24, offsetY - 12, (int) nrj, energyImg.getHeight(), null);

		// draw Energy
		double manaRatio = (manaImg.getWidth() / 100.0f);
		double mana = manaRatio * ((double) (player.attributes.get("mana")));
		g.drawImage(manaImg, offsetX + 24, offsetY - 2, (int) mana, manaImg.getHeight(), null);

		// draw Items
		inventory.render(ga, r);
	}

}
