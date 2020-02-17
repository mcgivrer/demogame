# Behaviors and LUA scripts

## Adding some well behave to our entities

To add some AI to the NPC, we need to enhance our GameObject with some behavioral capability.
At construction, when loading the Level, some behaviors can be added, like some java class, inheriting from Behavior interface, or lua script to freely script update GameObject method.

This interface could be :

```java
interface Behavior{
  void create(Game g, State s, GameObject go);
  void update(Game g, State s, GameObject go, double elapsed);
  void dispose(Game g, State s, GameObject go);
}
```

## Serving Behaviors

The corresponding implementation could be :

- a java class implementing the `Behavior` interface

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
        "behaviors":["demo.behaviors.FollowingTargetBehavior"]
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
- `[phase]` is one of the "create", "update", "render", "input" or "dispose" phase keyword, corresponding to one of the gameloop or `GameObject` lifecycle phases.
