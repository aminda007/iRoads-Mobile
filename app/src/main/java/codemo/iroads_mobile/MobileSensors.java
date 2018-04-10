package codemo.iroads_mobile;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import codemo.iroads_mobile.Fragments.SignalProcessor;
import codemo.iroads_mobile.Reorientation.NericellMechanism;

/**
 * A simple {@link Fragment} subclass.
 */
public class MobileSensors implements SensorEventListener {

    private static final String TAG = "MobileSensors";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;



    private static float currentMagneticX;
    private static float currentMagneticY;
    private static float currentMagneticZ;

    public static float getCurrentMagneticX() {
        return currentMagneticX;
    }
    public static float getCurrentMagneticY() {
        return currentMagneticY;
    }
    public static float getCurrentMagneticZ() {
        return currentMagneticZ;
    }

    private void setCurrentMagneticValues(float currentMagneticX,float currentMagneticY,float currentMagneticZ) {
        this.currentMagneticX = currentMagneticX;
        this.currentMagneticY = currentMagneticY;
        this.currentMagneticZ = currentMagneticZ;
    }


    private static float currentAccelerationX;
    private static float currentAccelerationY;
    private static float currentAccelerationZ;

    public static float getCurrentAccelerationX() {
        return currentAccelerationX;
    }

    public static float getCurrentAccelerationY() {
        return currentAccelerationY;
    }

    public static float getCurrentAccelerationZ() {
        return currentAccelerationZ;
    }

    private void setCurrentAccelerationValues(float currentAccelerationX,float currentAccelerationY,float currentAccelerationZ) {
        this.currentAccelerationX = currentAccelerationX;
        this.currentAccelerationY = currentAccelerationY;
        this.currentAccelerationZ = currentAccelerationZ;
    }



    public MobileSensors(MainActivity mainActivity) {
        // Required empty public constructor
        sensorManager = (SensorManager)  mainActivity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        }else{
            Log.d(TAG, "Accelorometer not available");
        }
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);

        }else{
            Log.d(TAG, "Magnetometer not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        int sensorType = sensor.getType();

        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            setCurrentAccelerationValues(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);

        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            setCurrentMagneticValues(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
        }

        /**
         *  all the data processing on sensor values are done in here
         */
        SensorDataProcessor.updateSensorDataProcessingValues();

        /**
         * drawing
         */
        GraphController.drawGraph(sensorEvent);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
