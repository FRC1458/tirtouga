/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTemplate extends SimpleRobot
{
    Gyro gyro = new Gyro(1, 1);
    Joystick sw = new Joystick(1);
    Joystick t = new Joystick(2);
    Victor leftDrive1 = new Victor(1);
    Victor leftDrive2 = new Victor(2);
    Victor leftDrive3 = new Victor(3);
    Victor rightDrive1 = new Victor(4);
    Victor rightDrive2 = new Victor(5);
    Victor rightDrive3 = new Victor(6);
    Victor intake = new Victor(7);
    Victor catapult1 = new Victor(8);
    Victor catapult2 = new Victor(9);
    Relay intake1 = new Relay(1);
    Relay intake2 = new Relay(2);
    Relay catapultFire = new Relay(3);
    
    
    JoystickButton Button1 = new JoystickButton(sw, 1);
    JoystickButton Button2 = new JoystickButton(sw, 2);
    AxisCamera camera;          // the axis camera object (connected to the switch)
    CriteriaCollection cc;      // the criteria for doing the particle filter operation
    final int AREA_MINIMUM = 150;

    public void robotInit()
    {
        camera = AxisCamera.getInstance();  // get an instance of the camera
        cc = new CriteriaCollection();      // create the criteria for the particle filter
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, AREA_MINIMUM, 65535, false);
    }

    //Adjust the steering whell input to normalize from -1 to 1
    double swAdjust(double i)
    {
        //increase so bottom is 0
        i += 0.945;
        //multiply so top is 2
        i *= 1.13378685;
        //put back into -1 to 1
        i--;
        return i;
    }

    public void autonomous()
    {
        VisionThingy vision = new VisionThingy();
        while(isAutonomous() && isEnabled())
        {
            vision.mainVision();
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {

        //declare array for holding motor powers
        Timer heartbeat = new Timer();
        heartbeat.start();

        double straightAngle = 0;
        double pCorrection;
        double iCorrection;
        double dCorrection;
        double totalCorrection;
        double pastRate = 0;
        double kP = 0.25, kI = 1.0, kD = 0.25;
        double lDp;
        double rDp;
        //main loop
        while(isOperatorControl() && isEnabled())
        {
            SmartDashboard.putDouble("Heartbeat", heartbeat.get());
            heartbeat.reset();
            //output data to SmartDashboard
            SmartDashboard.putDouble("Throttle", -(t.getRawAxis(2)));
            SmartDashboard.putDouble("swRot", swAdjust(sw.getAxis(Joystick.AxisType.kX)));

            SmartDashboard.putBoolean("Turn mode", (Button1.get() || Button2.get()));

            SmartDashboard.putDouble("Angle :", gyro.getAngle());
            SmartDashboard.putDouble("Rate: ", gyro.getRate());
            SmartDashboard.putDouble("Straight Angle: ", straightAngle);
            SmartDashboard.putDouble("Difference: ", straightAngle - gyro.getAngle());

            straightAngle = (swAdjust(sw.getAxis(Joystick.AxisType.kX)) * 180) + gyro.getAngle();

            pCorrection = (-gyro.getRate()) * kP;
            iCorrection = (straightAngle - gyro.getAngle()) * kI;
            dCorrection = 0 - pastRate * kD;
            totalCorrection = pCorrection + iCorrection + dCorrection;
            SmartDashboard.putDouble("Original Correction", totalCorrection);

            totalCorrection /= 10;
            if(totalCorrection > 1)
            {
                totalCorrection = 1;
            }
            else if(totalCorrection < -1)
            {
                totalCorrection = -1;
            }

            SmartDashboard.putDouble("Total Correction", totalCorrection);

            if((Button1.get() || Button2.get()))
            {
                lDp = (sw.getAxis(Joystick.AxisType.kX) * 0.65);
                rDp = (sw.getAxis(Joystick.AxisType.kX) * 0.65);
            }
            else
            {
                if(totalCorrection < 0)
                {
                    lDp = (-t.getRawAxis(2) * (1 - Math.abs(totalCorrection)));
                    rDp = (t.getRawAxis(2));
                }
                else if(totalCorrection > 0)
                {
                    lDp = (-t.getRawAxis(2));
                    rDp = (t.getRawAxis(2) * (1 - Math.abs(totalCorrection)));
                }
                else
                {
                    lDp = (-t.getRawAxis(2));
                    rDp = (t.getRawAxis(2));

                }
            }

            //Output motor powers to SMartDashboard
            SmartDashboard.putDouble("Left Drive Power", lDp);
            SmartDashboard.putDouble("Right Drive Power", rDp);

            leftDrive1.set(lDp);
            leftDrive2.set(lDp);
            leftDrive3.set(lDp);
            rightDrive1.set(rDp);
            rightDrive2.set(rDp);
            rightDrive3.set(rDp);

            //cameraThingy();

            SmartDashboard.putDouble("Gyro", gyro.getAngle());

        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test()
    {
    }
}