package frc.robot.commands.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.INTAKE.INTAKE_STATE;
import frc.robot.Constants.SWERVE_DRIVE;
import frc.robot.Constants.VISION.CAMERA_SERVER;
import frc.robot.commands.InterruptingCommand;
import frc.robot.commands.swerve.DriveForwardWithVisionInput;
import frc.robot.commands.swerve.SetSwerveOdometry;
import frc.robot.simulation.FieldSim;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.StateHandler;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.Wrist;
import frc.robot.utils.TrajectoryUtils;
import java.util.List;

public class LimeLightTest extends SequentialCommandGroup {
  private final List<PathPlannerTrajectory> m_trajectories;

  public LimeLightTest(
      String pathName,
      SwerveDrive swerveDrive,
      FieldSim fieldSim,
      Wrist wrist,
      Intake intake,
      Vision vision,
      Elevator elevator,
      StateHandler stateHandler) {

    m_trajectories =
        TrajectoryUtils.readTrajectory(
            pathName,
            new PathConstraints(
                SWERVE_DRIVE.kMaxSpeedMetersPerSecond * 0.5,
                SWERVE_DRIVE.kMaxSpeedMetersPerSecond * 0.5));

    List<PPSwerveControllerCommand> swerveCommands =
        TrajectoryUtils.generatePPSwerveControllerCommand(swerveDrive, m_trajectories);

    addCommands(
        /** Setting Up Auto Zeros robot to path flips path if necessary */
        new SetSwerveOdometry(
            swerveDrive, m_trajectories.get(0).getInitialHolonomicPose(), fieldSim),
        new PlotAutoTrajectory(fieldSim, pathName, m_trajectories),
        new InstantCommand(() -> vision.setPipeline(CAMERA_SERVER.INTAKE, 2)),
        new InterruptingCommand(
            swerveCommands.get(0),
            new DriveForwardWithVisionInput(swerveDrive, vision, () -> 0.2)
                .until(() -> intake.getIntakeState() == INTAKE_STATE.HOLDING_CONE),
            () -> vision.getValidTarget(CAMERA_SERVER.INTAKE)));
  }

  public List<PathPlannerTrajectory> getTrajectories() {
    return m_trajectories;
  }
}
