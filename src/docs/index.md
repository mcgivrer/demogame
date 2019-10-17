# core.Game

## Introduction

the core.Game project is a starter for discovering Game development with the java language and on the basic JDK proposed features.

The small tutorial will drive you to the build of an ABC 2D platform game with some reusable and extensible principles, where you will be able to create new level and new game play, from scratch to a minimum but operational framework.

## L for Loop

The core routine of a java game is a loop. 

This main loop will achieve multiple goals. 

1. capture the control input from the player, which keys are pressed, which are released, where is the mouse cursor, all those input will be useful to animate or move all the characters in that game. 

2. The first one consists in updating all the entities managed by the game, a player character, an enemy, a small item to be collected, all those little people need to be maintain and updated according to internal and player actions.

3. draw the graphical parts of the game. The updated items must be (or not, depending on their visibility) displayed on screen, and animated according to some rules.

And finally, restart at point 1, until the game end of the player clearly request to exit from the game.

This Loop can be written as this synthetic algorithm :

```java
while(!exit){
  input();
  update(time);
  render();
} 
```

In a well formed java class, let's name it `core.Game`, we will create those methods:


```java
public class core.Game {
  private boolean exit=false;
  
  public core.Game(){
  }
  
  public void initialize(){}
  
  public void run(){
    initialize();
    loop();
  }

  public loop(){
    float elapsed=0;
    while(!exit){
      input();
      update(elapsed);
      render();
    }
  }
  public void input(){}
  public void update(float elapsed){}
  public void render(){}
  
  public static void main(String[] argc){
    core.Game dm = new core.Game();
    rm.run();
  }
}
```

the structure of this class is a a basic for any game you would like to create.

First initialize things, then start looping.

With such simple class we will incrementally create our platform game.

## core.object.GameObject

As introduced before, I talk about some entities managed by the game. This where the `core.object.GameObject` is going to play.  

Any entity manage by the game,  the player character, the NPC (non playable characters), the score, the life, the all background display are objects in a game. The `core.object.GameObject` is going to be the core matter of the `core.Game`.

To be able to draw anything on screen, we will need for each object , a position, a size. We would need some other properties like an image of a color, a shape (square, circle, rectangle), and to be animated more new attributes we will discover later.

So, to start with a sustainable thing, create our class:

```java
public class core.object.GameObject {
	public String name;
	public float x,y;
	public float width,height;
	public Color color;
	public Color core.object.GameObjectType type;
	public Map<String,Object> attributes;
}
```

Manage our objects will be easier by adding a name and some free attributes map.

So, our core.Game class will be enhance with some objects:

```java
public class core.Game {
  ...
  
  private Map<String,core.object.GameObject> objects;
  
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

## Start displaying things

We now have some solid basis to create a game and manage objects, but we did not have display anything.

Let's add some graphics component to the game.

