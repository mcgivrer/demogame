# DemoGame

[![demogame](https://api.travis-ci.org/mcgivrer/demogame.svg?branch=develop)](https://travis-ci.org/mcgivrer/demogame "visit Travis-CI demogame project build page") [![Codacy Badge](https://api.codacy.com/project/badge/Grade/631ddda85cc24966bd29b8c1fcba10c5)](https://www.codacy.com/manual/SnapGames/demogame?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mcgivrer/demogame&amp;utm_campaign=Badge_Grade "visit Codacy demogame project quality page") [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fmcgivrer%2Fdemogame.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fmcgivrer%2Fdemogame?ref=badge_shield) [![Known Vulnerabilities](https://snyk.io//test/github/mcgivrer/demogame/badge.svg?targetFile=pom.xml)](https://snyk.io//test/github/mcgivrer/demogame?targetFile=pom.xml) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![framagit pipeline](https://img.shields.io/gitlab/pipeline/mcgivrer/demogame?gitlab_url=https%3A%2F%2Fframagit.org%2F&style=flat-square)](https://framagit.org/mcgivrer/demogame "visit framagit project")


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


## Some screenshot


![Screenshot of the old demo](src/docs/images/screen-1.png "An old view of the prototype !")

*figure 1 - the old prototype*

![Screenshot of the core](src/docs/images/screen-2.png "A Good view of the latest prototype !")

*figure 2 - the latest version with a HUD*

McG.
