package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Newton;

import com.ctre.phoenix6.hardware.TalonFXS;

public class Hopper_Sub extends SubsystemBase {
    private TalonFXS hopperMotor = new TalonFXS(32); 
    private TalonFXS rollerMotor = new TalonFXS(30);
    public Hopper_Sub() {

    }

    public void moveHopper (boolean intakeTrigger,boolean outtakeTrigger, boolean button){
        if(true){
            if(intakeTrigger){
                hopperMotor.set(-.6);
            }else if(hopperMotor.getAnalogVoltage().getValueAsDouble()> .1){
                hopperMotor.set(0);

            }
            else if(outtakeTrigger){
                hopperMotor.set(.6);
            }
            else if(button){
                rollerMotor.set(.4);
            }
            else{
                hopperMotor.set(0);;
                rollerMotor.set(0);
            }
        }

    }

    }

    

