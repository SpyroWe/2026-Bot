package frc.robot.subsystems;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix6.mechanisms.MechanismState;

public class Intake_Sub extends SubsystemBase {
    private TalonFXS endeffectormotor = new TalonFXS(31);
    public Intake_Sub() {

    }

    public void moveEffector (boolean leftTrigger,boolean rightTrigger){
    if(true){
    if(leftTrigger){
        endeffectormotor.set(13);//2

    }else if(rightTrigger){
        endeffectormotor.set(-10);//2
    }else{
        endeffectormotor.stopMotor();
    }

    }

    }


    
}
