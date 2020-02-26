package core.math;

/**
 * <p>
 * The {@link Material} class define some physic parameters for any object into
 * the game engine to compute physical reaction on collision, contact and under
 * forces.
 * 
 * <p>
 * 4 parameters will be used to simulate physical beahvior:
 * <ol>
 * <li><code>>elasticity</code> to factor to compute bouncing capability of an
 * object,</li>
 * <li><code>friction</code> a factor to define speed diminishing factor at any
 * contact</li>
 * <li><code>density</code> defining the density of the material (used to
 * compute attraction),</li>
 * <li><code>magnetism</code> defining material capability to attract or repulse
 * <code>METAL</code> objects</li>
 * </ol>
 */
public class Material {

    /**
     * Rock Density : 0.6 Restitution : 0.1
     * 
     * Wood Density : 0.3 Restitution : 0.2
     * 
     * Metal Density : 1.2 Restitution : 0.05
     * 
     * BouncyBall Density : 0.3 Restitution : 0.8
     * 
     * SuperBall Density : 0.3 Restitution : 0.95
     * 
     * Pillow Density : 0.1 Restitution : 0.2
     * 
     * Static Density : 0.0 Restitution : 0.0
     */
    public final static Material NONE = new Material("None", 1.0f, 1.0f, 1.0f);
    public final static Material ROCK = new Material("Rock", 0.1f, 0.20f, 0.6f);
    public final static Material WOOD = new Material("Wood", 0.2f, 0.80f, 0.3f);
    public final static Material METAL = new Material("Metal", 0.05f, 0.20f, 1.2f);
    public final static Material PILLOW = new Material("Pillow", 0.2f, 0.99f, 0.1f);
    public final static Material WATER = new Material("Water", 0.4f, 0.97f, 1.0f);
    public final static Material STATIC = new Material("Static", 0.0f, 0.50f, 0.0f);
    public final static Material BouncyBall = new Material("BouncyBall", 0.8f, 0.20f, 0.3f);
    public final static Material SuperBall = new Material("SuperBall", 0.95f, 0.98f, 0.3f);

    public String name = "noname";
    public float elasticity = 0.0f;
    public float friction = 0.0f;
    public float density = 0.0f;
    public float magnetism = 0.0f;

    public Material(String name, float elasticity, float friction, float density) {
        this.name = name;
        this.elasticity = elasticity;
        this.friction = friction;
        this.density = density;
    }

    public Material(String name, float elasticity, float friction, float density, float magnetism) {
        this(name, elasticity, friction, density);
        this.magnetism = magnetism;
    }

    public String toString() {
        return name;
    }
}