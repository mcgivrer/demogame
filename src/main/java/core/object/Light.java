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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;

import core.Game;
import lombok.Data;
import lombok.ToString;

/**
 * Light to illuminate the world !
 *
 * @author Frédéric Delorme <frederic.delorme@gmail.com>
 *
 */
@Data
@ToString
public class Light extends GameObject {

	public enum LightType {
		LIGHT_CONE, LIGHT_SPHERE, LIGHT_AMBIANT
	}

	public LightType lightType;
	public GameObject target;
	public double glitterEffect=1.0;

	public double intensity;
	public float[] dist = { 0.0f, 0.75f, 1.0f };
	public Color[] colors;

	RadialGradientPaint rgp;

	/**
	 * 
	 */
	public Light() {

	}

	/**
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
		switch (lightType) {
		case LIGHT_SPHERE:
			foregroundColor = brighten(foregroundColor, intensity);
			colors = new Color[] { foregroundColor,
					new Color(foregroundColor.getRed() / 2, foregroundColor.getGreen() / 2,
							foregroundColor.getBlue() / 2, foregroundColor.getAlpha() / 2),
					new Color(0.0f, 0.0f, 0.0f, 0.0f) };
			rgp = new RadialGradientPaint(new Point((int) (x+(10*Math.random()*glitterEffect)), (int) (y+(10*Math.random()*this.glitterEffect))), (int) width, dist, colors);

			g.setPaint(rgp);
			g.fill(new Ellipse2D.Double(x - width, y - width, width * 2, width * 2));
			break;
		case LIGHT_CONE:
			// TODO implement the CONE light type
			break;
		case LIGHT_AMBIANT:
			//g.setColor(brighten(foregroundColor, intensity));
			//g.fillRect(0,0,dg.config.screenWidth,dg.config.screenHeight);
			break;
		}
	}

    /**
     * Make a color brighten.
     *
     * @param color Color to make brighten.
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
