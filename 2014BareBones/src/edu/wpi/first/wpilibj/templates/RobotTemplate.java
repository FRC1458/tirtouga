/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends SimpleRobot
{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    //I love programming <3 && cookies
    Joystick wheel = new Joystick(1);
    Joystick throttle = new Joystick(2);
    Joystick xBox = new Joystick(3);
    Solenoid cameraLight = new Solenoid(2, 3);
    Victor leftDrive1 = new Victor(1);
    Victor leftDrive2 = new Victor(2);
    Victor leftDrive3 = new Victor(3);
    Victor rightDrive1 = new Victor(4);
    Victor rightDrive2 = new Victor(5);
    Victor rightDrive3 = new Victor(6);
    Victor intake = new Victor(7);
    Compressor compressor = new Compressor(1, 2);
    AnalogChannel ultrasonic = new AnalogChannel(1);
    DoubleSolenoid solenoidArm = new DoubleSolenoid(2, 1, 2);
    Victor catapault1 = new Victor(8);
    Victor catapault2 = new Victor(9);
    DigitalInput limCatapult = new DigitalInput(4);
    DoubleSolenoid solenoidShooter = new DoubleSolenoid(2, 5, 6);
    DriverStation driverStation = DriverStation.getInstance();
    boolean windupFlag = false;
    Timer shooterTimer = new Timer();
    Timer windupTimer = new Timer();
    Timer autoTime = new Timer();
    VisionThingy vision = new VisionThingy();
    Timer straightDriveTimer = new Timer();
    public final double MAX_MOTOR_POWER_FOR_COMPRESSION = 2;
    boolean compressorOn = true;
    /*
     
     =======
     /*Victor intake = new Victor(6, 7);
     Victor catapault1 = new Victor(6, 8);
     Victor catapault2 = new Victor(6, 9);
     >>>>>>> 765e53bf4ccbdb172aa19f30452705a889da2aee
     Gyro gyro = new Gyro(5, 2);
     
     
    
     
    
     boolean emergencyStop = false;
     
     //double KP = .2;

     public void gyroDashboard()
     {
     SmartDashboard.putDouble("Gyro's output: ", gyro.getAngle());
     }


     */

    //45 inches from wall
    public void wallDistance(final double distance)
    {
        final double kP = -0.125;

        double cP;

        double ultrasonicDistance;

        while((Math.abs(ultrasonicDistance() - distance) > 1) && isEnabled())
        {
            ultrasonicDistance = ultrasonicDistance();
            cP = (distance - ultrasonicDistance) * kP;
            SmartDashboard.putDouble("power: ", cP);
            driveStraight(cP);
        }

    }

    double swAdjust(double i)
    {
        //increase so bottom is 0
        i += 0.945;
        //multiply so top is 2
        i *= 1.13378685;
        //put back into -1 to 1
        i -= 1.001;
        return i;
    }

    public void lowerIntake()
    {
        solenoidArm.set(DoubleSolenoid.Value.kReverse);
    }

    public void raiseIntake()
    {
        solenoidArm.set(DoubleSolenoid.Value.kForward);
    }

    public double ultrasonicDistance()
    {
        SmartDashboard.putDouble("Ultrasonic Distance", (ultrasonic.getVoltage() * 3.47826087) - 0.25);
        return ((ultrasonic.getVoltage()) * 3.47826087) - 0.25;
    }

    public void drive()
    {
        if(throttle.getRawButton(1))
        {
            setLeftSpeed(swAdjust(-wheel.getAxis(Joystick.AxisType.kX)));
            setRightSpeed(swAdjust(-wheel.getAxis(Joystick.AxisType.kX)));
        }
        else if(wheel.getAxis(Joystick.AxisType.kX) <= 0)
        {
            //Older working version, no difference when reversed
             setLeftSpeed(throttle.getRawAxis(2) * (1 + swAdjust(wheel.getAxis(Joystick.AxisType.kX))));
             setRightSpeed(-throttle.getRawAxis(2));
            /* 
            if(throttle.getRawAxis(2) <= 0)
            {
                setLeftSpeed(throttle.getRawAxis(2) * (1 + swAdjust(wheel.getAxis(Joystick.AxisType.kX))));
                setRightSpeed(-throttle.getRawAxis(2));
            }
            else
            {
                setRightSpeed(-throttle.getRawAxis(2) * (1 + swAdjust(wheel.getAxis(Joystick.AxisType.kX))));
                setLeftSpeed(throttle.getRawAxis(2));
            }
*/
        }
        else
        {
            //Older working version, no difference when reversed
             setRightSpeed(-(throttle.getRawAxis(2) * (1 - swAdjust(wheel.getAxis(Joystick.AxisType.kX)))));
             setLeftSpeed(throttle.getRawAxis(2));
             
            /*
            if(throttle.getRawAxis(2) <= 0)
            {
                setRightSpeed(-throttle.getRawAxis(2) * (1 + swAdjust(wheel.getAxis(Joystick.AxisType.kX))));
                setLeftSpeed(throttle.getRawAxis(2));
            }
            else
            {
                setLeftSpeed(throttle.getRawAxis(2) * (1 + swAdjust(wheel.getAxis(Joystick.AxisType.kX))));
                setRightSpeed(-throttle.getRawAxis(2));
            }*/
        }
    }
    /*
     public void drive()
     {
     setLeftSpeed(xBox.getRawAxis(2));
     setRightSpeed(-xBox.getRawAxis(5));
     }*/

    public void driveStraight(double speed)
    {
        setRightSpeed(-speed);
        setLeftSpeed(speed);
    }

    /*public void charge(double distance)
     {
     double stable = 0;
     double speed = 0;
     while(true)
     {
     speed = KP * (distance - ultrasonicDistance());
     setLeftSpeed(speed);
     setRightSpeed(-speed);
     if(ultrasonicDistance() > distance - .5 || ultrasonicDistance() < distance + .5)
     {
     stable++;
     if(stable > 100)
     {
     break;
     }
     }
     }
     }*/
    public void setLeftSpeed(double speed)
    {
        leftDrive1.set(speed);
        leftDrive2.set(speed);
        leftDrive3.set(speed);
    }

    public void setRightSpeed(double speed)
    {
        rightDrive1.set(speed);
        rightDrive2.set(speed);
        rightDrive3.set(speed);
    }

    public void intake()
    {
        intake.set(xBox.getRawAxis(2) / 1.33);
    }

    public void fire()
    {
        solenoidShooter.set(DoubleSolenoid.Value.kForward);
        shooterTimer.start();
    }

    public void compressorCheck(boolean override, boolean manual)
    {
        boolean compressorOn = Math.abs((leftDrive1.get() * 3) - (-rightDrive1.get() * 3)) < 1.5;
        if(manual)
        {
            if(override)
            {
                compressor.start();
            }
            else
            {
                compressor.stop();
            }
        }
        else if(compressorOn)
        {
            compressor.start();
        }
        else
        {
            compressor.stop();
        }
    }

    public void checkBattery()
    {
        if(driverStation.getBatteryVoltage() < 12)
        {
            SmartDashboard.putBoolean("Replace Battery NOW", true);
        }
        else
        {
            SmartDashboard.putBoolean("Replace Battery NOW", false);
        }
    }

    public void windup()
    {
        if((solenoidShooter.get() == DoubleSolenoid.Value.kReverse) && !limCatapult.get())
        {
            /*if(windupTimer.get() == 0)
             {
             windupTimer.start();
             }
             catapault1.set(-(windupTimer.get() / 1)); //it was 4
             catapault2.set(-(windupTimer.get() / 1));
             if(windupTimer.get() > 1)
             {
             windupTimer.stop();
             }*/
            catapault1.set(-1);
            catapault2.set(-1);
        }
        else
        {
            catapault1.set(0);
            catapault2.set(0);
            windupTimer.reset();
            windupTimer.stop();
            windupFlag = false;
        }
    }

    public void autonomous()
    {
        cameraLight.set(true);
        compressor.start();
        autoTime.start();
        lowerIntake();
        
        driveStraight(0.75);
        Timer.delay(0.75);
        fire();
        Timer.delay(0.25);
        windup();
        driveStraight(0);
        driveStraight(-0.75);
        Timer.delay(1);
        driveStraight(0);
                
        
        
//        if(!driverStation.getDigitalIn(2))
//        {
//            autoTime.start();
//            lowerIntake();
//            Timer.delay(1);
//            //vision.mainVision();
//            //charge(14);
//            /*Temp disabled for calgames
//             * while(isAutonomous() && isEnabled())
//            {
//                //Hot goal, disabled for calgames
//                if(vision.isHot || autoTime.get() > 5)
//                {
//                    solenoidShooter.set(DoubleSolenoid.Value.kForward);
//                    Timer.delay(1);
//                    solenoidShooter.set(DoubleSolenoid.Value.kReverse);
//                    break;
//                }
//                
//            }*/
//            setLeftSpeed(0.39);
//            setRightSpeed(-0.35);
//            Timer.delay(1);
//            solenoidShooter.set(DoubleSolenoid.Value.kForward);
//            Timer.delay(1);
//            solenoidShooter.set(DoubleSolenoid.Value.kReverse);
//        
//            setLeftSpeed(0.29);
//            setRightSpeed(-0.26);
//            Timer.delay(1);
//            driveStraight(0);
//            setRightSpeed(0);
//            setLeftSpeed(0);
//            while(isAutonomous() && isEnabled())
//            {
//                windup();
//                /*straightDriveTimer.start();
//                 if(straightDriveTimer.get() < 1)
//                 {
//                 driveStraight(0.5);
//                 }
//                 else
//                 {
//                 setRightSpeed(0);
//                 setLeftSpeed(0);
//                 straightDriveTimer.stop();
//                 }
//
//                 straightDriveTimer.stop();*/
//            }
//        }
//        else
//        {
//            lowerIntake();
//            intake.set(0.4);
//            Timer.delay(0.4);
//            intake.set(0);
//            solenoidShooter.set(DoubleSolenoid.Value.kForward);
//            Timer.delay(1);
//            solenoidShooter.set(DoubleSolenoid.Value.kReverse);
//            while(isAutonomous() && isEnabled())
//            {
//                windup();
//                if(limCatapult.get())
//                {
//                    catapault1.set(0);
//                    catapault2.set(0);
//                    intake.set(0.5);
//                    Timer.delay(1);
//                    intake.set(0);
//                    solenoidShooter.set(DoubleSolenoid.Value.kForward);
//                    Timer.delay(1);
//                    solenoidShooter.set(DoubleSolenoid.Value.kReverse);
//                    break;
//                }
//            }
//            setLeftSpeed(0.7);
//            setRightSpeed(-0.6);
//            Timer.delay(1);
//            driveStraight(0);
//            while(isAutonomous() && isEnabled())
//            {
//                windup();
//                /*straightDriveTimer.start();
//                 if(straightDriveTimer.get() < 1)
//                 {
//                 driveStraight(0.5);
//                 }
//                 else
//                 {
//                 setRightSpeed(0);
//                 setLeftSpeed(0);
//                 straightDriveTimer.stop();
//                 }*/
//                return;
//            }
//        }
//
      }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {
        //vision.mainVision();

        Timer inputTimer = new Timer();
        boolean manualCompressorOn = false;
        boolean manualControl = true;
        compressor.start();
        solenoidShooter.set(DoubleSolenoid.Value.kReverse); //Pulled in
        solenoidArm.set(DoubleSolenoid.Value.kForward); //In
        windupFlag = false;

        catapault1.set(0);
        catapault2.set(0);
        autoTime.reset();
        autoTime.stop();
        inputTimer.start();
        while(isOperatorControl() && isEnabled())
        {
            checkBattery();

            if(xBox.getRawButton(4))
            { //Button X
                intake.set(0.35);
            }
            else if(xBox.getRawButton(3))
            { //Button Y
                intake.set(.2675);
            }
            else
            {
                intake();
            }

            /*Sasha's morse code thingy
             * if(MorseCode.isDone)
             {
             (new Thread(new MorseCode("Exterminate all cookie haters", cameraLight))).start();
             }*/

            // Swapped the lower and raise intake buttons first period 2/18. - Bonnie
            if(xBox.getRawButton(5))
            {
                lowerIntake();
            }
            else if(xBox.getRawButton(6))
            {
                raiseIntake();
            }
            SmartDashboard.putBoolean("Image: ", vision.isHot);


            if(throttle.getRawButton(2))
            {
                fire();
            }
            else if(shooterTimer.get() > 1)
            {
                solenoidShooter.set(DoubleSolenoid.Value.kReverse);
                shooterTimer.reset();
                shooterTimer.stop();
            }
            windup();

            if(driverStation.getBatteryVoltage() < 7.5 && compressorOn)
            {
                compressor.stop();
                compressorOn = false;
            }
            else if(driverStation.getBatteryVoltage() > 9.5 && !compressorOn)
            {
                compressor.start();
                compressorOn = true;
            }

            SmartDashboard.putNumber("left drive 1 power", leftDrive1.get());
            SmartDashboard.putNumber("right drive 1 power", rightDrive1.get());
            SmartDashboard.putBoolean("Manual compressor state", manualCompressorOn);
            SmartDashboard.putBoolean("Manual control enabled", manualControl);
            SmartDashboard.putNumber("Joystick: ", throttle.getRawAxis(2));
            SmartDashboard.putNumber("Wheel: ", swAdjust(wheel.getAxis(Joystick.AxisType.kX)));
            SmartDashboard.putNumber("Intake: ", xBox.getRawAxis(2));
            SmartDashboard.putBoolean("Pressure Switch: ", compressor.getPressureSwitchValue());
            SmartDashboard.putBoolean("Shooter Switch: ", limCatapult.get());
            ultrasonicDistance();
            drive();
            //superDrive(-throttle.getRawAxis(2), swAdjust(wheel.getAxis(Joystick.AxisType.kX)));
        }
    }

    public void test()
    {
    }
}
/*if(!limCatapult.get())
 {
 catapault1.set(-Math.abs(xBox.getRawAxis(5)));
 catapault2.set(-Math.abs(xBox.getRawAxis(5))); //Only takes negative
 }else{
 catapault1.set(0);
 catapault2.set(0);
 }
 System.out.println(limCatapult.get());
 if(xBox.getRawButton(1))
 {
 solenoidShooter.set(DoubleSolenoid.Value.kReverse);
 }
 else if(xBox.getRawButton(2))
 {
 solenoidShooter.set(DoubleSolenoid.Value.kForward); //B fires
 }*/
/*gyroDashboard();
           
 if(!emergencyStop)
 {
 windup();
 }*/
/*
 if(xBox.getRawButton(2))
 {
 emergencyStop = true;
 catapault1.set(0);
 catapault2.set(0);
 windupTimer.reset();
 windupTimer.stop();
 }
 else if(xBox.getRawButton(1))
 {
 emergencyStop = false;
 }
 }*/
/* if(throttle.getRawButton(6))
 {
 manualControl = false;
 }
 else if(throttle.getRawButton(7))
 {
 manualCompressorOn = false;
 }
 else if(throttle.getRawButton(10))
 {
 //compressor.stop();
 manualCompressorOn = true;
 }
 else if(throttle.getRawButton(11))
 {
 manualControl = true;
 }
 compressorCheck(manualCompressorOn, manualControl);*/
