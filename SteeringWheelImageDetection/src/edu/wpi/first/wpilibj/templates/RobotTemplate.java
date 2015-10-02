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
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.CriteriaCollection;
import edu.wpi.first.wpilibj.image.NIVision;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
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
    public final int time = 3;
    public final double testPower = 0.75;
    Gyro gyro = new Gyro(1, 2);
    Joystick sw = new Joystick(1);
    Joystick t = new Joystick(2);
    Victor leftDrive = new Victor(1);
    Victor rightDrive = new Victor(2);
    Victor shooter = new Victor(4);
    Relay shooterTilt = new Relay(1);
    JoystickButton Button1 = new JoystickButton(sw, 1);
    JoystickButton Button2 = new JoystickButton(sw, 2);
    JoystickButton powerUp = new JoystickButton(sw, 3);
    JoystickButton powerDown = new JoystickButton(sw, 5);
    Gyro newGyro = new Gyro(1, 1);
    Servo trigger1 = new Servo(6);
    Servo trigger2 = new Servo(5);
    Timer triggerTimer = new Timer();
    DigitalInput limTiltBottom = new DigitalInput(4);
    DigitalInput limTiltTop = new DigitalInput(5);
    boolean spin = false;
    //Camera constants used for distance calculation
    final int Y_IMAGE_RES = 480;		//X Image resolution in pixels, should be 120, 240 or 480
    final double VIEW_ANGLE = 49;		//Axis M1013
    //final double VIEW_ANGLE = 41.7;		//Axis 206 camera
    //final double VIEW_ANGLE = 37.4;  //Axis M1011 camera
    final double PI = 3.141592653;
    //Score limits used for target identification
    final int RECTANGULARITY_LIMIT = 40;
    final int ASPECT_RATIO_LIMIT = 55;
    //Score limits used for hot target determination
    final int TAPE_WIDTH_LIMIT = 50;
    final int VERTICAL_SCORE_LIMIT = 50;
    final int LR_SCORE_LIMIT = 50;
    //Minimum area of particles to be considered
    final int AREA_MINIMUM = 150;
    //Maximum number of particles to process
    final int MAX_PARTICLES = 8;
    AxisCamera camera;          // the axis camera object (connected to the switch)
    CriteriaCollection cc;      // the criteria for doing the particle filter operation
    TargetReport target = new TargetReport();
    int verticalTargets[] = new int[MAX_PARTICLES];
    int horizontalTargets[] = new int[MAX_PARTICLES];
    int verticalTargetCount, horizontalTargetCount;

    public class Scores
    {
        double rectangularity;
        double aspectRatioVertical;
        double aspectRatioHorizontal;
    }

    public class TargetReport
    {
        int verticalIndex;
        int horizontalIndex;
        boolean Hot;
        double totalScore;
        double leftScore;
        double rightScore;
        double tapeWidthScore;
        double verticalScore;
    }

    public void spinUp(double power)
    {
        if(t.getRawButton(2))
        {
            if(!spin)
            {
                shooter.set(-power);
                spin = true;
            }
            else
            {
                shooter.set(0);
                spin = false;
            }
        }
    }

    public void fire(boolean auto)
    {
        if(auto)
        {
            trigger1.setAngle(130);
            trigger2.setAngle(0);
        }
        else
        {
            trigger1.setAngle(0);
            trigger2.setAngle(110);
        }
    }

    public boolean fireControl()
    {
        if(triggerTimer.get() > 2)
        {
            triggerTimer.stop();
            triggerTimer.reset();
        }
        if(t.getRawButton(3) && triggerTimer.get() == 0)
        {
            triggerTimer.start();
        }
        if(triggerTimer.get() < 1 && triggerTimer.get() > 0)
        {
            return true;
        }
        return false;
    }

    public void tilt()
    {
        if(t.getRawButton(5) /*&& !limTiltTop.get()*/)
        {
            shooterTilt.set(Relay.Value.kReverse);
        }
        else if(t.getRawButton(4) /*&& limTiltBottom.get()*/)
        {
            shooterTilt.set(Relay.Value.kForward);
        }
        else
        {
            shooterTilt.set(Relay.Value.kOff);
        }
    }

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
    //calculation function

    double Dcalc(double swRot, double t, boolean m, boolean r)
    {
        //turning mode
        if(!r)
        {
            if(m)
            {
                return (1 * swRot);
            }
            else /*standard mode*/ {
                swRot++;
                swRot /= 2;
                return (t * swRot);
            }
        }
        else if(r)
        {
            if(m)
            {
                return (1 * -swRot);
            }
            else /*standard mode*/ {
                swRot++;
                swRot /= 2;
                return (t * (1 - swRot));
            }
        }
        return 0;
    }

    public void cameraThingy()
    {
        try
        {
            /**
             * Do the image capture with the camera and apply the algorithm
             * described above. This sample will either get images from the
             * camera or from an image file stored in the top level directory in
             * the flash memory on the cRIO. The file name in this case is
             * "testImage.jpg"
             *
             */
            ColorImage image;
            image = camera.getImage(); // comment if using stored image
            //ColorImage image;                           // next 2 lines read image from flash on cRIO
            //image = new RGBImage("/testImage.jpg");		// get the sample image from the cRIO flash
            BinaryImage thresholdImage = image.thresholdHSV(116, 227, 0/*62*/, 255, 181, 254);   // keep only green objects
            //0,255,0,255,250,255-look for white

            thresholdImage.write("/threshold.bmp");
            BinaryImage filteredImage = thresholdImage.particleFilter(cc);           // filter out small particles
            filteredImage.write("/filteredImage.bmp");

            //iterate through each particle and score to see if it is a target
            Scores scores[] = new Scores[filteredImage.getNumberParticles()];
            horizontalTargetCount = verticalTargetCount = 0;

            //System.out.println(filteredImage.getNumberParticles());
            SmartDashboard.putInt("# Particles", filteredImage.getNumberParticles());

            if(filteredImage.getNumberParticles() > 0)
            {
                for(int i = 0; i < MAX_PARTICLES && i < filteredImage.getNumberParticles(); i++)
                {
                    ParticleAnalysisReport report = filteredImage.getParticleAnalysisReport(i);
                    scores[i] = new Scores();

                    //Score each particle on rectangularity and aspect ratio
                    scores[i].rectangularity = scoreRectangularity(report);
                    scores[i].aspectRatioVertical = scoreAspectRatio(filteredImage, report, i, true);
                    scores[i].aspectRatioHorizontal = scoreAspectRatio(filteredImage, report, i, false);

                    //Check if the particle is a horizontal target, if not, check if it's a vertical target
                    if(scoreCompare(scores[i], false))
                    {
                        //System.out.println("particle: " + i + "is a Horizontal Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
                        SmartDashboard.putString("Horizontal Target Finding Status", ("particle: " + i + "is a Horizontal Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y));
                        horizontalTargets[horizontalTargetCount++] = i; //Add particle to target array and increment count
                    }
                    else if(scoreCompare(scores[i], true))
                    {
                        //System.out.println("particle: " + i + "is a Vertical Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
                        SmartDashboard.putString("Vertical Target Finding Status", ("particle: " + i + "is a Vertical Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y));
                        verticalTargets[verticalTargetCount++] = i;  //Add particle to target array and increment count
                    }
                    else
                    {
                        //System.out.println("particle: " + i + "is not a Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
                        SmartDashboard.putString("Non-target Particles Status", ("particle: " + i + "is not a Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y));
                    }
                    //System.out.println("rect: " + scores[i].rectangularity + "ARHoriz: " + scores[i].aspectRatioHorizontal);
                    SmartDashboard.putDouble("rectangulatity", scores[i].rectangularity);
                    SmartDashboard.putDouble("ARHoriz", scores[i].aspectRatioHorizontal);
                    SmartDashboard.putDouble("ARVert", scores[i].aspectRatioVertical);
                    //System.out.println("ARVert: " + scores[i].aspectRatioVertical);
                }

                //Zero out scores and set verticalIndex to first target in case there are no horizontal targets
                target.totalScore = target.leftScore = target.rightScore = target.tapeWidthScore = target.verticalScore = 0;
                target.verticalIndex = verticalTargets[0];
                for(int i = 0; i < verticalTargetCount; i++)
                {
                    ParticleAnalysisReport verticalReport = filteredImage.getParticleAnalysisReport(verticalTargets[i]);
                    for(int j = 0; j < horizontalTargetCount; j++)
                    {
                        ParticleAnalysisReport horizontalReport = filteredImage.getParticleAnalysisReport(horizontalTargets[j]);
                        double horizWidth, horizHeight, vertWidth, leftScore, rightScore, tapeWidthScore, verticalScore, total;

                        //Measure equivalent rectangle sides for use in score calculation
                        horizWidth = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
                        vertWidth = NIVision.MeasureParticle(filteredImage.image, verticalTargets[i], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
                        horizHeight = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);

                        //Determine if the horizontal target is in the expected location to the left of the vertical target
                        leftScore = ratioToScore(1.2 * (verticalReport.boundingRectLeft - horizontalReport.center_mass_x) / horizWidth);
                        //Determine if the horizontal target is in the expected location to the right of the  vertical target
                        rightScore = ratioToScore(1.2 * (horizontalReport.center_mass_x - verticalReport.boundingRectLeft - verticalReport.boundingRectWidth) / horizWidth);
                        //Determine if the width of the tape on the two targets appears to be the same
                        tapeWidthScore = ratioToScore(vertWidth / horizHeight);
                        //Determine if the vertical location of the horizontal target appears to be correct
                        verticalScore = ratioToScore(1 - (verticalReport.boundingRectTop - horizontalReport.center_mass_y) / (4 * horizHeight));
                        total = leftScore > rightScore ? leftScore : rightScore;
                        total += tapeWidthScore + verticalScore;

                        //If the target is the best detected so far store the information about it
                        if(total > target.totalScore)
                        {
                            target.horizontalIndex = horizontalTargets[j];
                            target.verticalIndex = verticalTargets[i];
                            target.totalScore = total;
                            target.leftScore = leftScore;
                            target.rightScore = rightScore;
                            target.tapeWidthScore = tapeWidthScore;
                            target.verticalScore = verticalScore;
                        }
                    }
                    //Determine if the best target is a Hot target
                    target.Hot = hotOrNot(target);
                }

                if(verticalTargetCount > 0)
                {
                    //Information about the target is contained in the "target" structure
                    //To get measurement information such as sizes or locations use the
                    //horizontal or vertical index to get the particle report as shown below
                    ParticleAnalysisReport distanceReport = filteredImage.getParticleAnalysisReport(target.verticalIndex);
                    double distance = computeDistance(filteredImage, distanceReport, target.verticalIndex);
                    if(target.Hot)
                    {
                        //System.out.println("Hot target located");
                        //System.out.println("Distance: " + distance);
                        SmartDashboard.putBoolean("Target is hot", true);
                        SmartDashboard.putDouble("Distance", distance);
                    }
                    else
                    {
                        //System.out.println("No hot target present");
                        //System.out.println("Distance: " + distance);
                        SmartDashboard.putBoolean("Target is hot", false);
                        SmartDashboard.putDouble("Distance", distance);
                    }
                }
            }

            /**
             * all images in Java must be freed after they are used since they
             * are allocated out of C data structures. Not calling free() will
             * cause the memory to accumulate over each pass of this loop.
             */
            filteredImage.free();
            thresholdImage.free();
            image.free();

//            } catch (AxisCameraException ex) {        // this is needed if the camera.getImage() is called
//                ex.printStackTrace();
        } catch(NIVisionException ex)
        {
            ex.printStackTrace();
        } catch(AxisCameraException ex)
        {
            ex.printStackTrace();
        }
        return;
    }

    public void autonomous()
    {
     
        Timer timer = new Timer();
        timer.start();

        while(isEnabled() && isAutonomous())
        {
            leftDrive.set(testPower);
            rightDrive.set(-testPower);
            if(timer.get() >= time)
            {
                break;
            }
            SmartDashboard.putDouble("Timer", timer.get());
        }
        leftDrive.set(0);
        rightDrive.set(0);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {

        //declare array for holding motor powers
        double p = 0;
        Timer heartbeat = new Timer();
        heartbeat.start();
        double lDp = 0;
        double rDp = 0;
        double fullmult = 0;
        double straightAngle = 0;
        double pCorrection;
        double iCorrection;
        double dCorrection;
        double totalCorrection;
        double pastRate = 0;
        double kP = 0.25, kI = 1.0, kD = 0.25;
        boolean pPressed = false, iPressed = false, dPressed = false;
        //main loop
        while(isOperatorControl() && isEnabled())
        {
            SmartDashboard.putDouble("Heartbeat", heartbeat.get());
            heartbeat.reset();
            //output data to SmartDashboard
            SmartDashboard.putDouble("Throttle", -(t.getRawAxis(2)));
            SmartDashboard.putDouble("swRot", swAdjust(sw.getAxis(Joystick.AxisType.kX)));
            //Do the calculations
            //Tell motors what to do
            //lDp = (Dcalc(swAdjust(sw.getAxis(Joystick.AxisType.kX)), -(t.getRawAxis(2)), (Button1.get() || Button2.get()), false));
            //rDp = (Dcalc(swAdjust(sw.getAxis(Joystick.AxisType.kX)), -(t.getRawAxis(2)), (Button1.get() || Button2.get()), true));
            SmartDashboard.putBoolean("Turn mode", (Button1.get() || Button2.get()));
            //SmartDashboard.putBoolean("limitTop", limitTiltTop.get());
            
            SmartDashboard.putDouble("Angle :", newGyro.getAngle());
            SmartDashboard.putDouble("Rate: ", newGyro.getRate());
            SmartDashboard.putDouble("Straight Angle: ", straightAngle);
            SmartDashboard.putDouble("Difference: ", straightAngle - newGyro.getAngle());

            SmartDashboard.putDouble("kP", kP);
            SmartDashboard.putDouble("kI", kI);
            SmartDashboard.putDouble("kD", kD);
            
            straightAngle = (swAdjust(sw.getAxis(Joystick.AxisType.kX))*180)+newGyro.getAngle();

            if(t.getRawButton(1))
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
            /*
            if(!(Button1.get() || Button2.get()))
            {
                if(lDp > rDp)
                {
                    fullmult = ((-t.getRawAxis(2)) / lDp);
                    lDp = lDp * fullmult;
                    rDp = rDp * fullmult;

                }
                else if(lDp < rDp)
                {
                    fullmult = ((-t.getRawAxis(2)) / rDp);
                    lDp = lDp * fullmult;
                    rDp = rDp * fullmult;
                }
                else
                {
                    fullmult = ((-t.getRawAxis(2)) / lDp);
                    lDp = lDp * fullmult;
                    rDp = rDp * fullmult;
                }
            }
            */
            if(totalCorrection < 0)
            {
                leftDrive.set(-t.getRawAxis(2) * (1 - Math.abs(totalCorrection)));
                rightDrive.set(t.getRawAxis(2));
            }
            else if(totalCorrection > 0)
            {
                leftDrive.set(-t.getRawAxis(2));
                rightDrive.set(t.getRawAxis(2) * (1 - Math.abs(totalCorrection)));
            }
            //leftDrive.set(lDp);
            //rightDrive.set(-rDp);
            //Output motor powers to SMartDashboard
            SmartDashboard.putDouble("Left Drive Power", lDp);
            SmartDashboard.putDouble("Right Drive Power", rDp);
            if(powerUp.get())
            {
                p += 0.1;
                if(p > 1)
                {
                    p = 1;
                }
            }
            else if(powerDown.get())
            {
                p -= 0.1;
                if(p < 0)
                {
                    p = 0;
                }
            }
            SmartDashboard.putDouble("Frisbee power", p);

            //firing and shooter-related stuff
            tilt();

            //spinUp(p);
            if(t.getRawButton(2))
            {
                if(!spin)
                {
                    shooter.set(-p);
                    spin = true;
                }
                else
                {
                    shooter.set(0);
                    spin = false;
                }
            }
            else if(spin)
            {
                shooter.set(-p);
            }
            fire(fireControl());

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

    double computeDistance(BinaryImage image, ParticleAnalysisReport report, int particleNumber) throws NIVisionException
    {
        double rectLong, height;
        int targetHeight;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        //using the smaller of the estimated rectangle long side and the bounding rectangle height results in better performance
        //on skewed rectangles
        height = Math.min(report.boundingRectHeight, rectLong);
        targetHeight = 5;

        return Y_IMAGE_RES * targetHeight / (height * 12 * 2 * Math.tan(VIEW_ANGLE * Math.PI / (180 * 2))) * 52.5;
    }

    /**
     * Computes a score (0-100) comparing the aspect ratio to the ideal aspect
     * ratio for the target. This method uses the equivalent rectangle sides to
     * determine aspect ratio as it performs better as the target gets skewed by
     * moving to the left or right. The equivalent rectangle is the rectangle
     * with sides x and y where particle area= x*y and particle perimeter= 2x+2y
     *
     * @param image The image containing the particle to score, needed to
     * perform additional measurements
     * @param report The Particle Analysis Report for the particle, used for the
     * width, height, and particle number
     * @param outer	Indicates whether the particle aspect ratio should be
     * compared to the ratio for the inner target or the outer
     * @return The aspect ratio score (0-100)
     */
    public double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean vertical) throws NIVisionException
    {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        idealAspectRatio = vertical ? (4.0 / 32) : (23.5 / 4);	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall

        //Divide width by height to measure aspect ratio
        if(report.boundingRectWidth > report.boundingRectHeight)
        {
            //particle is wider than it is tall, divide long by short
            aspectRatio = ratioToScore((rectLong / rectShort) / idealAspectRatio);
        }
        else
        {
            //particle is taller than it is wide, divide short by long
            aspectRatio = ratioToScore((rectShort / rectLong) / idealAspectRatio);
        }
        return aspectRatio;
    }

    /**
     * Compares scores to defined limits and returns true if the particle
     * appears to be a target
     *
     * @param scores The structure containing the scores to compare
     * @param outer True if the particle should be treated as an outer target,
     * false to treat it as a center target
     *
     * @return True if the particle meets all limits, false otherwise
     */
    boolean scoreCompare(Scores scores, boolean vertical)
    {
        boolean isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if(vertical)
        {
            isTarget &= scores.aspectRatioVertical > ASPECT_RATIO_LIMIT;
            //System.out.println("Vertical aspect ratio: " + scores.aspectRatioVertical);
            SmartDashboard.putDouble("Vertical aspect ratio", scores.aspectRatioVertical);
        }
        else
        {
            isTarget &= scores.aspectRatioHorizontal > ASPECT_RATIO_LIMIT;
            //System.out.println("Horizontal aspect ratio: " + scores.aspectRatioHorizontal);
            SmartDashboard.putDouble("Horizontal aspect ratio", scores.aspectRatioHorizontal);
        }

        return isTarget;
    }

    /**
     * Computes a score (0-100) estimating how rectangular the particle is by
     * comparing the area of the particle to the area of the bounding box
     * surrounding it. A perfect rectangle would cover the entire bounding box.
     *
     * @param report The Particle Analysis Report for the particle to score
     * @return The rectangularity score (0-100)
     */
    double scoreRectangularity(ParticleAnalysisReport report)
    {
        if(report.boundingRectWidth * report.boundingRectHeight != 0)
        {
            return 100 * report.particleArea / (report.boundingRectWidth * report.boundingRectHeight);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Converts a ratio with ideal value of 1 to a score. The resulting function
     * is piecewise linear going from (0,0) to (1,100) to (2,0) and is 0 for all
     * inputs outside the range 0-2
     */
    double ratioToScore(double ratio)
    {
        return (Math.max(0, Math.min(100 * (1 - Math.abs(1 - ratio)), 100)));
    }

    /**
     *
     * Takes in a report on a target and compares the scores to the defined
     * score limits to evaluate if the target is a hot target or not.
     *
     * Returns True if the target is hot. False if it is not.
     */
    boolean hotOrNot(TargetReport target)
    {
        boolean isHot = true;

        isHot &= target.tapeWidthScore >= TAPE_WIDTH_LIMIT;
        isHot &= target.verticalScore >= VERTICAL_SCORE_LIMIT;
        isHot &= (target.leftScore > LR_SCORE_LIMIT) | (target.rightScore > LR_SCORE_LIMIT);

        return isHot;
    }
    
}