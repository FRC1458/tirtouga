
package org.usfirst.frc.team1458.robot;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends SampleRobot {
	Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Joystick xbox = new Joystick(2);

	double[] lErrorLog = new double[1000000];
	double[] rErrorLog = new double[1000000];
	
	/*
	Encoder leftEncoder = new Encoder(0,1);
	Encoder rightEncoder = new Encoder(2,3);
    */
    
    public Robot() {
    
    }

    /**
     * Autonomous mode
     */
    public void autonomous() {
    
    }

    /**
     * operator controlled mode
     */
    public void operatorControl() {
    	double lDp=0;//left Drive power
    	double rDp=0;//right Drive power
    	double cDp=0;//centre Drive power

    	double lAverageError = 0.0;
    	double rAverageError = 0.0;
    	int counter = 0;

	
    	
    	while(isEnabled()) {
    		lDp = -left.getY();
    		rDp = -(-right.getY());

    		SmartDashboard.putNumber("Right: ", rDp);

    		SmartDashboard.putNumber("Left: ", lDp);
    		
    		
    		lErrorLog[counter]=lDp;
    		rErrorLog[counter]=rDp;
    		counter++;
    		
    		for(int i = 0; i<lErrorLog.length; i++) {
    			lAverageError+=lErrorLog[i];
    		}
    		lAverageError/=lErrorLog.length;
    		for(int i = 0; i<rErrorLog.length; i++) {
    			rAverageError+=rErrorLog[i];
    		}
    		rAverageError/=rErrorLog.length;
    		System.out.println("L Average Error "+lAverageError);
    		System.out.println("R Average Error "+rAverageError);
    		
    		SmartDashboard.putNumber("L Average Error",lAverageError);
    		SmartDashboard.putNumber("R Average Error",rAverageError);
    		
    		/*
    		System.out.println("Right encoder: "+rightEncoder.getRaw());
    		SmartDashboard.putNumber("Right encoder: ",rightEncoder.getRaw());
    		
    		System.out.println("Left encoder: "+leftEncoder.getRaw());
    		SmartDashboard.putNumber("Left encoder: ",leftEncoder.getRaw());
    		*/
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    	
    }
}