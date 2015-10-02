package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {

	Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Talon rightDriveFront = new Talon(3);
	Talon rightDriveRear = new Talon(2);
	Talon leftDriveFront = new Talon(1);
	Talon leftDriveRear = new Talon(0);

	public double sgn(double x) {
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}

	}

	public Robot() {

	}

	public void autonomous() {

	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		double y;
		double x;
		double m;
		double xa;
		double ya;
		double angle;
		double adjustAngle = Math.PI / 4;

		while (isEnabled()) {
			//adjustAngle=gyro.get();
			y = right.getRawAxis(1);
			x = right.getRawAxis(0);
			m = Math.sqrt(x * x + y * y);
			//hackish way to not divide by zero
			if(m==0) {
				m=0.000000000001;
			}
			angle = Math.asin(y / m);
			if (sgn(y)>=0&&sgn(x)>=0) {
				//Quadrant I
				angle = angle;
			} else if (sgn(x)<=0&&sgn(y)>=0) {
				//Quadrant II
				angle = Math.PI-angle;
			} else if(sgn(x)<=0&&sgn(y)<=0) {
				//Quadrant III
				angle+=Math.PI;
			} else if(sgn(x)>=0&&sgn(y)<=0) {
				//Quadrant IV
				angle=2*Math.PI-angle;
			}
			angle-=adjustAngle;
			xa=m*Math.cos(angle);
			ya=m*Math.sin(angle);
			rightDriveFront.set(xa);
			leftDriveRear.set(xa);
			rightDriveRear.set(ya);
			leftDriveFront.set(ya);
		}

	}

	/**
	 * Runs during test mode
	 */
	public void test() {

	}
}
