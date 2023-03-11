package frc.robot.commands.auto;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.WRIST;
import frc.robot.commands.swerve.AutoBalance;
import frc.robot.commands.swerve.SetSwerveNeutralMode;
import frc.robot.commands.swerve.SetSwerveOdometry;
import frc.robot.commands.wrist.AutoSetWristDesiredSetpoint;
import frc.robot.simulation.FieldSim;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Wrist;
import frc.robot.utils.TrajectoryUtils;
import java.util.function.DoubleSupplier;

public class Balancetest extends SequentialCommandGroup {
  public Balancetest(
      String pathName,
      SwerveAutoBuilder autoBuilder,
      SwerveDrive swerveDrive,
      DoubleSupplier throttleInput,
      DoubleSupplier strafeInput,
      DoubleSupplier rotationInput,
      FieldSim fieldSim,
      Wrist wrist) {

    var trajectory =
        TrajectoryUtils.readTrajectory(
            pathName, new PathConstraints(Units.feetToMeters(3), Units.feetToMeters(3)));

    var autoPath = autoBuilder.fullAuto(trajectory);
    addCommands(
        new AutoSetWristDesiredSetpoint(wrist, WRIST.SETPOINT.STOWED.get()),
        new SetSwerveOdometry(swerveDrive, trajectory.get(0).getInitialHolonomicPose(), fieldSim),
        autoPath,
        new AutoBalance(swerveDrive, throttleInput, strafeInput, rotationInput),
        new SetSwerveNeutralMode(swerveDrive, NeutralMode.Brake)
            .andThen(() -> swerveDrive.drive(0, 0, 0, false, false)));
  }
}
