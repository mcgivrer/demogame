/**
 * 
 */
package core.object.particle;

import java.awt.Color;
import java.awt.image.BufferedImage;

import core.math.Vector2D;

/**
 * The Particle object to be manage through a ParticleSystem. ParticleSystem
 * contains at least 1 particle and animate those object.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @Since 1.0
 *
 */
public class Particle {
	
	public enum ParticleType{
		POINT,
		LINE,
		ELLIPSE,
		RECTANGLE,
		IMAGE;
	}
	
	public Vector2D loc = new Vector2D(0, 0);
	public Vector2D vel = new Vector2D(0, 0);
	public Vector2D acc = new Vector2D(0, 0);
	public Vector2D size = new Vector2D(0, 0);

	public double mass = 1;
	public double friction = 1;
	public double elasticity = 0;
	
	public double lifeSpan=0; 
	
	public ParticleType type;
	
	public Color foreColor;
	public Color borderColor;
	

	public BufferedImage image;

	public Particle(double x, double y, double w, double h) {
		loc.x = x;
		loc.y = y;
		size.x = w;
		size.y = h;
	}

	public void update(ParticleSystem particleSystem, double elapsed) {

		acc.add(particleSystem.world.getGravity().multiply((float) (1 / mass)).multiply((float) friction));

		vel.add(acc.multiply((float) elapsed));

		loc.add(vel);
	}

}
