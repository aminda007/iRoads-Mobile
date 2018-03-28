package codemo.iroads_mobile.Fragments;

import java.util.ArrayList;

/**
 * Created by Pivithuru Thejan on 3/19/2018.
 */

public class SignalProcessor {
    private double constantFactor = 0.0;
    private ArrayList<Double> dataQueue = new ArrayList<Double>();
    private ArrayList<Double> constantFactorQueue = new ArrayList<Double>();
    private double noiseRemovedValue;
    private double dataSum;
    private double currentSensorValue = 0;
    private double previousSensorValue = 0;
    private double previousHighPassFilterValue = 0;
    private double highPassFilterDecayFactor = 1.0;
    private int sensivityLevel = 25;
    private int constantFactorSensivityLevel = 5;

    public void setHighPassFilterDecayFactor(double highPassFilterDecayFactor) {
        this.highPassFilterDecayFactor = highPassFilterDecayFactor;
    }

    public void setConstantFactorSensivityLevel(int constantFactorSensivityLevel) {
        this.constantFactorSensivityLevel = constantFactorSensivityLevel;
    }

    public void setSensivityLevel(int sensivityLevel) {
        this.sensivityLevel = sensivityLevel;
    }

    public void setConstantFactor(double constantFactor) { // Calculates constant noise
        double constantFactorSum = 0;
        this.constantFactorQueue.add(constantFactor);
        if (this.constantFactorQueue.size() > this.constantFactorSensivityLevel) {
            this.constantFactorQueue.remove(0);
        }

        for (Double d : this.constantFactorQueue) {
            constantFactorSum += d;
        }
        this.constantFactor = constantFactorSum / this.constantFactorQueue.size();
    }

    public double averageFilter(double sensorValue){ // Filters using the average of a data set
        this.dataSum = 0;
        this.dataQueue.add(sensorValue);

        if (this.dataQueue.size() > this.sensivityLevel) {
            this.dataQueue.remove(0);
        }

        for (Double d : this.dataQueue){
            this.dataSum += d;
        }

        this.noiseRemovedValue = (this.dataSum)/(this.dataQueue.size());
        this.noiseRemovedValue -= this.constantFactor; // Removes constant noise
        return this.noiseRemovedValue;
    }

    public double highPassFilter (double sensorValue) {
        this.currentSensorValue = sensorValue;
        double highPassFilterValue = this.highPassFilterDecayFactor*(this.
                previousHighPassFilterValue + this.currentSensorValue - this.previousSensorValue);
        this.previousHighPassFilterValue = highPassFilterValue;
        this.previousSensorValue = this.currentSensorValue;
        return highPassFilterValue;
    }
}
