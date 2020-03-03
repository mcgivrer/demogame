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
