package frc.robot.InterpolationSolver;

public class GraphingConfig {
    // Shuffleboard Theme Enum
    public enum GraphTheme {LIGHT, DARK, MIDNIGHT};

    // Values
    public Color background;
    public Color gridLineColor;
    public Color labelColor;
    public Color lineColor;
    public int pointRadius;
    public Color checkerColor;
    public Color equationColor;
    public int fps;

    // Create From Graph Appearance (Default Values)
    public GraphingConfig(GraphTheme theme) {
        switch (theme) {
            case DARK:
                background = new Color(33, 33, 33);
                break;
            case LIGHT:
                background = new Color(255, 255, 255);
                break;
            case MIDNIGHT:
                background = new Color(16, 16, 25);
                break;
            default:
                background = new Color(255, 255, 255);
                break;   
        }
        
        gridLineColor = new Color(233, 233, 234);
        labelColor = new Color(theme != GraphTheme.LIGHT);
        lineColor = new Color(theme);
        pointRadius = 4;
        checkerColor = new Color(0, 255, 0);
        equationColor = new Color(255, 0, 0);
        fps = 10;
    }

    // Create Blank
    public GraphingConfig() { this(GraphTheme.LIGHT); }
}
