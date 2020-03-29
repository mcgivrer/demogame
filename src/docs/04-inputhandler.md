

# Input Handler

The good way to manage all the input from users, is to delegate there management to a dedicated service.

This is why we start implementing a `InputHandler` service to capture keys input and in a near future, mouse and or touch events, and in a last step, gamepad and joystick input.

This new handler must support multiple sources of event and multiple connectors to processors, we will call those latest ones as listeners.

 First things first, `InputHandler` !

## Get keys events

The JDK provides some interesting interface to get input events. `KeyListener` is one of those.  So we are going to declare our `InputHandler` inheriting `KeyListener`. 

The goal is to maintain a buffer of boolean representing each keys from keyboard. on key pressed event, we set the corresponding boolean to true, and on key released event, we set corresponding boolean to false. This way , we keep alive an exact reflect of the keyboard status.

Then to get the current state of one key, we request the buffer for the corresponding key adn return the current boolean value.

```java
public class InputHandler implements KeyListener{
	public void keyPressed(KeyEvent e){ ... }
	public void keyReleased(KeyEvent e){ ... }
	public void keyTyped(KeyEvent e){ ... }
}
```

As seen in a previous chapter  about [System Manager], we need to inherit from another interface to make it a `GameSystem` to be managed by the `SystemManager`.

```java
public class InputHandler extends AbstractGameSystem implements KeyListener{
    boolean[] keys;
    boolean[] prevKeys;
    
    public String getName(){
        return this.getClass().getName();
    }
    
    public void initialize(Sample game){
        keys = new boolean[65535];
        prevKeys = new boolean[65535]
    }
	public void keyPressed(KeyEvent e){
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = true;
    }
	public void keyReleased(KeyEvent e){
        prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
        keys[e.getKeyCode()] = false;
    }
	public void keyTyped(KeyEvent e){
        // Nothing now
    }
}
```

But we need to dispatch the keys event not only to the input handler, we want to broadcast those to other service.  So we are going to add a registering capability.

```java
public class InputHandler extends AbstractGameSystem implements KeyListener{
    boolean[] keys;
    boolean[] prevKeys;
    List<KeyListener> keylisteners = new ArrayList<>();
    
    ...
    
    public void register(KeyListener kl){
        keyListeners.add(kl);
    }
    ...
}
```

And we need to modify the `keyPressed` and `keyReleased` event processor to dispatch the `KeyEvent`.

To achieve this for not only keys but also future mouse and game controller, we need to define a new interface grouping the JDK ones.

```java
public interface InputHandlerListener extends KeyListener{
	public void input(InputHandler il);    
}
```

This new interface must be inherited by all class which need to process input events in our future game.

Parsing the `keylisteners` list, we are going to call each `keyPressed()` methods.

```java
public void keyPressed(KeyEvent e){
    prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
    keys[e.getKeyCode()] = true;
    for(KeyListener kl:keyListeners){
        kl.keyPressed(e);
    }
}
```

And now the same processing but for the `keyReleased` event.

```java
public void keyReleased(KeyEvent e){
    prevKeys[e.getKeyCode()] = keys[e.getKeyCode()];
    keys[e.getKeyCode()] = false;
    for(KeyListener kl:keyListeners){
        kl.keyReleased(e);
    }
}
```

okay ! we now have a first step in our `InputHandler` implementation quest.

Now, what we have to do is to register our `SampleInputHandler` game to the `InputHandler`.



```java
public class SampleInputHandler implements InputHandlerListener {
    ...
        
    public void initialize() {
        gsm = GameSystemManager.initialize(this);

        InputHandler ih = new InputHandler(this);
        // add this new GameSystem to the manager
        gsm.add(ih);
        ih.register(this);
        frame.addKeyListener(ih);
        ...
    }
        
    ...
}
```

And we must satisfy the interface contract:

```java
public void input(InputHandler ih) {
    final List<String> excludedObjects = Arrays.asList("player");

    GameObject go = objects.get("player");

    if (ih.getKey(KeyEvent.VK_UP)) {
        go.dy = (go.dy > -go.maxD ? go.dy - 1 : go.dy);
    }
    if (ih.getKey(KeyEvent.VK_DOWN)) {
        go.dy = (go.dy < go.maxD ? go.dy + 1 : go.dy);
    }
    if (ih.getKey(KeyEvent.VK_LEFT)) {
        go.dx = (go.dx > -go.maxD ? go.dx - 1 : go.dx);
    }
    if (ih.getKey(KeyEvent.VK_RIGHT)) {
        go.dx = (go.dx < go.maxD ? go.dx + 1 : go.dx);
    }
    if (ih.getKey(KeyEvent.VK_SPACE)) {
        // Break the first object of the objects map.
        go.dx = 0;
        go.dy = 0;
        go.color = Color.BLUE;
    }
    if (ih.getKey(KeyEvent.VK_R)) {
        reshuffleVelocity(excludedObjects);
    }
}
```



> **INFO**
> maybe you've noticed the method `reshuffleVedlocity()` receiving a list of String.  This method will randomize all object velocity but excluded objects list.

And we need to implements the KeyListener interface for the KeyReleased event, to process global commands

```java
@Override
public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            exit = true;
            break;
        case KeyEvent.VK_D:
            debug = (debug < 5 ? debug + 1 : 0);
            break;
        case KeyEvent.VK_P:
        case KeyEvent.VK_PAUSE:
            pause = !pause;
            break;
        default:
            break;
    }
}
```

Then running the sample again will serve the same "sauce" !

## Get the mouse events

To illustrate the use of mouse events, I propose to implement in our game engine a mouse cursor that can be used to interact with screen.

So let's implement all the needed mouse listeners to interact with a mouse, and then, try to create a new `GameObject`, the `MouseCursor`.

### The mouse listeners

In the JDK all mouse events are split between multiple listeners:

- `MouseListener` mainly to detect Mouse click,
- `MouseMotionListener` to detect mouse moves,
- ans the `MouseWheelListener` to interact with the mouse wheel.

We must implement all of those event listeners to capture all mouse events.

We need to enhance our `InputHandler` with those capabilities:

```java
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    ...
}
```

And now we have to add new attributes to manage all those new events.

```java
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    ...
    private boolean[] mouseButtons;
    private double mouseWheelRotation=0;
    private double mouseX = 0;
    private double mouseY = 0;
    ...
}
```

At initialization, we need to detect the number of button supported by the connected mouse device. this is delegated to the [`MouseInfo`](https://docs.oracle.com/javase/8/docs/api/java/awt/MouseInfo.html "what is the MouseInfo from JDK ?") class provided by the JDK:

```java
public int initialize(Sample game){
	...
	int mouseNumberOfButtons = MouseInfo.getNumberOfButtons();
	mouseButtons = new boolean[mouseNumberOfButtons];
    ...
    return 0;
}

```

And feeding those attributes will need to implement all of  the mouse events methods:

```java
@Override
public void mouseClicked(MouseEvent e) {
    mouseButtons[e.getButton()] = true;
    mouseX = e.getX();
    mouseY = e.getY();
}

@Override
public void mouseEntered(MouseEvent e) {}
@Override
public void mouseExited(MouseEvent e) {}

@Override
public void mousePressed(MouseEvent e) {
    mouseButtons[e.getButton()] = true;
}

@Override
public void mouseReleased(MouseEvent e) {
    mouseButtons[e.getButton()] = false;
}
@Override
public void mouseDragged(MouseEvent e) {}

@Override
public void mouseMoved(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
}

@Override
public void mouseWheelMoved(MouseWheelEvent e) {
    mouseWheelRotation = e.getWheelRotation();
}
```

3 of those events are interesting for our game:

- `mouseClicked()` to be able to detect what has been clicked,
- `mouseMoved()` to know the mouse position,
- `mouseWheelMoved()` the value of the mouse wheel scroll value.

To test that new mouse events,  we need a mouse cursor, and this `MouseCursor` object will be an extension of the well known `GameObject` ! But without the `GameObject#update()`, we need to override this method.

```java
public class MouseCursor extends GameObject{
    public MouseCursor(String name){
        super(name);
    }
    @Override
    public void draw(SampleGameObject ga, Graphics2D g) {
        g.setColor(color);
        g.drawLine((int)(x-(width/2)),(int)(y),(int)(x+(width/2)),(int)(y+height));
        g.drawLine((int)(x),(int)(y-(height/2)),(int)(x+width),(int)(y+(height/2)));
    }
    @Override
    public void update(SampleGameObject ga, double elapsed) {}
}
```

Add an instance of this object to our `SampleInputHandler`:

```java
public void load() {
    ...
    MouseCursor mCursor = new MouseCursor("mouse_cursor");
    mCursor.color = Color.WHITE;
    mCursor.width = 16;
    mCursor.height = 16;
    objects.put(mCursor.name, mCursor);
    ...
}
```

And at the `input()` function, just update the `MouseCursor` position according to the mouse `InputHandler` `mouseX` and `mouseY`.

```java
public void input(InputHandler ih) {
	...
    MouseCursor m = (MouseCursor) objects.get("mouse_cursor");
    m.x = ih.getMouseX() / scale;
    m.y = ih.getMouseY() / scale;
    ...
}
```

And as the mouse cursor must stay centered on the screen it needs to follow the camera moves. Let's update the mouse cursor position :

```java
public void update(Sample ga, double elapsed) {
    offsetX = ga.getActiveCamera().x;
    offsetY = ga.getActiveCamera().y;
}
```

And now `MouseCursor` and `Camera` are moving as a duo.



## Complex input behaviors

To be able to detect more complex input interpretation, we also add new Event Queue to be able to process those events on demand.

Add a buffer to store those event, we try to use a `Queue`;  let's back into the `initialize()`

```java
public int initialize(Sample game){
    ...
    events = new LinkedBlockingQueue<InputEvent>(100);
    ...
}
```

And now, we just have to publish the event to the queue, but as it's a limit sized queue, we need to keep only the necessary last events. We create a new function to manage this:

```java
private void pushEvent(InputEvent e) {
    if(events.size()==100){
        // Remove the oldest element
        events.poll();
    }
    events.add(e);
}
```



## Get game device events

todo