package frc.robot.Commands;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.LimelightSubsystem;

/**
 * Rotates the robot using a PID on Limelight tx until tx = 0
 * (limelight crosshair is centered on the AprilTag center).
 * Driver retains full translation control.
 *
 * TUNING:
 *   kP — increase if rotation is too slow, decrease if it oscillates
 *   kD — increase to dampen overshoot
 *   kTolerance — degrees of tx considered "centered"
 *
 * If rotation goes the wrong direction, flip the sign on rawOutput.
 */
public class AlignToTagCOM extends Command {

    private static final double kP         = 0.06;
    private static final double kD         = 0.004;
    private static final double kTolerance = 1.0; // degrees

    private final CommandSwerveDrivetrain m_drivetrain;
    private final LimelightSubsystem      m_limelight;
    private final double                  m_maxAngularRate;
    private final DoubleSupplier          m_velocityX;
    private final DoubleSupplier          m_velocityY;

    private final PIDController m_rotPID = new PIDController(kP, 0, kD);

    private final SwerveRequest.FieldCentric m_request = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    public AlignToTagCOM(CommandSwerveDrivetrain drivetrain,
                         LimelightSubsystem limelight,
                         double maxAngularRate,
                         DoubleSupplier velocityX,
                         DoubleSupplier velocityY) {
        m_drivetrain     = drivetrain;
        m_limelight      = limelight;
        m_maxAngularRate = maxAngularRate;
        m_velocityX      = velocityX;
        m_velocityY      = velocityY;

        m_rotPID.setTolerance(kTolerance);
        m_rotPID.setSetpoint(0); // target is tx = 0 (crosshair on tag center)

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        m_rotPID.reset();
    }

    @Override
    public void execute() {
        double vx = m_velocityX.getAsDouble();
        double vy = m_velocityY.getAsDouble();

        if (!m_limelight.hasValidTarget()) {
            // No tag visible — pass driver translation, hold rotation
            m_drivetrain.setControl(m_request
                    .withVelocityX(vx)
                    .withVelocityY(vy)
                    .withRotationalRate(0));
            SmartDashboard.putString("AlignToTag/Status", "Searching...");
            return;
        }

        double tx = m_limelight.getTx();

        // PID drives tx → 0. Negate because positive tx requires positive (CCW) rotation
        // for a rear-mounted camera. Flip sign here if rotation goes the wrong way.
        double rotationalRate = MathUtil.clamp(
                -m_rotPID.calculate(tx),
                -m_maxAngularRate,
                m_maxAngularRate
        );

        m_drivetrain.setControl(m_request
                .withVelocityX(vx)
                .withVelocityY(vy)
                .withRotationalRate(rotationalRate));

        SmartDashboard.putBoolean("AlignToTag/Centered", m_rotPID.atSetpoint());
        SmartDashboard.putNumber("AlignToTag/TX", tx);
        SmartDashboard.putString("AlignToTag/Status", m_rotPID.atSetpoint() ? "Centered!" : "Aligning...");
    }

    @Override
    public void end(boolean interrupted) {
        m_drivetrain.setControl(m_request
                .withVelocityX(0)
                .withVelocityY(0)
                .withRotationalRate(0));
        SmartDashboard.putString("AlignToTag/Status", "Idle");
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
