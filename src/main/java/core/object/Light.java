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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;

import core.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Light to illuminate the world !
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class Light extends GameObject {

	public enum LightType {
		LIGHT_CONE, LIGHT_SPHERE, LIGHT_AMBIANT
	}

	public LightType lightType;
	public GameObject target;
	public double glitterEffect = 1.0;

	public double intensity;
	public float[] dist = { 0.0f, 0.75f, 1.0f };
	public Color[] colors;

	public RadialGradientPaint rgp;

	/**
	 *
	 */
	public Light() {

	}

	/**
	 * Create a new Light object with set parameters
	 *
	 * @param name
	 * @param x
	 * @param y
	 * @param radius
	 * @param intensity
	 * @param lightType
	 * @param target
	 */
	public Light(String name, double x, double y, double radius, double intensity, LightType lightType,
			GameObject target) {
		super(name, x, y, radius, radius);
		this.lightType = lightType;
		this.target = target;
		this.foregroundColor = Color.YELLOW;
	}

	@Override
	public void render(Game dg, Graphics2D g) {

	}

	/**
	 * Make a color brighten.
	 *
	 * @param color    Color to make brighten.
	 * @param fraction Darkness fraction.
	 * @return Lighter color.
	 */
	public static Color brighten(Color color, double fraction) {

		int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
		int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
		int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);

	}
}
