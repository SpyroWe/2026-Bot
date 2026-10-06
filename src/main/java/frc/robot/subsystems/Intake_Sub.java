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

     
    private TalonFXS IntakeMotor = new TalonFXS(31);// runs the intake floor id confirmed
    private TalonFXS feederFL = new TalonFXS(24);
    private TalonFXS feederFR = new TalonFXS(21);// change id once added
    private TalonFXS endeffectormotorTFR = new TalonFXS(20);// spin constantly with effector 2
    private TalonFXS endeffectormotor2MFR = new TalonFXS(22);
    private TalonFXS endeffectormotor3TFL = new TalonFXS(23);
    private TalonFXS endeffectormotor4MFL = new TalonFXS(25);



    public Intake_Sub() {
CurrentLimitsConfigs shootconfig = new CurrentLimitsConfigs();
CurrentLimitsConfigs NEWlimitfloor = new CurrentLimitsConfigs();
NEWlimitfloor.SupplyCurrentLimit = 40;
NEWlimitfloor.SupplyCurrentLimitEnable = true;

shootconfig.StatorCurrentLimit = 40;
shootconfig.SupplyCurrentLimitEnable = true;
shootconfig.SupplyCurrentLowerLimit = 50;
shootconfig.SupplyCurrentLowerTime = .1;

shootconfig.SupplyCurrentLimit = 40;
shootconfig.StatorCurrentLimitEnable = true;

endeffectormotorTFR.getConfigurator().apply(shootconfig);
endeffectormotor2MFR.getConfigurator().apply(shootconfig);
endeffectormotor3TFL.getConfigurator().apply(shootconfig);
endeffectormotor4MFL.getConfigurator().apply(shootconfig);
IntakeMotor.getConfigurator().apply(NEWlimitfloor);//used shootconfig
feederFL.getConfigurator().apply(shootconfig);
feederFR.getConfigurator().apply(shootconfig);


    }

    public Command shoot_auto(){
        return Commands.run(()->{feederFL.set(.7);feederFR.set(-.7);},this).withTimeout(1.5);
    }
     public Command stop_Auto(){
        return Commands.run(()->{feederFL.set(0);feederFR.set(0);},this);
    }//this was note here
    
    public Command Flywheel1(){
        return Commands.run(()->{endeffectormotorTFR.set(-.6);endeffectormotor2MFR.set(.6);},this);
        
    }
    
    
    public Command Flystop1(){
        return Commands.run(()->{endeffectormotorTFR.set(0);endeffectormotor2MFR.set(0);},this);
    }
    
    public Command shootSequence() {
    return Commands.sequence(
        // spin up flywheel for 1 second
        Commands.run(() -> {
            endeffectormotorTFR.set(-.46);
            endeffectormotor2MFR.set(-.46);
            endeffectormotor4MFL.set(.46);
            endeffectormotor3TFL.set(.46);

        }, this).withTimeout(1.0),
        // flywheel keeps spinning, feeder activates for 1.5 seconds
        Commands.run(() -> {
            endeffectormotorTFR.set(-.46);
            endeffectormotor2MFR.set(-.46);
            endeffectormotor4MFL.set(.46);
            endeffectormotor3TFL.set(.46);
            feederFL.set(-0.7);
            feederFR.set(.7);
            IntakeMotor.set(1);
        }, this).withTimeout(4)
    );
}


    
    

    public void moveEffector (boolean leftTrigger,boolean rightTrigger){
    if(true){
    if(leftTrigger){
        endeffectormotorTFR.set(-.50);// warms up the shooter: press before shooting
        endeffectormotor2MFR.set(-.50);
        endeffectormotor3TFL.set(.50);//may need to spin backward
        endeffectormotor4MFL.set(.50);
    if(rightTrigger&&leftTrigger){//if right originaly
       // takes the ball in and shoots it
        feederFL.set(-1);
        feederFR.set(1);
        IntakeMotor.set(1);
        endeffectormotorTFR.set(-.60);
        endeffectormotor2MFR.set(-.60);// MAY NEED TO BE SLOWER THAN .7
        endeffectormotor3TFL.set(.60);//may need to spin backward
        endeffectormotor4MFL.set(.60);
        //may need to spin backward
    }
    }

    else{
        endeffectormotorTFR.set(0);
        endeffectormotor2MFR.set(0);
        endeffectormotor3TFL.set(0);
        endeffectormotor4MFL.set(0);
        //may need to spin backward
        IntakeMotor.stopMotor(); 
        feederFL.stopMotor();
        feederFR.stopMotor();
    }

    }

    }


    
}
