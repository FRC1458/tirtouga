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
    Victor sLift = new Victor(3);
    Victor shooter = new Victor(4);
    Joystick lJoystick = new Joystick(1);
    Joystick rJoystick = new Joystick(2);
    Joystick xbox = new Joystick(3);
    Relay shooterTilt = new Relay(1);
    DigitalInput limLiftBot = new DigitalInput(3);
    DigitalInput limTiltBottom = new DigitalInput(4);
    DigitalInput limTiltTop = new DigitalInput(5);
    Servo trigger1 = new Servo(6);
    Servo trigger2 = new Servo(5);
    Timer triggerTimer = new Timer();
    Timer calibrate = new Timer();
    Timer strobeTimer = new Timer();
    Relay lights = new Relay(2);
    int heartbeat = 0;
    boolean strobeTimerOn = false;
    //Joystick logitech = new Joystick(2);
    //Victor l2Arms = new Victor(5);
    /*Servo trigger = new Servo(1);
    RobotDrive robotDrive;
    AnalogChannel tiltPot = new AnalogChannel(1);
    DigitalInput limTiltTop = new DigitalInput(3);
    DigitalInput limTiltBottom = new DigitalInput(4);
    Timer shooterTimer = new Timer();
    int shooterMode = 0;
    double shooterSpeed = 0;
    boolean shooterGood = false;
    boolean shooterOn = false;
    */
    public void robotInit() {
        //robotDrive = new RobotDrive(leftDrive, rightDrive);
    }
    
    public void waitSasha(double time){
        Timer wait = new Timer();
        wait.start();
        while(wait.get() < time){
            //Party Time !!!
        }
    }
    
    public void calibrate(){
        if(lJoystick.getRawButton(11) && rJoystick.getRawButton(11) && !limTiltTop.get()){
            calibrate.reset();
            shooterTilt.set(Relay.Value.kReverse);
            calibrate.start();
        }else{
            shooterTilt.set(Relay.Value.kOff);
            calibrate.stop();
        }
       
    }
    
    public void autonomous() {
        //Move takes 5 seconds
       /* leftDrive.set(-0.5);
        rightDrive.set(0.5);
        waitSasha(2);
        leftDrive.set(0.5);
        rightDrive.set(0.5);
        waitSasha(3);*/
        shooterTilt.set(Relay.Value.kReverse);
        waitSasha(7.9); //7.8 prev
        shooterTilt.set(Relay.Value.kOff);
        shooter.set(-1);
        waitSasha(2.9); 
        for(int i = 0; i < 3; i++){
            fire(true);
            waitSasha(0.3);
            fire(false);
            waitSasha(1.15);
        }
    }

     public void drive(){
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

    public void scissorLift() {
        if (rJoystick.getRawButton(3) /*&& limitSwitchTop.get()*/) { //Right Bumper: Robot Up
            sLift.set(1);
            if(!strobeTimerOn){
                strobeTimer.start();
                strobeTimerOn = true;
            }
        } else if (lJoystick.getRawButton(3) && limLiftBot.get()) { //Left Bumber: Robot Down
            sLift.set(-.65);
            if(!strobeTimerOn){
                strobeTimer.start();
                strobeTimerOn = true;
            }
        } else {
            sLift.set(0);
        }
        lightShow(strobeTimer);
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
/*
     public void level1Climb(){
         if(logitech.getRawButton(7)){ //Deploy Arms
             l1Arms.set(Relay.Value.kOn);
         }else if(logitech.getRawButton(8)){ //Lower Arms
             l1Arms.set(Relay.Value.kReverse);
         }else{
             l1Arms.set(Relay.Value.kOff);
         }
     }
     
     public void level2Climb(){
         if(logitech.getRawButton(10)){ //Deploy Arms
             l2Arms.set(0.5);
         }else if(logitech.getRawButton(11)){ //Lower Arms (Lift Robot)
             l2Arms.set(1);
         }else{
             l2Arms.set(0);
         }
     }
     
     public void shooterSet(){
         if(logitech.getRawButton(4)){
             shooterMode = 1;
             shooterSpeed = 0.3;
         }else if(logitech.getRawButton(3)){
             shooterMode = 2;
             shooterSpeed = 0.5;
         }else if(logitech.getRawButton(5)){
             shooterMode = 3;
             shooterSpeed = 1;
         }
     }
     */
     public void spinUp(){
         if (xbox.getRawButton(3)){
             //shooter.set(-1);
             shooter.set(-.5);
         }else if(xbox.getRawButton(2)){
             shooter.set(0);
         }
     }
     
     public void fire(boolean auto){
         if(auto){
            trigger1.setAngle(130);
            trigger2.setAngle(0);
         }else{
             trigger1.setAngle(0);
             trigger2.setAngle(110);
         }
     }
     
        
     public boolean fireControl(){
         if(triggerTimer.get() > 2){
             triggerTimer.stop();
             triggerTimer.reset();
         }
         if(xbox.getRawButton(1) && triggerTimer.get() == 0){
             triggerTimer.start();
         }
         if(triggerTimer.get() < 1 && triggerTimer.get() > 0){
             return true;
         }
         return false;
     }
     
     public void tilt(){
         if(xbox.getRawButton(6) && !limTiltTop.get()){
             shooterTilt.set(Relay.Value.kReverse);
         }else if(xbox.getRawButton(5) && limTiltBottom.get()){
             shooterTilt.set(Relay.Value.kForward);
         }else{
             shooterTilt.set(Relay.Value.kOff);
         }
     }
    
    public void printToClassmate(){
        DriverStationLCD driverStation;
        driverStation = DriverStationLCD.getInstance();
        driverStation.println(DriverStationLCD.Line.kUser1, 1, "Calibrate: " + calibrate.get());
        driverStation.updateLCD();
    }
    
    /*void updateDashboard() {
        Dashboard lowDashData = DriverStation.getInstance().getDashboardPackerLow();
        lowDashData.addCluster();
        {
            lowDashData.addCluster();
            {     //analog modules
                lowDashData.addCluster();
                {
                    for (int i = 1; i <= 8; i++) {
                        lowDashData.addFloat((float) AnalogModule.getInstance(1).getAverageVoltage(i));
                    }
                }
                lowDashData.finalizeCluster();
                lowDashData.addCluster();
                {
                    for (int i = 1; i <= 8; i++) {
                        lowDashData.addFloat((float) AnalogModule.getInstance(2).getAverageVoltage(i));
                    }
                }
                lowDashData.finalizeCluster();
            }
            lowDashData.finalizeCluster();

            lowDashData.addCluster();
            { //digital modules
                lowDashData.addCluster();
                {
                    lowDashData.addCluster();
                    {
                        int module = 1;
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayReverse());
                        lowDashData.addShort(DigitalModule.getInstance(module).getAllDIO());
                        lowDashData.addShort(DigitalModule.getInstance(module).getDIODirection());
                        lowDashData.addCluster();
                        {
                            for (int i = 1; i <= 10; i++) {
                                lowDashData.addByte((byte) DigitalModule.getInstance(module).getPWM(i));
                            }
                        }
                        lowDashData.finalizeCluster();
                    }
                    lowDashData.finalizeCluster();
                }
                lowDashData.finalizeCluster();

                lowDashData.addCluster();
                {
                    lowDashData.addCluster();
                    {
                        int module = 2;
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayReverse());
                        lowDashData.addShort(DigitalModule.getInstance(module).getAllDIO());
                        lowDashData.addShort(DigitalModule.getInstance(module).getDIODirection());
                        lowDashData.addCluster();
                        {
                            for (int i = 1; i <= 10; i++) {
                                lowDashData.addByte((byte) DigitalModule.getInstance(module).getPWM(i));
                            }
                        }
                        lowDashData.finalizeCluster();
                    }
                    lowDashData.finalizeCluster();
                }
                lowDashData.finalizeCluster();

            }
            lowDashData.finalizeCluster();
            //byte blarg = 0;
            //lowDashData.addByte(blarg);
            lowDashData.addByte(Solenoid.getAllFromDefaultModule());
            
            //comment out this piece of code to use old dashboard
            lowDashData.addByte((byte) (Math.sin(heartbeat)* 100));
        }
        lowDashData.finalizeCluster();
        lowDashData.commit();
    }*/
    
    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            drive();
            scissorLift();
            //updateDashboard();
            
            //calibrate();
            //level1Climb();
            //level2Climb();
            //shooterSet();
            tilt();
            spinUp();
            fire(fireControl());
            printToClassmate();
            heartbeat++;
        }   
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
    
    }
}
 