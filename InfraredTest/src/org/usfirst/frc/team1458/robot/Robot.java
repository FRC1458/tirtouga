package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SampleRobot {
	Infrared infrared = new Infrared(3, 1);
	Timer timer2 = new Timer();

	public Robot() {
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	public void autonomous() {
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		infrared.reset();
		timer2.start();
		while (isEnabled()) {
			if (timer2.get() >= 0.1) {
				SmartDashboard.putNumber("Infrared test distance",infrared.getDistance());
				timer2.reset();
			}
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
}
