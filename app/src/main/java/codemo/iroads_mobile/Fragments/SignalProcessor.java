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
    private int sensivityLevel = 5;
    private int constantFactorSensivityLevel = 5;

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
        this.noiseRemovedValue -= this.constantFactor;
        return this.noiseRemovedValue;
    }
}
