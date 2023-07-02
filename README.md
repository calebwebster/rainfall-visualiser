# Rainfall Visualiser

An application to visualize rainfall datasets from Australia's BOM website

---

![](https://raw.githubusercontent.com/calebwebster/rainfall-visualiser/main/screenshot.png)

## Installation

- Install [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- Download `rainfall-visualiser-x.x.x.jar`, `openjfx-x.x.x.zip`, and `rainfall-visualiser` (`rainfall-visualiser.bat` on Windows) from [latest release](https://github.com/calebwebster/rainfall-visualiser/releases/latest)
- Extract `javafx-sdk-x.x.x.zip` (inside `openjfx-x.x.x.zip`) into the same folder as `rainfall-visualiser-x.x.x.jar`
- Use `rainfall-visualiser` or `rainfall-visualiser.bat` (in the same directory) to run the application
- On Linux, add `rainfall-visualiser` to path
- On Windows, create a shortcut for `rainfall-visualiser.bat` 

You may download rainfall datasets from the BOM website [here](http://www.bom.gov.au/climate/data/index.shtml).

## Building from source

Requires [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher.

To run, rainfall-visualiser depends on the following libraries, which are automatically downloaded through Maven:

- `com.opencsv:opencsv@5.7.1`
- `com.apache.commons:commons-lang3@3.12.0`
- `org.openjfx:javafx-controls@17.0.7`

On top of this, rainfall-visualiser depends on the JavaFX SDK version 17.0.7 (LTS), which can be downloaded from [here](https://gluonhq.com/products/javafx/).

Place the extracted JavaFX SDK folder anywhere you like. You will need to provide a path to it at runtime.

To compile the application with dependencies, use the [Maven](https://maven.apache.org/download.cgi) build tool:

```mvn install```

You can run the application like this (just one of the many ways):

```java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls -jar .\target\rainfall-visualiser-1.0.0.jar```

Good luck!
 
