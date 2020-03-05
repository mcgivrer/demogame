# Physic Engine System

The `PhysicEngineSystem`, implementing the `System` interface, will perform GameObject update according to the own material, speed, acceleration to defines new there position.


## Global design 

Here is an overview of needed modification:

![The Physic Engine System class diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/mcgivrer/demogame/develop/src/docs/resources/diagrams/physic-engine-system.md "The Physic Engine System class diagram")

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
