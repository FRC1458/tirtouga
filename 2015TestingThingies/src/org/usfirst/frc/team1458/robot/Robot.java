package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//code for the guillotine
public class Robot extends SampleRobot {
	Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Joystick buttonPanel = new Joystick(2);

	Victor rightDriveFront = new Victor(1);
	Victor rightDriveRear = new Victor(0);
	Victor leftDriveFront = new Victor(3);
	Victor leftDriveRear = new Victor(2);
	Talon hDrive = new Talon(4);
	Talon armLeft = new Talon(7);
	Talon armRight = new Talon(6);
	Talon elevator = new Talon(5);
	
	Encoder leftEncoder = new Encoder(0,1);
	Encoder rightEncoder = new Encoder(2,3);
	Encoder hEncoder = new Encoder(4,5);
	Encoder elevatorEncoder = new Encoder(6,7);
	
	DigitalInput topLimit = new DigitalInput(8);
	DigitalInput bottomLimit = new DigitalInput(9);
	
	Compressor compressor = new Compressor();
	
	DoubleSolenoid hDriveLift = new DoubleSolenoid(0,1);
	DoubleSolenoid arms = new DoubleSolenoid(2,3);
	
	I2CMagnetometer maggie = new I2CMagnetometer();
	
	Infrared see = new Infrared(0,0);
	
	int leftMode, rightMode = 2;

	public Robot() {
		
	}

	public void autonomous() {
		while(isEnabled()) {
			hDrive.set(1);	
		}
		
	}

	public void operatorControl() {
		elevatorEncoder.reset();
		see.reset();
		while (isOperatorControl() && isEnabled()) {
			if(buttonPanel.getRawButton(5)) {
				buttonPanel.setOutput(1, false);
				buttonPanel.setOutput(2, true);
				buttonPanel.setOutput(3, true);
				buttonPanel.setOutput(4, true);
			} else if(buttonPanel.getRawButton(6)) {
				buttonPanel.setOutput(1, true);
				buttonPanel.setOutput(2, false);
				buttonPanel.setOutput(3, true);
				buttonPanel.setOutput(4, true);
			} else if(buttonPanel.getRawButton(7)) {
				buttonPanel.setOutput(1, true);
				buttonPanel.setOutput(2, true);
				buttonPanel.setOutput(3, false);
				buttonPanel.setOutput(4, true);
			} else if(buttonPanel.getRawButton(8)) {
				buttonPanel.setOutput(1, true);
				buttonPanel.setOutput(2, true);
				buttonPanel.setOutput(3, true);
				buttonPanel.setOutput(4, false);
			}
			maggie.update();
			SmartDashboard.putNumber("Direction", maggie.getAngle());
			SmartDashboard.putNumber("Infrared", see.getDistance());
			compressor.start();
			
			SmartDashboard.putNumber("L Encoder Count", leftEncoder.get());
			SmartDashboard.putNumber("L Encoder Raw Count", leftEncoder.getRaw());
			
			SmartDashboard.putNumber("R Encoder Count", rightEncoder.get());
			SmartDashboard.putNumber("R Encoder Raw Count", rightEncoder.getRaw());
			
			SmartDashboard.putNumber("H Encoder Count", hEncoder.get());
			SmartDashboard.putNumber("H Encoder Raw Count", hEncoder.getRaw());
			
			SmartDashboard.putNumber("E Encoder Count", elevatorEncoder.get());
			SmartDashboard.putNumber("E Encoder Raw Count", elevatorEncoder.getRaw());
			
			SmartDashboard.putBoolean("Top Limit", topLimit.get());
			SmartDashboard.putBoolean("Bottom Limit", bottomLimit.get());
			
			
			if (left.getRawButton(1)&&(leftMode!=0)) {
				//intake run
				armLeft.set(-1);
				armRight.set(1);
			} else if (leftMode!=0){
				//intake stop
				armLeft.set(0);
				armRight.set(0);
			}
			if (left.getRawButton(2))
			{
				leftMode = 0; // intake arms
			}
			if (left.getRawButton(3))
			{
				leftMode = 1; // drive motors
			}
			if (left.getRawButton(4))
			{
				leftMode = 2; // elevator
			}
			if (left.getRawButton(5))
			{
				arms.set(DoubleSolenoid.Value.kForward); //open arms
			}
			if (left.getRawButton(6))
			{
				arms.set(DoubleSolenoid.Value.kReverse); //close arms
			}
			
			if (right.getRawButton(1)&&(rightMode!=0)) {
				//intake run
				armLeft.set(1);
				armRight.set(-1);
			} else if (rightMode!=0){
				//intake stop
				armLeft.set(0);
				armRight.set(0);
			}
			if (right.getRawButton(2))
			{
				rightMode = 0; // intake arms
			}
			if (right.getRawButton(3))
			{
				rightMode = 1; // drive
			}
			if (right.getRawButton(4))
			{
				rightMode = 2; // hDrive
			}
			if (right.getRawButton(5))
			{
				hDriveLift.set(DoubleSolenoid.Value.kForward); //lower hDrive
			}
			if (right.getRawButton(6))
			{
				hDriveLift.set(DoubleSolenoid.Value.kReverse); //raise hDrive
			}
			
			switch (leftMode)
			{
			case 0: // intake
			{
				armLeft.set(-left.getAxis(Joystick.AxisType.kY));
				break;
			}
			case 1: // drive LEFT
			{
				leftDriveFront.set(-left.getAxis(Joystick.AxisType.kY));
				leftDriveRear.set(-left.getAxis(Joystick.AxisType.kY));
				break;
			}
			case 2: // elevator
			{
				if(-left.getAxis(Joystick.AxisType.kY)>0&&topLimit.get()) {
					elevator.set(0);
				} else if(-left.getAxis(Joystick.AxisType.kY)<0&&bottomLimit.get()) {
					elevator.set(0);
					elevatorEncoder.reset();
				} else {
					elevator.set(-left.getAxis(Joystick.AxisType.kY));
				}
		
				break;
				
			}
			}
			
			switch (rightMode)
			{
			case 0: // intake
			{
				armRight.set(-right.getAxis(Joystick.AxisType.kY));
				break;
			}
			case 1: // drive RIGHT
			{
				rightDriveFront.set(right.getAxis(Joystick.AxisType.kY));
				rightDriveRear.set(right.getAxis(Joystick.AxisType.kY));
				break;
			}
			case 2: // hDrive
			{
				hDrive.set(-right.getAxis(Joystick.AxisType.kY));
				break;
			}
			}
			
			
			
		}
	}

	public void test() {
	}
}
