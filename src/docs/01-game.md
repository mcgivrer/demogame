# The Game class

The Game is the entry point of our game. The main class is a frmework for all the game processing, from create window to animate all those beautiful sprites. Let's dive into this thing ! 

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
public class Game {
  private boolean exit=false;
  private long fps=60;
  
  public Game(String[] argc){
    argParser(argc);
  }
  
  public void initialize(){...}
  
  public void run(){
    initialize();
    loop();
  }

  public loop(){
    float elapsed=0;
    long startTime=System.currentMillis();
    long lastTime=startTime;
    while(!exit){
      startTime=System.currentMillis();
      input();
      update(elapsed);
      elapsed=startTime-lastTime;
      wait(fps,elapsed);
      render();
      lastTime=startTime;
    }
  }
  private void input(){...}
  private void update(float elapsed){}
  private void render(){...}

  private void argParser(String[] argc){...} 
  
  public static void main(String[] argc){
    Game dm = new Game(argc);
    rm.run();
  }
}
```

The structure of this class is a basic for any game you would like to create.

First initialize things, then start looping.

With such simple class we will incrementally create our platform game.