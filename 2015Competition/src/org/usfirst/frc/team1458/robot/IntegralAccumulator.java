package org.usfirst.frc.team1458.robot;
/**
 * Class to store integration values for the I2C Gyro class, imitates the function of an accumulator.
 * @author Brendan, Turtwig of Team 1458.
 *
 */
public class IntegralAccumulator {

	private double prevValue = 0;
	private double currentValue = 0;
	public double sum = 0;

	/**
	 * Resets the accumulator.
	 */
	public void reset() {
		prevValue = 0;
		currentValue = 0;
		sum = 0;
	}

	/**
	 * Updates the integral accumulator with new data, needs to be called continously each measurement.
	 * @param newValue The new measurement.
	 * @param timeSinceLastMeasurement Time elapsed since last measurement.
	 */
	public void update(double newValue, double timeSinceLastMeasurement) {
		prevValue = currentValue;
		currentValue = newValue;
		

		sum += (currentValue + prevValue) / 2 * timeSinceLastMeasurement;
	}

}
