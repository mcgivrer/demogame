/**
 * 
 */
package core.object.particle;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import core.Game;
import core.object.GameObject;
import core.object.World;

/**
 * A ParticleSystem is a specific GameObject to animate a system of particles.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * @since 1.0
 */
public class ParticleSystem extends GameObject {
	public List<Particle> particles = new ArrayList<>();

	World world;

	public ParticleSystem(World world, float x, float y) {
		this.world = world;
		this.x = x;
		this.y = y;
	}

	public void create(Game dg, World w) {
		behaviors.stream().forEach(b -> b.create(dg, w, this));
	}

	@Override
	public void update(Game dg, double elapsed) {
		super.update(dg, elapsed);
		if (behaviors != null && behaviors.size() > 0) {
			behaviors.stream().forEach(b -> b.update(dg, this, elapsed));
		} else {
			particles.stream().forEach(p -> p.update(this, elapsed));
		}
	}

	@Override
	public void render(Game dg, Graphics2D g) {
	}
}
