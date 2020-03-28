

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
    List<InputHandlerListener> keylisteners = new ArrayList<>();
    
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

```java
public class SampleInputHandler implements InputHandlerListener {
    
}
```



parsing the `keylisteners` list, we are going to call each `keyPressed()` methods.

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





## Get mouse events

To illustrate the use of mouse events, I propose to implement in our game engine a mouse cursor that can be used to interact with screen.

So let's implement all the needed mouse listeners to interact with a mouse, and then, try to create a new `GameObject`, the `MouseCursor`.

### The mouse listeners

In the JDK all mouse events are split between multiple listeners:

- `MouseListener` mainly to detect Mouse click,
- `MouseMotionListener` to detect mouse moves,
- ans the `MouseWheelListener` to interact with the mouse wheel.

We must implement all of those event listeners to capture all mouse events.

We need to enhance our InputHandler with those capabilities:

```java
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    ...
}
```

Ok, and now we have to add new attributes to manage all those new events.

We also add new Event Queue to be able to process those events on demand.



## Get game device events

todo