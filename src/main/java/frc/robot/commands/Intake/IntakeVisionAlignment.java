// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Intake;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.VISION.CAMERA_SERVER;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Vision;

public class IntakeVisionAlignment extends CommandBase {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final Vision m_vision;

  private final SwerveDrive m_swerve;

  public IntakeVisionAlignment(Vision vision, SwerveDrive swerve) {
    m_vision = vision;
    m_swerve = swerve;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_swerve.enableHeadingTarget(true);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_vision.searchLimelightTarget(CAMERA_SERVER.INTAKE)) {
      m_swerve.enableHeadingTarget(true);
      m_swerve.setRobotHeading(
          m_swerve
              .getHeadingRotation2d()
              .minus(Rotation2d.fromDegrees(m_vision.getTargetXAngle(CAMERA_SERVER.INTAKE)))
              .getRadians());
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_swerve.enableHeadingTarget(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
