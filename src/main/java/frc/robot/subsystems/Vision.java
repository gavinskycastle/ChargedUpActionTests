// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.Vision.CAMERA_POSITION;

public class Vision extends SubsystemBase {

  private final SwerveDrive m_swerveDrive;

  private final NetworkTable intake;
  private final NetworkTable outtake;
  private final NetworkTable forwardLocalizer;
  private final NetworkTable rearLocalizer;

  private DataLog m_logger;
  private DoubleLogEntry limelightTargetValid;
  private DoubleLogEntry forwardLocalizerTargetValid;

  Pose2d defaultPose = new Pose2d(-5, -5, new Rotation2d());

  int[] deafaultIntArray = {0};
  double[] defaultDoubleArray = {0, 0, 0, 0, 0, 0};

  int[] tagId = new int[10];
  double[] xPoses = new double[10];
  double[] yPoses = new double[10];
  double[] zPoses = new double[10];

  int[] pnpTagId = new int[10];
  double[] pnpXPoses = new double[10];
  double[] pnpyPoses = new double[10];

  double avgXPose = 0;
  double avgYPose = 0;
  double headingPose = 0;

  public Vision(SwerveDrive swerveDrive, DataLog logger) {
    m_swerveDrive = swerveDrive;

    intake = NetworkTableInstance.getDefault().getTable("limelight");
    outtake = NetworkTableInstance.getDefault().getTable("limelight");
    forwardLocalizer = NetworkTableInstance.getDefault().getTable("fLocalizer");
    rearLocalizer = NetworkTableInstance.getDefault().getTable("rLocalizer");

    PortForwarder.add(5800, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5800);
    PortForwarder.add(5801, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5801);
    PortForwarder.add(5802, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5802);
    PortForwarder.add(5803, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5803);
    PortForwarder.add(5804, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5804);
    PortForwarder.add(5805, Constants.Vision.SERVER_IPS.INTAKE.toString(), 5805);


    m_logger = logger;
    limelightTargetValid = new DoubleLogEntry(logger, "/vision/limelight_tv");
    forwardLocalizerTargetValid = new DoubleLogEntry(logger, "/vision/fLocalizer_tv");
  }

  /**
   * Given a camera, return a boolean value based on if it sees a target or not.
   *
   * @return true: Camera has a target. false: Camera does not have a target
   */
  public boolean getValidTarget(CAMERA_POSITION position) {
    return getValidTargetType(position) > 0;
  }

  public double getValidTargetType(CAMERA_POSITION position) {
    switch (position) {
      case INTAKE:
        return intake.getEntry("tv").getDouble(0);
      case OUTTAKE:
        return outtake.getEntry("tv").getDouble(0);
      case FORWARD_LOCALIZER:
      case REAR_LOCALIZER:
        var tagIds = getAprilTagIds(position);
        if(tagIds.length == 0)
          return 0;
        return tagIds[0];
      default:
        return 0;
    }
  }

  public double[] getAprilTagIds(CAMERA_POSITION position) {
    switch (position) {
      case FORWARD_LOCALIZER:
        return forwardLocalizer.getEntry("tid").getDoubleArray(defaultDoubleArray);
      case REAR_LOCALIZER:
        return rearLocalizer.getEntry("tid").getDoubleArray(defaultDoubleArray);
      default:
        return defaultDoubleArray;
    }
  }

  public double getTargetXAngle(CAMERA_POSITION position, int index) {
    switch (position) {
      case INTAKE:
        return -intake.getEntry("tx").getDouble(0);
      case OUTTAKE:
        return -outtake.getEntry("tx").getDouble(0);
      default:
        return 0;
    }
  }

  public double getTargetYAngle(CAMERA_POSITION position, int index) {
    switch (position) {
      case INTAKE:
        return intake.getEntry("ty").getDouble(0);
      case OUTTAKE:
        return outtake.getEntry("ty").getDouble(0);
      default:
        return 0;
    }
  }

  public double getCameraLatency(CAMERA_POSITION position) {
    switch (position) {
      case INTAKE:
        return intake.getEntry("tl").getDouble(0);
      case OUTTAKE:
        return outtake.getEntry("tl").getDouble(0);
      default:
        return 0;
    }
  }

  /*
   * json: Full JSON dump of targeting results
   */
  public double getJSON(CAMERA_POSITION position) {
    switch (position) {
      case FORWARD_LOCALIZER:
        return forwardLocalizer.getEntry("json").getDouble(0);
      case REAR_LOCALIZER:
        return rearLocalizer.getEntry("json").getDouble(0);
      default:
        return 0;
    }
  }

  /*
   * botpose: Robot transform in field-space. Translation (X,Y,Z) Rotation(X,Y,Z)
   */
  public double[] getBotPose(CAMERA_POSITION position) {
    switch (position) {
      case FORWARD_LOCALIZER:
        return forwardLocalizer.getEntry("botpose").getDoubleArray(defaultDoubleArray);
      case REAR_LOCALIZER:
        return rearLocalizer.getEntry("botpose").getDoubleArray(defaultDoubleArray);
      default:
        return defaultDoubleArray;
    }
  }

  public double[] getBotPoses(CAMERA_POSITION position) {
    switch (position) {
      case FORWARD_LOCALIZER:
        return forwardLocalizer.getEntry("botpose").getDoubleArray(defaultDoubleArray);
      case REAR_LOCALIZER:
        return rearLocalizer.getEntry("botpose").getDoubleArray(defaultDoubleArray);
      default:
        return defaultDoubleArray;
    }
  }
  /**
   * Get the timestamp of the detection results.
   *
   * @return Robot Pose in meters
   */
  public double getDetectionTimestamp(CAMERA_POSITION position) {
    switch (position) {
      case FORWARD_LOCALIZER:
        return forwardLocalizer.getEntry("timestamp").getDouble(0);
      case REAR_LOCALIZER:
        return rearLocalizer.getEntry("timestamp").getDouble(0);
      default:
        return 0;
    }
  }

  public Pose2d getRobotPose2d(CAMERA_POSITION position) {
    double[] pose = getBotPose(position);
    return new Pose2d(pose[0], pose[1], m_swerveDrive.getHeadingRotation2d());
  }
  public Pose2d[] getRobotPoses2d(CAMERA_POSITION position) {
    Pose2d[] poseArray = {
        new Pose2d(-1, -1, Rotation2d.fromDegrees(0))
    };

    if(getValidTarget(position))
      switch (position) {
        case FORWARD_LOCALIZER:
          xPoses = forwardLocalizer.getEntry("X Poses").getDoubleArray(new double[]{});
          yPoses = forwardLocalizer.getEntry("Y Poses").getDoubleArray(new double[]{});
          zPoses = forwardLocalizer.getEntry("Z Poses").getDoubleArray(new double[]{});
          poseArray = new Pose2d[xPoses.length];
          for (int i = 0; i < xPoses.length; i++)
            poseArray[i] = new Pose2d(xPoses[i], yPoses[i], Rotation2d.fromDegrees(0));
          break;
        case REAR_LOCALIZER:
          break;
      }

    return poseArray;
  }

  private void updateVisionPose(CAMERA_POSITION position) {
    m_swerveDrive.getOdometry().addVisionMeasurement(getRobotPose2d(position), getDetectionTimestamp(position));
  }

  private void logData() {
    limelightTargetValid.append(getValidTargetType(CAMERA_POSITION.INTAKE));
    forwardLocalizerTargetValid.append(getValidTargetType(CAMERA_POSITION.OUTTAKE));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateVisionPose(CAMERA_POSITION.FORWARD_LOCALIZER);
    logData();
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
