
package org.usfirst.frc.team1458.robot;


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Gyro;


public class Robot extends SampleRobot {
	/*Joystick right = new Joystick(0);
	Joystick left = new Joystick(1);
	Joystick xbox = new Joystick(2);
	Talon rightDriveFront = new Talon(1);
	Talon rightDriveRear = new Talon(0);
	Talon leftDriveFront = new Talon(3);
	Talon leftDriveRear = new Talon(2);
	Talon centreDrive = new Talon(4);
	
	
	Encoder leftEncoder = new Encoder(0,1);
	Encoder rightEncoder = new Encoder(2,3);*/
	
	I2CGyro gyro = new I2CGyro();


    
    public Robot() {
    
    }
    /*
    public double ultrasonicDistance()
    {
        SmartDashboard.putDouble("Ultrasonic Distance", (ultrasonic.getVoltage() * 3.47826087) - 0.25);
    }
    */

    /**
     * Autonomous mode
     */
    public void autonomous() {
    
    }

    /**
     * operator controlled mode
     */
    public void operatorControl() {
    	
    	gyro.reset();
    	gyro.update();
    	double gyroAngle;
    	double gyroRate;
    	/*
    	rightEncoder.setDistancePerPulse(1.0);
    	leftEncoder.setDistancePerPulse(1.0);
    	*/
    	while(isEnabled()) {
    		gyroAngle = gyro.getAngle();
    		gyroRate = gyro.getRate();
    		System.out.println("Gyro Angle: " + gyroAngle);
    		System.out.println("GyroRate: " + gyroRate);
    		SmartDashboard.putNumber("Gyro Angle: ", gyroAngle);
    		SmartDashboard.putNumber("Gyro Rate: ", gyroRate);
    		SmartDashboard.putNumber("Gyro Angle Graph: ", gyroAngle);
    		SmartDashboard.putNumber("Gyro Rate Graph: ", gyroRate);
    		gyro.update();
    		
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    	
    }
}
/*
double lDp=0;//left Drive power
double rDp=0;//right Drive power
double cDp=0;//centre Drive power

final double kPGyro=0.0;
final double kIGyro=0.0;
final double kDGyro=0.0;

final double kPEncoder=0.0;
final double kIEncoder=0.0;
final double kDEncoder=0.0;

double adjustPGyro;
double adjustIGyro;
double adjustDGyro;

double adjustPEncoder;
double adjustIEncoder;
double adjustDEncoder;

double adjustGyro;
double adjustEncoder;
double adjustTotal;

double idealGyroAngle = 0;

double power;
*/

/*
power=right.getY();

adjustPGyro=kPGyro*(gyro.getAngle()-idealGyroAngle);
adjustIGyro=kIGyro*0;//not applicable but included for consistency
adjustDGyro=kDGyro*gyro.getRate();//based on rotation to right in degrees/second
adjustGyro=adjustPGyro+adjustIGyro+adjustPGyro;

adjustPEncoder=kPEncoder*(rightEncoder.get()-leftEncoder.get());
adjustIEncoder=kIEncoder*0;//also not applicable
adjustDEncoder=kDEncoder*(leftEncoder.getRate()-rightEncoder.getRate());
adjustEncoder=adjustPEncoder+adjustIEncoder+adjustPEncoder;

adjustTotal=adjustGyro+adjustEncoder;
rDp=power-adjustTotal;
lDp=power+adjustTotal;


rightDriveFront.set(rDp);
rightDriveRear.set(rDp);
leftDriveFront.set(lDp);
leftDriveRear.set(lDp);
centreDrive.set(cDp);

System.out.println("Right: " + rDp);
SmartDashboard.putNumber("Right: ", rDp);

System.out.println("Left: " + lDp);
SmartDashboard.putNumber("Left: ", lDp);

System.out.println("Centre: "+cDp);
SmartDashboard.putNumber("Centre:",cDp);


System.out.println("Right encoder: "+rightEncoder.getRaw());
SmartDashboard.putNumber("Right encoder: ",rightEncoder.getRaw());

System.out.println("Left encoder: "+leftEncoder.getRaw());
SmartDashboard.putNumber("Left encoder: ",leftEncoder.getRaw());
*/