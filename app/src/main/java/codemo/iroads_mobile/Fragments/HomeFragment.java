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
import java.util.Timer;
import java.util.TimerTask;

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
    private boolean enableFilter;
    private ArrayList<Double> dataArray;
    private ArrayList<Double> timeArray;
    private ArrayList<Location> locationArray;
    private boolean previousDirection;
    private static Location cuurentLoc;
    private StringBuilder dataReport;
    private NericellMechanism nericellMechanism;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = false;
    private int maxEntries = 200;

    private Sensor accelerometer;
    private Sensor magnetometer;
    TextView zValueReoriented, yValueReoriented, xValueReoriented;
    private CheckBox xValue, xValueFiltered, yValue, yValueFiltered, zValue, zValueAverageFiltered,
            zValueHighPassFiltered ;
    private boolean xValueChecked, xValueFilteredChecked, yValueChecked, yValueFilteredChecked, zValueChecked, zValueAverageFilteredChecked,
            zValueHighPassFilteredChecked ;
    private TextView xMagValue, yMagValue, zMagValue;
    private Button saveBtn;
    private static TextView  lat, lng;

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
        dataArray = new ArrayList<Double>(); // create array for storing acceleration data
        timeArray = new ArrayList<Double>(); // create array for storing time
        locationArray = new ArrayList<Location>(); // create array for storing locations
        previousDirection = true; // if positive
        dataReport = new StringBuilder();
        if(accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            xValue = (CheckBox) view.findViewById(R.id.xValue);
            xValueFiltered = (CheckBox) view.findViewById(R.id.xValueFiltered);
            xValueReoriented = (TextView) view.findViewById(R.id.xValueReoriented);
            // filtering process for sensor values
            yValue = (CheckBox) view.findViewById(R.id.yValue);
            yValueFiltered = (CheckBox) view.findViewById(R.id.yValueFiltered);
            yValueReoriented = (TextView) view.findViewById(R.id.yValueReoriented);
            zValue = (CheckBox) view.findViewById(R.id.zValue);
            zValueAverageFiltered = (CheckBox) view.findViewById(R.id.zValueAverageFiltered);
            zValueHighPassFiltered = (CheckBox) view.findViewById(R.id.zValueHighPassFiltered);
            zValueReoriented = (TextView) view.findViewById(R.id.zValueReoriented);
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
//      initializing listners for each acceleration output
        xValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xValue.isChecked()){
                    xValueChecked = true;
                }else{
                    xValueChecked = false;
                    deleteSet("x");
                }
            }
        });
        yValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yValue.isChecked()){
                    yValueChecked = true;
                }else{
                    yValueChecked = false;
                    deleteSet("y");
                }
            }
        });
        zValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zValue.isChecked()){
                    zValueChecked = true;
                }else{
                    zValueChecked = false;
                    deleteSet("z");
                }
            }
        });
        xValueFiltered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xValueFiltered.isChecked()){
                    xValueFilteredChecked = true;
                }else{
                    xValueFilteredChecked = false;
                    deleteSet("x avg");
                }
            }
        });
        yValueFiltered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yValueFiltered.isChecked()){
                    yValueFilteredChecked = true;
                }else{
                    yValueFilteredChecked = false;
                    deleteSet("y avg");
                }
            }
        });
        zValueAverageFiltered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zValueAverageFiltered.isChecked()){
                    zValueAverageFilteredChecked = true;
                }else{
                    zValueAverageFilteredChecked = false;
                    deleteSet("z avg");
                }
            }
        });
        zValueHighPassFiltered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zValueHighPassFiltered.isChecked()){
                    zValueHighPassFilteredChecked = true;
                }else{
                    zValueHighPassFilteredChecked = false;
                    deleteSet("z high pass");
                }
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
        mChart.getDescription().setEnabled(false);
//        mChart.getDescription().setText("Accelerometer Z axis");

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.TRANSPARENT);

        YAxis lAxis = mChart.getAxisLeft();

        YAxis rAxis = mChart.getAxisRight();
        rAxis.setEnabled(false);

        XAxis xAxis  = mChart.getXAxis();
        xAxis.setEnabled(true);

        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.LTGRAY);


        startPlot();
        
        return view;

    }

    private void startPlot() {
        if(thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG,"--------------- thrad started --------- /// ");
                while(true){
                    plotData=true;
//                    Log.d(TAG,"--------------- inside while loop--------- /// ");
                    try {
                        Thread.sleep(100);

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
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
            xValueReoriented.setText("X ValueReoriented: "+ this.nericellMechanism.reOrientX(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
            yValue.setText("Y Value: "+sensorEvent.values[1]);
            yValueFiltered.setText("Y ValueFiltered: \n"+ this.yValueSignalProcessor.
                    averageFilter(sensorEvent.values[1]));
            yValueReoriented.setText("Y ValueReoriented: "+ this.nericellMechanism.reOrientY(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
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
//            Log.d(TAG,"--------------- plot data is --------- /// "+plotData);
            if(plotData){
                if(xValueChecked){
                    addEntry(sensorEvent.values[0], "x", Color.LTGRAY);
                }
                if(xValueFilteredChecked){
                    addEntry((float) this.xValueSignalProcessor.averageFilter(sensorEvent.values[0]),
                            "x avg", Color.BLACK);
                }
                if(yValueChecked){
                    addEntry(sensorEvent.values[1], "y", Color.CYAN);
                }
                if(yValueFilteredChecked){
                    addEntry((float)this.yValueSignalProcessor.averageFilter(sensorEvent.values[1]),
                            "y avg", Color.BLUE);
                }
                if(zValueChecked){
                    addEntry(sensorEvent.values[2], "z", Color.GREEN);
                }
                if(zValueAverageFilteredChecked){
                    addEntry((float)this.zValueSignalProcessor.averageFilter(sensorEvent.values[2]),
                            "z avg", Color.MAGENTA);
                }
                if(zValueHighPassFilteredChecked){
                    addEntry((float)this.zValueSignalProcessor.highPassFilter(sensorEvent.values[2]),
                            "z high pass", Color.RED);
                }
                plotData = false;
            }

            zValueReoriented.setText("Z ValueReoriented: " + this.nericellMechanism.reorientZ(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            xMagValue.setText("X Value: "+sensorEvent.values[0]);
            yMagValue.setText("Y Value: "+sensorEvent.values[1]);
            zMagValue.setText("Z Value: "+sensorEvent.values[2]);
        }
    }

    private void addEntry(float value, String type, int color) {
        LineData data = mChart.getLineData();
        if(data != null){
            ILineDataSet set = data.getDataSetByLabel(type, true);
            if(set == null){
                set = createSet(type, color);
                data.addDataSet(set);
            }
//            Log.d(TAG,"--------------- data set is  --------- /// "+set.getLabel());
//            (float) Math.random()*75+75f
            data.addEntry(new Entry(set.getEntryCount(), value),data.getIndexOfDataSet(set));
//            Log.d(TAG,"--------------- z data --------- /// "+value+"...."+set.getEntryCount());
            if(set.getEntryCount() > maxEntries){
                set.removeFirst();
                for (int i=0; i<set.getEntryCount(); i++) {
                    Entry entryToChange = set.getEntryForIndex(i);
                    entryToChange.setX(entryToChange.getX() - 1);
                }
            }
//            Log.d(TAG,"--------------- entry count is --------- /// "+set.getEntryCount());
            mChart.notifyDataSetChanged();
//            mChart.setMaxVisibleValueCount(150);
//            mChart.setFocusable(true);
//            mChart.setVisibleXRangeMaximum(100);
            mChart.setVisibleXRange(200f,200f);
            mChart.moveViewToX(data.getEntryCount());
//            mChart.invalidate();
        }
    }

    private LineDataSet createSet(String type, int color) {
        LineDataSet set = new LineDataSet(null, type);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setLineWidth(1f);
        set.setColor(color);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        for(int i=0; i<maxEntries; i++){
            set.addEntry(new Entry(i,0));
        }
        Log.d(TAG,"---------------  data set created --------- /// ");
        return  set;
    }

    public void deleteSet(String type){
        Log.d(TAG,"---------------  data set deleted --------- /// ");
        LineData data = mChart.getLineData();
//        ILineDataSet set = data.getDataSetByLabel(type, true);
        data.removeDataSet(data.getDataSetByLabel(type, true));
//        set.clear();
        mChart.invalidate();
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
