package frc.robot.subsystems;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;

import com.ctre.phoenix6.mechanisms.MechanismState;
//End effector subsystem, controls the intake and outtake of the game piece
public class Intake_Sub extends SubsystemBase {
    // 30 hopper intake motor
    // 31 floor intake motor

    private TalonFXS endeffectormotor = new TalonFXS(40);// spin constantly with effector 2 
    private TalonFXS IntakeMotor = new TalonFXS(31);// runs the intake floor id confirmed
    private TalonFXS feeder = new TalonFXS(33);// change id once added
    private TalonFXS endeffectormotor2 = new TalonFXS(34); // Spin constantly with effector 1



    public Intake_Sub() {
CurrentLimitsConfigs shootconfig = new CurrentLimitsConfigs();
shootconfig.StatorCurrentLimit = 40;
shootconfig.StatorCurrentLimitEnable = true;
endeffectormotor.getConfigurator().apply(shootconfig);
endeffectormotor2.getConfigurator().apply(shootconfig);
IntakeMotor.getConfigurator().apply(shootconfig);
feeder.getConfigurator().apply(shootconfig);


    }

    public Command shoot_auto(){
        return Commands.run(()->feeder.set(.7),this).withTimeout(1.5);
    }
     public Command stop_Auto(){
        return Commands.run(()->feeder.set(0),this);
    }//this was note here
    
    public Command Flywheel1(){
        return Commands.run(()->{endeffectormotor.set(-.6);endeffectormotor2.set(.6);},this);
        
    }
    
    
    public Command Flystop1(){
        return Commands.run(()->{endeffectormotor.set(0);endeffectormotor2.set(0);},this);
    }
    
    public Command shootSequence() {
    return Commands.sequence(
        // spin up flywheel for 1 second
        Commands.run(() -> {
            endeffectormotor.set(-.46);
            endeffectormotor2.set(.46);
        }, this).withTimeout(1.0),
        // flywheel keeps spinning, feeder activates for 1.5 seconds
        Commands.run(() -> {
            endeffectormotor.set(-.46);
            endeffectormotor2.set(.46);
            feeder.set(0.7);
            IntakeMotor.set(0.55);
        }, this).withTimeout(4)
    );
}


    
    

    public void moveEffector (boolean leftTrigger,boolean rightTrigger){
    if(true){
    if(leftTrigger){
        endeffectormotor.set(-.55);// warms up the shooter: press before shooting
        endeffectormotor2.set(.55);//may need to spin backward
    if(rightTrigger&&leftTrigger){//if right originaly
       // takes the ball in and shoots it
        feeder.set(1);
        IntakeMotor.set(.55);
        endeffectormotor.set(-.55);
        endeffectormotor2.set(.55);//may need to spin backward
    }
    }

    else{
        endeffectormotor.set(-.45);
        endeffectormotor2.set(.45);//may need to spin backward
        IntakeMotor.stopMotor(); 
        feeder.stopMotor();
    }

    }

    }


    
}
