# Behaviors and LUA scripts

## Adding some well behave to our entities

To add some AI to the NPC, we need to enhance our GameObject with some behavioral capability.
At construction, when loading the Level, some behaviors can be added, like some java class, inheriting from Behavior interface, or lua script to freely script update GameObject method.

This interface could be :

```java
interface Behavior{
  void initialize(Game g);
  void create(Game g, Scene s, GameObject go);
  void input(Game g);
  void update(Game g, Scene s, GameObject go, double elapsed);
  void render(Game g, Scene s, GameObject go, Renderer r);
  void dispose(Game g, Scene s, GameObject go);
}
```

Those interface are hook where you can implement specific behavior to your `GameObject`.
Ultimately, this behavior interface will help implmenting the ParticleSystem and their specific animations.

- **initialize** the intiialize phase will implment any resources initialization
- **create** the create phase will offer to automatically regenerate or create things. Particularly useful for Particle animation.
- **input** where interaction with real-life player happened. here will be intercepted any key or mouse clicks and moves.
- **update** to be animated or moved, some attrbutes of our GameObject must be modified according to some behavior. This is where all is processed.
- **render** to render in a specific way the GameObject linked to your Behavior implementation.
- **dispose** this when the GameObject is destroyed, to free specific resources previously initialized by *initialize*.

## Serving Behaviors

The corresponding implementation could be :

- a java class implementing the `Behavior` interface, like the `PlayerInputBehavior`, intercepting some of the keys pressed and released, to move and animate the player `GameObject`.

- a `LuaScript` class implementing the Behavior interface, but delegating execution to a LUA script served through /res/scripts directory to process the required behavior. this script is defined in the "scripts" GameObject attributes in the level JSON definition file,

e.g.:

```javasript
"F": {
    "id": "E",
    "type": "enemy",
    "name": "enemy_#",
    "clazz": "core.object.GameObject",
    "image": "todo",
    "offset": "6,1",
    "size": "32,32",
    "priority": 2,
    "layer": 2,
    "bbox": {
        "top": 8.0,
        "bottom": 0.0,
        "left": 4.0,
        "right": 4.0
    },
    "color": "RED",
    "canCollect": false,
    "attributes": {
        "energy": 100.0,
        "damage": 10.0,
        "coins": 5,
        "scripts":["/res/scripts/enemy_update.lua"]
    }
},
```

The "behaviors" attrobutes may contain one or more java class implmenting the Behavior interface.

```javascript
    "attributes": {
        "energy": 100.0,
        "damage": 10.0,
        "coins": 5,
        "behaviors":["demo.behaviors.PLayerInputBehavior"]
    },
```

## Scripting with LUA

The "scripts" attribute contains one or more scripts. each script implements a requested behavior.

```javascript
    "attributes": {
        "energy": 100.0,
        "damage": 10.0,
        "coins": 5,
        "scripts":["/res/scripts/enemy_update.lua"]
    }
```

And here is a sample lua script to be executed on the update game loop phase:

```lua
local m = {
}
function init(game,world)
  m.world = world;
end
function update(game, world, object, context)
  local player = {}
  player = context:get("player")
  if( player:getX() > object:getX() or player:getX() < object:getX() ) then
      object:setDx( object:getDx() * -1)
  end
  return object
end
```

The script naming will respect the following rules:

`[XXXXXXXX]_[phase].lua`

where:

- `[XXXXXXXX]` will describe the goal of the script,
- `[phase]` is one of the "create", "update", "render", "input" or "dispose" phase keyword, corresponding to one of the gameloop or `GameObject` lifecycle phases. Today, only the "update" is implemented and usable.



