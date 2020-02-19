/**
 * 
 */
package core.behaviors.particle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.Game;
import core.behaviors.Behavior;
import core.object.GameObject;
import core.object.World;
import core.object.particle.Particle;
import core.object.particle.Particle.ParticleType;
import core.object.particle.ParticleSystem;
import core.resource.ResourceManager;

/**
 * A first Behavior implementation for a Particle System simulating Fire.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 *
 */
public class FireParticleBehavior implements Behavior {

	int maxParticles = 20;

	private BufferedImage image;

	@Override
	public void initialize(Game dg, ResourceManager rm) {
		image = rm.getImage("/res/images/tileset-1.png").getSubimage(0, 0, 4, 4);
	}

	/**
	 * The create phase is called to generate particles for this ParticleSystem.
	 * 
	 * @param dg the parent Game entity
	 * @param w  the World object containing all world parameters
	 * @see core.object.World
	 */
	@Override
	public void create(Game dg, World w, GameObject go) {
		ParticleSystem ps = (ParticleSystem) go;
		if (ps.particles.size() < maxParticles) {

			Particle p = new Particle(ps.x, ps.y, 4.0, 4.0);
			p.type = ParticleType.ELLIPSE;
			p.lifeSpan = (Math.random() * 10) + 10;

			ps.particles.add(p);
		}
	}

	/**
	 * update process to animate particles
	 * 
	 * @param dg      the parent Game entity.
	 * @param go      the GameObject (here is a ParticleSystem) containing the
	 *                particles
	 * @param elapsed the time elapsed since previous call.
	 */
	@Override
	public void update(Game dg, GameObject go, double elapsed) {
		ParticleSystem ps = (ParticleSystem) go;
		ps.particles.stream().forEach(p -> {
			p.lifeSpan--;
			p.update(ps, elapsed);
			if (p.lifeSpan < 0) {
				p.lifeSpan = 0;
			}
			if (p.lifeSpan == 0) {
				ps.particles.remove(p);
			}
		});

	}

	/**
	 * Rendering for this fire particle is just not implemented here but in the
	 * Renderer.
	 * 
	 * @param dg the parent Game entity.
	 * @param go the GameObject (here is a ParticleSystem) containing the particles
	 * @param g  the standard java rendering.
	 * 
	 */
	@Override
	public void render(Game dg, GameObject go, Graphics2D g) {
	}

	/**
	 * Here will be free all resources needed by the ParticleSystem.
	 */
	@Override
	public void dispose(Game dg, World w, GameObject go) {
		// TODO Auto-generated method stub

	}

}
