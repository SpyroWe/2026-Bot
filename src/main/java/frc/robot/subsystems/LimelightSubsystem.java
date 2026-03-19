package frc.robot.subsystems;

import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {

    private static final String kTableName = "limelight";

    // Tag IDs to target (2026 field)
    public static final int[] TARGET_TAG_IDS = {26, 9};

    private final NetworkTable m_table;
    private final DoubleEntry m_tv;  // valid targets (1 = has target)
    private final DoubleEntry m_tx;  // horizontal offset from crosshair (degrees)
    private final DoubleEntry m_ty;  // vertical offset from crosshair (degrees)
    private final DoubleEntry m_tid; // primary AprilTag ID

    public LimelightSubsystem() {
        m_table = NetworkTableInstance.getDefault().getTable(kTableName);
        m_tv  = m_table.getDoubleTopic("tv").getEntry(0);
        m_tx  = m_table.getDoubleTopic("tx").getEntry(0);
        m_ty  = m_table.getDoubleTopic("ty").getEntry(0);
        m_tid = m_table.getDoubleTopic("tid").getEntry(-1);

        // Set crop window to full frame so the LL3 detects tags across its entire FOV (~63°)
        // Values are [-1, 1] normalized: [xMin, xMax, yMin, yMax]
        m_table.getEntry("crop").setDoubleArray(new double[]{-3.0, 3.0, -3.0, 3.0});
    }

    /** Returns true if the limelight sees any target. */
    public boolean hasTarget() {
        return m_tv.get(0) == 1.0;
    }

    /** Horizontal angle to target (degrees). Positive = target is to the right. */
    public double getTx() {
        return m_tx.get(0);
    }

    /** Vertical angle to target (degrees). Positive = target is above crosshair. */
    public double getTy() {
        return m_ty.get(0);
    }

    /** ID of the primary AprilTag in view, or -1 if none. */
    public int getTagId() {
        return (int) m_tid.get(-1);
    }

    /** Returns true if a target is visible AND it is one of the desired tag IDs. */
    public boolean hasValidTarget() {
        if (!hasTarget()) return false;
        int tid = getTagId();
        for (int id : TARGET_TAG_IDS) {
            if (tid == id) return true;
        }
        return false;
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Limelight/HasTarget", hasTarget());
        SmartDashboard.putBoolean("Limelight/ValidTarget", hasValidTarget());
        SmartDashboard.putNumber("Limelight/TX", getTx());
        SmartDashboard.putNumber("Limelight/TagID", getTagId());
    }
}
