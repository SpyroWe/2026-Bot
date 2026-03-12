package frc.robot.subsystems;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix6.mechanisms.MechanismState;
//End effector subsystem, controls the intake and outtake of the game piece
public class Intake_Sub extends SubsystemBase {
    // 30 hopper intake motor
    // 31 floor intake motor

    private TalonFXS endeffectormotor = new TalonFXS(40);// spin constantly with effector 2
    private TalonFXS IntakeMotor = new TalonFXS(31);// runs the intake floor
    private SparkMax feeder = new SparkMax(0, MotorType.kBrushless);// change id once added
    private TalonFXS endeffectormotor2 = new TalonFXS(0); // Spin constantly with effector 1
    private TalonFXS Shooter = new TalonFXS(0);// shoots the ball

    public Intake_Sub() {

    }

    public void moveEffector (boolean leftTrigger,boolean rightTrigger){
    if(true){
    if(leftTrigger){
        endeffectormotor.set(1);// warms up the shooter: press before shooting
        endeffectormotor2.set(1);
        Shooter.set(.5);

    }else if(rightTrigger){
        Shooter.set(1);// takes the ball in and shoots it
        feeder.set(.7);
        IntakeMotor.set(.7);
    }else{
        endeffectormotor.set(.1);
        endeffectormotor2.set(.1);
        IntakeMotor.stopMotor(); 
        Shooter.stopMotor();
        feeder.stopMotor();
    }

    }

    }


    
}
