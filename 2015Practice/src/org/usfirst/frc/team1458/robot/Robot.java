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

	Encoder leftEncoder = new Encoder(0, 1);
	Encoder rightEncoder = new Encoder(2, 3);
	Encoder centreEncoder = new Encoder(4, 5);

	// I2CGyro gyro = new I2CGyro();

	/*
	 * Infrared seeRight = new Infrared(3, 0); Infrared seeLeft = new
	 * Infrared(4, 1);
	 */

	// LED ledStrip =.
	new LED();

	Compressor compressor = new Compressor(0);
	//
	DoubleSolenoid hSolenoid = new DoubleSolenoid(0, 1);
	DoubleSolenoid intakeSolenoid = new DoubleSolenoid(2, 3);

	PowerDistributionPanel pdp = new PowerDistributionPanel();

	RobotFunctions robot;

	Elevator elevator;

	Elevator.ElevatorMode elevatorMode = Elevator.ElevatorMode.CARRY;

	private final double kDriveToInches = 0.0697777;// circumference is 25.12
													// inches
	private final double kHToInches = (3 * Math.PI / 8);
	private int autoMode = 1;

	boolean abortMode = false;
	double desiredAngle;

	public Robot() {
		// initialise all of the things, for the elevator, robotfunctions, gyro,
		// etc.
		robot = new RobotFunctions();
		elevator = new Elevator();

	}

	public void reportEncoder() {
		SmartDashboard.putNumber("H Encoder", centreEncoder.getRaw());
		SmartDashboard.putNumber("L Encoder", leftEncoder.get());
		SmartDashboard.putNumber("R Encoder", rightEncoder.get());
	}

	/**
	 * Autonomous mode
	 */
	public void autonomous() {
		if (autoMode == 0) {// 3 tote auto with no container removal
			final double leftRightDistance = 9.0;
			// intial setup-should already be aligned at first tote with intake
			// at
			// bottom and arms open and h up
			if (isAutonomous() && isEnabled()) {
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
			}

			// drive forward a bit
			straightDistance(7.5);

			SmartDashboard.putString("Auto Status", "drove straight");
			// grab first tote
			if (isAutonomous() && isEnabled()) {
				elevator.setLevelMod(Levels.LevelMod.LOAD);
				elevator.setMainLevel(Levels.MainLevel.TWO);
				elevator.update();
				robot.eDrive(elevator.motorMovement);
			} else {
				return;
			}
			if (isAutonomous() && isEnabled()) {
				waitSasha(0.3);
				SmartDashboard.putString("Auto Status", "picked up");
				// lower h and move to right
				robot.hSolenoid(true);
				hDistance(leftRightDistance);
				SmartDashboard.putString("Auto Status", "went right");
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// go back forward
				robot.hSolenoid(false);
				straightDistance(24.0);
				SmartDashboard.putString("Auto Status", "went back forward");
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// left again-push first container out of way
				robot.hSolenoid(true);
				hDistance(-leftRightDistance);
				SmartDashboard.putString("Auto Status", "went left");
			} else {
				return;
			}
			if (isAutonomous() && isEnabled()) {
				// go forward for second tote-4 inches behind the right amount
				robot.hSolenoid(false);
				robot.aLeftDrive(1);
				robot.aRightDrive(-1);
				straightDistance(53.0);
				robot.intakeSolenoid(false);
				waitSasha(0.3);
				robot.aLeftDrive(0);
				robot.aRightDrive(0);
			} else {
				return;
			}
			if (isAutonomous() && isEnabled()) {
				// lower elevator
				elevator.setMainLevel(Levels.MainLevel.ONE);
				elevator.setLevelMod(Levels.LevelMod.UNLOAD);
				elevator.update();
				waitSasha(0.3);
			} else {
				return;
			}
			if (isAutonomous() && isEnabled()) {
				// raise elevator
				elevator.setMainLevel(Levels.MainLevel.TWO);
				elevator.setLevelMod(Levels.LevelMod.LOAD);
				elevator.update();
				waitSasha(0.3);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// lower h and move to right
				robot.hSolenoid(true);
				hDistance(leftRightDistance);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// go forward to third tote
				robot.hSolenoid(false);
				straightDistance(24.0);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// go back to left, pushing second container
				robot.hSolenoid(true);
				hDistance(-leftRightDistance);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// go forward to grab third tote
				robot.hSolenoid(false);
				robot.intakeSolenoid(true);
				robot.aLeftDrive(1);
				robot.aRightDrive(-1);
				straightDistance(57.0);
				robot.intakeSolenoid(false);
				waitSasha(0.3);
				robot.aLeftDrive(0);
				robot.aRightDrive(0);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// lower elevator
				elevator.setMainLevel(Levels.MainLevel.ONE);
				elevator.setLevelMod(Levels.LevelMod.UNLOAD);
				elevator.update();
				waitSasha(0.3);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// raise elevator
				elevator.setLevelMod(Levels.LevelMod.LOAD);
				elevator.update();
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// go right into auto zone
				robot.hSolenoid(true);
				hDistance(107.0);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// lower elevator to floor
				robot.hSolenoid(false);
				elevator.setLevelMod(Levels.LevelMod.UNLOAD);
			} else {
				return;
			}

			if (isAutonomous() && isEnabled()) {
				// back up to clear
				straightDistance(-36.0);
			} else {
				return;
			}
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

			robot.intakeSolenoid(true);
			robot.aLeftDrive(1.0);
			robot.aRightDrive(-1.0);
			elevator.setMainLevel(Levels.MainLevel.TWO);
			elevator.update();
			straightDistance(100); // 150ish in real
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

			elevator.setLevelMod(Levels.LevelMod.LOAD);// raise intake to grab
														// 1st tote
			elevator.setMainLevel(Levels.MainLevel.TWO);

			straightDistance(9.0);// move forward to intake 1st container
			elevator.setLevelMod(Levels.LevelMod.LOAD);
			elevator.setMainLevel(Levels.MainLevel.ONE);
			waitSasha(0.1);
			robot.intakeSolenoid(false);
			robot.aLeftDrive(-1.0);
			robot.aRightDrive(-1.0);
			waitSasha(0.3);
			robot.intakeSolenoid(true);
			elevator.setLevelMod(Levels.LevelMod.LOAD);
			elevator.setMainLevel(Levels.MainLevel.TWO);
			robot.aLeftDrive(1.0);
			robot.aRightDrive(-1.0);

			straightDistance(54.0);
			robot.intakeSolenoid(false);
			straightDistance(6.0);
			elevator.setLevelMod(Levels.LevelMod.LOAD);
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
			
		}

	}

	public void waitSasha(double time) {
		Timer t = new Timer();
		t.start();
		while (t.get() < time && isEnabled() && isAutonomous()) {
			// Party Time!
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			reportEncoder();
			SmartDashboard.putNumber("waitSasha time", t.get());
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

		rightEncoder.setDistancePerPulse(1.0);
		leftEncoder.setDistancePerPulse(1.0);
		centreEncoder.setDistancePerPulse(1.0);

		timer.start();
		// timer2.start();

		while (isEnabled() && isOperatorControl()) {
			reportEncoder();
			SmartDashboard.putNumber("H encoder", centreEncoder.get());
			SmartDashboard.putNumber("H encoder raw", centreEncoder.getRaw());

			// SmartDashboard.putNumber("test encoder", leftEncoder.get());

			SmartDashboard.putNumber("Update speed (Hz)", 1 / timer.get());
			timer.reset();
			// SmartDashboard.putNumber("Timer 2", timer2.get());
			/*
			 * if (timer2.get() >= 0.1) {
			 * SmartDashboard.putNumber("Infrared test distance",
			 * seeRight.getDistance()); timer2.reset(); }
			 */

			// gyroAngle = gyro.getAngle();
			// gyroRate = gyro.getRate();
			// System.out.println("Gyro Angle: " + gyroAngle);
			// System.out.println("Gyro Rate: " + gyroRate);
			// SmartDashboard.putNumber("Gyro Angle: ", gyroAngle);
			// SmartDashboard.putNumber("Gyro Rate: ", gyroRate);

			// gyro.update();

			elevator.update();
			robot.eDrive(elevator.motorMovement);
			if (elevator.getManual() || elevator.carryObject == Levels.CarryObject.CONTAINER) {
				manualIntake = true;
			} else {
				manualIntake = false;

			}
			if (manualIntake && !abortMode) {
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
					robot.aLeftDrive(1);
					robot.aRightDrive(-1);
				} else if (buttonPanel.getRawButton(2)) {
					robot.aLeftDrive(-1);
					robot.aRightDrive(1);
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
					abortMode = true;
				} else {
					abortMode = false;
				}
				if (buttonPanel.getRawButton(14) && abortMode == true) {
					robot.aLeftDrive(1);
					robot.aRightDrive(-1);
				} else if (buttonPanel.getRawButton(15) && abortMode == true) {
					robot.aLeftDrive(1);
					robot.aRightDrive(1);
				} else if (abortMode == true) {
					robot.aLeftDrive(0);
					robot.aRightDrive(0);
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

					if (/*toteCheck.getDistance() > 1.23 && */elevator.carryObject == Levels.CarryObject.TOTE) {
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

			/*
			 * // intake of a stack n high if (buttonPanel.getRawButton(9)) { //
			 * raise elevator to level n+1-done by humans elevatorMode =
			 * elevatorMode.INTAKE;
			 * elevator.setLevelMod(elevatorMode.getLevelMod()); // run intake
			 * // left 1, right -1
			 * 
			 * robot.intakeSolenoid(true); robot.aLeftdrive(1);
			 * robot.aRightdrive(-1);
			 * 
			 * // lower elevator to level 1 //
			 * 
			 * } else if (buttonPanel.getRawButton(10)) {
			 * robot.intakeSolenoid(true);
			 * elevator.setMainLevel(Levels.MainLevel.ONE); elevator.update();
			 * 
			 * // raise elevator to level n+1 // lower elevator to level n //
			 * disengage // drive straight back a little bit
			 * 
			 * } // intake loop stuff if (elevatorMode ==
			 * Elevator.ElevatorMode.INTAKE) { robot.intakeSolenoid(false); if
			 * (toteCheck.getDistance() < 1.23) {
			 * elevator.setMainLevel(Levels.MainLevel.ONE);
			 * elevator.setLevelMod(Levels.LevelMod.UNLOAD); elevator.update();
			 * }
			 * 
			 * } else if (elevatorMode == Elevator.ElevatorMode.OUTTAKE) {
			 * 
			 * } // check if complete if ((elevatorMode ==
			 * Elevator.ElevatorMode.INTAKE) && elevator.getAtHeight() &&
			 * (toteCheck.getDistance() > 1.23)) { elevatorMode =
			 * Elevator.ElevatorMode.CARRY; } else if
			 * ((elevatorMode==Elevator.ElevatorMode
			 * .OUTTAKE)&&elevator.getAtHeight()&&(toteCheck.getDistance()<0.5))
			 * { elevatorMode = Elevator.ElevatorMode.CARRY; }
			 */

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
			/*
			 * robot.ldrive(-left.getAxis(Joystick.AxisType.kY));
			 * robot.rdrive(-right.getAxis(Joystick.AxisType.kY));
			 */
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
			} else if (buttonPanel.getRawButton(12) && elevator.getManual()) {
				elevator.manualDown();
			} else if (elevator.getManual()) {
				elevator.stop();
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

	public void elevatorMotor(double power) {
		robot.eDrive(power);
	}

	/**
	 * Runs during test mode
	 */
	// in inches
	private void straightDistance(double distance) {
		double marginOfError = 0.3;// in inches
		double distanceTraveled = 0;// in inches
		double forwardSpeed = 0;
		double rightSpeed = 0;
		double leftSpeed = 0;
		// desiredAngle = maggie.getAngle();
		leftEncoder.reset();
		rightEncoder.reset();

		while (Math.abs(distanceTraveled - distance) > marginOfError && isEnabled() && isAutonomous()) {
			distanceTraveled = kDriveToInches * ((-leftEncoder.get() + rightEncoder.get()) / 2);
			SmartDashboard.putNumber("distance Traveled", distanceTraveled);
			// slowed down for now
			forwardSpeed = Math.atan(0.125 * (distance - distanceTraveled)) / (0.5 * Math.PI);//replace arctan with 1/(1+(1.0*e^(-1.0*x)))
			/*rightSpeed = forwardSpeed * (1 + (0.01 * (maggie.getAngle() - desiredAngle)));
			leftSpeed = forwardSpeed * (1 - 0.01 * (maggie.getAngle() - desiredAngle));*/
			robot.lDrive(leftSpeed);
			robot.rDrive(rightSpeed);
			elevator.update();
			robot.eDrive(elevator.motorMovement);
			reportEncoder();
		}
	}

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

	public void test() {

	}
}