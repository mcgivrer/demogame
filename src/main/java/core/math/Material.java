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
public class Material implements Cloneable {

    /**
     * List of already existing materials.
     */
    public enum MaterialName {
        NONE("None"), ROCK("Rock"), WOOD("Wood"), METAL("Metal"), PILLOW("Pillow"), WATER("Water"), STATIC("Static"),
        BOUNCYBALL("BouncyBall"), SUPERBALL("SuperBall"), CUSTOM("Custom");

        MaterialName(String name) {
            this.name = name;
        }

        private String name;

        public String getString() {
            return name;
        }
    }

    public static int index = 0;

    /**
     * Rock Density : 0.6 Restitution : 0.1 Wood Density : 0.3 Restitution : 0.2
     * Metal Density : 1.2 Restitution : 0.05 BouncyBall Density : 0.3 Restitution :
     * 0.8 SuperBall Density : 0.3 Restitution : 0.95 Pillow Density : 0.1
     * Restitution : 0.2 Static Density : 0.0 Restitution : 0.0
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
    public double elasticity = 0.0f;
    public double friction = 0.0f;
    public double density = 0.0f;
    public double magnetism = 0.0f;

    public Material() {
        name = String.format("noname_%03d", index++);
        elasticity = 0.0f;
        friction = 0.0f;
        density = 0.0f;
        magnetism = 0.0f;
    }

    public Material(String name, double elasticity, double friction, double density) {
        this();
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

    public static Material builder(String name) {
        MaterialName m;
        Material mat=null;
        try {
            m = MaterialName.valueOf(name);
            mat = builder(m);
        } catch (Exception e) {
            m = MaterialName.CUSTOM;
            mat = builder(m);
            mat.name = name;
        }
        return mat;
    }

    public static Material builder(MaterialName matName) {
        Material mat = null;
        switch (matName) {
            case NONE:
                mat = Material.NONE;
                break;
            case BOUNCYBALL:
                mat = Material.BouncyBall;
                break;
            case METAL:
                mat = Material.METAL;
                break;
            case PILLOW:
                mat = Material.PILLOW;
                break;
            case ROCK:
                mat = Material.ROCK;
                break;
            case STATIC:
                mat = Material.STATIC;
                break;
            case SUPERBALL:
                mat = Material.SuperBall;
                break;
            case WATER:
                mat = Material.WATER;
                break;
            case WOOD:
                mat = Material.WOOD;
                break;
            case CUSTOM:
                mat = new Material();
                break;
            default:
                mat = new Material();
                break;
        }
        return mat;
    }

    public Material set(String name, double elasticity, double friction, double density) {
        this.name = name;
        this.elasticity = elasticity;
        this.friction = friction;
        this.density = density;
        return this;
    }
}