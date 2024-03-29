# DemoGame

## Badges

[![pipeline status](https://framagit.org/mcgivrer/demogame/badges/develop/pipeline.svg)](https://framagit.org/mcgivrer/demogame/commits/develop) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fmcgivrer%2Fdemogame.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fmcgivrer%2Fdemogame?ref=badge_shield) [![Gitter](https://img.shields.io/gitter/room/mcgivrer/demogame?color=%23A57&logo=gitter&logoColor=%23CCC)](https://gitter.im/room/snapgames/demogame.git)  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 

## Description

This is a simple java game demonstration to play with ABC of game development. introducing some of the basics like the game loop, some system & resources management, and GameObject !

Let's dive into some simple Java ([JDK8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) "see and download the necessary JDK") code to create your first platform game.

To be able to work on and build this project, you will need 2 more things:

1. Apache maven build tool, see the [maven download page](https://maven.apache.org/download.cgi "Download the maven release according to your OS/preferences")
2. An IDE to edit and debug ths project. My own heart balances between [IntelliJ](https://www.jetbrains.com/idea/download/ "Download IntelliJ Community edition") and [Eclipse](https://www.eclipse.org/downloads/packages/ "Download the Eclipse fundation IDE").

## Build

Build with :

    $> mvn clean install

## execute ?

Play with :

    $> mvn exec:java

Or after a build, just execute:

    $> java -jar target/DemoGame-0.0.1-SNAPSHOT-shaded.jar

## latest

- start adding Lua scripting capability
- add Lights
- add items collect and display
- update collision between `GameObject` and `TileMap`.


## Some screenshot

![Screenshot of the old demo](docs/pages/images/screen-1.png "An old view of the prototype !")

*figure 1 - the old prototype*

![Screenshot of the core](docs/pages/images/screen-2.png "A Good view of the latest prototype !")

*figure 2 - the latest version with a HUD*

![Tilemap debugging activated](docs/pages/images/screen-3.png "Tilemap debugging activated")

*figure 3 - the same display with tilemap debug activated*

![A lot of new things like lights !](docs/pages/images/screen-4.png "Adding lights and text")

*figure 4 - A lot of new things like lights, Gothic Score, Items and Text!*

![One other just for fun](docs/pages/images/screen-5.png "One other just for fun")

*figure 5 - One other just for fun*

![So much debug information !](docs/pages/images/screen-7.png "So much debug information !")

*figure 6 - So much debug information !*

McG.
