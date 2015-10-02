package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Joystick buttonPanel = new Joystick(2);

	Timer timer = new Timer();
	// Timer timer2 = new Timer();

	Encoder leftEncoder = new Encoder(0, 1); // should be 0,1
	Encoder rightEncoder = new Encoder(2, 3);
	Encoder centreEncoder = new Encoder(4, 5);

	// I2CGyro gyro = new I2CGyro();

	I2CMagnetometer maggie;

	Infrared toteCheck = new Infrared(0, 0);
	/*
	 * Infrared seeRight = new Infrared(3, 0); Infrared seeLeft = new
	 * Infrared(4, 1);
	 */

	// LED ledStrip = new LED();

	Compressor compressor = new Compressor(0);
	// DoubleSolenoid hSolenoid = new DoubleSolenoid(0, 1);
	// DoubleSolenoid intakeSolenoid = new DoubleSolenoid(2, 3);

	PowerDistributionPanel pdp = new PowerDistributionPanel();

	RobotFunctions robot;

	Elevator elevator;

	Elevator.ElevatorMode elevatorMode = Elevator.ElevatorMode.CARRY;

	private final double kDriveToInches = /* ((8*Math.PI)/360) */0.0697777777;// circumference
																			// is
																			// 25.12
																			// inches
	private final double kHToInches = (3 * Math.PI / 8);
	/**
	 * Sets which autoMode to use. 
	 * -1= do nothing, 
	 * 0=nothing, 
	 * 1=old container push, 
	 * 2=3 tote w/ intake move, 
	 * 3=150in forward, 
	 * 4=accurate into auto zone, 
	 * 5=inaccurate into auto zone, 
	 * 6=1 tote spin then auto, 
	 * 7=container back into auto, 
	 * 8= accurate into auto
	 * 9= ???
	 * 10 = start around a can and pick it up to level 2
	 * 11 = pickup can no move
	 */
	private int autoMode = 10;

	// boolean abortMode = false;
	double desiredAngle;
	private boolean safetyOpen = false;

	/**
	 * Initialisation code
	 */
	public Robot() {
		// initialise all of the things, for the elevator, robotfunctions, gyro,
		// etc.
		robot = new RobotFunctions();
		elevator = new Elevator();
		maggie = new I2CMagnetometer();
		maggie.setCalibration(1236, 1287, 228, 219);

	}

	/**
	 * Outputs encoder values for h, l, and r to smartDashboard.
	 */
	public void reportEncoder() {
		SmartDashboard.putNumber("H Encoder", centreEncoder.getRaw());
		SmartDashboard.putNumber("L Encoder", -leftEncoder.get());
		SmartDashboard.putNumber("R Encoder", rightEncoder.get());
		SmartDashboard.putNumber("Elevator Height", elevator.elevatorHeight);
	}

	/**
	 * Autonomous mode
	 */
	public void autonomous() {
		if (autoMode == 0) {// 3 tote auto with no container removal
		} else if (autoMode == 1) {
			// straight push container
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;
			desiredAngle = maggie.getContinousAngle();

			robot.intakeSolenoid(true);
			robot.aLeftDrive(1.0);
			robot.aRightDrive(-1.0);
			elevator.setMainLevel(Levels.MainLevel.TWO);
			elevator.update();
			straightDistance(150); // 150ish in real
			elevator.setMainLevel(Levels.MainLevel.ONE);
			straightDistance(-5);
		} else if (autoMode == 2) {
			// 3 tote with intake to move container

			// initialisation code
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;
			desiredAngle = maggie.getContinousAngle();

			elevator.setLevelMod(Levels.LevelMod.LOAD);
			elevator.setMainLevel(Levels.MainLevel.TWO);
			robot.aLeftDrive(1.0);
			robot.aRightDrive(-1.0);

			straightDistance(54.0);
			robot.intakeSolenoid(false);
			straightDistance(6.0);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			waitSasha(0.1);
			robot.aLeftDrive(-1.0);
			robot.aRightDrive(-1.0);

			waitSasha(0.3);
			elevator.setLevelMod(Levels.LevelMod.LOAD);
			elevator.setMainLevel(Levels.MainLevel.TWO);
			waitSasha(0.1);
			robot.aLeftDrive(1.0);
			robot.aRightDrive(-1.0);
			robot.intakeSolenoid(true);

			straightDistance(54.0);
			robot.intakeSolenoid(false);
			straightDistance(6.0);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			waitSasha(0.1);
			waitSasha(0.3);
			elevator.setLevelMod(Levels.LevelMod.LOAD);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			waitSasha(0.1);
			robot.hSolenoid(true);
			hDistance(104.0);
			robot.hSolenoid(false);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			straightDistance(-36.0);

		} else if (autoMode == 3) {
			desiredAngle = maggie.getContinousAngle();
			robot.intakeSolenoid(true);
			straightDistance(150.0);

			waitSasha(10.0);
		} else if (autoMode == 4) {
			desiredAngle = maggie.getContinousAngle();
			straightDistance(61.0); // was 24.0, we needed to cut it in half

			waitSasha(10.0);
		} else if (autoMode == 5) {
			robot.lDrive(1.0);
			robot.rDrive(1.0);
			waitSasha(0.8);
			robot.lDrive(0);
			robot.rDrive(0);
			waitSasha(15);
		} else if (autoMode == 6) {
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;

			elevator.setMainLevel(Levels.MainLevel.TWO);
			waitSasha(0.3);

			makeSpinEncoder(90);
			desiredAngle = maggie.getContinousAngle();
			straightDistance(24.0);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			waitSasha(15);
		} else if (autoMode == 7) {
			// container then back up
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;

			elevator.setMainLevel(Levels.MainLevel.TWO);
			waitSasha(3.0);
			straightDistance(-85.0);
			waitSasha(15.0);

		} else if (autoMode == 8) {

			straightDistance(58); //was 61, then 56 robot is 55.75 inches lon
			waitSasha(15);
		} else if (autoMode==9){
			//container move test
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;
			
			robot.aLeftDrive(-0.75);
			robot.aRightDrive(-0.75);
			straightDistance(24.0);
			robot.aLeftDrive(0);
			robot.aRightDrive(0);
		}else if (autoMode==10){
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;

			elevator.setMainLevel(Levels.MainLevel.THREE);
			waitSasha(5.0);
			straightDistance(-85.0);
			waitSasha(15.0);
			
		} else if (autoMode==11){
			SmartDashboard.putString("Auto Status", "starting");
			elevator.setCarryObject(Levels.CarryObject.TOTE);
			elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			elevator.setLevelMode(Levels.LevelMode.FLOOR);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			robot.intakeSolenoid(true);
			robot.hSolenoid(false);
			elevator.isManual = false;

			elevator.setMainLevel(Levels.MainLevel.THREE);
			waitSasha(15.0);
			
		}
		else {
			// do nothing
			waitSasha(15);
		}

	}

	/**
	 * Makes the robot go fowards a certain distance
	 * 
	 * @param distance
	 *            Distance to go forwards in inches. Negative values will move
	 *            backwards.
	 */
	private void straightDistance(double distance) {
		double marginOfError = 0.5;// in inches
		double distanceTraveled = 0;// in inches
		double forwardSpeed = 0;
		double rightSpeed = 0;
		double leftSpeed = 0;
		double leftEncoderValue = 0;
		double rightEncoderValue = 0;
		double leftDistance = 0;
		double rightDistance = 0;
		double leftRate = 0;
		double rightRate = 0;
		double rate = 0;
		double diff = 0;
		// desiredAngle = maggie.getContinousAngle();
		leftEncoder.reset();
		rightEncoder.reset();
		leftEncoder.setDistancePerPulse(1);
		rightEncoder.setDistancePerPulse(1);

		while (Math.abs(distanceTraveled - distance) > marginOfError && isEnabled() && isAutonomous()) {
			leftEncoderValue = (double) -leftEncoder.get();
			rightEncoderValue = (double) rightEncoder.get();
			leftRate = -leftEncoder.getRate();
			rightRate = rightEncoder.getRate();
			rate = (leftRate + rightRate) / 2;
			SmartDashboard.putNumber("R Encoder", rightEncoderValue);
			SmartDashboard.putNumber("L Encoder", leftEncoderValue);
			leftDistance = (double) (0.0697777 * leftEncoderValue);
			rightDistance = (double) (0.0697777 * rightEncoderValue);
			diff=rightDistance-leftDistance;
			SmartDashboard.putNumber("Left Distance", leftDistance);
			SmartDashboard.putNumber("Right Distance", rightDistance);
			distanceTraveled = (leftDistance + rightDistance) / 2;
			SmartDashboard.putNumber("distance Traveled", distanceTraveled);
			SmartDashboard.putNumber("L DistanceEncoder", SmartDashboard.getNumber("Left Distance") / SmartDashboard.getNumber("L Encoder"));
			SmartDashboard.putNumber("R DistanceEncoder", SmartDashboard.getNumber("Right Distance") / SmartDashboard.getNumber("R Encoder"));
			SmartDashboard.putNumber("R Distance calc-output diff", rightEncoderValue * 0.0697777 - SmartDashboard.getNumber("Right Distance"));
			SmartDashboard.putNumber("L Distance calc-output diff", leftEncoderValue * 0.0697777 - SmartDashboard.getNumber("Left Distance"));
			// this line intentionally left blank
			forwardSpeed = 0.99 * (2 / (1 + Math.pow(1.4, -0.25 * (distance - distanceTraveled))) - 1) + 0.01 * (-(0.0697777 * rate));//was 99, 1
			rightSpeed = forwardSpeed/* *(1-0.2*diff)*/;
			leftSpeed = forwardSpeed/* *(1+0.2*diff)*/;
			robot.lDrive(leftSpeed);
			robot.rDrive(rightSpeed);
			elevator.update();
			robot.eDrive(elevator.motorMovement);

			SmartDashboard.putNumber("kDriveToInches", kDriveToInches);

			// SmartDashboard.putNumber("Number that should work",
			// 0.06977777*393.000);//calculated as 13.676
			// reportEncoder();
		}
		robot.lDrive(0);
		robot.rDrive(0);
	}

	/**
	 * Makes robot go right a certain distance
	 * 
	 * @param distance
	 *            Distance to go right in inches. Negative values will move
	 *            left.
	 */
	private void hDistance(double distance) {
		double marginOfError = 1.5;
		double hSpeed = 0;
		centreEncoder.reset();
		while ((Math.abs((kHToInches * centreEncoder.get()) - distance) > marginOfError) && isEnabled() && isAutonomous()) {
			hSpeed = Math.atan(0.075 * (distance - (kHToInches * centreEncoder.get()))) / (Math.PI * 0.5);
			robot.hDrive(hSpeed);
			SmartDashboard.putNumber("hSpeed", hSpeed);
			SmartDashboard.putNumber("H distance", kHToInches * centreEncoder.get());
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			reportEncoder();
		}
	}

	/**
	 * Makes the robot change its direction.
	 * 
	 * @param targetAngle
	 *            Angle to change direction by, positive is to right
	 */
	private void makeSpin(double targetAngle) {
		double marginOfError = 3;
		double leftSpeed = 0;
		double rightSpeed = 0;
		double delta = targetAngle;
		targetAngle += maggie.getContinousAngle();
		SmartDashboard.putNumber("targetAngle", targetAngle);
		/*
		 * if(targetAngle<0) { targetAngle+=360; } targetAngle=targetAngle%360;
		 */
		while ((Math.abs(delta)) > marginOfError && isEnabled() && isAutonomous()) {
			maggie.update();
			delta = targetAngle - maggie.getContinousAngle();
			SmartDashboard.putNumber("delts", delta);
			rightSpeed = -delta * 0.02;
			leftSpeed = delta * 0.02;
			robot.rDrive(rightSpeed);
			robot.lDrive(leftSpeed);
		}

	}

	private void makeSpinEncoder(double targetAngle) {
		double moveamount = -((targetAngle / 360) * (Math.PI * 24));// -18.1
		double marginOfError = 0.8;
		double lvalue;
		double rvalue;
		double ldistance = 0;
		double rdistance = 0;
		double avgdistance = 0;
		double speed = 0;
		leftEncoder.reset();
		rightEncoder.reset();
		while (isAutonomous() && isEnabled() && Math.abs(avgdistance - moveamount) > marginOfError) {
			maggie.update();

			lvalue = (double) leftEncoder.get();
			rvalue = (double) rightEncoder.get();
			SmartDashboard.putNumber("L Encoder", lvalue);
			SmartDashboard.putNumber("R Encoder", rvalue);

			rdistance = (double) (rvalue * 0.0697777);
			ldistance = (double) (lvalue * 0.0697777);
			SmartDashboard.putNumber("Right Distance", rdistance);
			SmartDashboard.putNumber("Left Distance", ldistance);
			avgdistance = (rdistance + ldistance) / 2;
			SmartDashboard.putNumber("distance traveled", avgdistance);
			speed = (2 / (1 + Math.pow(1.4, (-0.6 * (moveamount - avgdistance))))) - 1;

			robot.lDrive(-speed);
			robot.rDrive(speed);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
		}
		robot.lDrive(0);
		robot.rDrive(0);

	}

	/**
	 * Waits for some time while updating elevator and maggie
	 * 
	 * @param time
	 *            The time in seconds to wait for.
	 */
	public void waitSasha(double time) {
		Timer t = new Timer();
		t.start();
		while (t.get() < time && isEnabled() && isAutonomous()) {
			// Party Time!
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			reportEncoder();
			SmartDashboard.putNumber("waitSasha time", t.get());
			maggie.update();
		}
		return;
	}

	/**
	 * operator controlled mode
	 */
	public void operatorControl() {
		elevatorMode = Elevator.ElevatorMode.CARRY;
		robot.aLeftDrive(0);
		robot.aRightDrive(0);
		robot.hDrive(0);
		robot.lDrive(0);
		robot.rDrive(0);
		robot.intakeSolenoid(true);

		Timer intakeTimer = new Timer();

		compressor.start();

		// gyro.reset();
		// gyro.update();
		// double gyroAngle;
		// double gyroRate;

		boolean manualIntake = false;

		// seeRight.reset();

		maggie.zero();

		rightEncoder.setDistancePerPulse(1.0);
		leftEncoder.setDistancePerPulse(1.0);
		centreEncoder.setDistancePerPulse(1.0);

		timer.start();

		while (isEnabled() && isOperatorControl()) {

			reportEncoder();

			SmartDashboard.putNumber("toteCheck IR", toteCheck.getDistance());

			SmartDashboard.putNumber("Update speed (Hz)", 1 / timer.get());
			timer.reset();

			if (maggie.isReady()) {
				maggie.update();
			}
			SmartDashboard.putNumber("Direction", maggie.getAngle());
			SmartDashboard.putNumber("Continous Direction", maggie.getContinousAngle());

			elevator.update();
			robot.eDrive(elevator.motorMovement);
			if (elevator.getManual() || elevator.carryObject == Levels.CarryObject.CONTAINER) {
				manualIntake = true;
			} else {
				manualIntake = false;

			}
			if (manualIntake) {
				if (buttonPanel.getRawButton(14)) {
					elevator.setLevelMod(Levels.LevelMod.LOAD);
				} else {
					elevator.setLevelMod(Levels.LevelMod.UNLOAD);
				}
				if (buttonPanel.getRawButton(9)) {
					robot.intakeSolenoid(true);
				} else if (buttonPanel.getRawButton(10)) {
					robot.intakeSolenoid(false);
				}
				if (buttonPanel.getRawButton(1)) {
					// in
					robot.aLeftDrive(0.75);
					robot.aRightDrive(-0.75);
				} else if (buttonPanel.getRawButton(3)) {
					// right
					robot.aLeftDrive(0.75);
					robot.aRightDrive(0.75);
				} else if (buttonPanel.getRawButton(2)) {
					// out
					robot.aLeftDrive(-0.75);
					robot.aRightDrive(0.75);
				} else if (buttonPanel.getRawButton(4)) {
					// left
					robot.aLeftDrive(-0.75);
					robot.aRightDrive(-0.75);
				} else {
					robot.aLeftDrive(0);
					robot.aRightDrive(0);
				}
			} else {
				// new intake code
				if (buttonPanel.getRawButton(9) && elevatorMode == Elevator.ElevatorMode.CARRY) {
					// start intake
					elevatorMode = Elevator.ElevatorMode.INTAKELIFT;
					if (elevator.carryObject == Levels.CarryObject.TOTE) {
						elevator.setMainLevel(Levels.MainLevel.TWO);
						elevator.setLevelMod(Levels.LevelMod.LOAD);
						//
						/*
						 * robot.aLeftDrive(1); robot.aRightDrive(-1);
						 * robot.intakeSolenoid(false); waitSasha(1.0);
						 * robot.aLeftDrive(0); robot.aRightDrive(0);
						 */
					} else {
						elevator.setMainLevel(Levels.MainLevel.ONE);
						elevator.setLevelMod(Levels.LevelMod.UNLOAD);
					}
					elevator.update();
					robot.eDrive(elevator.motorMovement);

				} else if (buttonPanel.getRawButton(10) && elevatorMode == Elevator.ElevatorMode.CARRY) {
					// start outtake
					elevatorMode = Elevator.ElevatorMode.OUTTAKE;
					// this was commented out for some reason? I brought it back
					// at lunch today.
					elevator.setMainLevel(Levels.MainLevel.ONE);
					elevator.setLevelMod(Levels.LevelMod.UNLOAD);
					if (elevator.carryObject == Levels.CarryObject.CONTAINER) {
						elevator.setMainLevel(Levels.MainLevel.THREE);
					}
					elevator.update();
				}
				if (buttonPanel.getRawButton(16)) {
					elevatorMode = Elevator.ElevatorMode.CARRY;

				} else {

				}

				if (elevatorMode == Elevator.ElevatorMode.INTAKELIFT) {
					// intake lift loop
					elevator.update();
					if (elevator.getAtHeight()) {
						elevatorMode = Elevator.ElevatorMode.INTAKESUCK;
						robot.intakeSolenoid(true);
						robot.aLeftDrive(1);
						robot.aRightDrive(-1);
						intakeTimer.reset();
						intakeTimer.start();

					}

				} else if (elevatorMode == Elevator.ElevatorMode.INTAKESUCK) {
					if (intakeTimer.get() > 2) {
						robot.intakeSolenoid(false);
					}

					if (toteCheck.getDistance() > 1.23 && elevator.carryObject == Levels.CarryObject.TOTE) {
						// set to drop
						elevatorMode = Elevator.ElevatorMode.INTAKEDROP;
						elevator.setMainLevel(Levels.MainLevel.ONE);
						elevator.setLevelMod(Levels.LevelMod.UNLOAD);
						robot.aLeftDrive(0);
						robot.aRightDrive(0);
						elevator.update();
					} else if (elevator.carryObject == Levels.CarryObject.CONTAINER && intakeTimer.get() > 2) {
						elevatorMode = Elevator.ElevatorMode.CARRY;
					}

				} else if (elevatorMode == Elevator.ElevatorMode.INTAKEDROP) {
					// drop loop
					elevator.update();
					if (elevator.getAtHeight()) {
						// intake end
						elevator.setLevelMod(Levels.LevelMod.LOAD);
						elevator.setMainLevel(Levels.MainLevel.TWO);
						elevator.update();
						elevatorMode = Elevator.ElevatorMode.CARRY;
					}
				} else if (elevatorMode == Elevator.ElevatorMode.OUTTAKE) {
					// outtake loop
					robot.intakeSolenoid(true);
					if (elevator.getAtHeight()) {
						// outtake end
						robot.intakeSolenoid(false);
						elevatorMode = Elevator.ElevatorMode.CARRY;
					}
				} else if (elevatorMode == Elevator.ElevatorMode.CARRY) {
					// carry loop
					elevator.setLevelMod(Levels.LevelMod.UNLOAD);
					if (elevator.carryObject == Levels.CarryObject.CONTAINER) {
						elevator.setLevelMod(Levels.LevelMod.LOAD);
					}
				}
			}

			// drive code
			if (left.getRawButton(1)) {
				// straight drive
				robot.tankDrive(-right.getAxis(Joystick.AxisType.kY), -right.getAxis(Joystick.AxisType.kY));
			} else if (!robot.hMode) {
				// normal tank drive
				robot.tankDrive(-right.getAxis(Joystick.AxisType.kY), -left.getAxis(Joystick.AxisType.kY));
			}

			if (right.getRawButton(1)) {
				// deploy H
				robot.hSolenoid(true);
				robot.hMode = true;
				// robot.hdrive(right.getAxis(Joystick.AxisType.kX));
			} else {
				// retract H
				robot.hSolenoid(false);
				robot.hMode = false;
			}
			if (robot.hMode) {
				// drive in h mode
				robot.hDrive(right.getAxis(Joystick.AxisType.kX));
			}

			// use buttons to set mainLevel
			if (buttonPanel.getRawButton(1)) {
				elevator.setMainLevel(Levels.MainLevel.ONE);
			} else if (buttonPanel.getRawButton(2)) {
				elevator.setMainLevel(Levels.MainLevel.TWO);
			} else if (buttonPanel.getRawButton(3)) {
				elevator.setMainLevel(Levels.MainLevel.THREE);
			} else if (buttonPanel.getRawButton(4)) {
				elevator.setMainLevel(Levels.MainLevel.FOUR);
			}
			// use buttons to set levelmode and manual
			if (buttonPanel.getRawButton(5)) {
				elevator.setLevelMode(Levels.LevelMode.FLOOR);
				elevator.setManual(false);
			} else if (buttonPanel.getRawButton(6)) {
				elevator.setLevelMode(Levels.LevelMode.PLATFORM);
				elevator.setManual(false);
			} else if (buttonPanel.getRawButton(7)) {
				elevator.setLevelMode(Levels.LevelMode.STEP);
				elevator.setManual(false);
			} else if (buttonPanel.getRawButton(8)) {
				elevator.setManual(true);
			}

			// use switch to set tote/container
			if (!buttonPanel.getRawButton(13)) {
				elevator.setCarryObject(Levels.CarryObject.TOTE);
			} else {
				elevator.setCarryObject(Levels.CarryObject.CONTAINER);
			}

			// control in manual mode
			if (buttonPanel.getRawButton(11) && elevator.getManual()) {
				elevator.manualUp();
				safetyArmThing();
			} else if (buttonPanel.getRawButton(12) && elevator.getManual()) {
				elevator.manualDown();
				safetyArmThing();
			} else if (elevator.getManual()) {
				elevator.manualStop();
				safetyArmThing();
			}
			// control leds on driver station
			if (!elevator.getManual()) {
				if (elevator.levelMode == Levels.LevelMode.FLOOR) {
					buttonPanel.setOutput(1, false);
					buttonPanel.setOutput(2, true);
					buttonPanel.setOutput(3, true);
					buttonPanel.setOutput(4, true);
				} else if (elevator.levelMode == Levels.LevelMode.PLATFORM) {
					buttonPanel.setOutput(1, true);
					buttonPanel.setOutput(2, false);
					buttonPanel.setOutput(3, true);
					buttonPanel.setOutput(4, true);
				} else if (elevator.levelMode == Levels.LevelMode.STEP) {
					buttonPanel.setOutput(1, true);
					buttonPanel.setOutput(2, true);
					buttonPanel.setOutput(3, false);
					buttonPanel.setOutput(4, true);
				}
			} else {
				buttonPanel.setOutput(1, true);
				buttonPanel.setOutput(2, true);
				buttonPanel.setOutput(3, true);
				buttonPanel.setOutput(4, false);
			}
		}
	}

	private void safetyArmThing() {
		boolean heightRange = elevator.elevatorHeight >= 0.1 && elevator.elevatorHeight <= 12.75;
		SmartDashboard.putBoolean("Arms Open", robot.armsOpen);
		if (heightRange && safetyOpen == false && (!robot.armsOpen)) {
			robot.intakeSolenoid(true);
			safetyOpen = true;
		} else if (safetyOpen && !heightRange && robot.armsOpen) {
			robot.intakeSolenoid(false);
			safetyOpen = false;
		} else if (safetyOpen && !heightRange) {
			safetyOpen = false;
		}
	}

	/**
	 * Test mode. We've never used this.
	 */
	public void test() {

	}
}