# Painter

Painter is a Java application made using Java Swing that allows users to draw on a canvas. The application has a toolbar with multiple different features that allows the user to do many different drawing functions with ease.

## Build the application

Compile the application and package it with:

```sh
mkdir -p build/classes && javac -d build/classes *.java && jar cfe Painter.jar Launch -C build/classes .
```

Run the .jar file with:

```sh
java -jar Painter.jar
```