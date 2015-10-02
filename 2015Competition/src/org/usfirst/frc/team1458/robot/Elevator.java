package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {

	// Elevator.Level.ONE.TOTE.CARRY
	// LIP=lip of tote
	// CARRY= carry level
	// PLACE = placing level
	Levels.MainLevel mainLevel = Levels.MainLevel.ONE;
	Levels.CarryObject carryObject = Levels.CarryObject.TOTE;
	Levels.LevelMode levelMode = Levels.LevelMode.FLOOR;
	Levels.LevelMod levelMod = Levels.LevelMod.LOAD;

	// Infrared elevatorBottom = new Infrared(0, 1);
	// Infrared elevatorTop = new Infrared(1, 1);
	private final double ACCEPTABLEERROR = 1.0;
	private boolean atHeight = false;

	Encoder elevatorEncoder = new Encoder(6, 7);

	DigitalInput topLimit = new DigitalInput(8);//should be 8
	DigitalInput bottomLimit = new DigitalInput(9);//should be 9
	AnalogInput analogLimit = new AnalogInput(1);

	double elevatorHeight;
	double desiredElevatorHeight;

	boolean isManual = true;
	boolean canMoveUp = true;
	boolean canMoveDown = true;
	boolean manualHold = false;
	boolean manualButtonsPressed = false;
	boolean manualStopHeight = false;

	Timer buttonTimer = new Timer();

	Levels levelHandler = new Levels();

	public double motorMovement;

	/**
	 * Enumerator for storing elevator states.
	 * @author Brendan, Turtwig of Team 1458
	 *
	 */
	public enum ElevatorMode {

		INTAKELIFT(), INTAKESUCK(), INTAKEDROP(), OUTTAKE(), CARRY();

		private ElevatorMode() {

		}
	}

	/**
	 * Tells whether the elevator is at its designated height, or within margin of error.
	 * @return Whether robot is within margin of error of designated height
	 */
	public boolean getAtHeight() {
		return atHeight;
	}
/**
 * Outputs the current elevator height.
 * @return Height of elevator in inches, relative to the lowest point of the elevator's travel.
 */
	public double getElevatorHeight() {
		return elevatorHeight;
	}
/**
 * Sets which mainLevel the elevator should be going to
 * @param mainLevel The mainLevel (one, two, etc.) that the elevator should head to.
 */
	public void setMainLevel(Levels.MainLevel mainLevel) {
		this.mainLevel = mainLevel;
		update();
	}
/**
 * The levelMode the elevator should be going to.
 * @param levelMode The LevelMode (FLOOR, STEP, etc.) that the elevator should head to.
 */
	public void setLevelMode(Levels.LevelMode levelMode) {
		this.levelMode = levelMode;
		update();
	}
/**
 * The CarryObject the elevator should be going to.
 * @param carryObject The CarryObject (TOTE or CONTAINER) the elevator should head to.
 */
	public void setCarryObject(Levels.CarryObject carryObject) {
		this.carryObject = carryObject;
		update();
	}
/**
 * The LevelMod the elevator should be going to.
 * @param levelMod The LevelMod (LOAD or UNLOAD) the elevator should head to.
 */
	public void setLevelMod(Levels.LevelMod levelMod) {
		this.levelMod = levelMod;
		update();
	}
/**
 * Gives the current LevelMode
 * @return current LevelMode of elevator
 */
	public Levels.LevelMode getLevelMode() {
		return levelMode;
	}
/**
 * Updates elevator height, movement, etc. Needs to be called each time in main loop, and followed by telling the elevator to move elevatorMovement.
 */
	public void update() {
		if (topLimit.get()) {
			canMoveUp = false;
		} else {
			canMoveUp = true;
		}
		if (!bottomLimit.get()) {
			elevatorEncoder.reset();
			canMoveDown = false;
		} else {
			canMoveDown = true;
		}
		if (!isManual) {
			desiredElevatorHeight = levelHandler.getHeight(mainLevel, levelMode, carryObject, levelMod);
		}
		if(!topLimit.get()) {
			SmartDashboard.putString("Turtwig", "Turtwig is the best!");
		}

		SmartDashboard.putNumber("desiredElevatorHeight", desiredElevatorHeight);
		SmartDashboard.putBoolean("Top Limit", topLimit.get());
		SmartDashboard.putBoolean("Bottom Limit", !bottomLimit.get());
		SmartDashboard.putNumber("Analog Limit", analogLimit.getVoltage());
		seeHeight();
		goTowardsDesired();

	}

	private void goTowardsDesired() {
		if ((!isManual)) {
			// code
			motorMovement = 0.2 * (desiredElevatorHeight - elevatorHeight);// 0.2
																			// is
																			// coeffecient
			if ((!canMoveUp) && motorMovement > 0) {
				motorMovement = 0;

			}
			if ((!canMoveDown) && motorMovement < 0) {
				motorMovement = 0;

			}
			if (Math.abs(desiredElevatorHeight - elevatorHeight) < ACCEPTABLEERROR) {
				atHeight = true;
			} else {
				atHeight = false;
			}
		} else if (manualHold) {
			motorMovement = 0.7 * (desiredElevatorHeight - elevatorHeight);
			if ((!canMoveUp) && motorMovement > 0) {
				motorMovement = 0;

			}
			if ((!canMoveDown) && motorMovement < 0) {
				motorMovement = 0;

			}
		} else {
			// do nothing
		}

	}
/**
 * Sets elevator to stop. Must be followed by setting elevator motor to motorMovement.
 */
	public void stop() {
		motorMovement = 0;
	}
	/**
	 * Sets elevator to stop. Must be followed by setting elevator motor to motorMovement. Also sets variables so manual hold code will engage.
	 */
	public void manualStop() {
		motorMovement = 0;
		manualButtonsPressed = false;
		buttonTimer.stop();
		manualHold = true;
		seeHeight();
		if (!manualStopHeight) {
			desiredElevatorHeight = elevatorHeight;
			manualStopHeight = true;
		}

	}
/**
 * Moves elevator at full power up for manual control.
 */
	public void manualUp() {
		update();
		manualHold = false;
		manualStopHeight = false;

		if (canMoveUp && isManual) {
			motorMovement = 1;
		} else if (!canMoveUp) {
			motorMovement = 0;
		}

	}
/**
 * Moves elevator at full power down for manual control.
 */
	public void manualDown() {
		update();
		manualHold = false;
		manualStopHeight = false;

		if (canMoveDown && isManual) {
			motorMovement = -1;
		} else if (!canMoveDown) {
			motorMovement = 0;
		}

	}
/**
 * Sets the power for manual mode
 * @param power The power the elevator should move at.
 */
	public void manualAmount(double power) {
		update();
		if (canMoveUp && canMoveDown && isManual) {
			motorMovement = power;
		}
	}
/**
 * Sets whether the robot should be in manual mode.
 * @param manual Whether robot should be in manual mode.
 */
	public void setManual(boolean manual) {
		this.isManual = manual;
	}
/**
 * Tells whether the robot is in manual mode.
 * @return Whether is in manual mode.
 */
	public boolean getManual() {
		return isManual;
	}

	private void seeHeight() {
		// returns height in inches
		elevatorHeight = /*
						 * 7.3125 + disabled b/c measuring from bottom of
						 * elevator
						 */
		(0.01121383 * elevatorEncoder.get());
		// 0.01126126
		// 0.01125687
		// 0.01112335

	}
}