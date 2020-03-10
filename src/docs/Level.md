# Level, map and tiles

To get playable area in a platform game, we need to define finely the level design, the objects to be collected, the non player characters and the enemies, there ca=haracteristics and there position in the level.
We also need to be able to define the chain between each levels.

To achieve those requirements, we need to implement some new objects and some files to set those values.

2 files will be defined:

- `asset_\[99\].json` to define objects to be used and displayed in the level map,
- `map_\[99\].json` the defintion of the layers of a map in a level.

Let's dive in to those definitions !

## What are the entities ?

### Level

A level is a playground for a player, with one or more layers, and each layer can be a tilemap or a simple image. 

### TileMap

A map, defined in a `map_*.json` file is composed of one or more layer. Each layer can be map of ile or an image, and have their own size (rows, columns. The tiles and entities used are the one defined in an `asset_*.json` file.

### Layers



### Tiles

### Entities

## the files !

### The asset

An asset is a list of objects to be use (and reuse) in some map levels. Those defined objects will be dispatch through all the layers, according to their type and renderer according to their priority.

