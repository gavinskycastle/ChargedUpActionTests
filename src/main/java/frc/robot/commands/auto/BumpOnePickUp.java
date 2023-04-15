package frc.robot.commands.auto;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.pathplanner.lib.PathConstraints;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.AUTOTIMES.WAIT;
import frc.robot.Constants.INTAKE.INTAKE_SPEEDS;
import frc.robot.Constants.STATE_HANDLER.SETPOINT;
import frc.robot.commands.intake.AutoSetIntakeSetpoint;
import frc.robot.commands.statehandler.AutoSetSetpoint;
import frc.robot.commands.statehandler.SetSetpoint;
import frc.robot.commands.swerve.SetSwerveNeutralMode;
import frc.robot.commands.swerve.SetSwerveOdometry;
import frc.robot.simulation.FieldSim;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.StateHandler;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.Wrist;
import frc.robot.utils.TrajectoryUtils;

// TODO: Rewrite without AutoBuilder
public class BumpOnePickUp extends SequentialCommandGroup {

  public BumpOnePickUp(
      String pathName,
      SwerveDrive swerveDrive,
      FieldSim fieldSim,
      Wrist wrist,
      Intake intake,
      Vision vision,
      Elevator elevator,
      StateHandler stateHandler) {

    var m_trajectories =
        TrajectoryUtils.readTrajectory(
            pathName, new PathConstraints(Units.feetToMeters(6), Units.feetToMeters(6)));
    var swerveCommands =
        TrajectoryUtils.generatePPSwerveControllerCommand(swerveDrive, m_trajectories);

    addCommands(
        /** Setting Up Auto Zeros robot to path flips path if necessary */
        new SetSwerveOdometry(
            swerveDrive, m_trajectories.get(0).getInitialHolonomicPose(), fieldSim),
        new PlotAutoTrajectory(fieldSim, pathName, m_trajectories),

        /** Brings elevator & wrist to High Pulls up cone */
        new ParallelCommandGroup(
            new AutoSetSetpoint(stateHandler, elevator, wrist, SETPOINT.SCORE_HIGH_CONE)
                .withTimeout(WAIT.SCORE_HIGH_CONE.get()),
            new AutoSetIntakeSetpoint(intake, INTAKE_SPEEDS.HOLDING_CONE, vision, swerveDrive)
                .withTimeout(WAIT.SCORE_HIGH_CONE.get())),
        /** Outakes cone */
        new WaitCommand(WAIT.WAIT_TO_PLACE_CONE.get()),
        new AutoSetIntakeSetpoint(intake, INTAKE_SPEEDS.SCORING_CONE, vision, swerveDrive)
            .withTimeout(WAIT.SCORING_CONE.get()),
        new WaitCommand(WAIT.SCORING_CONE.get()),
        /** Stows Wrist, Elevator, and Stops intake */
        new ParallelCommandGroup(
            new AutoSetSetpoint(stateHandler, elevator, wrist, SETPOINT.STOWED)
                .withTimeout(WAIT.STOW_HIGH_CONE.get()),
            new AutoSetIntakeSetpoint(intake, INTAKE_SPEEDS.STOP, vision, swerveDrive)
                .withTimeout(WAIT.STOW_HIGH_CONE.get())),
        new WaitCommand(WAIT.STOW_HIGH_CONE.get()),

        /** Runs Path with Intaking cube during */
        new ParallelDeadlineGroup(
            swerveCommands.get(0),
            new SequentialCommandGroup(
                new WaitCommand(3),
                new ParallelCommandGroup(
                    new AutoSetSetpoint(stateHandler, elevator, wrist, SETPOINT.INTAKING_LOW_CUBE)
                        .withTimeout(2),
                    new AutoSetIntakeSetpoint(
                            intake, INTAKE_SPEEDS.INTAKING_CUBE, vision, swerveDrive)
                        .withTimeout(2)))),

        /** Stows and Stops Intake */
        new ParallelCommandGroup(
            new SetSetpoint(stateHandler, elevator, wrist, SETPOINT.STOWED).withTimeout(0.5),
            new AutoSetIntakeSetpoint(intake, INTAKE_SPEEDS.HOLDING_CUBE, vision, swerveDrive)
                .withTimeout(0.5)),
        new SetSwerveNeutralMode(swerveDrive, NeutralMode.Brake)
            .andThen(() -> swerveDrive.drive(0, 0, 0, false, false)));
  }
}
