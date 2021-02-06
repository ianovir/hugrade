# Hugrade
Graph designer in java FX.

## Intro
Hugrade is a very simple software that allows to draw and analyze bidirected weighted graphs from scratch. 

|IDE|Build|Java|JavaFX|
|:---:|:---:|:---:|:---:|
| IntelliJ IDEA |  gradle | 15.0.1+ |15|

### Features
* User-friendly: nodes and edge creation/configuration with few clicks
  * negative edges
  * bidirected edges
* Export formats:
  * GML (Graph Modelling Language)
  * CSV (transition matrix)
  * TXT (transition matrix)
  * XGP (hugrade native)  
* Path solvers:
  * A*
  * Bellman-Ford
  * Dijkstra
* Graph normalization (partially or completely)
* Absorbing states probability (Markov chain)

## Wiki
Take a look at the dedicated Wiki page on Github.

## Run Hugrade
You have three ways to launch Hugrade:
1. Launch it from IntelliJ IDEA.
2. Build and launch lightweight executable .jar.
3. Build and launch a (fat) runtime image.

Take a look at the [release page](https://github.com/ianovir/hugrade/releases) for pre-built releases.

### 1. Launch from IDE
From IntelliJ, execute the `gradlew run` task.

### 2. Lightweight executable .jar
To build an executable, lightweight `.jar`, execute the `gradlew jar` task: the output will be located under the
`{project_root}/build/libs` directory. 

To launch the `.jar` you need java and [javafx SDK](https://gluonhq.com/products/javafx/), then use the following parameters for the VM:

```
java --module-path "path\to\javafx\lib" --add-modules=javafx.controls,javafx.fxml -jar hugrade-1.0.0.jar
```

###  3. Runtime image
The main module has been configured to be built to a runtime image using the (badass) 
[org.beryx.jlink](hhttps://badass-jlink-plugin.beryx.org/releases/latest/) plugin.
Just execute the `gradlew jlink` task, the build image will be located under `{project_root}/build/image` directory.
Launch the `/image/bin/main` file to run Hugrade.

To generate custom JDK+JFX images for different platforms, please take a look [here](https://openjfx.io/openjfx-docs/). 

## Disclaimer
This code is provided "as is", some features are incomplete or missing. The software can present bugs and can be unstable.
Feel free to contribute to this project, make your changes and pull requests.

# License
2021 Sebastiano Campisi - [ianovir.com](https://ianovir.com).
Read LICENSE file for more details.