/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTemplate extends SimpleRobot
{
    public final int DIGITAL_MODULE_SLOT = 6;
    public final int ANALOG_MODULE_SLOT = 1;
    public final int SOLENOID_MODULE_SLOT = 7;
    public final double MAX_MOTOR_POWER_FOR_COMPRESSION = 3;
    public final double IDEAL_SHOOTING_DISTANCE = 14;
    //uses 1 & 2
    Gyro gyro = new Gyro(ANALOG_MODULE_SLOT, 1);
    Joystick steerWheel = new Joystick(1);
    Joystick throttle = new Joystick(2);
    Joystick xbox = new Joystick(3);
    Joystick extra = new Joystick(4);
    Victor leftDrive1 = new Victor(DIGITAL_MODULE_SLOT,1);
    Victor leftDrive2 = new Victor(DIGITAL_MODULE_SLOT,2);
    Victor leftDrive3 = new Victor(DIGITAL_MODULE_SLOT,3);
    Victor rightDrive1 = new Victor(DIGITAL_MODULE_SLOT,4);
    Victor rightDrive2 = new Victor(DIGITAL_MODULE_SLOT,5);
    Victor rightDrive3 = new Victor(DIGITAL_MODULE_SLOT,6);
    Victor intake = new Victor(DIGITAL_MODULE_SLOT,7);
    Victor catapult1 = new Victor(DIGITAL_MODULE_SLOT,8);
    Victor catapult2 = new Victor(DIGITAL_MODULE_SLOT,9);
    //Relay catapultFire = new Relay(3);
    DriverStation stupidDriverStation = DriverStation.getInstance();
    AnalogChannel ultrasonic = new AnalogChannel(3);
    JoystickButton Button1 = new JoystickButton(steerWheel, 1);
    JoystickButton Button2 = new JoystickButton(steerWheel, 2);
    JoystickButton Button3 = new JoystickButton(throttle, 1);
    DigitalInput limCatapult = new DigitalInput(3);
    final int AREA_MINIMUM = 150;
    Compressor compressor1 = new Compressor(1, 1);
    Solenoid cameraLight = new Solenoid(SOLENOID_MODULE_SLOT, 3);
    
    DoubleSolenoid solenoidShooter = new DoubleSolenoid(SOLENOID_MODULE_SLOT, 5, 6);
    
    DoubleSolenoid solenoidIntakeArm = new DoubleSolenoid(SOLENOID_MODULE_SLOT, 1, 2);
    
    ADXL345_I2C accel = new ADXL345_I2C(DIGITAL_MODULE_SLOT, ADXL345_I2C.DataFormat_Range.k2G);
    ADXL345_I2C.AllAxes axes = new ADXL345_I2C.AllAxes();
    Timer time = new Timer();
    //double acceleration;
    //double velocity = 0;
    //double distance;
    
    //0 = outside robot, 1 = in shooter, 2 = in assist position
    int ballPosition = 0;
    
    boolean catapultReady;
    
    //using MaxBotix HRLV-EZ4

    public double ultrasonicDistance()
    {
        SmartDashboard.putDouble("Ultrasonic Voltage", ultrasonic.getVoltage());
        return ((ultrasonic.getVoltage()) * 3.47826087) - 0.25;
    }
    /*
     * This is where the main PID driving code has been moved, so don't panic
     */

    public void compressorCheckThingy()
    {
        if((Math.abs(leftDrive1.get()) + Math.abs(leftDrive2.get()) + Math.abs(leftDrive3.get()) + Math.abs(rightDrive1.get()) + Math.abs(rightDrive2.get()) + Math.abs(rightDrive3.get())) < MAX_MOTOR_POWER_FOR_COMPRESSION)
        {
            compressor1.start();
        }
        else
        {
            compressor1.stop();
        }
    }

    public void superDrive(double power, double direction)
    {
        double pCorrection;
        double iCorrection;
        double totalCorrection;
        final double kP = 0.25, kI = 1.0;
        //d was 0.25
        direction = (direction) + gyro.getAngle();
        //straightAngle = direction;
        pCorrection = (-gyro.getRate()) * kP;
        iCorrection = (direction - gyro.getAngle()) * kI;
        totalCorrection = pCorrection + iCorrection;

        totalCorrection /= 10;
        if(totalCorrection > 1)
        {
            totalCorrection = 1;
        }
        else if(totalCorrection < -1)
        {
            totalCorrection = -1;
        }
        if(totalCorrection < 0)
        {
            leftSet(power * (1 - Math.abs(totalCorrection)));
            rightSet(power);
        }
        else if(totalCorrection > 0)
        {
            leftSet(power);
            rightSet(power * (1 - Math.abs(totalCorrection)));
        }
        else
        {
            bothSet(power);
        }
    }

    public void wallDistance(final double distance)
    {
        final double kP = -0.125;

        double cP;

        double power;

        double ultrasonicDistance;

        double straightAngle;
        double pCorrection;
        double iCorrection;
        double totalCorrection;
        final double GkP = 0.25, GkI = 1.0;
        straightAngle = gyro.getAngle();

        while(isEnabled())
        {
            ultrasonicDistance = ultrasonicDistance();
            cP = (distance - ultrasonicDistance) * kP;
            power = 1.0 * cP;

            pCorrection = (-gyro.getRate()) * GkP;
            iCorrection = (straightAngle - gyro.getAngle()) * GkI;
            totalCorrection = pCorrection + iCorrection;

            totalCorrection /= 10;
            if(totalCorrection > 1)
            {
                totalCorrection = 1;
            }
            else if(totalCorrection < -1)
            {
                totalCorrection = -1;
            }
            if(totalCorrection < 0)
            {
                leftSet(-power * (1 - Math.abs(totalCorrection)));
                rightSet(-power);
            }
            else if(totalCorrection > 0)
            {
                leftSet(-power);
                rightSet(-power * (1 - Math.abs(totalCorrection)));
            }
            else
            {
                bothSet(-power);
            }
            if(Math.abs(ultrasonicDistance - distance) < 0.25)
            {
                break;
            }
            bothSet(0);
        }

    }

    public void leftSet(double lDp)
    {
        leftDrive1.set(lDp);
        leftDrive2.set(lDp);
        leftDrive3.set(lDp);
        SmartDashboard.putDouble("Left Drive Power", lDp);
    }

    public void rightSet(double rDp)
    {
        rightDrive1.set(-rDp);
        rightDrive2.set(-rDp);
        rightDrive3.set(-rDp);
        SmartDashboard.putDouble("Right Drive Power", rDp);
    }

    public void bothSet(double bDp)
    {
        leftSet(bDp);
        rightSet(bDp);
    }

    public void turnSet(double tDp)
    {
        leftSet(tDp);
        rightSet(-tDp);
    }

    public void checkBattery()
    {
        if(stupidDriverStation.getBatteryVoltage() < 12)
        {
            SmartDashboard.putBoolean("Replace Battery NOW", true);
        }
        else
        {
            SmartDashboard.putBoolean("Replace Battery NOW", false);
        }
    }

    public double batteryVoltage()
    {
        return stupidDriverStation.getBatteryVoltage();
    }

    public void lowerIntake()
    {
        // motor on intake
        solenoidIntakeArm.set(DoubleSolenoid.Value.kForward);
        /*
         waitBrendan(1);//change
         solenoidArm1.set(DoubleSolenoid.Value.kOff);
         solenoidArm2.set(DoubleSolenoid.Value.kOff);
         // stop motor on intake?
         * */
    }

    public void raiseIntake()
    {
        solenoidIntakeArm.set(DoubleSolenoid.Value.kReverse);
        /*
         waitBrendan(time);//change 
         solenoidArm1.set(DoubleSolenoid.Value.kOff);
         solenoidArm2.set(DoubleSolenoid.Value.kOff);
         * */
    }

    public void expel()
    {
        if(ballPosition == 1)
        {
            //kick out the ball
            intake.set(-1);
            lowerIntake();
            intake.set(0);
            raiseIntake();
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball not in shooter");
        }

    }

    public boolean reload()
    {
        if(!(ballPosition == 1))
        {
            if(limCatapult.get())
            {
                catapult1.set(1);
                catapult2.set(1);
                return false;
            }
            else
            {
                catapult1.set(0);
                catapult2.set(0);
                ballPosition = 1;
                return true;
            }
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball in catapult");
            return true;
        }

    }

    public void intake()
    {
        if(ballPosition == 0)
        {
            lowerIntake();
            intake.set(1);//could be reversed
            raiseIntake();
            intake.set(0);
            ballPosition = 1;
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball not outside robot");
        }

    }

    public void assistExpel()
    {
        if(ballPosition == 2)
        {
            //stuffiness
            intake.set(-1);
            waitBrendan(1);
            intake.set(0);
            ballPosition = 0;
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball not in assist position");
        }
    }

    public void assistIntake()
    {
        if(ballPosition == 0)
        {
            //stuffiness            
            lowerIntake();
            intake.set(0.4);//could be reversed
            raiseIntake();
            intake.set(0);
            ballPosition = 2;
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball not outside robot");
        }
    }

    public void robotInit()
    {
        System.out.println("Electrical peoples are slow /(Both ways/)");
    }

    public void fire()
    {
        if(ballPosition == 1)
        {
            //do whatever the heck the shooter team made to make it shoot thingies at the other thingies
            solenoidShooter.set(DoubleSolenoid.Value.kForward);
            waitBrendan(1);
            solenoidShooter.set(DoubleSolenoid.Value.kReverse);
            catapultReady = false;
        }
        else
        {
            SmartDashboard.putString("Error Messages", "Ball not in catapult");
        }
    }

    public void outputAccelData()
    {
        axes = accel.getAccelerations();
        SmartDashboard.putDouble("X Acceleration", axes.XAxis);
        SmartDashboard.putDouble("Y Acceleration", axes.YAxis);
        SmartDashboard.putDouble("Z Acceleration", axes.ZAxis);
    }

    //Adjust the steering whell input to normalize from -1 to 1
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

    public void waitBrendan(double time)
    {
        Timer wait = new Timer();
        //wait.start();
        wait.delay(time);
        /*
         while(wait.get() < time)
         {
         System.out.println("Party time!");
         //Party Time !!!
         }
         * */
    }

    public void disabled()
    {
        System.out.println("I'm going to sleep now.");
        while(isDisabled())
        {
            checkBattery();
        }
    }

    public void computerAssistedFire()
    {
        double distance = ultrasonicDistance();
        turnSet(1);
        waitBrendan(0.1);
        bothSet(0);

        if(ultrasonicDistance() > distance)
        {
            turnSet(0.4);
            while(isEnabled())
            {
                if(ultrasonicDistance() > distance)
                {
                    break;
                }
                distance = ultrasonicDistance();
            }
        }
        else
        {
            turnSet(-0.4);
            while(isEnabled())
            {
                if(ultrasonicDistance() > distance)
                {
                    break;
                }
                distance = ultrasonicDistance();
            }
        }
        wallDistance(IDEAL_SHOOTING_DISTANCE);
        fire();
    }

    public void computerAssistedFireLinear()
    {
        wallDistance(IDEAL_SHOOTING_DISTANCE);
        fire();
    }

    public void autonomous()
    {
        /*
         compressor1.start();
         solenoidShooter.set(DoubleSolenoid.Value.kForward);
         * */
        cameraLight.set(true);
        VisionThingy vision = new VisionThingy();
        final double STARTING_DISTANCE = ultrasonicDistance();
        double[][] horizontalTargetLocations;
        double[][] verticalTargetLocations;
        if(stupidDriverStation.getDigitalIn(4))
        {
            System.out.println("Not doing anything");
            return;
        }
        //fire with pickup
        if(stupidDriverStation.getDigitalIn(2))
        {
            while(isAutonomous() && isEnabled())
            {
                compressorCheckThingy();
                horizontalTargetLocations = vision.horizontalTargetLocations();
                verticalTargetLocations = vision.verticalTargetLocations();
                Timer timer = new Timer();

                wallDistance(IDEAL_SHOOTING_DISTANCE);
                fire();
                wallDistance(STARTING_DISTANCE);
                intake();
                computerAssistedFireLinear();
                checkBattery();
                break;
            }
            //Party time!
        }
        //Fire without pickup
        if(stupidDriverStation.getDigitalIn(3))
        {
            //assumes ball is in robot at start
            ballPosition = 1;
            vision.mainVision();
            if(vision.isHot())
            {
                while(isAutonomous() && isEnabled())
                {
                    compressorCheckThingy();
                    computerAssistedFireLinear();
                    checkBattery();
                    break;
                }
            }
            else
            {

                waitBrendan(5);
                while(isAutonomous() && isEnabled())
                {
                    compressorCheckThingy();
                    computerAssistedFireLinear();
                    checkBattery();
                    break;
                }
            }

        }
        cameraLight.set(false);
    }

    public void operatorControl()
    {
        
        //cameraLight.set(true);
        Timer heartbeat = new Timer();
        catapultReady = false;
        heartbeat.start();
        //main loop
        {
            while(isOperatorControl() && isEnabled())
            {
                checkBattery();
                compressorCheckThingy();
                SmartDashboard.putDouble("Heartbeat", heartbeat.get());
                heartbeat.reset();
                //output data to SmartDashboard
                SmartDashboard.putDouble("Throttle", -(throttle.getRawAxis(2)));
                SmartDashboard.putDouble("swRot", swAdjust(steerWheel.getAxis(Joystick.AxisType.kX)));
                SmartDashboard.putDouble("Ultrasonic Distance", ultrasonicDistance());

                if(!stupidDriverStation.getDigitalIn(1))
                {
                    if(Button1.get() || Button2.get() || throttle.getRawButton(1))
                    {
                        turnSet(swAdjust(steerWheel.getAxis(Joystick.AxisType.kX)));
                    }
                    else
                    {
                        superDrive(-throttle.getRawAxis(2), swAdjust(steerWheel.getAxis(Joystick.AxisType.kX)) * 180);
                    }
                }
                else
                {
                    leftSet(-extra.getRawAxis(2));
                    rightSet(-throttle.getRawAxis(2));
                }

                SmartDashboard.putDouble("Gyro", gyro.getAngle());
                //Joystick controls: Moving/Firing
                //
                if(throttle.getRawButton(2))
                {
                }
                //manual fire
                if(throttle.getRawButton(3))
                {
                    fire();
                }
                //computer assisted fire: normal to wall
                if(throttle.getRawButton(4))
                {
                    computerAssistedFire();
                }
                //computer assisted fire: assume normal to wall
                if(throttle.getRawButton(5))
                {
                    computerAssistedFireLinear();
                }

                //xbox controller does: intake
                //
                if(xbox.getRawButton(1))
                {
                    //auto normal pickup
                    intake();
                }
                if(xbox.getRawButton(2))
                {
                    //auto expel
                    expel();
                }
                if(xbox.getRawButton(3))
                {
                    //auto slow pickup
                    assistIntake();
                }
                if(xbox.getRawButton(4))
                {
                    //auto slow expel
                    assistExpel();
                }
                if(xbox.getRawAxis(3) < -0.1)
                {
                    //up
                    solenoidIntakeArm.set(DoubleSolenoid.Value.kForward);
                }
                else if(xbox.getRawAxis(3) > 0.1)
                {
                    //down
                    solenoidIntakeArm.set(DoubleSolenoid.Value.kReverse);

                }
                else
                {
                    /*
                     intakeintakeaaa.set(Relay.Value.kOff);
                     intake2.set(Relay.Value.kOff);
                     * */
                }
                intake.set(xbox.getRawAxis(5));
                if(catapultReady == false)
                {
                    catapultReady = reload();
                }
            }
        }
    }

    public void test()
    {
        while(isTest() && isEnabled())
        {
            //outputAccelData();
            SmartDashboard.putDouble("Ultrasonic Distance", ultrasonicDistance());
            checkBattery();
        }
    }
}