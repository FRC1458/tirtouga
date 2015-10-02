package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.util.BoundaryException;
import edu.wpi.first.wpilibj.AccumulatorResult;

public class I2CGyro extends edu.wpi.first.wpilibj.SensorBase {
	IntegralAccumulator accum;
	I2C m_i2c;
	private double rate;
	private byte[] rawInput = new byte[2];
	private final int address = 0b01101000;
	private Timer timer = new Timer();

	/**
	 * Initialize the gyro. Calibrate the gyro by running for a number of
	 * samples and computing the center value for this part. Then use the center
	 * value as the Accumulator center value for subsequent measurements. It's
	 * important to make sure that the robot is not moving while the centering
	 * calculations are in progress, this is typically done when the robot is
	 * first turned on while it's sitting at rest before the competition starts.
	 */
	private void initGyro() {
		accum = new IntegralAccumulator();
		byte[] buffer = new byte[1];
		if (m_i2c == null) {
			System.out.println("Null m_i2c");
		}
		accum.reset();

		m_i2c.read(0x0, 1, buffer);
		if ((int) buffer[0] != address) {
			System.out.println("Something has gone terribly wrong.");
			System.out.println(buffer[0]);
		}

		m_i2c.write(0x16, 0b00011010);
		m_i2c.write(0x15, 0b00110010);
		m_i2c.write(0x3e, 0b00110000);

		timer.start();
		update();

	}

	/**
	 * Gyro constructor with only a channel.
	 *
	 * Use the default analog module slot.
	 *
	 * @param channel
	 *            The analog channel the gyro is connected to.
	 */
	public I2CGyro() {
		// m_analog = new AnalogChannel(channel);
		m_i2c = new I2C(I2C.Port.kOnboard, address);
		// m_channelAllocated = true;
		initGyro();
	}

	/**
	 * Reset the gyro. Resets the gyro to a heading of zero. This can be used if
	 * there is significant drift in the gyro and it needs to be recalibrated
	 * after it has been running.
	 */
	public void reset() {
		accum.reset();
		update();
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
		update();
		// do math stuff to make more accurate;
		return accum.sum;
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

	public void update() {
		
		m_i2c.read(0x21, 2, rawInput);
		// convert it into number format
		int f = (int) rawInput[0];
		f *= 256;
		f += (int) rawInput[1];
		if ((int) rawInput[1] < 0) {
			f += (256 + (int) rawInput[1]);
		} else {
			f += (int) rawInput[1];
		}
		rate=(0.8)*rateAdjust(f)+(0.2)*rate;
		
		accum.update(rate, timer.get());
		timer.reset();
	}
	
	public double rateAdjust(int i) {
		return (((double) i)-1.81347822)/16.384;//old was 3.584,16.66666; next was 1.5015,8.93879626, then 1.11951544, then 0.058795502
	}
}