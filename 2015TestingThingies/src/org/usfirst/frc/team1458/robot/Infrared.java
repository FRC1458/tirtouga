package org.usfirst.frc.team1458.robot;

import edu.wpi.first.wpilibj.AnalogInput;
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

public class Infrared extends edu.wpi.first.wpilibj.SensorBase {
	private double rate;
	private double distance;
	private int curveType;
	AnalogInput analogInput;
	private int inputNum;
	private Timer t = new Timer();
	private double average = 0;
	private int count = 0;
	private final double[] curveArrayA = { 2.670, 2.369, 2.049, 1.709, 1.427, 1.227, 1.080, 0.961, 0.895, 0.847, 0.811, 0.771, 0.751, 0.736, 0.713, 0.690 };// 5Y

	private final double[] curveArrayB = { 2.718, 2.392, };// For B which is a 45
															

	public Infrared(int inputPort, int curveType) {
		analogInput = new AnalogInput(inputPort);
		this.curveType = curveType;
		inputNum = inputPort;
		t.start();
	}

	public void reset() {
		average = 0;
		count = 0;
	}

	public void update() {
		distance = curve(analogInput.getVoltage(), curveType);
		average = (count * average + distance) / (count + 1);
		count++;
		SmartDashboard.putNumber("average" + inputNum, average);
		SmartDashboard.putNumber("count" + inputNum, count);
	}

	public double getDistance() {
		update();
		return distance;
	}

	// 0: Raw voltage, 1: 20-150cm curve, 2: 10-80cm curve 3: kyle calibrated
	// for Tri, 4: weighted average broken slope for B
	// line
	private double curve(double voltage, int curveType) {
		switch (curveType) {
		case 0: {
			return voltage;
		}
		case 1: {
			// return 52.654*Math.pow(voltage, -1.158);// change 1 to whatever
			return 37.4 * Math.pow(voltage, 4) - 279.37 * Math.pow(voltage, 3) + 757.39 * Math.pow(voltage, 2) - 897.4 * voltage + 429.33;
		}
		case 2: {
			return 1 / (voltage - 0);// change 1 & 0 to whatever
		}
		case 3: {
			for (int i = 1; i < curveArrayA.length; i++) {
				// 15+5i is the distance
				if (voltage > curveArrayA[i]) {
					double turtle = ((voltage - curveArrayA[i]) / (voltage - curveArrayA[i] + Math.abs(voltage - curveArrayA[i - 1])));
					return (1 - turtle) * (voltage * (15 + 5 * i) / curveArrayA[i]) + turtle * (voltage * (10 + 5 * i) / curveArrayA[i - 1]);

				}
			}
			return voltage * (15 + 5 * curveArrayA.length) / curveArrayA[curveArrayA.length - 1];

		}
		case 4: {
			for (int i = 1; i < curveArrayB.length; i++) {
				// 15+5i is the distance
				if (voltage > curveArrayB[i]) {
					double turtle = ((voltage - curveArrayB[i]) / (voltage - curveArrayB[i] + Math.abs(voltage - curveArrayB[i - 1])));
					return (1 - turtle) * (voltage * (15 + 5 * i) / curveArrayB[i]) + turtle * (voltage * (10 + 5 * i) / curveArrayB[i - 1]);

				}
			}
			return voltage * (15 + 5 * curveArrayB.length) / curveArrayB[curveArrayB.length - 1];

		}
		}

		return 1 / 0;
	}

	public double getRawDistance() {
		return curve(analogInput.getVoltage(), curveType);
	}

}