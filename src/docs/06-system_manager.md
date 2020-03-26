# System Manager

System manager is the manager of all game systems and is the first System.

A `System` is a singleton service to be used by multiple tenants and will provide one spécifique domain. The `StateManagerSystem` is one those services, the `SoundSystem` or the `ResourceManagerSystem` will be (soon) also other services.

The `SystemManager` itself is one of them, but having the specific role of being there parent.

From the `Game` class, `SystemManager` instance will be the first to be instantiated.
And then, adding other system instance to the manager, there will be all available to any other system.

The ease of this approach is that at any time, you can add or.remove a service, and dispose there resources.

## The System

A system is an interface to provide minimalistic method to manage the system lifecycle.

```java
interface System {
    String getName();
    int initialize(Game game);
    void dispose();
}
```

The `initialize` method will be called at system initialization to sart the System.
The `getName` will be used to retrieve name of the System. This is the logged name, bt also the name used a key in the System registry.
The latest `dispose` method will be called in the endof system lifecycle, just before ending of the system need, certainly with the end of the game execution.

## The manager

the main class for the System is where all is handled, the SystemManager.

This will be the API to add, start or remove a System to the manager.

```java
public class SystemManager {
  private static Map<Class<?>, AbstractSystem> systems = new HashMap<>();
  private Game game;

  private SystemManager(Game game) {...}

  public static SystemManager initialize(Game game) {...}

  @SuppressWarnings("unchecked")
  public static <T extends AbstractSystem> T get(Class<T> systemName) {...}

  public void add(AbstractSystem s) {...}

  @SuppressWarnings("unchecked")
  public <T extends AbstractSystem> T getSystem(Class<T> systemName) {...}

  public void dispose() {...}
}

```

A first `systems` attributes will host all initialized systems, ready to be served.
The second `game` attribute is the parent Game instance attwhaed with.

Then, tha main contrusctor is just a way to crete the instance with the parent Game.
The `initialize` method is the one called for the first time.

```java
  public static SystemManager initialize(Game game) {
    return new SystemManager(game);
  }
```

And the add is used to add a System to the manager.

```java
  public void add(AbstractSystem s) {
    if (s != null) {
      Class<? extends AbstractSystem> systemType = s.getClass();
      systems.put(systemType, s);
      log.debug("Add system {}", s.getName());
      s.initialize(game);
      log.debug("System {} initialized.", s.getName());
    }
  }
```

The two following methods will retriee an already set system, statically or not:

```java
  @SuppressWarnings("unchecked")
  public static <T extends AbstractSystem> T get(Class<T> systemName) {
    return (T) systems.get(systemName);
  }
  @SuppressWarnings("unchecked")
  public <T extends AbstractSystem> T getSystem(Class<T> systemName) {
    log.debug("retrieve system {}", systemName.getCanonicalName());
    return (T) systems.get(systemName);
  }
```

And finaly, the laytest one will be used ti free all system and their necessary allocated resources:

```java
  public void dispose() {
    for (System s : systems.values()) {
      log.debug("disposing system {}", s.getName());
      s.dispose();
    }
  }
```

## Integration with Game

The SystemManager is instantiated from the Game at initialization time :

```java
public class Game {
    ...

    public void initialize() {

    // start System Manager
        sysMan = SystemManager.initialize(this);
        ...
    }
    ...
}
```

And a end of game, jsut before leaving the game main progrm, free the Syste and their resources:

```java
public class Game {
    ...
  private void dispose() {
    sysMan.dispose();
  }
    ...
}
```

And Here we are !

We can add our first Systemn, the StateManagerSystem to the stack :

```java
public class Game {
    ...
    public void initialize() {
    // start System Manager
        sysMan = SystemManager.initialize(this);
    // start State manager system
    stateManager = new StateManager(this);
    sysMan.add(stateManager);
        ...
    }
    ...
}
```

And from anywhere from code, you can gather the added system from the SystemManager :

```java
  StateManagerSystem  sms = SystemManager.get(StateManagerSystem.class);
```

That's all for the System Manager chapter.



