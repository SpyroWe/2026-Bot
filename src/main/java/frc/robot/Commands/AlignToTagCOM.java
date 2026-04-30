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
 * Aim + Range: uses two PIDs to place the limelight crosshair on the
 * center of the selected AprilTag (tx=0, ty=0).
 *
 *  AIM  — rotates robot until tx = 0  (crosshair centered horizontally)
 *  RANGE — drives forward/back until ty = 0  (crosshair centered vertically)
 *  STRAFE — driver retains left/right control via right stick X
 *
 * TUNING:
 *   kRotP / kRotD   — rotation speed / damping
 *   kRangeP / kRangeD — range speed / damping
 *
 * If rotation or ranging goes the WRONG DIRECTION flip the sign on that
 * PID output line in execute() (look for the "Flip sign" comments).
 */
public class AlignToTagCOM extends Command {

    // ----- PID gains — tune on the robot -----
    private static final double kRotP   = 0.06;
    private static final double kRotD   = 0.004;
    private static final double kRangeP = 0.05;
    private static final double kRangeD = 0.003;

    private static final double kTxTolerance = 1.0; // degrees
    private static final double kTyTolerance = 1.0; // degrees

    private final CommandSwerveDrivetrain m_drivetrain;
    private final LimelightSubsystem      m_limelight;
    private final double                  m_maxSpeed;
    private final double                  m_maxAngularRate;
    private final DoubleSupplier          m_strafe; // driver left/right

    private final PIDController m_rotPID   = new PIDController(kRotP,   0, kRotD);
    private final PIDController m_rangePID = new PIDController(kRangeP, 0, kRangeD);

    // RobotCentric so VelocityX is always robot-forward regardless of field heading
    private final SwerveRequest.RobotCentric m_request = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    public AlignToTagCOM(CommandSwerveDrivetrain drivetrain,
                         LimelightSubsystem limelight,
                         double maxSpeed,
                         double maxAngularRate,
                         DoubleSupplier strafe) {
        m_drivetrain     = drivetrain;
        m_limelight      = limelight;
        m_maxSpeed       = maxSpeed;
        m_maxAngularRate = maxAngularRate;
        m_strafe         = strafe;

        m_rotPID.setTolerance(kTxTolerance);
        m_rotPID.setSetpoint(0);

        m_rangePID.setTolerance(kTyTolerance);
        m_rangePID.setSetpoint(0);

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        m_rotPID.reset();
        m_rangePID.reset();
    }

    @Override
    public void execute() {
        double strafe = m_strafe.getAsDouble();

        if (!m_limelight.hasValidTarget()) {
            m_drivetrain.setControl(m_request
                    .withVelocityX(0)
                    .withVelocityY(strafe)
                    .withRotationalRate(0));
            SmartDashboard.putString("AlignToTag/Status", "Searching...");
            return;
        }

        double tx = m_limelight.getTx();
        double ty = m_limelight.getTy();

        // AIM — rotate until tx = 0
        // Flip sign if rotation goes the wrong direction
        double rotRate = MathUtil.clamp(
                -m_rotPID.calculate(tx),
                -m_maxAngularRate,
                m_maxAngularRate
        );

        // RANGE — drive forward/back until ty = 0
        // Positive ty = tag above crosshair = camera is pointing up = too close (rear cam, 18° down-tilt)
        //   → drive forward (robot-relative) to back away → VelocityX positive
        // Flip sign if the robot drives the wrong way
        double forwardRate = MathUtil.clamp(
                -m_rangePID.calculate(ty),
                -m_maxSpeed,
                m_maxSpeed
        );

        m_drivetrain.setControl(m_request
                .withVelocityX(forwardRate)    // auto range (robot forward/back)
                .withVelocityY(strafe)          // driver strafe (robot left/right)
                .withRotationalRate(rotRate));  // auto aim

        boolean aimed  = m_rotPID.atSetpoint();
        boolean ranged = m_rangePID.atSetpoint();

        SmartDashboard.putNumber("AlignToTag/TX", tx);
        SmartDashboard.putNumber("AlignToTag/TY", ty);
        SmartDashboard.putBoolean("AlignToTag/Aimed",  aimed);
        SmartDashboard.putBoolean("AlignToTag/Ranged", ranged);
        SmartDashboard.putString("AlignToTag/Status",
                (aimed && ranged) ? "Locked!"
                : aimed           ? "Ranging..."
                : ranged          ? "Aiming..."
                                  : "Aligning...");
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
