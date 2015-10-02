
package org.usfirst.frc.team1458.robot;


import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
*
 */
public class Robot extends SampleRobot {
	Compressor stupidC;
	DoubleSolenoid s;
	Joystick stick = new Joystick(0);

    public Robot() {
        stupidC=new Compressor();
        s=new DoubleSolenoid(1,0);
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
    	
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	stupidC.start();
    	while(isEnabled()) {
    		SmartDashboard.putBoolean("Compressor enabled", stupidC.enabled());
    		if(stick.getRawButton(1)) {
    			s.set(DoubleSolenoid.Value.kForward);
    			SmartDashboard.putBoolean("Solenoid should be forward", true);
    		} else if(stick.getRawButton(2)) {
    			s.set(DoubleSolenoid.Value.kReverse);
    			SmartDashboard.putBoolean("Solenoid should be forward", false);
    		} else if (stick.getRawButton(3)) {
    			s.set(DoubleSolenoid.Value.kOff);
    			
    		}
    		if(stick.getRawButton(4)) {
    			stupidC.start();
    			SmartDashboard.putBoolean("Compressor should be enabled", true);
    		} else if (stick.getRawButton(5)) {
    			stupidC.stop();
    			SmartDashboard.putBoolean("Compressor should be enabled", false);
    		}
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
