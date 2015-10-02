package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTemplate extends SimpleRobot
{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    Joystick sw = new Joystick(1);
    /*
     Joystick throttle = new Joystick(1);
     double motorValue = 0;
     boolean limitValue;
     Victor motor = new Victor(1);
     DigitalInput limitSwitch = new DigitalInput(1);
     * */

    double swAdjust(double i)
    {
        //increase so bottom is 0
        i += 0.945;
        //multiply so top is 2
        i *= 1.13378685;
        //put back into -1 to 1
        i-=1.001;
        return i;
    }

    public void autonomous()
    {
        while(isAutonomous() && isEnabled())
        {
            SmartDashboard.putDouble("Steering wheel raw", sw.getAxis(Joystick.AxisType.kX));
            SmartDashboard.putDouble("Steering wheel adjusted", swAdjust(sw.getAxis(Joystick.AxisType.kX)));
        }
    }
    /*
     public void printToClassmate(){
     DriverStationLCD driverStation;
     driverStation = DriverStationLCD.getInstance();
     driverStation.println(DriverStationLCD.Line.kUser1, 1, "Motor: " + motorValue);
     driverStation.println(DriverStationLCD.Line.kUser2, 1, "Limit Switch: " + limitValue);
     driverStation.updateLCD();
     }
     /**
     * This function is called once each time the robot enters operator control.
     */

    public void operatorControl()
    {

        while(isOperatorControl() && isEnabled())
        {
            /*
             double throttleAxis = throttle.getRawAxis(2);
             motorValue = throttleAxis;
             limitValue = limitSwitch.get();
             //SmartDashboard.putDouble("Motor: ", motorValue);
             if(limitValue)
             motor.set(motorValue);
             printToClassmate();
             motorValue = 0;
             * */
            SmartDashboard.putDouble("Steering wheel raw", sw.getAxis(Joystick.AxisType.kX));
            SmartDashboard.putDouble("Steering wheel adjusted", swAdjust(sw.getAxis(Joystick.AxisType.kX)));
        }

    }
}
