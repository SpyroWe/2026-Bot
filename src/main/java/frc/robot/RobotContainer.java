// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.FollowPathCommand;


import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.subsystems.Intake_Sub;
import frc.robot.Commands.IntakeCOM;

import frc.robot.subsystems.Hopper_Sub;
import frc.robot.Commands.HopperCOM;

import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.Commands.AlignToTagCOM;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;



public class RobotContainer {
    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.15).withRotationalDeadband(MaxAngularRate * 0.15) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

private final Intake_Sub effectorbase = new Intake_Sub();

private final Hopper_Sub hopperbase = new Hopper_Sub();

private final LimelightSubsystem m_limelight = new LimelightSubsystem();


private final CommandXboxController m_driverController = new CommandXboxController(0);

private final CommandXboxController CO_Controller = new CommandXboxController(1);

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {

        NamedCommands.registerCommand("feed shooter",effectorbase.shoot_auto().asProxy());
        NamedCommands.registerCommand("Stop feeder",effectorbase.stop_Auto().asProxy());

        NamedCommands.registerCommand("stop shoot1", effectorbase.Flystop1().asProxy());
        NamedCommands.registerCommand("shoot1", effectorbase.Flywheel1().asProxy());
        NamedCommands.registerCommand("shoot sequence", effectorbase.shootSequence().asProxy());
        
    
           autoChooser = AutoBuilder.buildAutoChooser("Move Forward");
           SmartDashboard.putData("auto Mode", autoChooser);

        configureBindings();
        
        FollowPathCommand.warmupCommand().schedule();
        
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically

            // adjust this to change the joystcks used, and rotation direction
            drivetrain.applyRequest(() ->
                drive.withVelocityX((-joystick.getLeftY()) * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY((-joystick.getLeftX()) * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(joystick.getRightX() * MaxAngularRate/2) // Drive counterclockwise with negative X (left)
            )
        );

        // getRightX




         //effector
        effectorbase.setDefaultCommand(new IntakeCOM(effectorbase,
        () ->CO_Controller.leftTrigger().getAsBoolean(),
        () ->CO_Controller.rightTrigger().getAsBoolean()
        ));

        //hopper
        hopperbase.setDefaultCommand(new HopperCOM(hopperbase,
        () ->CO_Controller.y().getAsBoolean(),
        () ->CO_Controller.b().getAsBoolean(),
        () ->m_driverController.rightTrigger().getAsBoolean(),
        ()-> m_driverController.leftTrigger().getAsBoolean()
        ));



        // Idle while the robot is disabled. This ensures the configured
        // neutral mode is applied to the drive motors while disabled.
        /*final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
            drivetrain.applyRequest(() -> idle).ignoringDisable(true)
        );

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake));
        joystick.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))
        ));*/

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // Reset the field-centric heading on left bumper press.
        joystick.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        // Align to AprilTag 26 or 9 while operator right bumper is held.
        // Driver retains full translation control; only rotation is overridden by limelight.
        // Right bumper: auto aim (tx→0) + auto range (ty→0), driver keeps strafe (right stick X)
        /*CO_Controller.rightBumper().whileTrue(new AlignToTagCOM(
                drivetrain,
                m_limelight,
                MaxSpeed,
                MaxAngularRate,
                () -> MathUtil.applyDeadband(-joystick.getRightX(), 0.1) * MaxSpeed
        ));*/

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        // Simple drive forward auton
       return autoChooser.getSelected();
       //return null;
    }
}
