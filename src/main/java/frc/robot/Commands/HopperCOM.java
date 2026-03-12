package frc.robot.Commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Hopper_Sub;



public class HopperCOM extends Command {

private BooleanSupplier intakeTrigger ;
private BooleanSupplier outtakeTrigger;
private Hopper_Sub hopperSub;
private BooleanSupplier button;

public HopperCOM(Hopper_Sub hopS , BooleanSupplier iT, BooleanSupplier oT,BooleanSupplier but) {
    intakeTrigger = iT;
    outtakeTrigger = oT;
    button = but;
    hopperSub = hopS;

    addRequirements(hopperSub);
}

@Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  hopperSub.moveHopper(intakeTrigger.getAsBoolean(),outtakeTrigger.getAsBoolean(),button.getAsBoolean());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
    

