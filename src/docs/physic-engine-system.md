# Physic Engine System

The `PhysicEngineSystem`, implementing the `System` interface, will perform GameObject update according to the own material, speed, acceleration to defines new there position.


## Global design 

Here is an overview of needed modification:

```plantuml
skinparam monochrome true
interface System{
    +initialize(g:Game)
    +getName():String
    +dispose()
}
class Game{
    +run()
    +input(i:InputHandler)
    +render(r:Renderer);
    +update(elapsed:double);
}
class PhysicEngineSystem implements System{
  -objects:List<GameObject>
  +clear()
  +add(go:GameObject)
  +remove(go:GameObject)
  +update(g:Game, o:GameObject, e:double)
}
class CollisionSystem implements System{
  -objects:List<GameObject>
  +clear()
  +add(o:GameObject)
  +remove(o:GameObject)
  +update(g:Game, o:GameObject)
}
class GameObject{
  +loc:Vector2D
  +vel:Vector2D
  +acc:Vector2D
  +size:Vector2D
  +mass:double
  +update(e:double)
  +render(r:Renderer)
}
class Material{
  +density:double
  +friction:double
  +magnetism:double
  +elasticity:double
}
class World{
  +gravity:Vector2D
  +forces:List<Vector2D>
}
class BBox{
  +loc:Vector2D
  +size:Vector2D
}

Game "1"--"1" CollisionSystem:colliderSys
Game "1"--"1" PhysicEngineSystem:physicEngineSys
Game "1"--"1" SystemManager:sysMan
Material "1"--"1" GameObject:material
BBox "1"--"1" GameObject:bbox
GameObject "1"--"*" GameObject:child
PhysicEngineSystem "1"--"*" GameObject:objects
PhysicEngineSystem "1"--"1" World:world
```

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
