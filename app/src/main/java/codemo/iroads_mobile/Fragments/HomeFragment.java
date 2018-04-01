package codemo.iroads_mobile.Fragments;


import android.animation.ObjectAnimator;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.Random;

import codemo.iroads_mobile.GraphController;
import codemo.iroads_mobile.HomeController;
import codemo.iroads_mobile.MainActivity;
import codemo.iroads_mobile.R;
import codemo.iroads_mobile.Reorientation.NericellMechanism;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";

    private static MainActivity mainActivity;

    private boolean enableFilter;
    private ArrayList<Double> dataArray;
    private ArrayList<Double> timeArray;
    private ArrayList<Location> locationArray;
    private boolean previousDirection;
    private static Location cuurentLoc;
    private StringBuilder dataReport;

    private LineChart mChart;
    private LineChart iriChart;
    private LineChart fuelChart;
    private ImageButton saveBtn;
    private ImageButton bConnectBtn;
    private ImageButton reOriBtn;
    private static TextView  lat, lng;

    private static TextView  obd2speed, obd2rpm;
    private static ProgressBar speedProgressBar;
    private static ProgressBar rpmProgressBar;
    private Thread fakethread;
    private Handler handler;
    private Runnable handlerTask;
    private ProgressBar spinnerObd;
    private ProgressBar spinnerReori;
    private ProgressBar spinnerSave;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dataReport = new StringBuilder();

//        lat = (TextView) view.findViewById(R.id.lat);
//        lng = (TextView) view.findViewById(R.id.lng);

//        obd2rpm = (TextView) view.findViewById(R.id.obd2rpm);
        obd2speed = (TextView) view.findViewById(R.id.obd2speed);

        saveBtn = (ImageButton) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                writeLog("\n \n" + sdf.format(new Date()) + dataReport.toString() + "\n \n");
                dataReport = new StringBuilder();
            }
        });

        spinnerObd=(ProgressBar)view.findViewById(R.id.progressBarLoadingObd);
        spinnerObd.getIndeterminateDrawable().setColorFilter(	ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinnerObd.setVisibility(View.GONE);
        spinnerReori=(ProgressBar)view.findViewById(R.id.progressBarLoadingReori);
        spinnerReori.getIndeterminateDrawable().setColorFilter(	ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinnerReori.setVisibility(View.GONE);
        spinnerSave=(ProgressBar)view.findViewById(R.id.progressBarLoadingSave);
        spinnerSave.getIndeterminateDrawable().setColorFilter(	ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinnerSave.setVisibility(View.GONE);

        bConnectBtn = (ImageButton) view.findViewById(R.id.obdBtn);
        bConnectBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                spinnerObd.setVisibility(View.VISIBLE);
                mainActivity.onConnectBtn();

            }
        });

        reOriBtn = (ImageButton) view.findViewById(R.id.reOriBtn);
        reOriBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
//                spinnerReori.setVisibility(View.VISIBLE);
                reOriBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
            }
        });

        mChart = (LineChart) view.findViewById(R.id.chartAccelerationZ);
        mChart.getDescription().setEnabled(false);

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
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setEnabled(false);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.LTGRAY);

        GraphController.setRmsChart(mChart);

        iriChart = (LineChart) view.findViewById(R.id.chartIRI);
        iriChart.getDescription().setEnabled(false);

        iriChart.setTouchEnabled(true);
        iriChart.setDragEnabled(true);
        iriChart.setScaleEnabled(false);
        iriChart.setDrawGridBackground(false);
        iriChart.setPinchZoom(false);
        iriChart.setBackgroundColor(Color.TRANSPARENT);

        YAxis lAxisIRI = iriChart.getAxisLeft();

        YAxis rAxisIRI = iriChart.getAxisRight();
        rAxisIRI.setEnabled(false);

        XAxis xAxisIRI  = iriChart.getXAxis();
        xAxisIRI.setEnabled(true);
        xAxisIRI.setDrawLabels(false);
        xAxisIRI.setDrawAxisLine(false);
        xAxisIRI.setDrawGridLines(false);

        LineData dataIRI = new LineData();
//        data.setValueTextColor(Color.WHITE);
        iriChart.setData(dataIRI);

        Legend lIRI = iriChart.getLegend();
        lIRI.setEnabled(false);

        GraphController.setIRIChart(iriChart);

        fuelChart = (LineChart) view.findViewById(R.id.chartFuel);
        fuelChart.getDescription().setEnabled(false);

        fuelChart.setTouchEnabled(true);
        fuelChart.setDragEnabled(true);
        fuelChart.setScaleEnabled(false);
        fuelChart.setDrawGridBackground(false);
        fuelChart.setPinchZoom(false);
        fuelChart.setBackgroundColor(Color.TRANSPARENT);

        YAxis lAxisFuel = fuelChart.getAxisLeft();

        YAxis rAxisFuel = fuelChart.getAxisRight();
        rAxisFuel.setEnabled(false);

        XAxis xAxisFuel  = fuelChart.getXAxis();
        xAxisFuel.setEnabled(true);
        xAxisFuel.setDrawLabels(false);
        xAxisFuel.setDrawAxisLine(false);
        xAxisFuel.setDrawGridLines(false);

        LineData dataFuel = new LineData();
//        data.setValueTextColor(Color.WHITE);
        fuelChart.setData(dataFuel);

        Legend lFuel = fuelChart.getLegend();
        lFuel.setEnabled(false);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.LTGRAY);

        GraphController.setFuelChart(fuelChart);

        speedProgressBar = (ProgressBar) view.findViewById(R.id.speed_progress_bar);
//        rpmProgressBar = (ProgressBar) view.findViewById(R.id.rpm_progress_bar);


//        startFakeProgress();
        startTimer();
        return view;

    }

    public static void setMainActivity(MainActivity activity){
        mainActivity=activity;
    }

    public static  void updateLocation(Location loc){
        cuurentLoc = loc;
        lat.setText("Latitude: "+ loc.getLatitude());
        lng.setText("Longitude: "+ loc.getLongitude());
    }

    public void startTimer(){
        handler = new Handler();
        handlerTask = new Runnable()
        {
            @Override
            public void run() {
                int min = 20;
                int max = 120;
                Random r1 = new Random();
                int i1 = r1.nextInt(max - min + 1) + min;
                Random r2 = new Random();
                int i2 = r2.nextInt(max - min + 1) + min;
                updateOBD2Data(i1,i2);
                handler.postDelayed(handlerTask, 1000);
            }
        };
        handlerTask.run();
    }

    public static  void updateOBD2Data(int speed,int rpm){
        int lastSpeed =speed;
        int lastRpm =rpm;
        obd2speed.setText(speed+" km/h");
//        obd2rpm.setText("RPM: "+ rpm);
        ObjectAnimator animSpeed = ObjectAnimator.ofInt(speedProgressBar, "progress", speedProgressBar.getProgress(), speed*10000);
        animSpeed.setDuration(900);
        animSpeed.setInterpolator(new DecelerateInterpolator());
        animSpeed.start();
//        ObjectAnimator animRpm = ObjectAnimator.ofInt(rpmProgressBar, "progress", rpmProgressBar.getProgress(), rpm*10000);
//        animRpm.setDuration(900);
//        animRpm.setInterpolator(new DecelerateInterpolator());
//        animRpm.start();
    }


    public boolean isEnableFilter() {
        return enableFilter;
    }

    public void setEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
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
