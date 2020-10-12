# Scene & Manager

A `Scene` is defining a gameplay or a step in the game. Every different screen type in a game is a Scene.

As a sample, we can enumerate the kind of Scenes composing a traditional 2D platform game:

- the title screen,
- the menu screen to choose to create a game party, or continue an existing one,
- the play screen, the game itself,
- the inventory screen where player manages his items,
- the pause screen, accessing some quick menu entries to resume or quit the party game.

I think you're got it ;)

A Scene interface will define a contract to an implementation.
As soon as you will get more than one Scene, you will need a manager to switch gracefully between them.

## Scene interface {#scene-interface}

The Scene interface is the ring to rules them all, a contract to switch easily from one Scene to another. 
To achieve this goal, the interface must define what are the necessary game operation like manage input, update the Scene, and some specific rendering processing.

```java
public interface Scene {

  String getName();

  void initialize(Game g);
  void load(Game g);
  boolean isLoaded();
  void onFocus(Game g);
  void lostFocus(Game g);
  void dispose(Game g);

  void input(Game g);
  void update(Game g, float elapsed);
  void render(Game g, Renderer r);

  void addObject(GameObject go);
  void addAllObject(Collection<GameObject> objects);
  void removeObject(GameObject go);
  void removeObject(String name);
  void removeFilteredObjects(String nameFilter);
}
```

Diving into the interface signature, you will recognize some of the methods coming from the Game class itself, to answer to the famous trio "input, update,render". And others are useful to manage the Scene life-cycle from instantiating (initialize, load and activate) to release it (dispose).

Each of those methods receive the parent Game object to access easily to all systems. In a next chapter we will cover how to access systems throw a dedicated object manager.

## AbstractScene

As we have already describe it, some methods processing from one Scene to another are common and standard. We won't ^re^code each time the same thing, so we are going to create an abstract class to support the Scene life-cycle management.

The base class of all Scene's implementations will provide operational mechanism to the Scene manager to initialize, switch, and release resources, but also to delegate the inputs, updates and renders to each implementation correspond to each Scene and game-play.

```java
public abstract class AbstractScene implements Scene{
	/*---- Attributes for a Scene ----*/
	protected Game game;
	protected String name;
	public Map<String, GameObject> objects = new ConcurrentHashMap<>();

	public AbstractScene() {...}
	public AbstractScene(Game g) {...}


	/*---- Scene Lifecycle management ----*/
	@Override
	public abstract void initialize(Game g);
	@Override
	public abstract void load(Game g);
	@Override
	public void activate(Game g) {...}
	@Override
	public void onFocus(Game g) {...}
	@Override
	public void lostFocus(Game g) {...}

	/*---- Processing Scene game ----*/
	@Override
	public abstract void input(Game g);
	@Override
	public abstract void update(Game g, float elapsed);
	@Override
	public abstract void render(Game g, Renderer r);
}
```

Those are mandatory attributes and methods.

2 groups are clearly identified:

1. Life-cycle management,
2. Scene game processing.

But to be easy to manage, we will need more operation around `GameObject` management, so let's add more methods :

```java
  /*---- Objects management ----*/
  public void addObject(GameObject go) {...}
  public void addAllObject(Collection<GameObject> objects) {...}
  public void removeObject(GameObject go) {...}
  public void removeObject(String name) {...}
  public void removeAllObjects(List<GameObject> objectsToBeRemoved) {...}
  public void removeFilteredObjects(String nameFilter) {...}
  public Camera getActiveCamera() {...}
  public Map<String, GameObject> getObjects() {...}
```

And to implement common behaviors on some user interaction, like intercepting key pressed event, we will need to implement some user key input listener. let's implement the `KeyListener` interface in our `AbstractScene`:

```java
public abstract class AbstractScene implements Scene, KeyListener{
  ...
  /*---- KeyListener implementation ----*/
  @Override
  public void keyTyped(KeyEvent e) {...}
  @Override
  public void keyPressed(KeyEvent e) {...}
  @Override
  public void keyReleased(KeyEvent e) {...}
}
```

## SceneManager{System}

As we are building our first `System` in our project, we have to set some basics (one more time).

A System is a kind of processor instantiated only one time for all the game. This is well known as a [Singleton](https://en.wikipedia.org/wiki/Singleton_pattern "from Wikipedia, what is a Singleton?" ), a common [Software Design Pattern](https://en.wikipedia.org/wiki/Software_design_pattern "go and learn about software design pattern on wikipedia").

We will cover this particular `SystemManager` in the next chapter, but what we only need to know right now is that the `System` is an *interface*, and our `SceneManager` must implements all the necessary contract to be compatible with this contract. 

As a System is the following interface :

```java
public interface System {
    String getName();
    int initialize(Game game);
    void dispose();
}
```

(see description in the next chapter [SystemManager](SystemManager))

Our `SceneManager`will need to manage a Scene lifecycle, and the delegation of operation from Game class to the Scene implementation to be activated on demand.

First thing first, lets build the first methods to load, initialize and activate a Scene.

```java
@Slf4j
public class SceneManager extends AbstractSystem {

    private Map<String, Scene> Scenes = new HashMap<>();
    private Scene current;

    public SceneManager(Game g) {
        super(g);
        load(g.config.ScenesPath);
    }
    public void loadFromFile(String path) { ... }

    public void activate(String s) { ... }

    @Override
    public String getName() {
        return SceneManager.class.getCanonicalName();
    }

    public int initialize(Game g) {
        log.debug("SceneManager system initialized");
        return 0;
    }

    public void startScene(Game g) {
        if (current != null && current.isLoaded()) {
            current.initialize(g);
            log.debug("{} Scene started", this.current.getName());
        }
    }

    @Override
    public void dispose() { }

    public void load(Game g) {
        current.load(g);
    }

    public void input(Game g) {
        current.input(g);
    }

    public void update(Game g, float elapsed) {
        current.update(g, elapsed);
    }

    public void render(Game g, Renderer r) {
        current.render(g, r);
    }

    public void dispose(Game g) {
        current.dispose(g);
    }

    public void release(Game g) {
        for (Scene s : Scenes.values()) {
            s.dispose(g);
        }
    }

    public Scene getCurrent() {return current;}
}
```

### Load Scenes

At `SceneManagerSystem` initialization, a list of configured Scenes must be prefetch into the Scenes internal cache. this is achieved by the `loadFromFile(String pathFile)` method.

This method is loading from a JSON file a list of  available Scenes implementation for your game. Here is the heart of your game, where the game structure is defined.

The file *game.json* at root of projet :

```json
{
  "defaultScene": "game",
  "scenes": {
    "game": "demo.scenes.DemoScene"
  }
}
```

The JSON structure is quite easy to understand. a list of "Scenes" in a map where `"game":"demo.Scenes.TestClass"`is one Scene. The "defaultScene" is setting the first Scene to be activated at game start.

#### loading ?

The `loadFromFile()` method is based on the *Gson* library:

```java
public void load(String path) {
  try {
    String gameScenes = ResourceManager.getString("/res/game.json");
    Gson gs = new Gson();
    ScenesMap ScenesMap = gs.fromJson(gameScenes, ScenesMap.class);
    for (Entry<String, String> SceneItem : ScenesMap.Scenes.entrySet()) {
       Class<Scene> cs = (Class<Scene>) Class.forName(SceneItem.getValue());
       Scene s = cs.newInstance();
       s.setGame(game);
       Scenes.put(SceneItem.getKey(), s);
       log.info("load Scene {}", SceneItem.getKey());
    }
    activate(ScenesMap.defaultScene);
  } catch (IllegalAccessException 
         | InstantiationException 
         | ClassNotFoundException e) {
    log.info("Unable to create class ", e);
  }
}
```

A simple trick is used here to load and access quickly the Json attributes values: A class ScenesMap is the exact reflect of the game.json file:

```java
public class ScenesMap {
  public String defaultScene = "";
  public Map<String, String> Scenes;
}
```

So after reading the json file, you can just access the `ScenesMap`  properties.

For this first implementation, we will just load corresponding class from class path, an store in a map the corresponding instance of each declared Scenes.

So, after loading the file, all Scenes have there own instance and then are ready to be activated.

#### Activate a Scene

The main purpose for `SceneManager` is to activate a specific Scene, this is accomplished by the activate (sic) method:

```java
public void activate(String s) {
  if(current!=null){
    current.lostFocus(game);
  }
  current = Scenes.get(s);
  if (!current.isLoaded()) {
    current.load(game);
    current.activate(game);
    log.debug("activate Scene {}", s);
  }
  current.onFocus(game);
}
```

The method's steps are the following:

1. if a  Scene is already activated, we need to notify the current Scene that it is going to be disabled. we first tell him that it lost focus.

2. we get the request Scene from the list of loaded Scene  and is Scene load operation has not been done, call the load method.

3. execute the specific `activate` operation on the Scene to be activated. 

4. finally tell the current activated step that it got focus.

But activating the Scene is not enough. You must initialize the active Scene to instantiate or set its own objects, ready to "play".

 

### A Test Scene

A Test Scene to test the `SystemManager`.

```java
public class TestScene extends AbstractScene {
    
    public TestScene(Game g) {
        super(g);
    }
	/*---- Scene Lifecycle Management ----*/
    public void initialize(Game g) {}
    public void load(Game g) {}
    public boolean isLoaded() {return true;}
    public void dispose(Game g) {}

    /*---- Game loop Scene's implementation ----*/
    public void input(Game g) {}
    public void update(Game g, float elapsed) {}
    public void render(Game g, Renderer r) {}
}
```

The first methods will `initialize()` all needed objects to be manage by this Scene.

So yes, this `TestScene` is doing nothing ! Its purpose is only to test the manager...  

Integrating the `SceneManagerSystem` with the `Game` class is the next step.

## SceneManager into the Game

The `SceneManager` must be initialized at game start. let's modify the Game class accordingly to this aim:

```java
public class Game {
  ...
  // Constructor
  public Game(String[] argc) {
    super();
    config = Config.analyzeArgc(argc);
  }
  // the run open the game   
  public void run() {
    initialize();
    loop();
    dispose();
    System.exit(0);
  }
  
  public void initialize() {
    SceneManager = new SceneManager(this);
  }
        
  private void loop() {
    SceneManager.startScene(this);
  ...
  	while (!exitRequest) {
	...
      SceneManager.input(this);
      SceneManager.update(this, elapsed);
      SceneManager.render(this, renderer);
	...
    }
  }
  ...
  public static void main(String[] argc) {
    Game dg = new Game(argc);
    dg.run();
  }
}
```

In the initialization, we create the `SceneManager` instance, and then initialize it. And from the `loop()`, delegates all processing to Scene:

```java
while (!exitRequest) {
  ...
  SceneManager.input(this);
  SceneManager.update(this, elapsed);
  SceneManager.render(this, renderer);
  ...
}
```

We've just created a new way to make a greater game ;)

Next step is how to manage render those `GameObject`.



