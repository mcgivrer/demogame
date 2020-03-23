# GameObject

As introduced before, I talk about some entities managed by the game. This where the `core.object.GameObject` is going to play in the framwork.  

Any entity manage by the game,  the player character, the NPC (non playable characters), the score, the life, the all background display are objects in a game. The `core.object.GameObject` is going to be the core matter of the `core.Game`.

To be able to draw anything on screen, we will need for each object , a position, a size. We would need some other properties like an image of a color, a shape (square, circle, rectangle), and to be animated more new attributes we will discover later.

### Before enhancing our framework

In the previous `SampleGameLoop` example, we try to move a red square box from side to side. Let start from the requirement for this sample code:

- position(x,y)
- speed( dx,dy)
- color

#### GameObject class

All those  will be centralized into only one object, the `GameObject` class.

So, to start with sustainable things, create our class:

```java
public class GameObject {
	public int x;
	public int y;
	public int width;
	public int height;
	public int dx;
    public int dy;
  	public Color color;   
}
```

Now we have some attributes, we need to update and render things. In a common way to Object development, we need to specialized objects. In the previous sample, all things were performed by the main class. let's delegate some of the operations to this new `GameObject` class. The update process can be performed into the object itself, and the draw process so:

```java
public void udpate(Game ga, long elapsed){
    x += dx;
    y += dx;
}
public void draw(Game ga, Graphics2D g){
    g.setColor(color);
    g.fillRect(x,y,16,16);
}
```

The main class now will just have to initialize a `GameObject` and delegate its update and its draw to.

```java
public class SampleGameObject {
  ...
  GameObject object;
  ...
  public void initialize(){
    objects = new GameObject();
  }
}
```

And now adapt update processing to objects update:

```java
void update(long elapsed){
    object.update(this, elapsed);
}
```
And adapt the rendering process:
```java
void render(Graphics2D g){
	object.draw(this, g);
}
```

Ok, we moved from a One big class to 2 smaller classes. But where are the benefits ? The impact of such class is louder if we decide to maintain multiple `GameObject` instances.

So let's manage more objects !

#### One class to rules them all

lets change a little bit our main class `SampleGameObject` by update some things.

First, replace the object  attribute by a more useful list of object:

```java
private List<GameObject> objects = new ArrayList<>();
```

And then modify the initialization, to add our object to the list:

```java
  public void initialize(){
    objects.add(new GameObject());
  }
```

And let's adapt the update and render methods:

```java
void update(long elapsed){
    for(GameObject go:objects){
        go.update(this, elapsed);
        constrainGameObject(this,go);
    }
}
void render(Graphics2D g){
    for(GameObject go:objects){
        go.draw(this, g);
    }
}
```

So, now, adding one or a dozen of `GameObject`, the main class `SampleGameObject` will remain the same (except the initialize method).

```java
  public void initialize(){
      for(int i=0;i<20;i++){
          GameObject go = new GameObject()
          go.x = (int) Math.random() * (screenBuffer.getWidth() - 16);
          go.y = (int) Math.random() * (screenBuffer.getHeight() - 16);
          go.width = 16;
          go.height = 16;
          go.dx = (int) (Math.random() * 8);
          go.dy = (int) (Math.random() * 8);             
          objects.add(go);   
      }      
  }
```

Then run this sample code :

```bash
$> cd src/main/samples
$> javac SampleGameObject.java
$> java SampleGameObject
```

And you will get something like this :

![More than just one GameObject](./resources/illustrations/SampleGameObject.png "Using a GameObject class for multiple objects on screen")

#### Adding a Pause mode

We also create a small Pause mode by adding a pause boolean flag, changing its state with the `P` or the `PAUSE` key.

```java
public class SampleGameObject implements KeyListener{
	...
    // pause flag
    boolean pause = false;
    
    ...
    public void keyReleased(KeyEvent e) {
        GameObject go = objects.get(0);
        switch (e.getKeyCode()) {
			...
            case KeyEvent.VK_P:
            case KeyEvent.VK_PAUSE:
                pause = !pause;
                break;
            default:
                break;
        }
    }
    ...
    public void loop() {
        ...
        while (!exit) {
            nextTime = System.currentTimeMillis();
            if (!pause) {
                update(elapsed);
            }
            render();
            ...
        }
    }
    ...
}
```

So if you press a first  time the pause key, the update of all objects will stop. pressing the pause key again will restart the update process.

## In out Framework

> TODO