package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
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

	DigitalInput topLimit = new DigitalInput(8);
	DigitalInput bottomLimit = new DigitalInput(9);

	double elevatorHeight;
	double desiredElevatorHeight;

	boolean isManual = true;
	boolean canMoveUp = true;
	boolean canMoveDown = true;


	Levels levelHandler = new Levels();

	public double motorMovement;

	public enum ElevatorMode {

		INTAKELIFT(), INTAKESUCK(), INTAKEDROP(), OUTTAKE(), CARRY();

		private ElevatorMode() {
			
		}
	}
	
	public boolean getAtHeight() {
		return atHeight;
	}

	public double getElevatorHeight() {
		return elevatorHeight;
	}

	public void setMainLevel(Levels.MainLevel mainLevel) {
		this.mainLevel = mainLevel;
		update();
	}

	public void setLevelMode(Levels.LevelMode levelMode) {
		this.levelMode = levelMode;
		update();
	}

	public void setCarryObject(Levels.CarryObject carryObject) {
		this.carryObject = carryObject;
		update();
	}

	public void setLevelMod(Levels.LevelMod levelMod) {
		this.levelMod = levelMod;
		update();
	}

	public Levels.LevelMode getLevelMode() {
		return levelMode;
	}

	public void update() {
		if (/*topLimit.get()*/false) {
			canMoveUp = false;
		} else {
			canMoveUp = true;
		}
		if (/*!bottomLimit.get()*/false) {
			elevatorEncoder.reset();
			canMoveDown = false;
		} else {
			canMoveDown = true;
		}
		desiredElevatorHeight = levelHandler.getHeight(mainLevel, levelMode, carryObject, levelMod);
		SmartDashboard.putNumber("desiredElevatorHeight", desiredElevatorHeight);
		SmartDashboard.putBoolean("Top Limit",topLimit.get());
		SmartDashboard.putBoolean("Bottom Limit",!bottomLimit.get());
		seeHeight();
		goTowardsDesired();

	}

	public void goTowardsDesired() {
		if (!isManual) {
			// code
			motorMovement = 0.2 * (desiredElevatorHeight - elevatorHeight);// 0.2
																			// is
																			// coeffecient
			if ((!canMoveUp)&&motorMovement>0) {
				motorMovement=0;

			}
			if ((!canMoveDown)&&motorMovement<0) {
				motorMovement=0;

			}
			if(Math.abs(desiredElevatorHeight-elevatorHeight)<ACCEPTABLEERROR) {
				atHeight = true;
			} else {
				atHeight = false;
			}
		} else {
			// do nothing
		}

	}

	public void stop() {
		motorMovement = 0;
	}

	public void manualUp() {
		update();
		if (canMoveUp && isManual) {
			motorMovement = 1;
		} else if (!canMoveUp) {
			motorMovement = 0;
		}

	}

	public void manualDown() {
		update();
		if (canMoveDown && isManual) {
			motorMovement = -1;
		} else if (!canMoveDown) {
			motorMovement = 0;
		}

	}

	public void manualAmount(double power) {
		update();
		if (canMoveUp && canMoveDown && isManual) {
			motorMovement = power;
		}
	}

	public void setManual(boolean manual) {
		this.isManual = manual;
	}

	public boolean getManual() {
		return isManual;
	}

	public void seeHeight() {
		// returns height in inches
		elevatorHeight = /*
						 * 7.3125 + disabled b/c measuring from bottom of
						 * elevator
						 */(0.01121383 * elevatorEncoder.get());
		// 0.01126126
		// 0.01125687
		// 0.01112335

	}
}