package frc.robot.InterpolationSolver;

import org.opencv.core.Scalar;

import frc.robot.InterpolationSolver.GraphingConfig.GraphTheme;

public class Color {
    // Values
    public int red;
    public int green;
    public int blue;

    // RGB Constructor
    public Color(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }

    // Boolean Constructor
    public Color(boolean value) {
        red = (value) ? 255 : 0;
        green = (value) ? 255 : 0;
        blue = (value) ? 255 : 0;
    }

    // Graph Appearence Constructor
    public Color(GraphTheme theme) {
        switch (theme) {
            case DARK:
                red = 239; green = 64; blue = 49;
                break;
            case LIGHT:
                red = 18; green = 148; blue = 246;
                break;
            case MIDNIGHT:
                red = 119; green = 39; blue = 198;
                break;
            default:
                red = 0; green = 0; blue = 0;
                break;
        }
    }

    // Get Scalar Value
    public Scalar getScalarValue() { return new Scalar(blue, green, red); }

    // Blend With Other Color
    public Scalar blend(Color source2, double beta) {
        return new Scalar(
            (blue * beta) + (source2.blue * (1.0-beta)),
            (green * beta) + (source2.green * (1.0-beta)),
            (red * beta) + (source2.red * (1.0-beta))
        );
    }
}
