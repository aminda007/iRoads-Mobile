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
            GraphController.drawGraph(sensorEvent);
        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
