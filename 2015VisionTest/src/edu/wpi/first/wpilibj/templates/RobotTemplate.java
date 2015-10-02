/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.AnalogModule;
import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Random;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends SimpleRobot {
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    Victor leftDrive = new Victor(1);
    Victor rightDrive = new Victor(2);

    Joystick lJoystick = new Joystick(1);
    Joystick rJoystick = new Joystick(2);
    Joystick xbox = new Joystick(3);
    Timer calibrate = new Timer();
    Timer strobeTimer = new Timer();
    Relay lights = new Relay(2);
    int heartbeat = 0;
    boolean strobeTimerOn = false;

    VisionThingy vision = new VisionThingy();
    
    public void robotInit() {
        //robotDrive = new RobotDrive(leftDrive, rightDrive);
       ;
    }
    
    public void waitSasha(double time){
        Timer wait = new Timer();
        wait.start();
        while(wait.get() < time){
            //Party Time !!!
        }
    }
    
    
    public void autonomous() {
        Timer testTimer = new Timer();
        rightDrive.set(0.75);
        leftDrive.set(-0.75);
        vision.mainVision();
        testTimer.start();
        while(!vision.isSee) {
            vision.mainVision();
            rightDrive.set(0.75);
            leftDrive.set(-0.75);
            if(testTimer.get()>4) {
                break;
            }
        }
        testTimer.stop();
        leftDrive.set(0);
        rightDrive.set(0);
        SmartDashboard.putString("Vision Errors","Success, No Errors");
    }

     public void drive(){
        vision.mainVision();
        double leftVal = lJoystick.getRawAxis(2);
        double rightVal = rJoystick.getRawAxis(2);
        double speed = 1;
        if(rJoystick.getRawButton(2)){
            speed = 0.5;
        }
        if(rJoystick.getRawButton(1)){
            leftVal = rightVal;
        }
        if(Math.abs(leftVal) > .01){
            leftDrive.set(-leftVal * speed);
        }else{
          leftDrive.set(0);  
        } 
        if(Math.abs(rightVal) > .01){
         rightDrive.set(rightVal * speed);
        }else{
            rightDrive.set(0);
        }
    }
     
    public void lightShow(Timer strobeTimer){
       if(strobeTimer.get() != 0){
           if(strobeTimer.get() < 0.25){
               lights.set(Relay.Value.kForward);
           }else if(strobeTimer.get() < 0.5){
               lights.set(Relay.Value.kOff);
           }else{
               strobeTimer.stop();
               strobeTimer.reset();
               strobeTimer.start();
           }
       }
    }
    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            drive();
            //updateDashboard();
            
            //calibrate();
            //level1Climb();
            //level2Climb();
            //shooterSet();
            heartbeat++;
            
        }   
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        
    }
}
 