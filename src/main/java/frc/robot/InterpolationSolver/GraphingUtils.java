package frc.robot.InterpolationSolver;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class GraphingUtils {
    // Config
    private GraphingConfig config;
    private double[] xRange = new double[2]; // start, stop
    private double[] yRange = new double[2];

    // Objects
    private Mat blankGraph;
    private CvSource streamSource;
    public double[] mostRecentPrediction = {Double.NaN, Double.NaN};

    // Get Text Size (in Pixels)
    private double getTextWidth(String content, double scale) {
        int[] baseline = {0};
        return Imgproc.getTextSize(content, 0, scale, 1, baseline).width;
    }

    private double getTextWidth(String content) { return getTextWidth(content, 0.4); }
    
    // Limit Text Size (in Pixels)
    private String limitTextSize(Object content, int maxSize) {
        String data = String.valueOf(content);
        while (getTextWidth(data) > maxSize) {
            data = data.substring(0, data.length()-1);
        }
        return data;
    }

    // Convert Coordinate on Graph to Coordinate on Mat
    private double percent(double x, double min, double max) { return (x - min) / (max - min); }

    private Point getCoordinateOfPoint(double x, double y) {
        return new Point(
            31 + (percent(x, xRange[0], xRange[1]) * 289),
            (1.0 - percent(y, yRange[0], yRange[1])) * 193
        );
    }

    // Constructor
    public GraphingUtils(String title, GraphingConfig graphConfig, double[] xData, double[] yData, PolynomialUtils polynomial) {
        // Safety/Sanity Checks
        if (xData.length < 1 || xData.length != yData.length) {
            DriverStation.reportError("Invalid data: xData and yData must be the same length, and have at least one number", false);
            return;
        }

        if (graphConfig.fps < 1) {
            DriverStation.reportError("Invalid config: FPS (frames per second) can not be less than 1!", false);
            return;
        }
        
        // Load CV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Save Values
        config = graphConfig;

        // Create CvSource
        streamSource = CameraServer.putVideo(title, 320, 240);
        streamSource.setFPS(Math.min(1, config.fps));

        // Create graph, but rotated 90 degrees clockwise (to add X-axis labels)
        Mat rotatedGraph = new Mat(320, 240, 16, config.background.getScalarValue());
        blankGraph = Mat.zeros(rotatedGraph.cols(), rotatedGraph.rows(), rotatedGraph.type());

        // Add horizontal (now vertical) lines
        for (int x = 0; x < 7; x++) {
            Imgproc.line(
                rotatedGraph, 
                new Point(47 + (32 * x), 31), 
                new Point(47 + (32 * x), 319), 
                config.gridLineColor.getScalarValue(),
                1
            );
        }

        // Add vertical (now horizontal) lines
        for (int y = 0; y < 10; y++) {
            Imgproc.line(
                rotatedGraph, 
                new Point(47, 31 + (32 * y)), 
                new Point(239, 31 + (32 * y)), 
                config.gridLineColor.getScalarValue(), 
                1
            );
        }

        // Determine Graph Range & Steps
        xRange[0] = xData[0];
        xRange[1] = xData[0];

        yRange[0] = yData[0];
        yRange[1] = yData[0];

        for (int index = 1; index < xData.length; index ++) {
            if (xData[index] < xRange[0]) {
                xRange[0] = xData[index];
            } else if (xData[index] > xRange[1]) {
                xRange[1] = xData[index];
            }

            if (yData[index] < yRange[0]) {
                yRange[0] = yData[index];
            } else if (yData[index] > yRange[1]) {
                yRange[1] = yData[index];
            }
        }

        double xStep = (xRange[1] - xRange[0])/7;
        double yStep = (yRange[1] - yRange[0])/4;

        xRange[0] -= xStep;
        xRange[1] += xStep;

        yRange[0] -= yStep;
        yRange[1] += yStep;

        // Add X-Axis Labels
        for (int x = 0; x < 10; x++) {
            String label = limitTextSize(xRange[0] + (xStep * x), 30);

            Imgproc.putText(
                rotatedGraph, 
                label, 
                new Point(46 - getTextWidth(label), 38 + (31.2 * x)), 
                0, 
                0.4, 
                config.labelColor.getScalarValue(),
                1
            );
        }

        // Rotate Graph
        Core.rotate(rotatedGraph, blankGraph, Core.ROTATE_90_COUNTERCLOCKWISE);
        rotatedGraph = null;

        // Add Y Axis Labels
        for (int y = 0; y < 7; y++) {
            String label = limitTextSize(yRange[0] + (yStep * y), 30);

            Imgproc.putText(
                blankGraph, 
                label, 
                new Point(30 - getTextWidth(label), 190 - (y * 30)), 
                0, 
                0.4, 
                config.labelColor.getScalarValue()
            );
        }

        // Add Points
        if (config.pointRadius > 0) {
            for (int index = 0; index < xData.length; index++) {
                Imgproc.circle(
                    blankGraph, 
                    getCoordinateOfPoint(xData[index], yData[index]), 
                    config.pointRadius, 
                    config.lineColor.getScalarValue()
                );
            }
        }

        // Add Line
        Point lastPlottedLineSeg = null;

        for (double x = xRange[0]; x < xRange[1]; x += xStep/50) { // 50 points per grid box
            double y = polynomial.predict(x);

            if (y > yRange[0] && y < yRange[1]) {
                Point pt = getCoordinateOfPoint(x, y);

                if (lastPlottedLineSeg != null) {
                    Imgproc.line(blankGraph, lastPlottedLineSeg, pt, config.lineColor.getScalarValue());
                }

                lastPlottedLineSeg = pt;
            } else {
                lastPlottedLineSeg = null;
            }
        }

        // Add Equation
        double scale = 0.4;

        while (getTextWidth(polynomial.toString(), scale) > 320 && scale > 0.05) {
            scale -= 0.05;
        }

        Imgproc.putText(
            blankGraph, 
            polynomial.toString(), 
            new Point(0, blankGraph.height() - 4), 
            0, 
            scale, 
            config.equationColor.getScalarValue(),
            1
        );
    }

    // Clamp Number
    private double clamp(double value, double min, double max) { return Math.min(Math.max(value, min), max); }

    // Limit Number to 4 Decimal Places
    private String limitDecimalPlaces(double value) {
        String s = String.valueOf(value);

        if (s.indexOf(".") != -1 && s.split("\\.")[1].length() > 4) { s = s.substring(0, s.split("\\.")[0].length()+5); }

        return s;
    }

    // Stream Thread
    private Thread streamThread = new Thread(() -> {
        Mat workingGraph = blankGraph.clone();
        double loopStart = 0.0;
        double lastUpdateX = Double.NaN;

        while (!Thread.interrupted()) {
            loopStart = Timer.getFPGATimestamp();

            // Only redraw line if new point has been checked
            if (lastUpdateX != mostRecentPrediction[0]) {
                workingGraph = blankGraph.clone();

                double realX = clamp(mostRecentPrediction[0], xRange[0], xRange[1]);

                Point top = getCoordinateOfPoint(realX, yRange[1]);
                Imgproc.line(
                    workingGraph, 
                    top,
                    getCoordinateOfPoint(realX, yRange[0]),
                    config.checkerColor.getScalarValue(),
                    1
                );

                Point textOrg = getCoordinateOfPoint(realX, 0);
                textOrg.y = 50;
                textOrg.x += 5;

                String content = String.format("y = %s", limitDecimalPlaces(mostRecentPrediction[1]));

                if (top.x >= 315 - getTextWidth(content)) {
                    textOrg.x -= (getTextWidth(content) + 10);
                }

                Imgproc.putText(
                    workingGraph,
                    content,
                    textOrg,
                    0,
                    0.4,
                    config.checkerColor.getScalarValue()
                );

                lastUpdateX = mostRecentPrediction[0];
            }

            streamSource.putFrame(workingGraph);

            // Wait to reach target FPS
            try {
                Thread.sleep(Double.valueOf(Math.max(0, (1/(double)config.fps) - (Timer.getFPGATimestamp() - loopStart)) * 1000).longValue());
            } catch (InterruptedException e) {
                break;
            }
        }
    });

    // Start Stream
    public void startStream() {
        streamThread.setDaemon(true);
        streamThread.start();
    }

    // Get Stream
    public CvSource getCvSource() { return streamSource; }
}
