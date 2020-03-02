# Physic Engine System

The `PhysicEngineSystem`, implementing the `System` interface, will perform `GameObject` update according to their own material, speed, and acceleration to define there new position.

Any object to be managed by the physic engine must be added ad scene level, during the initialization phase (see [Scene#initialize(Game)](./scene_and_manager_system.md/#scene-interface  "see details")).

## Global design 

Here is an overview of needed modification:

![Physic Engine system integration](http://www.plantuml.com/plantuml/proxy?src=https://raw.github.com/mcgivrer/demogame/blob/feature/add-physic-system/src/docs/resources/diagrams/physic_engine_system.iuml)

## The classes

### World

As we want to integrate some physical ant natural behaviors, the World class will be the *configuration* place for the characteristics like the gravity, and any other global world influencers, like wind, magnetism, etc...

The simple World class will porpose something like the following piece of code:

```java

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
