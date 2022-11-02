package frc.robot.InterpolationSolver;

import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.wpilibj.DriverStation;

public class InterpolationSolver {
    // Polynomial Regression
    private PolynomialUtils regression;

    // Points & Data
    private double[] xPoints;
    private double[] yPoints;
    private int polynomialDegree;
    private double[] bounds = new double[2];

    // Line Types
    public static class LineType {
        public static final int CONSTANT = 0;
        public static final int LINEAR = 1;
        public static final int QUADRATIC = 2;
        public static final int CUBIC = 3;
        public static final int QUARTIC = 4;
        public static final int QUINTIC = 5;
    }

    // Graphing
    private GraphingUtils graph;
    private boolean isGraphing = false;

    // Constructor
    public InterpolationSolver(double[] x, double[] y, int degree, double minValue, double maxValue) {
        xPoints = x;
        yPoints = y;
        polynomialDegree = degree;

        regression = new PolynomialUtils(xPoints, yPoints, polynomialDegree, "x");

        if (minValue > maxValue) {
            DriverStation.reportError("Min value can not be greater than max value!", false);
            bounds = new double[]{Double.NaN, Double.NaN};
        } else {
            bounds = new double[]{minValue, maxValue};
        }
    }

    public InterpolationSolver(double[] x, double[] y, int degree) { this(x, y, degree, Double.NaN, Double.NaN); }

    // Calculate
    public double solve(double x) {
        double y = regression.predict(x);

        if (!Double.isNaN(bounds[0])) { y = Math.max(y, bounds[0]); }
        if (!Double.isNaN(bounds[1])) { y = Math.min(y, bounds[1]); }

        if (isGraphing) { graph.mostRecentPrediction = new double[]{x, y}; }

        return y;
    }

    // Get Equation as String
    @Override
    public String toString() { return regression.toString(); }

    // Graph
    public void initGraph(String title, GraphingConfig config) {
        if (isGraphing) {
            DriverStation.reportWarning("Could not staring graphing, as it has already been started!", false);
            return;
        }

        graph = new GraphingUtils(title, config, xPoints, yPoints, bounds, regression);
        graph.startStream();
        isGraphing = true;
    }

    public void initGraph(String title) { initGraph(title, new GraphingConfig()); }

    public CvSource getCvSource() {
        if (isGraphing) {
            return graph.getCvSource();
        } else {
            DriverStation.reportWarning("Could not return CvSource because graphing never started (initGraph was never called)", false);
            return null;
        }
    }
}