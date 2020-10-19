# Introduction

Demo Game is a small project to explore the java game development on the java AWT standard library.
Adding some just necessary outer libs, it will implement a 2D Platform engine to create Game.

Most of the game is out of configuration and some character and game-play behaviors must be coded with help of Java.

![DemoGame screenshot](https://raw.githubusercontent.com/mcgivrer/demogame/develop/src/docs/images/screen-2.png)

As a short introduction to what is going to happen here, a beautiful and complex UML class diagram is the best thing to have.


<figure>
<div styles="hidden">

```plantuml resources/diagrams/class-diagram-overview.png
@startuml
class Game{
 +objects:Map<String,GameObject>
 +title:String
 +run()
 -initialize()
 -input()
 -update(float elapsed)
 -render(Renderer r)
 -loop()
 -dispose()

}
class GameObject{
 -id:long
 +name:String
 +x:double
 +y:double
 +dx:double
 +dy:double
 +animations:Map<String,Animation>
 +image:BufferedImage
 +input():void
 +render(Renderer r):void
 +update(double elapsed):void
}
class Animation{
 +frameImages:List<BufferedImage>
 +frameTimes:List<Integer>
 +elapsed:double
 +frameIndex:int
}
class AbstractState implements State{
 +getName():String
 +objects:Map<String,GameObject>
}
class SystemManager{
 -systems:Map<Class<?>,System>
}
class SoundSystem implements System{}
class Renderer implements System{
  -renderingPipeline:List<GameObject>
  -lights:List<Light>
  -layers:List<Layer> 
}
class Layer{
  -index:int
  -objects:List<GameObject>
}
class Light extends GameObject {
  -lightType:LightType
  -target:GameObject
}
class Camera extends GameObject {
  -tween:double
  -target:GameObject
}
class TextObject extends GameObject {
  -text:String
}
class InputHandler implements System{}
class ResourceManager implements System{}
class StateManager implements System{
 - states:Map<String,State>
 - current:State
}
SystemManager -- Game:sysMan
Renderer -- Game:rendererSystem
Render "1"--* Layer:layers
InputHandler -- Game:inputHandler
ResourceManager -- Game:resourceManager
StateManager -- Game:stateManager
SoundSystem -- Game:soundSystem
SoundSystem -- SoundClip:sounds
StateManager "1"--* AbstractState:states
Game "1"--* GameObject :objects
AbstractState "1"--* GameObject:objects
GameObject "1"--* Animation:animations
@enduml

```
<figurecaption>This is a draft of the global classes overview. It will be refined during the development process.</figurecaption>
</figure>

</div>

![A global class diagram overview](diagrams/class-diagram-overview.png)

