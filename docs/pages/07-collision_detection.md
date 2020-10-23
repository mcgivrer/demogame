# Collision Detection

In any game, the most part is not only animated objects, but detect when there are colliding, and propose a adapted behavior facing such collision.

This chapter consists in proposing such processing.

## Collision between boxes

The most simple way to determine if 2 objects are colliding, consists in comparing the 2 boundingbox of those objects and if there are intersecting, so, the 2 objects are colliding.

Let's start with a simple diagram, better than complex sentences.

- `A` and `B` are not colliding:

```text

    ya   wa
  xa+--------+
    |        |
 ha |   A    |
    |        |
    +--------+        yb   wb
                    xb+--------+
                      |        |
                   hb |    B   |
                      |        |
                      +--------+
```

- `A` and `B` are colliding, but `A` is on left of `B`:

```text
    ya   wa
  xa+--------+
    |  A  yb |  wb
 ha |   xb+--------+
    |     |XX|     |
    +-----|--+ B   |
        hb|        |
          +--------+
```

The following formula is true :

```
   ( xa < xb + wb )
&& ( xa + wa > bx )
&& ( ya < yb + hb )
&& ( ha + ya > yb )
```

This is AABB algorithm.

## Quadtree !

For one to 4 objects, collision detection can be easy, but cost some CPU cycles. So when the number of objects displayed be closed to 100, we can just not do all comparison. We need to limit the objects to test to the ones near the player (for example).

To achieve such a miracle, we are going to split the space into multiple zones. but how to dispatch all our objects in those sub spaces ? But furthermore how are we going to keep up to date those objects in the right zone according to their moves ?

Mulitple way exists to split spaces in the list of algorithm for [space partitioning](https://en.wikipedia.org/wiki/Space_partitioning "visit wikipedia about space partitioning"):

- BSP trees;
- Quadtrees;
- Octrees;
- k-d trees;
- Bins;
- R-trees;
- Bounding volume hierarchies.

Each os those piece of softwawre have their own pro's & con's.

For our own needs, we are going to use one of the simplest one, the `Quadtree`.

We have our level having `width` x `height` and 10 objects:

```
+------------------------------------+
|         o                    o     |
|                                    |
|      P        o        o     o     |
|                                    |
|                                    |
|         o       o          o       |
|                                    |
|                                    |
|  o                  o              |
|                                    |
+------------------------------------+
```

`P` is our player and `o` are enemies.

We could test all collision possibilities between all those objects, but the number of combinations is monstruous !

Quadtree is based on simple law

If a zone has more than 3 objects, split it in equal 4 sub-zones.

Here is a graphics diagram explainaition :

```
+------------------+-----------------+
|         o        |            o    |
|                  |                 |
|      P         o |     o      o    |
|                  |                 |
+------------------+-----------------+
|         o        |o         o      |
|                  |                 |
|                  |                 |
|  o               |         o       |
|                  |                 |
+------------------+-----------------+
```

If we add one more object (the `X` on the diagram) in the play zone, we will have to split it into 4 equals sub zones

```
+------------------+-----------------+
|         o        |            o    |
|                  |                 |
|      P         o |     o      o    |
|                  |                 |
+------------------+--------+--------+
|         o        |o       | o      |
|                  |        |        |
|                  +--------+--------+
|  o               |   X    |o       |
|                  |        |        |
+------------------+--------+--------+
```

Okay, now we need to code such thing.

You can imagine that each zone is a rectange. each of those retangles can have 4 neighbours.

In our data structure, we are going to keep links between all those rectangles, this can be depicted by a tree.

We are going to call each node of this structure a quadtree.

we need to keep on each quadtree:

- its own position in the 2D space
- its size,
- the list of attached objects

and for the main structure,

- the max number of objects for each quadtree,

and just to keep some reasonably small structure in memory,

- the maximum depth of subdivision for the quadtree we will reach.

And finally,

- the list of the 4 possible QuadtreeNode's subdividing the parent QuadtreeNode.

The quadrant are located like bellow:

```
+-----+-----+
|  1  |  0  |
+-----+-----+
|  2  |  3  |
+-----+-----+
```

So left quadrants are 1 and 2, and right quadrants are 0 and 3. This will be very important to ompute the right quadrant where to store a new object in the structure.

So our `QuatreeNode` class becomes:

```Java
public class QuadtreeNode {
    int level;
    double posX;
    double posY;
    double width;
    double height;
    int maxObjects;
    int maxDepth;
    list<GameObject> objects;
    QuadtreeNode[] nodes;
}
```

and we will need 3 public operations to maintain this tree:

- clean all the structure,
- insert an object,
- retrieve an object in the structure.

To be able to manage only `GameObject` needed properties, we won't use directly a `GameObject`, but an extracted smart interface, a `Collidable`.

This interface will be also managed by our `CollidingSystem`, so not only Quadtree needed infirmation are declared here. 

But let us check the interesting ones, the `BoundingBox` of the `GameObject`:

```java
public interface Collidable {
    BBox getBoundingBox();
}
```
Yes, only te bounding box will be useful to define position and size of the object in the quadtree.

```Java
public class QuadtreeNode {
    ...
    public void clean(){ ... }
    public void insert(Collidable go){ ... }
    public List<Collidable> retrieve(
        List<Collidbale> list,
        Collidable c){ ... }
    ...
}
```

To insert an object (a Collidable) in the quadtree, we need to create a first one. We need a constructor, it will get the width and height of the initial zone:

```java
public class QuadtreeNode {
    ...
    public QuadtreeNode(float width, float height) {
        this(0, 0, 0, width, height);
    }
    ...
}
```

But you can see that we call another constructor, we define a quadtree position `(posX, posY)`, and in the first Quad, it must be `(0,0)`.

```java
public class QuadtreeNode {
    ...
    public QuadTreeNode(
            int pLevel,
            double x, double y,
            double width, double height) {
        level = pLevel;
        objects = new ArrayList<>();
        posX = x;
        posY = y;
        this.width = width;
        this.height = height;
        nodes = new QuadTreeNode[4];
    }
    ...
}
```

To insert an object in this tree structure, we need to identified the zone or subzone the object belongs to.

So first, try to search in the first parent node.

## onCollision

## collisionResponse
