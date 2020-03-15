# Physic Engine System

The `PhysicEngineSystem`, implementing the `System` interface, will perform `GameObject` update according to their own `Material`, speed, and acceleration to define there new position.

Any object to be managed by the physic engine must be added ad scene level, during the initialization phase (see [Scene#initialize(Game)](./scene_and_manager_system.md/#scene-interface "see details")).

## Global design 

Here is an overview of needed modification:

![Physic Engine system integration](http://www.plantuml.com/plantuml/png/hL9DRzGm4BtxLqGzfMnxWAD1LLL0W4G1KGdEi_OqE-2FQCPPkaNyTvp4IvAI7f3OWzVptlFa-Oql9G6dpZibtocqmE0h7qCqEuuUgyGTAWe9-HOCLZS7IUX_gYh_DXGe4JYwnxhHRy3ZwOWtc3xagZxLDuafD2DgITee6LM_b74WKlL7IYlkGYPArxPBDUbt_VzR2DOX5uenMEIQzUTXa_7d8z6r5XBMwA0LjDh6RkimaySlVTeTXCphq530yGeL-TQXnv1aTgdpkFs69eb-Jv9UzFu-3i15vZR68V3WSWFMratKas106NtSumensYiXdLLp_gp2OdVAvMLqZeHY-5zM7tFwBqOdUnFM-zYuQFINN4H-zgglz-WMDPWbBtIF2y23ICbWvc6APMtmavlhB6O2DtYn68JIOTRjbiaaYc46UMW29X8_mp3tIcHcPu_zlqPsTcZUCEopOE7zDhB1CeCZVl7dxDLLl9jdj9v1bgi-q-hawSdvULuUFW1j-jeYvtAfN7dOkbr03m-Ceci8q21hEKZUgcE6asoQiVQ5LFrjLXNRRRnJKrqqJnOQioDdrTzsLyNbQJygpyiu5_sZNoynsCwxtm00)

## The classes

This beautiful class dagram depicts the entire solution to compute some physic data.

The System `PhysicEngineSystem` is computing all the moves with a `World` object, defining the default *gravity*. Any `GameObject` will have a `Material` defining some physic properties like *friction*, *elasticity*.

All the fancy things are in some simple formulas:

```
for(g : pe.objects){

  g.forces.add(w.gravity)
  g.forces.add(w.forces)

  g.acc = g.acc + sum ( g.forces )
  g.vel = g.vel + 1/2 g.acc * t²
  g.pps = g.pos + g.vel * t

  g.forces.clear();
  
}
```

### World

As we want to integrate some physical ant natural behaviors, the World class will be the *configuration* place for the characteristics like the gravity, and any other global world influencers, like wind, magnetism, etc...

The simple World class will porpose something like the following piece of code:

```java
public class World {

	private Game game;
  private Map<String, Vector2D> forces = new ConcurrentHashMap<>();
  private Vector2D gravity = new Vector2D(0.0f, 9.81f);

  public World(Game game) {
      this.game = game;
      this.forces.put("gravity",gravity);
  }

  public Collection<Vector2D> getForces() {
      return forces.values();
  }

  public void addForce(Vector2D f) {
      forces.put(f.getName(), f);
  }

  public void removeForce(Vector2D f) {
      forces.remove(f.getName());
  }

  public void removeForce(String n) {
      forces.remove(n);
  }
}
```

### Material definition

Must be introduced a new object to simulate Material physic behavior:

`Material` class will defined the needed attributes:

```java
class Material {
  String name;
  double elasticity;
  double friction;
  double density;
  double magnetism;
}
```

Optionally, it can define some default materials like, rock, water, pillow, plastic, wood, metal, glass, and some fancy ones like bouncy ball and/or super ball.

### GameObject

The `GameObject` class must add new attributes:

```java
Vector2D pos;
Vector2D vel;
Vector2D acc;
Vector2D size;

Material material;
double mass;
```
Now let's dive into the `PhysicEngineSystem` main `System` implementation.

### PhsyicEngineSystem

The Core of our physic engine is the system itself. It will propose 3 way to compute physics attributes to any `GameObject` entity. Those mode of computation re defined by the `GameObject`'s physic type:

- **STATIC** the object is static and does not contribute to the physic world
- **KINETIC** this particular physic type of computation will only update `GameObject` position according to its own velocity. No acceleration and or frictino, elasticity, material will be tae in account to compute physic moves.
- **DYNAMIC** the most accurate physic computation way of thing, all (as far as this engine try to go) the physic parameters are engaged in the computation moves of the `GameObject` having this physc type. Acceleration is the sum of applied forces, velocity is based on acceleration and position is the resulting computation og velocity. All those computation are performed with the Material of the `GameObject`.

These types are defined into the `PhysicType` enumeration in the `PhysicEngineSystem` class.

```Java
public enum PhysicType {
  STATIC, // object move without collision and limitation
  KINETIC, // object moves only on speed attributes with collision detection
  DYNAMIC // object will move according to a full physic simulation.
}
```

The main class of the system is as below:

```java
public class PhysicEngineSystem extends AbstractSystem {

	public PhysicEngineSystem(final Game game, final World world) {
		super(game);
		this.world = world;
	}

	@Override
	public int initialize(final Game game) {
		if (objects == null) {
			objects = new ArrayList<>();
		}
		objects.clear();
		return 0;
	}


	public void update(final Game game, final Scene scn, final double elapsed) {
		this.scene = scn;

		objects.forEach(o -> {
			update(game, o, elapsed);
		});

	}

	public void update(final Game game,final GameObject o, final double elapsed) {
		// Process Camera or other object update
		if (o instanceof Camera) {

			// This is a camera object, need to be updated !
			((Camera) o).update(game, elapsed);

		// This is not a camera object
		} else if (o != null && o.pos != null && o.vel != null) {
    
    	switch (o.physicType) {
				case DYNAMIC:
          // compute for dynamic
          // ...
					break;

				case KINETIC:
          //compute for KINETIC
          // ...
          break;
          
				case STATIC:
					// compute for STATIC
          // ...
					break;
			}
		}
	}

	public String getName() {
		return "physic_engine";
	}

	public void add(final GameObject o) {
		if (!objects.contains(o)) {
			objects.add(o);
			if (!o.child.isEmpty()) {
				o.child.values().forEach(go -> {
					objects.add(go);
				});
			}
		}
	}

	public void dispose() {}

}

```

As any [System](SystemManager), initialize(Game) is called at system initialization (sic:)), to clear the computed objects list.
