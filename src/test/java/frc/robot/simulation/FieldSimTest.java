package frc.robot.simulation;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants;
import frc.robot.subsystems.*;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

public class FieldSimTest {
    protected final static DataLog m_log = new DataLog();
    protected final static SwerveDrive m_swerveDrive = new SwerveDrive();
    protected final static Elevator m_elevator = new Elevator();
    protected final static Intake m_intake = new Intake();
    protected final static Wrist m_wrist = new Wrist(m_intake);
    protected final static Controls m_controls = new Controls();
    protected final static Vision m_vision = new Vision(m_swerveDrive, m_log,m_controls, m_intake);

    protected final static FieldSim m_fieldSim = new FieldSim(m_swerveDrive, m_vision, m_elevator, m_wrist, m_controls);

    @Test
    public void FieldSimTest() {
        testRedAllianceRedNodes();
        testRedAllianceBlueCooperatitionNodes();
        testBlueAllianceBlueNodes();
        testBlueALlianceRedCooperatitionNodes();
    }

    @Test
    public void testRedAllianceRedNodes() {
        TestUtils.setPrivateField(m_swerveDrive, "m_currentAlliance", DriverStation.Alliance.Red);
        m_swerveDrive.setOdometry(new Pose2d(SimConstants.fieldLength, 0, Rotation2d.fromDegrees(0)));
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.LOW);
        assert(m_fieldSim.getValidNodes().size() == 9);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CONE);
        assert(m_fieldSim.getValidNodes().size() == 6);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CUBE);
        assert(m_fieldSim.getValidNodes().size() == 3);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
    }

    @Test
    public void testRedAllianceBlueCooperatitionNodes() {
        TestUtils.setPrivateField(m_fieldSim, "m_currentAlliance", DriverStation.Alliance.Red);
        m_swerveDrive.setOdometry(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.LOW);
        assert(m_fieldSim.getValidNodes().size() == 3);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CONE);
        assert(m_fieldSim.getValidNodes().size() == 2);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CUBE);
        assert(m_fieldSim.getValidNodes().size() == 1);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
    }

    @Test
    public void testBlueAllianceBlueNodes() {
        TestUtils.setPrivateField(m_fieldSim, "m_currentAlliance", DriverStation.Alliance.Blue);
        m_swerveDrive.setOdometry(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.LOW);
        assert(m_fieldSim.getValidNodes().size() == 9);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CONE);
        assert(m_fieldSim.getValidNodes().size() == 6);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CUBE);
        assert(m_fieldSim.getValidNodes().size() == 3);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() < SimConstants.fieldLength / 2);
        }
    }

    @Test
    public void testBlueALlianceRedCooperatitionNodes() {
        TestUtils.setPrivateField(m_fieldSim, "m_currentAlliance", DriverStation.Alliance.Blue);
        m_swerveDrive.setOdometry(new Pose2d(SimConstants.fieldLength, 0, Rotation2d.fromDegrees(0)));
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.LOW);
        assert(m_fieldSim.getValidNodes().size() == 3);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CONE);
        assert(m_fieldSim.getValidNodes().size() == 2);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
        m_fieldSim.updateValidNodes(Constants.SCORING_STATE.MID_CUBE);
        assert(m_fieldSim.getValidNodes().size() == 1);
        for(var node:m_fieldSim.getValidNodes()) {
            assert(node.getX() > SimConstants.fieldLength / 2);
        }
    }
}
