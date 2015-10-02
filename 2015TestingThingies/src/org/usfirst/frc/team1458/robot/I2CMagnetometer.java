package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.util.BoundaryException;
import edu.wpi.first.wpilibj.AccumulatorResult;

public class I2CMagnetometer extends edu.wpi.first.wpilibj.SensorBase {

	private double angle;
	private double offsetAngle; //angle in degrees that the robot starts out pointing
	I2C m_i2c;
	private double rate;
	private byte[] rawInput = new byte[6];
	private final int address = 0x0e;
	private Timer timer = new Timer();
	private double[] axes = new double[3]; // x y z
	byte[] buffer = new byte[1];
	
	private double m;

	/**
	 * Initialize the gyro. Calibrate the gyro by running for a number of
	 * samples and computing the center value for this part. Then use the center
	 * value as the Accumulator center value for subsequent measurements. It's
	 * important to make sure that the robot is not moving while the centering
	 * calculations are in progress, this is typically done when the robot is
	 * first turned on while it's sitting at rest before the competition starts.
	 */
	private void initMagnetometer() {
		offsetAngle=0;
		
		if (m_i2c == null) {
			System.out.println("Null m_i2c");
		}
		
		// interrogate the buffer to make sure that we are not trying to read the values before they are
		// ready to be read, so we're getting a gibberish value
		m_i2c.read(0x07, 1, buffer);
		if ((int) buffer[0] != 0xc4) {
			System.out.println("Something has gone terribly wrong.");
			System.out.println(buffer[0]);
		} else {
			System.out.println("Found who am i");
		}

		m_i2c.write(0x10, 0b00011001);
		m_i2c.write(0x11, 0b10000000);

		m_i2c.write(0x09, 0b00000000);
		m_i2c.write(0x0a, 0b00000000);
		m_i2c.write(0x0b, 0b00000000);
		m_i2c.write(0x0c, 0b00000000);
		m_i2c.write(0x0d, 0b00000000);
		m_i2c.write(0x0e, 0b00000000);

		SmartDashboard.putNumber("X Max", 0);
		SmartDashboard.putNumber("Y Max", 0);
		SmartDashboard.putNumber("Z Max", 0);
		SmartDashboard.putNumber("X Min", 10000);
		SmartDashboard.putNumber("Y Min", 10000);
		SmartDashboard.putNumber("Z Min", 10000);

		update();
		zero();

	}

	/**
	 * Gyro constructor with only a channel.
	 *
	 * Use the default analog module slot.
	 *
	 * @param channel
	 *            The analog channel the gyro is connected to.
	 */
	public I2CMagnetometer() {
		// m_analog = new AnalogChannel(channel);
		m_i2c = new I2C(I2C.Port.kOnboard, address);
		// m_channelAllocated = true;
		initMagnetometer();
	}

	/**
	 * Return the actual angle in degrees that the robot is currently facing.
	 *
	 * The angle is based on the current accumulator value corrected by the
	 * oversampling rate, the gyro type, and the A/D calibration values. The
	 * angle is continuous, that is can go beyond 360 degrees. This make
	 * algorithms that wouldn't want to see a discontinuity in the gyro output
	 * as it sweeps past 0 on the second time around.
	 *
	 * @return the current heading of the robot in degrees. This heading is
	 *         based on integration of the returned rate from the gyro.
	 */
	public double getAngle() {
		// update();
		// do math stuff to make more accurate;
		return angle;
	}

	/**
	 * Return the rate of rotation of the gyro
	 * 
	 * The rate is based on the most recent reading of the gyro analog value
	 * 
	 * @return the current rate in degrees per second
	 */
	public double getRate() {

		update();
		return rate;
	}

	public void zero() {
		//offsetAngle=angle;
		SmartDashboard.putNumber("X Max", 0);
		SmartDashboard.putNumber("Y Max", 0);
		SmartDashboard.putNumber("Z Max", 0);
		SmartDashboard.putNumber("X Min", 10000);
		SmartDashboard.putNumber("Y Min", 10000);
		SmartDashboard.putNumber("Z Min", 10000);
	}
	
	public boolean isReady() {
		return true;
		//m_i2c.read(0x0, 1, buffer);
		//return (((buffer[0]>>3)&1)==1);
	}

	public void update() {
		m_i2c.read(0x01, 6, rawInput);
		// convert it into number format
		
		
		for(int f = 0; f<3; f++) {
			int i = (int) rawInput[0+2*f];
			i *= 256;
			if((int)rawInput[1+2*f]<0) {
				i+=(256+(int)rawInput[1+2*f]);
			} else {
				i+=(int)rawInput[1+2*f];
			}
			//i += (int) rawInput[1+2*f];
			axes[f] = rateAdjust(i);
			//axes[f] = 0.8*rateAdjust(i)+0.2*axes[f];
		}

		axes[0]/=-1;//flip x to be right

		axes[0]-=270; //maggie 1247, margaret 270
		axes[1]-=1025; //maggie 1186, margaret 1025
		//axes[2]-=0;
		axes[0]/=298; //maggie 239, margaret 298
		axes[1]/=340; //maggie 241, margaret 340
		//axes[2]/=1;
		

		SmartDashboard.putNumber("X field", axes[0]);
		SmartDashboard.putNumber("Y field", axes[1]);
		SmartDashboard.putNumber("Z field", axes[2]);

		if(SmartDashboard.getNumber("X Max",0)<axes[0]) {
			SmartDashboard.putNumber("X Max", axes[0]);
		}
		if(SmartDashboard.getNumber("X Min",10000)>axes[0]) {
			SmartDashboard.putNumber("X Min", axes[0]);
		}
		if(SmartDashboard.getNumber("Y Max",0)<axes[1]) {
			SmartDashboard.putNumber("Y Max", axes[1]);
		}
		if(SmartDashboard.getNumber("Y Min",10000)>axes[1]) {
			SmartDashboard.putNumber("Y Min", axes[1]);
		}
		if(SmartDashboard.getNumber("Z Max",0)<axes[2]) {
			SmartDashboard.putNumber("Z Max", axes[2]);
		}
		if(SmartDashboard.getNumber("Z Min",10000)>axes[2]) {
			SmartDashboard.putNumber("Z Min", axes[2]);
		}
		
		SmartDashboard.putNumber("X Raw field", axes[0]);
		SmartDashboard.putNumber("Y Raw field", axes[1]);
		SmartDashboard.putNumber("Z Raw field", axes[2]);

		//do correction stuffs


		//calculate stuffs
		//assuming that positive x is to the right of the robot, positive y is straight forward
		angle = Math.atan(axes[1]/axes[0]);
		if(axes[0]<0) {
			angle+=Math.PI;
		}
		/*
			m = Math.sqrt(axes[0] * axes[0] + axes[1] * axes[1]);
			if(m==0) {
				m=0.000000000000000000000000000001;
			}
			angle = Math.asin(axes[1] / m);
			System.out.println("asin angle: "+angle*180/Math.PI);


			if (sgn(axes[1])>=0&&sgn(axes[0])>=0) {
				//Quadrant I
				angle = angle;
			} else if (sgn(axes[0])<=0&&sgn(axes[1])>=0) {
				//Quadrant II
				angle = Math.PI-angle;
			} else if(sgn(axes[0])<=0&&sgn(axes[1])<=0) {
				//Quadrant III
				angle+=Math.PI;
			} else if(sgn(axes[0])>=0&&sgn(axes[1])<=0) {
				//Quadrant IV
				angle=2*Math.PI-angle;
			}
		 */

		angle*=(180/Math.PI);
		

		angle+=90;//placeholder
		angle = Math.round(angle * 10) / 10.0;
	}

	public double sgn(double n) {
		if(n>0) {
			return 1;
		} else if (n<0) {
			return -1;
		} else {
			return 0;
		}
	}

	public double rateAdjust(int i) {
		return (((double) i) - 0) / 1;// old was 3.584,16.66666;
		// next was
		// 1.5015,8.93879626, then
		// 1.11951544, then
		// 0.058795502
	}
}