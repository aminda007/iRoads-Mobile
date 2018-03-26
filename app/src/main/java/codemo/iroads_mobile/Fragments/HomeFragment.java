package codemo.iroads_mobile.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
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
import java.util.Timer;
import java.util.TimerTask;

import codemo.iroads_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private SignalProcessor zValueSignalProcessor;
    private SignalProcessor yValueSignalProcessor;
    private SignalProcessor xValueSignalProcessor;
    private boolean enableFilter;
    private ArrayList<Double> dataArray;
    private ArrayList<Double> timeArray;
    private ArrayList<Location> locationArray;
    private boolean previousDirection;
    private static Location cuurentLoc;
    private StringBuilder dataReport;

    Sensor accelerometer;
    private LineChart mChart;
    private Thread thread;
    boolean plotData = true;
    Sensor magnetometer;
    TextView xValue, xValueFiltered, yValue, yValueFiltered, zValue, zValueAverageFiltered,
            zValueHighPassFiltered ;
    TextView xMagValue, yMagValue, zMagValue;
    Button saveBtn;
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
        dataArray = new ArrayList<Double>(); // create array for storing acceleration data
        timeArray = new ArrayList<Double>(); // create array for storing time
        locationArray = new ArrayList<Location>(); // create array for storing locations
        previousDirection = true; // if positive
        dataReport = new StringBuilder();
        if(accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            xValue = (TextView) view.findViewById(R.id.xValue);
            xValueFiltered = (TextView) view.findViewById(R.id.xValueFiltered);
            // filtering process for sensor values
            yValue = (TextView) view.findViewById(R.id.yValue);
            yValueFiltered = (TextView) view.findViewById(R.id.yValueFiltered);
            zValue = (TextView) view.findViewById(R.id.zValue);
            zValueAverageFiltered = (TextView) view.findViewById(R.id.zValueAverageFiltered);
            zValueHighPassFiltered = (TextView) view.findViewById(R.id.zValueHighPassFiltered);
        }else{
            Log.d(TAG, "Accelorometer not available");
        }


        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            xMagValue = (TextView) view.findViewById(R.id.xMagValue);
            yMagValue = (TextView) view.findViewById(R.id.yMagValue);
            zMagValue = (TextView) view.findViewById(R.id.zMagValue);
        }else{
            Log.d(TAG, "Magnetometer not available");
        }

        lat = (TextView) view.findViewById(R.id.lat);
        lng = (TextView) view.findViewById(R.id.lng);

        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                writeLog("\n \n" + sdf.format(new Date()) + dataReport.toString() + "\n \n");
                dataReport = new StringBuilder();
            }
        });

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                writeLog("\n \n" + sdf.format(new Date()) + dataReport.toString() + "\n \n");
//                dataReport = new StringBuilder();
//                Log.d(TAG,"\n********************** File Writting *************\n");
//            }
//        }, 10000);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // this code will be executed after 2 seconds
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                writeLog("\n \n" + sdf.format(new Date()) + dataReport.toString() + "\n \n");
//                dataReport = new StringBuilder();
//                Log.d(TAG,"\n********************** File Writting *************\n");
//            }
//        }, 10000);

        mChart = (LineChart) view.findViewById(R.id.chartAccelerationZ);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Accelerometer Z axis");

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);

        YAxis lAxis = mChart.getAxisLeft();

        YAxis rAxis = mChart.getAxisRight();
        rAxis.setEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);


//        startPlot();
        
        return view;

    }

    private void startPlot() {
        if(thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    plotData=true;
                    try {
                        Thread.sleep(10);

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
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
            xValueFiltered.setText("X ValueFiltered: \n"+ this.xValueSignalProcessor.
                    averageFilter(sensorEvent.values[0]));
            yValue.setText("Y Value: "+sensorEvent.values[1]);
            yValueFiltered.setText("Y ValueFiltered: \n"+ this.yValueSignalProcessor.
                    averageFilter(sensorEvent.values[1]));
            zValue.setText("Z Value: "+sensorEvent.values[2]);
            zValueAverageFiltered.setText("Z ValueAverageFiltered: \n"+ this.zValueSignalProcessor.
                    averageFilter(sensorEvent.values[2]));
            zValueHighPassFiltered.setText("Z ValueHighPassFiltered: \n"+ this.zValueSignalProcessor.
                    highPassFilter(sensorEvent.values[2]));
            setEnableFilter(false);
            if(cuurentLoc != null){
                if(isEnableFilter()){
                    processIRI(this.zValueSignalProcessor.averageFilter(sensorEvent.values[2]));
                }else{
//                    processIRI(sensorEvent.values[2]);  // start calculating IRI
//                Log.d(TAG,"--------------- data --------- /// "+sensorEvent.values[2]);
                }
            }
            if(true){
                addEntry(sensorEvent.values[2]);
//                plotData = false;
            }
        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            xMagValue.setText("X Value: "+sensorEvent.values[0]);
            yMagValue.setText("Y Value: "+sensorEvent.values[1]);
            zMagValue.setText("Z Value: "+sensorEvent.values[2]);
        }
    }

    private void addEntry(float value) {
        LineData data = mChart.getData();
        if(data != null){
            ILineDataSet set = data.getDataSetByIndex(0);
            if(set == null){
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), value),0);
            Log.d(TAG,"--------------- z data --------- /// "+value+"...."+set.getEntryCount());
            data.notifyDataChanged();
            mChart.setMaxVisibleValueCount(150);
//            mChart.setVisibleXRange(10f,100f);
            mChart.moveViewToX(data.getEntryCount()-100);
        }
    }

    private ILineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return  set;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static  void updateLocation(Location loc){
        cuurentLoc = loc;
        lat.setText("Latitude: "+ loc.getLatitude());
        lng.setText("Longitude: "+ loc.getLongitude());
    }


    public boolean isEnableFilter() {
        return enableFilter;
    }

    public void setEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
    }

    public void processIRI(double z){
//        boolean directionChanged = false;

        boolean currentDirection = true;  // if positive
        double earthGravity = 9.95;
        double thresholdPos = 0.4;
        double thresholdNeg = -0.4;
        double accelerationDiff = z-earthGravity;
        if(accelerationDiff > thresholdPos || accelerationDiff < thresholdNeg ){
            Double time = Double.valueOf(System.currentTimeMillis());

            Log.d(TAG,"--------------- time ---------"+time);

            if(accelerationDiff > 0.0){
                Log.d(TAG,"--------------- Accelleration POSITIVE Diff ---------  "+accelerationDiff);
            }else{
                Log.d(TAG,"++++++++++++++++ Accelleration NEGATIVE Diff ++++++++++  "+accelerationDiff);
                currentDirection = false;
            }
            if(previousDirection != currentDirection){
                if(dataArray.size() > 1){
                    calculateArea();
                }
                dataArray.clear();
                dataArray.add(accelerationDiff);
                timeArray.add(time);
                locationArray.add(cuurentLoc);
            }else{
                dataArray.add(accelerationDiff);
                timeArray.add(time);
            }
            previousDirection = currentDirection;
            dataReport.append("\nAcceleration Difference: "+accelerationDiff);
            dataReport.append(" TimeStamp: "+time);
            dataReport.append(" Location: "+cuurentLoc.getLatitude() + " " + cuurentLoc.getLongitude());

        }else{
            // do nothing
        }
    }

    public void calculateArea(){
        double sum= 0.0;
        double totTime=0.0;
        for (int i= 0; i<dataArray.size()-1; i++){
            double deltaT = timeArray.get(i+1)-timeArray.get(i);
            Log.d(TAG,"********************** Delta time *************" + deltaT);
            if(deltaT < 145.0){
                sum += (dataArray.get(i) + dataArray.get(i+1)) * deltaT;
                totTime += deltaT;
            }else{
                if(sum != 0.0){
                    double velocityDiff = sum / 2000;
                    Log.d(TAG,"********************** Velocity Diff *************" + velocityDiff);
                    double displacement = velocityDiff * totTime / 2;
                    dataReport.append("\nDisplacement: " + displacement);
                    Log.d(TAG,"********************** Displacement ********************************************************" + displacement);
                    sum = 0.0;
                    totTime = 0.0;
                }
            }
        }
        if(sum != 0.0){
            double velocityDiff = sum / 2000;
            Log.d(TAG,"********************** Velocity Diff *************" + velocityDiff);
            double displacement = velocityDiff * totTime / 2;
            dataReport.append("\nDisplacement: " + displacement);
            Log.d(TAG,"********************** Displacement ********************************************************" + displacement);
        }
//        totTime = dataArray.get(dataArray.size()-1) - dataArray.get(0)


    }

    public void writeLog(String text)
    {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
