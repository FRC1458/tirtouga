package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Joystick xbox = new Joystick(2);
	Talon rightDriveFront = new Talon(1);
	Talon rightDriveRear = new Talon(0);
	Talon leftDriveFront = new Talon(3);
	Talon leftDriveRear = new Talon(2);
	Talon centreDrive = new Talon(4);
	I2CMagnetometer maggie = new I2CMagnetometer();

	/*
	 * Encoder leftEncoder = new Encoder(0,1); Encoder rightEncoder = new
	 * Encoder(2,3);
	 */

	public Robot() {

	}

	/**
	 * Autonomous mode
	 */
	public void autonomous() {

	}

	/**
	 * operator controlled mode
	 */
	public void operatorControl() {
		Timer t = new Timer();
		int xCentre;
		int yCentre;
		int xScale;
		int yScale;
		boolean buttonPress = false;

		while (isEnabled()) {
			maggie.update();
			xCentre = (int) ((maggie.xMax + maggie.xMin) / 2);
			yCentre = (int) ((maggie.yMax + maggie.yMin) / 2);
			xScale = (int) maggie.xMax - xCentre;
			yScale = (int) maggie.yMax - yCentre;

			SmartDashboard.putNumber("xCentre", xCentre);
			SmartDashboard.putNumber("yCentre", yCentre);
			SmartDashboard.putNumber("xScale", xScale);
			SmartDashboard.putNumber("yScale", yScale);

			if (right.getRawButton(1) && !buttonPress) {
				maggie.setCalibration(xCentre, yCentre, xScale, yScale);
				buttonPress=true;
			} else if (right.getRawButton(2) && !buttonPress) {
				maggie.setCalibration(0, 0, 1, 1);
				buttonPress=true;
			} else if (right.getRawButton(3)) {
				maggie.setCalibration(1236, 1287, 228, 219);
				buttonPress=true;
			} else {
				if(t.get()==0) {
					t.start();
				}
				
				if(t.get()>1.0) {
					buttonPress = false;
					t.reset();
					t.stop();
					t.reset();
				}
				
			}
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {

	}
}