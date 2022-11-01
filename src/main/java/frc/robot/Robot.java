// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.InterpolationSolver.GraphingConfig;
import frc.robot.InterpolationSolver.InterpolationSolver;
import frc.robot.InterpolationSolver.GraphingConfig.GraphTheme;

public class Robot extends TimedRobot {
  // Create Data
  double[] x = {1, 2, 3, 4, 5};
  double[] y = {1, 4, 10, 15, 23};

  // Create Interpolation Solver and Graph Config
  InterpolationSolver test = new InterpolationSolver(x, y, InterpolationSolver.LineType.QUADRATIC, 3, 20);
  GraphingConfig config = new GraphingConfig(GraphTheme.LIGHT);

  // Shuffleboard Values
  NetworkTableEntry testSlider;
  NetworkTableEntry testOutput;

  @Override
  public void robotInit() {
    // Start Graph
    test.initGraph("Test Graph", config);

    // Add Network Tables
    ShuffleboardTab testTab = Shuffleboard.getTab("Test");
    
    testSlider = testTab.add("X Test Value", 0.0)
      .withWidget(BuiltInWidgets.kNumberSlider)
      .withProperties(Map.of("min", -10, "max", 10))
      .withPosition(4, 0)
      .withSize(3, 1)
      .getEntry();
    
    testOutput = testTab.add("Y Value", 0.0)
      .withPosition(4, 1)
      .withSize(3, 1)
      .getEntry();

    testTab.add(test.getCvSource())
      .withPosition(0, 0)
      .withSize(4, 3)
      .withProperties(Map.of("show crosshair", false, "show controls", false));
  }

  double lastChecked = 0.0;

  @Override
  public void robotPeriodic() {
    // Calculate value if slider moved
    if (lastChecked != testSlider.getDouble(0.0)) {
      lastChecked = testSlider.getDouble(0.0);
      testOutput.setDouble(test.solve(lastChecked));
    }
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
