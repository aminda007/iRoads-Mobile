package codemo.iroads_mobile.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import codemo.iroads_mobile.MainActivity;
import codemo.iroads_mobile.R;
import codemo.iroads_mobile.Reorientation.NericellMechanism;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    private static final String TAG = "GraphFragment";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static SignalProcessor zValueSignalProcessor;
    private static SignalProcessor yValueSignalProcessor;
    private static SignalProcessor xValueSignalProcessor;
    private static NericellMechanism nericellMechanism;
    private boolean enableFilter;
    private CheckBox xValue, xValueFiltered, yValue, yValueFiltered, zValue, zValueAverageFiltered,
            zValueHighPassFiltered, zValueReoriented, yValueReoriented, xValueReoriented;
    private static boolean xValueChecked, xValueFilteredChecked, yValueChecked, yValueFilteredChecked, zValueChecked, zValueAverageFilteredChecked,
            zValueHighPassFilteredChecked, xValueReorientedChecked, yValueReorientedChecked, zValueReorientedChecked;
    private static LineChart mChart;
    private static LineChart rmsChart;
    private Thread thread;
    private static boolean plotData = false;
    private static int maxEntries = 200;
    private static MainActivity activity;

    public GraphFragment() {
        // Required empty public constructor
    }

    public static void setRmsChart(LineChart rmsChart) {
        GraphFragment.rmsChart = rmsChart;
    }

    public static void setActivity(MainActivity Activity) {
        activity = Activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
//        View viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "Init sensor services");
        sensorManager = (SensorManager)  getActivity().getSystemService(Context.SENSOR_SERVICE);


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.zValueSignalProcessor = new SignalProcessor(); // creates filters for sensor values
        this.yValueSignalProcessor = new SignalProcessor();
        this.xValueSignalProcessor = new SignalProcessor();
        this.nericellMechanism = new NericellMechanism();

        if(accelerometer != null){
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            xValue = (CheckBox) view.findViewById(R.id.xValue);
            xValueFiltered = (CheckBox) view.findViewById(R.id.xValueFiltered);
            xValueReoriented = (CheckBox) view.findViewById(R.id.xValueReorientedBox);
            // filtering process for sensor values
            yValue = (CheckBox) view.findViewById(R.id.yValue);
            yValueFiltered = (CheckBox) view.findViewById(R.id.yValueFiltered);
            yValueReoriented = (CheckBox) view.findViewById(R.id.yValueReorientedBox);
            zValue = (CheckBox) view.findViewById(R.id.zValue);
            zValueAverageFiltered = (CheckBox) view.findViewById(R.id.zValueAverageFiltered);
            zValueHighPassFiltered = (CheckBox) view.findViewById(R.id.zValueHighPassFiltered);
            zValueReoriented = (CheckBox) view.findViewById(R.id.zValueReorientedBox);
        }else{
            Log.d(TAG, "Accelorometer not available");
        }

        //      initializing listners for each acceleration output
        xValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xValue.isChecked()){
                    xValueChecked = true;
                }else{
                    xValueChecked = false;
                    deleteSet("x", mChart);
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
                    deleteSet("y", mChart);
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
                    deleteSet("z", mChart);
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
                    deleteSet("x avg", mChart);
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
                    deleteSet("y avg", mChart);
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
                    deleteSet("z avg", mChart);
                }
            }
        });
        xValueReoriented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xValueReoriented.isChecked()){
                    xValueReorientedChecked = true;
                }else{
                    xValueReorientedChecked = false;
                    deleteSet("x reori", mChart);
                }
            }
        });
        yValueReoriented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yValueReoriented.isChecked()){
                    yValueReorientedChecked = true;
                }else{
                    yValueReorientedChecked = false;
                    deleteSet("y reori", mChart);
                }
            }
        });
        zValueReoriented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zValueReoriented.isChecked()){
                    zValueReorientedChecked = true;
                }else{
                    zValueReorientedChecked = false;
                    deleteSet("z reori", mChart);
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
                    deleteSet("z high pass", mChart);
                }
            }
        });

        mChart = (LineChart) view.findViewById(R.id.chartAcceleration);
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
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(false);
        xAxis.setEnabled(true);

        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setEnabled(false);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.LTGRAY);

//        setRmsChart((LineChart) viewHome.findViewById(R.id.chartAccelerationZ));
//        LineData rmsData = new LineData();
//        mChart.setData(rmsData);

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


    public static void drawGraph(SensorEvent sensorEvent) {
            if(plotData){
                if(xValueChecked){
                    addEntry(sensorEvent.values[0], "x", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorX), mChart);
                }
                if(xValueFilteredChecked){
                    addEntry((float) xValueSignalProcessor.averageFilter(sensorEvent.values[0]),
                            "x avg", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorXAvg), mChart);
                }
                if(yValueChecked){
                    addEntry(sensorEvent.values[1], "y", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorY), mChart);
                }
                if(yValueFilteredChecked){
                    addEntry((float)yValueSignalProcessor.averageFilter(sensorEvent.values[1]),
                            "y avg", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorYAvg), mChart);
                }
                if(zValueChecked){
                    addEntry(sensorEvent.values[2], "z", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorZ), mChart);
                }
                if(zValueAverageFilteredChecked){
                    addEntry((float)zValueSignalProcessor.averageFilter(sensorEvent.values[2]),
                            "z avg", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorZAvg), mChart);
                }
                if(zValueHighPassFilteredChecked){
                    addEntry((float)zValueSignalProcessor.highPassFilter(sensorEvent.values[2]),
                            "z high pass", ContextCompat.getColor(activity.getApplicationContext(), R.color.colorZHighPass), mChart);
                }
                if(xValueReorientedChecked){
                    addEntry((float)nericellMechanism.reOrientX(sensorEvent.values[0],
                            sensorEvent.values[1],sensorEvent.values[2]), "x reori",
                            ContextCompat.getColor(activity.getApplicationContext(), R.color.colorXReori), mChart);
                }
                if(yValueReorientedChecked){
                    addEntry((float)nericellMechanism.reOrientY(sensorEvent.values[0],
                            sensorEvent.values[1],sensorEvent.values[2]), "y reori",
                            ContextCompat.getColor(activity.getApplicationContext(), R.color.colorYReori), mChart);
                }
                if(zValueReorientedChecked){
                    addEntry((float)nericellMechanism.reorientZ(sensorEvent.values[0],
                            sensorEvent.values[1],sensorEvent.values[2]), "z reori",
                            ContextCompat.getColor(activity.getApplicationContext(), R.color.colorZReori), mChart);
                }
                addEntry((float)Math.sqrt(Math.pow(sensorEvent.values[0],2)+Math.pow(sensorEvent.values[1],2)+
                        Math.pow(sensorEvent.values[2],2)), "rms", Color.RED, rmsChart);
                plotData = false;
//            }
        }
    }

    public boolean isEnableFilter() {
        return enableFilter;
    }

    public void setEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
    }

    private static void addEntry(float value, String type, int color, LineChart chart) {
        if(Float.isNaN(value)){
            return;
        }
        LineData data = chart.getLineData();
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
            chart.notifyDataSetChanged();
//            mChart.setMaxVisibleValueCount(150);
//            mChart.setFocusable(true);
//            mChart.setVisibleXRangeMaximum(100);
            chart.setVisibleXRange(200f,200f);
            chart.moveViewToX(data.getEntryCount());
//            mChart.invalidate();
        }
    }

    private static LineDataSet createSet(String type, int color) {
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

    public void deleteSet(String type, LineChart chart){
        Log.d(TAG,"---------------  data set deleted --------- /// ");
        LineData data = chart.getLineData();
//        ILineDataSet set = data.getDataSetByLabel(type, true);
        data.removeDataSet(data.getDataSetByLabel(type, true));
//        set.clear();
        chart.invalidate();
    }

}
