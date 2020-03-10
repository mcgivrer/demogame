The project is based on the maven structure:

```
DemoGame
|_ src
|   |_ main
|   |   |_ java
|   |   |_ docs
|   |   |_ resources
|   |_ test
|      |_ java
|      |_ resources
|_ .travis.yml
|_ .gitignore
|_ CODE_OF_CONDUCT.md
|_ LICENSE
|_ pom.xml
|_ README.md
```

To build the project:

    $> mvn clean install

To run the project

    $> mvn exec:java

or you can run with the java command:

    $> java -jar target/DemoGame-0.0.1-SNAPSHOT-shaded.jar

and then a great window open !

![A fantastic screenshot](https://raw.githubusercontent.com/mcgivrer/demogame/develop/src/docs/images/screen-2.png)

_figure 1 - the fantastic screenshot_

## Dependencies

Just to be clear, only Java is not the only way in our project, I added some small libraries to perform 3 things that Java offer but without the selected libraries ease of using:

- facilitating Pojo getter/setters and Logging output,
- converting Json<=>Pojo
- logging information for mainly debug purpose.

#### PoJo easily

For the first use case, I propose to use the `lombok` library to simplify PoJo writing:
```java
@Data
@NoArgsConstructor
@ToString
class MyPojo{
  String name;
  int is;
}
```

The qualifiers `@Data` will authomaticaly at compilation time add setters and getters to all attributes, `@NoArgConstructor` will generate the default class constructor, while `@ToString` will build a default implementation of the toString() Java converter.

For the second use case, the Google `gson` library, and for the second, rely on the `slf4j-simple`.

#### converting Pojo

```java
Gson gson= new Gson();
MyPojo mp=gson.fromJson("myjsonstructure", MyPojo.class);
```

#### Logging

```java

```