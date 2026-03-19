package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Newton;

import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TorqueCurrentConfigs;

import com.ctre.phoenix6.mechanisms.MechanismState;

public class Hopper_Sub extends SubsystemBase {
    private TalonFXS hopperMotor = new TalonFXS(32); // change id once added
    private TalonFXS rollerMotor = new TalonFXS(30);

    
    public Hopper_Sub() {
CurrentLimitsConfigs currentlimits = new CurrentLimitsConfigs();
CurrentLimitsConfigs currentlimit1 = new CurrentLimitsConfigs();
currentlimits.StatorCurrentLimit = 40;
currentlimits.StatorCurrentLimitEnable = true;
currentlimit1.StatorCurrentLimit = 60;
currentlimit1.StatorCurrentLimitEnable = true;
hopperMotor.getConfigurator().apply(currentlimits);//this was not here
rollerMotor.getConfigurator().apply(currentlimit1);


    }
    
    
    public void moveHopper (boolean intakeTrigger,boolean outtakeTrigger, boolean button,boolean button_two){
        if(true){
            if(intakeTrigger){
                hopperMotor.set(-.7);
            }
            else if(outtakeTrigger){
                hopperMotor.set(.7);
                rollerMotor.set(.8);
            }
            else if(button){
                rollerMotor.set(.8);
            
            }
            else if(button_two){
                rollerMotor.set(-.8);
            }
            else{
                hopperMotor.set(0);;
                rollerMotor.set(0);
            }
        }

    }

    }

    

