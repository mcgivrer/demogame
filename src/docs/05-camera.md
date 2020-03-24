# Camera

One of the concept in a game is the `Camera`.  Like in movies, the player want to follow its character on the screen. 
The way we will implement this behavior will consist in simulate a movie camera following the player's character moves.

But to make this more realistic, the camera will follow the player a small delay , as if an elastic was joining the character and the Camera. 

So the camera will obey to this simple math formula:

`pos += pos + (pos-target.pos)*tween*t`

Where `pos` is the camera position (as a 2D vector) and target the object to follow, so `target.pos` is the position 2d vector for the target to be followed, like the player character.
`tween` is the elastic factor for this camera. the more the fact reach 1 the faster the camera is to follow the target. the more the factor is near 0, the less the camera will be reactive to target movement.

The `Camera` will be a [`GameObject`](GameObject) like the other, but some `update(float)` method a little bit different:

```java
class Camera extends GameObject{
  public GameObject target;
  public float tween;

  ...
  void update(float elapsed){
    x += x + (x - target.x) * tween * elapsed;
    y += y + (y - target.y) * tween * elapsed;
  }
  ...

}
```

the Game class must be updated:

```Java
class Game{
  ...
  public Camera camera;
  ...
  void Camera(float x, float y, target o, float tween){
    x=x;
    y=y;
    target=target;
    tween=tween;
  }
  ...
  public void render(Graphics2D r){
    if(camera!=null){
      g.translate(-camera.x, -camera.y);
    }

    ... rendering process...
 
    if(camera!=null){
      g.translate(camera.x, camera.y);
    }
  }
}
```



