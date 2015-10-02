/*----------------------------------------------------------------------------*/
/*Copyright (c) FIRST2008. AllRights Reserved.                             */
/* Software modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    //Joystick steeringWheel = new Joystick(1);
    Joystick throttle = new Joystick(1);
    Victor victorLeft = new Victor(1);

    public void autonomous() {
        /*   while (isAutonomous() && isEnabled()) {
         SmartDashboard.putBoolean("Button 1", steeringWheel.getRawButton(1));
         SmartDashboard.putBoolean("Button 2", steeringWheel.getRawButton(2));
         SmartDashboard.putBoolean("Button 3", steeringWheel.getRawButton(3));
         SmartDashboard.putBoolean("Button 4", steeringWheel.getRawButton(4));
         SmartDashboard.putBoolean("Button 5", steeringWheel.getRawButton(5));
         SmartDashboard.putBoolean("Button 6", steeringWheel.getRawButton(6));
         } */
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {

        while (isOperatorControl() && isEnabled()) {
            victorLeft.set(throttle.getRawAxis(2));
            ///victorRight.set(rightValue);
            //SmartDashboard.putDouble("Right motor", rightValue);
            SmartDashboard.putDouble("Left motor", throttle.getRawAxis(2));
            //throttleAxis = 0;
        }

    }
}
