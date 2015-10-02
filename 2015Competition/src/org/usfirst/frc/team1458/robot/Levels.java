package org.usfirst.frc.team1458.robot;

public class Levels {
	/**
	 * Calculates height given states
	 * @param m MainLevel to calc with
	 * @param l LevelMode to calc with
	 * @param c CarryObject to calc with
	 * @param d LevelMod to calc with
	 * @return The height the elevator should move to based on the objects given.
	 */
	public double getHeight(MainLevel m, LevelMode l, CarryObject c, LevelMod d) {
		if(d==LevelMod.LOAD) {
			return m.getHeight()+l.getHeight()+c.getHeight()+d.getHeight();
		} else {
			return m.getHeight()+l.getHeight()+d.getHeight();
		}
		
	}
	
	public Levels() {
		
	}
	
	/**
	 * Enumerator storing the MainLevels (ONE, TWO, etc.)
	 * @author Brendan, Turtwig of Team 1458
	 *
	 */
	public enum MainLevel {
		ONE(0.0),
		TWO(11.5),
		THREE(23.0),
		FOUR(34.5);
		
		private double height;
		
		private MainLevel(double height) {
			this.height = height;
		}
		public double getHeight() {
			return this.height;
		}
	}
	/**
	 * Enumerator storing LevelMode (FLOOR, PLATFORM, etc.)
	 * @author Brendan, Turtwig of Team 1458
	 *
	 */
	public enum LevelMode {
		FLOOR(0.0),
		PLATFORM(2.0),
		STEP(6.0);
		
		private double height;
		
		private LevelMode(double height) {
			this.height = height;
		}
		public double getHeight() {
			return this.height;
		}
	}
	/**
	 * Enumerator storing CarryObject (TOTE or CONTAINER)
	 * @author Brendan, Turtwig of Team 1458
	 *
	 */
	public enum CarryObject {
		TOTE(0.0),
		CONTAINER(13.0);
		
		private double height;
		
		private CarryObject(double height) {
			this.height = height;
		}
		public double getHeight() {
			return this.height;
		}
	}
	/**
	 * Enumerator storing LevelMod (LOAD or UNLOAD)
	 * @author Brendan, Turtwig of Team 1458
	 *
	 */
	public enum LevelMod {
		LOAD(5.5),
		UNLOAD(0.0);
		
		private double height;
		
		private LevelMod(double height) {
			this.height = height;
		}
		public double getHeight() {
			return this.height;
		}
	}
}