package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.PWM;

public class LED {
	private byte r;
	private byte g;
	private byte b;
	private boolean on = false;
	private PWM pwmred = new PWM(11);//change channel to be correct
	private PWM pwmgreen = new PWM(12);//change channel to be correct
	private PWM pwmblue = new PWM(13);//change channel to be correct
	LED() {
		
	}
	public void set(byte r, byte g, byte b) {
		this.r = r;
		this.g = g;
		this.b = b;
		update();
	}
	public void turnOn() {
		
		on = true;
	}
	public void turnOff() {
		on = false;
	}
	private void update() {
		if(on) {
		pwmred.setRaw(r);
		pwmgreen.setRaw(g);
		pwmblue.setRaw(b);
		} else {
			pwmred.setRaw(0);
			pwmgreen.setRaw(0);
			pwmblue.setRaw(0);
		}
	}

}
