package org.usfirst.frc.team1458.robot;

public class IntegralAccumulator {

	private double prevValue = 0;
	private double currentValue = 0;
	private double totalTimeElapsed = 0;
	public double sum = 0;

	public void reset() {
		prevValue = 0;
		currentValue = 0;
		sum = 0;
		totalTimeElapsed = 0;
	}

	public void update(double newValue, double timeSinceLastMeasurement) {
		prevValue = currentValue;
		currentValue = newValue;
		

		sum += (currentValue + prevValue) / 2 * timeSinceLastMeasurement;

		totalTimeElapsed += timeSinceLastMeasurement;
	}

}
