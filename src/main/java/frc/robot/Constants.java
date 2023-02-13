// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  // Add any constants that do not change between robots here, as well as all enums

  public static final class Vision {
    public enum CAMERA_TYPE {
      OAK,
      LIMELIGHT,
      PHOTONVISION
    }

    public enum CAMERA_POSITION {
      INTAKE,
      OUTTAKE,
      LEFT_LOCALIZER,
      RIGHT_LOCALIZER
    }

    public static Transform3d[] cameraPositions = {
      new Transform3d(new Translation3d(Units.inchesToMeters(-(3 + (3.0/8.0))),
                                        Units.inchesToMeters(12),
                                        Units.inchesToMeters(20)),
                      new Rotation3d()),
      new Transform3d(new Translation3d(Units.inchesToMeters(-(3 + (3.0/8.0))),
                                        Units.inchesToMeters(-12),
                                        Units.inchesToMeters(20)),
                      new Rotation3d()),
    };

    public enum SERVER_IPS {
      INTAKE("10.42.1.10"),
      FORWARD_LOCALIZER("10.42.1.11"),
      RIGHT_LOCALIZER("10.42.1.12");

      private final String ip;

      SERVER_IPS(final String ip) {
        this.ip = ip;
      }

      @Override
      public String toString() {
        return ip;
      }
    }
  }

  public enum SwerveDriveModulePosition {
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT
  }

  public static ConstantsAlpha constants = new ConstantsAlpha();

  // TODO: Get actual MAC address of alpha robot
  public static final String alphaRobotMAC = "00:00:00:00:00:01";
  public static final String betaRobotMAC = "00:80:2F:25:BC:FD";
}
