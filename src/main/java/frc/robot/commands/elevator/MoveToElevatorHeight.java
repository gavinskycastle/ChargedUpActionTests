// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Called when the joystick moves up/down, also acts as manual override
package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.StateHandler;

public class MoveToElevatorHeight extends CommandBase {
  /** Creates a new IncrementElevatorHeight. */
  private Elevator m_elevator;

  private StateHandler.elevatorStates heightEnum;

  public MoveToElevatorHeight(Elevator elevator, StateHandler.elevatorStates heightEnum) {

    // Use addRequirements() here to declare subsystem dependencies.
    m_elevator = elevator;
    this.heightEnum = heightEnum;
    addRequirements(m_elevator);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //if (m_elevator.getElevatorDesiredHeightState() != heightEnum) {
      m_elevator.setElevatorDesiredHeightState(heightEnum);
    //}
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
