/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
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
    /*
     * This function is called once each time the robot enters autonomous mode.
     */
    /* Swag */
    Joystick a = new Joystick(1);
    Joystick b = new Joystick(2);
    public int thingyCount;

    public void autonomous()
    {
        while(isEnabled() && isAutonomous())
        {
            SmartDashboard.putDouble("1", a.getRawAxis(2));
            SmartDashboard.putDouble("2", b.getRawAxis(2));
        }

    }

    public void waitSasha(double time)
    {
        Timer wait = new Timer();
        wait.start();
        while(wait.get() < time)
        {
            //Party Time !!!
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void robotInit()
    {
        thingyCount = 0;
    }

    public void operatorControl()
    {
    }

    public void disabled()
    {
        SmartDashboard.putNumber("thingyCount", thingyCount);
    }
}
