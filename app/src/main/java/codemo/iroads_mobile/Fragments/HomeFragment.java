package codemo.iroads_mobile.Fragments;


import android.content.Context;
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
import android.widget.TextView;

import codemo.iroads_mobile.R;
import codemo.iroads_mobile.Reorientation.NericellMechanism;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private SignalProcessor zValueSignalProcessor;
    private SignalProcessor yValueSignalProcessor;
    private SignalProcessor xValueSignalProcessor;
    private NericellMechanism nericellMechanism;

    Sensor accelerometer;
    Sensor magnetometer;
    TextView xValue, xValueFiltered, yValue, yValueFiltered, zValue, zValueAverageFiltered,
            zValueHighPassFiltered, zValueReoriented, yValueReoriented, xValueReoriented;
    TextView xMagValue, yMagValue, zMagValue;
    static TextView  lat, lng;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "Init sensor services");
        sensorManager = (SensorManager)  getActivity().getSystemService(Context.SENSOR_SERVICE);


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.zValueSignalProcessor = new SignalProcessor(); // creates filters for sensor values
        this.yValueSignalProcessor = new SignalProcessor();
        this.xValueSignalProcessor = new SignalProcessor();
        this.nericellMechanism = new NericellMechanism();
        if(accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            xValue = (TextView) view.findViewById(R.id.xValue);
            xValueFiltered = (TextView) view.findViewById(R.id.xValueFiltered);
            xValueReoriented = (TextView) view.findViewById(R.id.xValueReoriented);
            // filtering process for sensor values
            yValue = (TextView) view.findViewById(R.id.yValue);
            yValueFiltered = (TextView) view.findViewById(R.id.yValueFiltered);
            yValueReoriented = (TextView) view.findViewById(R.id.yValueReoriented);
            zValue = (TextView) view.findViewById(R.id.zValue);
            zValueAverageFiltered = (TextView) view.findViewById(R.id.zValueAverageFiltered);
            zValueHighPassFiltered = (TextView) view.findViewById(R.id.zValueHighPassFiltered);
            zValueReoriented = (TextView) view.findViewById(R.id.zValueReoriented);
        }else{
            Log.d(TAG, "Accelorometer not available");
        }


        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            xMagValue = (TextView) view.findViewById(R.id.xMagValue);
            yMagValue = (TextView) view.findViewById(R.id.yMagValue);
            zMagValue = (TextView) view.findViewById(R.id.zMagValue);
        }else{
            Log.d(TAG, "Magnetometer not available");
        }

        lat = (TextView) view.findViewById(R.id.lat);
        lng = (TextView) view.findViewById(R.id.lng);

        return view;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        int sensorType = sensor.getType();

//        Log.d(TAG,
//                "Sensor changed X: "+sensorEvent.values[0]+
//                "Sensor changed Y: "+sensorEvent.values[1]+
//                "Sensor changed Z: "+sensorEvent.values[2]);

        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            xValue.setText("X Value: "+sensorEvent.values[0]);
            xValueFiltered.setText("X ValueFiltered: "+ this.xValueSignalProcessor.averageFilter(sensorEvent.values[0]));
            xValueReoriented.setText("X ValueReoriented: "+ this.nericellMechanism.reOrientX(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
            yValue.setText("Y Value: "+sensorEvent.values[1]);
            yValueFiltered.setText("Y ValueFiltered: "+ this.yValueSignalProcessor.averageFilter(sensorEvent.values[1]));
            yValueReoriented.setText("Y ValueReoriented: "+ this.nericellMechanism.reOrientY(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
            zValue.setText("Z Value: "+sensorEvent.values[2]);
            zValueAverageFiltered.setText("Z ValueAverageFiltered: "+ this.zValueSignalProcessor.
                    averageFilter(sensorEvent.values[2]));
            zValueHighPassFiltered.setText("Z ValueHighPassFiltered: "+ this.zValueSignalProcessor.
                    highPassFilter(sensorEvent.values[2]));
            zValueReoriented.setText("Z ValueReoriented: " + this.nericellMechanism.reorientZ(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            xMagValue.setText("X Value: "+sensorEvent.values[0]);
            yMagValue.setText("Y Value: "+sensorEvent.values[1]);
            zMagValue.setText("Z Value: "+sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static  void updateLocation(Location loc){
        lat.setText("Latitude: "+ loc.getLatitude());
        lng.setText("Longitude: "+ loc.getLongitude());
    }
}
