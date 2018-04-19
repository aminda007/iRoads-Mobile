package codemo.iroads_mobile;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import codemo.iroads_mobile.Database.SensorData;

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
//            Log.d("DATA=======",SensorData.getMacceX());
            SensorData.setMacceX(Float.toString(sensorEvent.values[0]));
            SensorData.setMacceY(Float.toString(sensorEvent.values[1]));
            SensorData.setMacceZ(Float.toString(sensorEvent.values[2]));

        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
