package frc.robot.Commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake_Sub;

public class IntakeCOM extends Command {

private BooleanSupplier leftTrigger;
private BooleanSupplier rightTrigger;
private Intake_Sub effectorbase;

public IntakeCOM(Intake_Sub effsub, BooleanSupplier LT, BooleanSupplier RT) {
leftTrigger = LT;
rightTrigger = RT;
effectorbase = effsub;

addRequirements(effectorbase);
}

@Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  effectorbase.moveEffector(leftTrigger.getAsBoolean(),rightTrigger.getAsBoolean());
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
    
