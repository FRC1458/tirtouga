package org.usfirst.frc.team1458.robot;

public class Levels {
	
	public double getHeight(MainLevel m, LevelMode l, CarryObject c, LevelMod d) {
		if(d==LevelMod.LOAD) {
			return m.getHeight()+l.getHeight()+c.getHeight()+d.getHeight();
		} else {
			return m.getHeight()+l.getHeight()+d.getHeight();
		}
		
	}
	
	public Levels() {
		
	}
	
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