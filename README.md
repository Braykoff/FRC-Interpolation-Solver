# FRC Interpolation Solver
Calculates a line based on a given set of points to allow interpolation and extrapolation of the data. The data can also be graphed. For usage in the First Robotics Competition.

---

## Implementation
Create an `InterpolationSolver` object with your X and Y data, and specify the [polynomial degree](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). Most applications will be best with a linear line (degree 1) or quadratic line (degree 2)
```java
// Imports
import frc.robot.InterpolationSolver.InterpolationSolver;

double[] x = {1, 2, 3, 4, 5}; // X Points
double[] y = {1, 4, 10, 15, 23}; // Y Points

InterpolationSolver solver = new InterpolationSolver(x, y, 2);
```
For better code readability, you can also specify the degree like this:
```java
// Imports
import frc.robot.InterpolationSolver.InterpolationSolver.LineType; // Add this import

...

InterpolationSolver solver = new InterpolationSolver(x, y, LineType.QUADRATIC);
```

### Adding Range
A minimum and/or maximum value can be specified in the constructor
```java
InterpolationSolver solver = new InterpolationSolver(x, y, 2, 3.0, 20.0); // minimum value: 3.0, maximum value: 20.0
```
Also, you can specify only one by leaving one NaN
```java
InterpolationSolver solver = new InterpolationSolver(x, y, 2, 3.0, Double.NaN); // minimum value: 3.0, no maximum value
```
The range is also sometimes referred to as 'bounds'.

### Solving
The `solve` method takes a double value for X, and returns the Y prediction. If the graph is enabled, it will also update it.
```
Double y = solver.solve(10.0);
```

### Get Equation
For debugging and testing purposes, it is sometimes needed to get the equation being used. The `toString()` method will return this, along with the [coefficent of determination (r^2)](https://en.wikipedia.org/wiki/Coefficient_of_determination)
```java
String equation = solver.toString();
```

## Graphing
To show the graph as a camera stream, first you need to create a `GraphingConfig` object. If you leave this blank, default values will be used.
```java
// Imports
import frc.robot.InterpolationSolver.GraphingConfig;

GraphingConfig config = new GraphingConfig();
```
Then, run the `initGraph()` method, with the name to stream the graph under. If no `GraphingConfig` object is provided, the defaut will be used.
```java
solver.initGraph("Graph Name", config);
// OR
solver.initGraph("Graph Name");
```

### Changing the Graph Theme
3 built-in themes are added, to match [Shuffleboard's](https://docs.wpilib.org/en/stable/docs/software/dashboards/shuffleboard/index.html) built-in themes, Light, Dark, and Midnight. Specifying one of them in the constructor of a `GraphingConfig` object will use these defaults.
```java
// Imports
import frc.robot.InterpolationSolver.GraphingConfig.GraphTheme;

// Light Theme
GraphingConfig config = new GraphingConfig(GraphTheme.LIGHT); // (this is default)

// Dark Theme
GraphingConfig config = new GraphingConfig(GraphTheme.DARK);

// Midnight Theme
GraphingConfig config = new GraphingConfig(GraphTheme.MIDNIGHT);
```

### Further Graph Customization
Individual values of the `GraphingConfig` object can be changed. A complete list is below:
|Property Name\*|Value Type|Description|
|---|---|---|
|background|`Color`|The background color of the graph|
|gridLineColor|`Color`|The color of the graph's grid lines|
|labelColor|`Color`|The color of the labels along the graph's X and Y axis|
|lineColor|`Color`|The color of the line, as well as each of the points on the line|
|pointRadius|`int`|The radius of each point on the line (input data), as well as each prediction|
|checkerColor|`Color`|The color of the checker line, which shows the position of each prediction|
|equationColor|`Color`|The color of the text of the equation at the bottom|
|boundsColor|`Color`|The color of the upper and lower range on the graph|
|fps|`int`|The number of frames per second that the graph will be streamed at|

\*case sensitive \
*import `Color` object from `frc.robot.InterpolationSolver.Color`*

### Getting the CvSource object
While graphing, the [`CvSource`](https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/cscore/CvSource.html) object can be accessed, to push to Shuffleboard or other Driver Station dashboards.
```
CvSrouce graph = solver.getCvSource();
```