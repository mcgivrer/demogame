# Renderer

Even if we were delegating rendering to `GameObject`, we need some utilities and commons way to display things. The `Renderer` will be the library and the orchestrator of the rendering process.

The Renderer, as the mains rendering service will take the job to display everything in the game.

It will have its own `GameObject` rendering list, and perform some sort activities to get object from back layer to front one. This is achieve by using an internal Layer structure where each object is assign to.

The `GameObject` has a `layer` attribute defining to which of the layer the object is linked to, and in this layer, the attribute `priority` defines is rendering sort order.

According to these attributes, a layers list is created each time an object is added or removed from the rendering object list.

*Let's visit this incrrredible part of code !*

## Class structure 

First, this class implements the `System` interface, because, yes, it is a system managed by the `SystemManager` (sic).

```java
public class Renderer implements System {
    private BufferedImage screenBuffer;
    
    public Renderer(Game g){ ... }
    public void initialize(Game g){ ... }
	public void render(Game g,double elapsed){ ... }	    
}
```

This system has some public method:

* `Renderer(Game)` : the Renderer system constructor, 

- `initialize(Game)` : inherited from the System interface,

- `render(Game,double)`: the main processing for this renderer: display everything graphics.

## Renderer

The Renderer constructor is first initialize the Window where the game will be displayed.

```java
Renderer(Game g){
    super(g);
	jf = createWindow(g);
	screenBuffer = new BufferedImage( ... );
}
```

First delegate to the inherited `AbstractSystem` constructor some internal initialization. Then create the Window (a JDK [JFrame](https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html "see JFrame documentation in the Open JDK") is used to host the game display), and finally, create the screen buffer where all rendering and drawing will be performed, before refreshing real window frame.

### initialize(Game)

What is the initialize method is setting ?

```java
public void initialize(Game g){
    mapRenderer = new MapRenderer();
	return 0;
}
```

### add(GameObject)

Adding an object ? Yes, this is an unattended complex operation.

```java
public void add(GameObject go) {
    disptachToLayer(go);
    if (!go.child.isEmpty()) {
        putAll(go.child);
    }
}
```

First, this new `GameObject` must be dispatched in the right `Layer` entry. The is the `dispatchToLayer()` job.

Based on the `GameObject#layer` and `GameObject#priority` attributes, the `GameObject` is attached to a virtual Layer, identified by the layer value. If it does not already exists in th e Renderer layers list, it is created, and the object is attached to.

```java
private void disptachToLayer(GameObject go) {
    Layer l;
    if (layers.get(go.layer) == null) {
        l = new Layer();
        l.index = go.layer;
        if (go.fixed) {
            l.fixed = true;
        }
        layers.put(go.layer, l);
    }
    l = layers.get(go.layer);
    l.objects.add(go);
    Collections.sort(l.objects, new Comparator<GameObject>() {
        public int compare(GameObject g1, GameObject g2) {
            return g1.layer < g2.layer 
                ? -1 
                : (g1.priority < g2.priority 
                     ? -1 
                       : 1);
        }
    });
}
```

After each adding operation, the list of object for the targeted layer is completely sorted.



> **WARNING**
>
> A very important thing is about the `GameObject#fixed` attribute. If one of the object in the layer has the `fixed` attribute set to `true`, all the `Layer` is connected to the `Camera` moves. The `fixed` attribute is particularly useful to render screen fixed things like the HUD, some text message, or anything attached to the camera moves.



Then, is the object is a parent one, all its `child` objects are dispatched with a `putAll()`.

### render(Game, double)

Let's explore the first level of complexity of the render method.

From a small flag `renderingPause`, the display refresh is depending on (see `setPause()` for details).

In one of the next chapter, you will discover the power of [`Scene`](./03-scene_and_manager_system "read about Scene and their management") and their management. let's say that this Scene have an active [`Camera`](./05-Camera "got and read about Camera").

To be able to smooth rendering of the really squared pixel, we activate the anti-aliasing [`RenderingHints`](https://docs.oracle.com/javase/8/docs/api/java/awt/RenderingHints.html "see what are rendering hints in the java jdk") on graphics but also on text. 

Then we clear the screen buffer with the most darker color we got: black.

and we will render all layered moving  objects according to the active camera position (the reason why the [`g.translate()`](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html#translate-double-double- "what is a Graphics2D#translate() operation ?") method are used.)

```java
public void render(Game dg, double elapsed){
    if (!renderingPause) {
			DebugInfo.debugFont = g.getFont().deriveFont(8.0f);

			Camera camera = dg.sceneManager.getCurrent().getActiveCamera();

			// activate Anti-aliasing for image and text rendering.
			g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

			// clear image
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dg.config.screenWidth, dg.config.screenHeight);
			for (Layer layer : layers.values()) {
				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(-camera.pos.x, -camera.pos.y);
				}
				renderObjects(dg, elapsed, g, camera, layer);
				// if a camera is set, use it.
				if (camera != null && !layer.fixed) {
					g.translate(camera.pos.x, camera.pos.y);
				}
			}

			// draw HUD
			dg.sceneManager.getCurrent().drawHUD(dg, this, g);
			// render image to real screen (applying scale factor)
			renderToScreen(dg, realFPS, realUPS);
		}
}
```

### renderObjects(...)

Where the magic happened !

the `renderObjects`w method is a private one. No way to call it from outside the Renderer.

The input attributes are a little bit numerous but all are necessary.

- The parent `game` Game
- the double  `elapsed` time since previous rendering (for future special rendering effects needs),
- The necessary [`Graphics2D`](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html "What is the Graphics2D java API and what is intend to ?")  `g` for this purely Java rendering implementation,
- The active `camera`, to correctly scope our rendering
- and the mandatory `layer` to render its own list of `GameObject`.

```java
private void renderObjects(Game game, double elapsed, Graphics2D g, Camera camera, Layer layer) {
		// draw all objects
		for (GameObject go : layer.objects) {
			if (go.enable && go.displayed) {
				if (go instanceof MapLevel) {
					// if MapLevel, delegates rendering operation to the MapRenderer.
					mapRenderer.render(game, g, (MapLevel) go, camera, elapsed);

				} else if (go instanceof TextObject) {
					TextObject to = (TextObject) go;
					drawText(game, g, to);

				} else if (go instanceof Light) {
					Light l = (Light) go;
					drawLight(game, g, l);

				} else if (go instanceof GameObject) {
					drawObject(game, g, go);
				}

				// if debug mode activated, draw debug info
				if (dg.config.debug > 2) {
					DebugInfo.displayCollisionTest(g, go);
					DebugInfo.display(g, go);
				}
			}
		}
	}
```

For one of the `layers` (some Renderer internal virtual group of `GameObject`  based on the `GameObject#layer` attribute) , parsing all objects, and according to `enable` and `displayed` flags, the `GameObject` is rendered.

The `renderObjects` method is delegating to the specialized  `drawXxxx(Game, Graphics2D, GameObject)` the draw processing of the object. It can a `MapLevel`, a `TextObject`, a `Light` or a basic `GameObject`.

In the particularly complex case of the `MapLevel` object, a specific renderer is provided, the `MapRenderer`. See the [`MapLevel`](09-maplevel "go and read quickly the MapLevel story") chapter for details.

> **NOTE**
> In a next release, this kind of object type based  processing will be implemented for all nature of `GameObject` child. An internal mechanism with delegate the rendering to a specialized renderer according to the `GameObject` child class.

And before processing the next object in the list,  if the graphical debug mode is activated (see the Config#debug attribute ) and debug level greater than 2, let's display fun debug information for the object.

#### drawText

*TODO*

#### drawLight

*TODO*

#### drawObject

*TODO*

