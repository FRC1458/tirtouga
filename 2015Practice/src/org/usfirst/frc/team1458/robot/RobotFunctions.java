package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Gyro;

public class RobotFunctions {
	
	/*boolean hMode = false;
	Victor rightDrive = new Victor(1);
	Victor leftDrive = new Victor(3);
	Victor hDrive = new Victor(4 2);
	Talon armLeft = new Talon(7);
	Talon armRight = new Talon(6);
	Victor elevator = new Victor(4);*/
	
	boolean hMode = false;
	private Victor rightDriveFront = new Victor(1);
	private Victor rightDriveRear = new Victor(0);
	private Victor leftDriveFront = new Victor(3);
	private Victor leftDriveRear = new Victor(2);
	private Talon hDrive = new Talon(4);
	private Talon armLeft = new Talon(7);
	private Talon armRight = new Talon(6);
	private Talon elevator = new Talon(5);
	
	DoubleSolenoid centreSolenoid = new DoubleSolenoid(0, 1);
	DoubleSolenoid intakeSolenoid = new DoubleSolenoid(2, 3);
	
	RobotFunctions() {
		
	}
	/**
	 * Sets the solenoid on the h drive to be up or down.
	 * @param down Whether the h Solenoid is down.
	 */
	public void hSolenoid(boolean down) {
		if(down) {
			centreSolenoid.set(DoubleSolenoid.Value.kReverse);
		} else {
			centreSolenoid.set(DoubleSolenoid.Value.kForward);
		}
	}
	/**
	 * Sets the solenoid on the arms to be open or closed
	 * @param open Whether the arms are open are closed.
	 */
	public void intakeSolenoid(boolean open) {
		if(open) {
			intakeSolenoid.set(DoubleSolenoid.Value.kForward);
		} else {
			intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
	}
	/**
	 * Easier way of setting both right and left motors to move.
	 * @param rightPower Power to right motors, positive is forwards
	 * @param leftPower Power to left motors, positive is forwards
	 */
	public void tankDrive(double rightPower, double leftPower)
	{
		rDrive(rightPower);
		lDrive(leftPower);
	
	}
	/**
	 * Sets right motor power.
	 * @param power Power to right motors, positive is forwards
	 */
	public void rDrive(double power) {
		rightDriveFront.set(-power);
		rightDriveRear.set(-power);
		hDrive.set(0);
		SmartDashboard.putNumber("R Power", power);
	}
	/**
	 * Sets left motor power.
	 * @param power Power to left motors, positive is forwards
	 */
	public void lDrive(double power) {
		leftDriveFront.set(power);
		leftDriveRear.set(power);
		hDrive.set(0);
		SmartDashboard.putNumber("L Power", power);
	}
	/**
	 * Sets h motor power.
	 * @param power Power to h motor
	 */
	public void hDrive(double power) {
		hDrive.set(power);
		rightDriveFront.set(0);
		rightDriveRear.set(0);
		leftDriveFront.set(0);
		leftDriveRear.set(0);
		SmartDashboard.putNumber("H power", power);
	}
	/**
	 * Sets left arm power
	 * @param power Left arm motor power
	 */
	public void aLeftDrive(double power) {
		armLeft.set(power);
	}
	/**
	 * Sets right arm power
	 * 
	 * @param power Right arm motor power
	 */
	public void aRightDrive(double power) {
		armRight.set(power);
	}
	
	/**
	 * Sets elevator motor power
	 * @param power Elevator motor power, positive is up
	 */
	public void eDrive(double power) {
		elevator.set(power);
		SmartDashboard.putNumber("E power", power);
	}
	
}