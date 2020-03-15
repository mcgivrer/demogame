# GameObject

As introduced before, I talk about some entities managed by the game. This where the `core.object.GameObject` is going to play.  

Any entity manage by the game,  the player character, the NPC (non playable characters), the score, the life, the all background display are objects in a game. The `core.object.GameObject` is going to be the core matter of the `core.Game`.

To be able to draw anything on screen, we will need for each object , a position, a size. We would need some other properties like an image of a color, a shape (square, circle, rectangle), and to be animated more new attributes we will discover later.

So, to start with a sustainable thing, create our class:

```java
public class GameObject {
	public String name;
	public float x,y;
	public float width,height;
	public Color color;
	public GameObjectType type;
	public Map<String,Object> attributes;
}
```

Manage our objects will be easier by adding a name and some free attributes map.

So, our core.Game class will be enhance with some objects:

```java
public class Game {
  ...
  
  private Map<String,GameObject> objects;
  
  ...
  
  public void initialize(){
    objects = new HashMap<>();
  }
  
  public void input(){}
  public void update(float elapsed){}
  public void render(){}
  
  ...
}
```

and now adapt update processing to objects update:

```java
void update(float elapsed){
  for(GameObject go:objects){
    go.update(elapsed);
  }
}
```
And adapt the rendering process:
```java
void render(Graphics2D g){
  for(GameObject go:objects){
    go.render(r);
  }
}
```

Now, we need to add some object to our game; modify Game class to add:

```java

void generateObjects(){
  ...TODO...
}
```

and let's have a try and execute this.
