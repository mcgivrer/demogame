/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * DemoGame
 * 
 * @year 2019
 */
package core.object;

import java.awt.Graphics2D;

import core.Game;

/**
 * Light to illuminate the world !
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 *
 */
public class Light extends GameObject {

	public enum LightType {
		LIGHT_CONE, LIGHT_SPHERE, LIGHT_AMBIANT
	}

	public LightType lightType;
	public GameObject target;

	/**
	 * 
	 */
	public Light() {

	}

	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Light(String name, 
			float x, float y, 
			float width, float height, 
			LightType lightType, GameObject target) {
		super(name, x, y, width, height);
		this.lightType = lightType;
		this.target = target;
	}

	@Override
	public void render(Game dg, Graphics2D g) {
		super.render(dg, g);
	}
}
