/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;
import java.lang.Math;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTemplate extends SimpleRobot
{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    Joystick throttle = new Joystick(1);
    Gyro newGyro = new Gyro(1, 1);
    Victor left = new Victor(1);
    Victor right = new Victor(2);
    final double pi = Math.PI;

    public void autonomous()
    {
    }

    public void robotInit()
    {
        System.out.println("I'm working don't kill me please");



    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {
        double straightAngle = 0;
        double pCorrection;
        double iCorrection;
        double dCorrection;
        double totalCorrection;
        double pastRate = 0;
        double kP = 0.25, kI = 1.0, kD = 0.25;
        boolean pPressed = false, iPressed = false, dPressed = false;


        while(isOperatorControl() && isEnabled())
        {

            SmartDashboard.putDouble("Angle :", newGyro.getAngle());
            SmartDashboard.putDouble("Rate: ", newGyro.getRate());
            SmartDashboard.putDouble("Straight Angle: ", straightAngle);
            SmartDashboard.putDouble("Difference: ", straightAngle - newGyro.getAngle());

            SmartDashboard.putDouble("kP", kP);
            SmartDashboard.putDouble("kI", kI);
            SmartDashboard.putDouble("kD", kD);

            if(throttle.getRawButton(1))
            {
                straightAngle = newGyro.getAngle();
            }

            pCorrection = (-newGyro.getRate()) * kP;

            iCorrection = (straightAngle - newGyro.getAngle()) * kI;

            dCorrection = 0 - pastRate * kD;

            totalCorrection = pCorrection + iCorrection + dCorrection;

            SmartDashboard.putDouble("Original Correction", totalCorrection);
            
            /*
            totalCorrection = java.lang.Math.atan(totalCorrection);
            totalCorrection /= (pi / 2);
            */
            
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

            motorStuffiness(totalCorrection);

            if(throttle.getRawButton(6) && pPressed == false)
            {
                kP += 0.05;
                pPressed = true;
            }
            else if(throttle.getRawButton(7) && pPressed == false)
            {
                kP -= 0.05;
                pPressed = true;
            }
            else if(!throttle.getRawButton(6) && !throttle.getRawButton(7))
            {
                pPressed = false;
            }

            if(throttle.getRawButton(3) && iPressed == false)
            {
                kI += 0.05;
                iPressed = true;
            }
            else if(throttle.getRawButton(2) && iPressed == false)
            {
                kI -= 0.05;
                iPressed = true;
            }
            else if(!throttle.getRawButton(3) && !throttle.getRawButton(2))
            {
                iPressed = false;
            }

            if(throttle.getRawButton(11) && dPressed == false)
            {
                kD += 0.05;
                dPressed = true;
            }
            else if(throttle.getRawButton(10) && dPressed == false)
            {
                kD -= 0.05;
                dPressed = true;
            }
            else if(!throttle.getRawButton(11) && !throttle.getRawButton(10))
            {
                dPressed = false;
            }
            pastRate = totalCorrection;
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test()
    {
    }

    public void disabled()
    {
        System.out.println("I'm going to stop now");
    }

    private void motorStuffiness(double totalCorrection)
    {
        double power = throttle.getRawAxis(2);
        if(power > 0)
        {
            totalCorrection *= -1;
        }

        if(throttle.getRawButton(4))
        {
            left.set(0.5);
            right.set(0.5);
        }
        else if(throttle.getRawButton(5))
        {
            left.set(-0.5);
            right.set(-0.5);
        }
        else
        {
            if(totalCorrection < 0)
            {
                left.set(-power * (1 - Math.abs(totalCorrection)));
                right.set(power);
            }
            else if(totalCorrection > 0)
            {
                left.set(-power);
                right.set(power * (1 - Math.abs(totalCorrection)));
            }
        }
    }
}