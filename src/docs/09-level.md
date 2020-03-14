# Level, map and tiles

To get playable area in a platform game, we need to define finely the level design, the objects to be collected, the non player characters and the enemies, there ca=haracteristics and there position in the level.
We also need to be able to define the chain between each levels.

To achieve those requirements, we need to implement some new objects and some files to set those values.

2 files will be defined:

- `asset_\[99\].json` to define objects to be used and displayed in the level map,
- `map_\[99\].json` the defintion of the layers of a map in a level.

Let's dive in to those definitions !

## What are the entities ?

To manage a `MapLevel`, we need some basic and some complex entities.
Any MapLevel is composed of some MapLayer's that can be tile map or image. An Asset is a list of pre-defined MapObject, a tile or a GameObject, with its own parameters. This MapObject will be referenced in a MapLayer's map with a simple character.

Let's dive into a sample to understand, here is a simple [map.json](resources/maps/map.json "open to see the json file") file.

The JSON structure is quite simple.

A first section defining the `name` of this map, and a short `description`.

```javascript
{
	"name": "Name of this level",
	"description": "A short description for this MapLevel.\nThis text can be displayed at start during 10 seconds, a default value",
```

the short decription will be displayed at level start, during a default delay of 10seconds. This can be changed by a parameter in the next "environment" section

This `environment` section can contains a bunch of thing like parameters for ambient light, gravity, etc...

> **NOTE**<br/> > _this section will be documented soon._

```javascript
	"environment": {
        "ambiantlight": {
			"color": [
				1.0,
				1.0,
				1.0
			],
			"intensity": 1.0
		},
        "physic":{
            "gravity":[0,-0.981],
            "wind":{
                "velocity":[-0.20,0],
                "randomFreq":0.26
            }
        }
     },
```

Then, the main part of the definition, the list of layers.

Here is a first layer as a simple image, the `type` attribute is set to `LAYER_BACKGROUND_IMAGE`.

Each layer has their own `name`, a display sort order `index`. Lower the value is, farer rendered it is. Then, the `background` attribute will define the path to the image to be used as a background.

```javascript
	"layers": {
		"background": {
			"name": "background",
			"index": "1",
			"type": "LAYER_BACKGROUND_IMAGE",
			"background": "/res/images/background-1.jpg"
		},
```

A second `MapLayer` named `mid` has `type` of `LAYER_TILEMAP` will render a real Map.

Based on some MapObject defined by one of the list of `MapObjectAsset` defined by the `assets` attribute the map will use a file `/res/assets/asset-2.json` as tile assets (a list of MapObject definition).

You can noticed the `index` fixed at value 2, it will be rendereed on top of the previous set MapLayer named `background`.

```javascript
		"mid": {
			"name": "mid",
			"index": "2",
			"type": "LAYER_TILEMAP",
			"assets": [
				"/res/assets/asset-2.json"
			],
			"map": [ ... ]
		},
```

And the latest MapLayer is like the previous one. Using the same asset to dipslay the tiles.

```javascript
		"front": {
			"name": "front",
			"index": "3",
			"type": "LAYER_TILEMAP",
			"assets": [
				"/res/assets/asset-2.json"
			],
			"map": [ ... ]
		}
```

And then, the `MapObjectAsset` definition is also a json file. Its structure is used to define each character as `MapObject`, used as tile in `MapLayer` map.

Before diving into a `MapObject` defined into an `asset.json` file, you need to understand that each entity in this asset file is going to be interpreted as a simple tile, a basic graphical element to be displayed, a real `GameObject` like an enemy or the main player character, or an internal code to define some parameters for object `Behavior`.

This Behavior has been explained in a previous chapter, but, added to a specific asset definition and a GameObject, these code will help GameObject understand its environment to move on predefined some zone.

The specific characters '`[`' and '`]`' define a moving zone for the enclosed entity. This can delimit moves for an enemy.

The asset file has a first section, with a `name`, an `image` path for a file where the graphic parts of the tiles or `GameObject` are going to be extracted.

And a default tile size with a `tileWidth` and a `tileHeight`. And then all the `objects` aref defined.

```javascript
{
	"name": "Objects Set 1",
	"image": "/res/images/tileset-1.png",
	"tileWidth": 16,
	"tileHeight": 16,
	"objects": { ... }
}
```

Let's visit an objects list entry definition. this entry will be converted to a `GameObject` in our game, this is toled by the `clazz` attribute defining the implementation class.
`type` and `name` attribute define the king of entity.

```javascript
"E": {
    "id": "E",
    "type": "ENEMY",
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
        "coins": 5
    }
}
```

Then let's see a MapObject to be a Tile

```javascript

```

And finaly, a simple action action code.

```javascript
```

### MapLevel

A `MapLevel` is a playground for a player, with one or more layers, and each `MapLayer` can be a tilemap or a simple image.
This MapLevel is read from a map file, where used asset and map are defined (see the [`MapReader`](the-map-reader) below for details).

### MapLayer

A map, defined in a `map_*.json` file is composed of one or more layer. Each layer can be map of ile or an image, and have their own size (rows, columns. The tiles and entities used are the one defined in an `asset_*.json` file.

### MapObjectAsset

### MapObject

## The MapReader

TODO

### The Asset

An asset is a list of objects to be use (and reuse) in some map levels. Those defined objects will be dispatch through all the layers, according to their type and renderer according to their priority.

### The Map

TODO

## The MapRenderer

TODO
