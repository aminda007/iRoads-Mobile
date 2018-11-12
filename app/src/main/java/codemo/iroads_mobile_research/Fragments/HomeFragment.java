package codemo.iroads_mobile_research.Fragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import codemo.iroads_mobile_research.Database.DatabaseHandler;
import codemo.iroads_mobile_research.Database.SensorData;
import codemo.iroads_mobile_research.GraphController;
import codemo.iroads_mobile_research.MainActivity;
import codemo.iroads_mobile_research.MobileSensors;
import codemo.iroads_mobile_research.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private DatabaseHandler dbHandler;

    private static final String TAG = "HomeFragment";

    private static MainActivity mainActivity;
    private static boolean obdDataAvailable = false;

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
    private static ImageButton saveBtn;
    private static ImageButton bConnectBtn;
    private ImageButton reOriBtn;
    private ImageButton startBtn;
    private static TextView  lat, lng;

    private static TextView  obd2speed, obd2rpm;
    private static ProgressBar speedProgressBar;
    private static ProgressBar rpmProgressBar;
    private Thread fakethread;
    private Handler handler;
    private Runnable handlerTask;
    private static ProgressBar spinnerObd;
    private ProgressBar spinnerReori;
    private static ProgressBar spinnerSave;
    private static boolean autoSaveON = false;

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
                if(isAutoSaveON()){
                    Toast.makeText( getContext(),"Auto Save is currently enabled", Toast.LENGTH_SHORT).show();
                }else{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    writeLog("\n \n" + sdf.format(new Date()) + dataReport.toString() + "\n \n");
                    dataReport = new StringBuilder();
                    dbHandler.startReplication();
//                    dbHandler.writeToFile(dbHandler.createString());
                    MainActivity.setReplicationStopped(false);
                    startSaving();
                    Toast.makeText( getContext(),"Sync up Started", Toast.LENGTH_SHORT).show();
                }
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
        reOriBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        dbHandler = new DatabaseHandler(mainActivity.getApplicationContext());
        reOriBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
//                spinnerReori.setVisibility(View.VISIBLE);
                reOriBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
                DatabaseHandler.printDocCount();
            }
        });

        startBtn = (ImageButton) view.findViewById(R.id.startBtn);
//        reOriBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        startBtn.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View view) {
//                spinnerReori.setVisibility(View.VISIBLE);
                if(!GraphFragment.isStarted()){
                    // check whether the permission granted to retrieve IMEI number
                    TelephonyManager telephonyManager = (TelephonyManager) mainActivity.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        showAlertPhPermission();
                        return;
                    }
                    String deviceId = telephonyManager.getDeviceId();
                    SensorData.setDeviceId(deviceId);
                    Log.d(TAG,"--------------- DeviceId --------- /// "+ deviceId);
                    askJourneyName();
                }else{
                    // change the btn icon back to idle state
                    startBtn.setImageResource(R.drawable.ic_play_blue_outline);
                    GraphFragment.setStarted(false);
                    Toast.makeText( getContext(),"Journey Stopped", Toast.LENGTH_SHORT).show();
                }

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
        Log.d(TAG,"--------------- Location changed --------- /// "+ loc.getLatitude()+ " "+loc.getLongitude());
//        lat.setText("Latitude: "+ loc.getLatitude());
//        lng.setText("Longitude: "+ loc.getLongitude());
        if(!obdDataAvailable){
            Double speed = SpeedCalculator.getSpeed(loc.getLatitude(), loc.getLongitude());
            MobileSensors.setGpsSpeed(speed);// updates vehicle speed using GPS
            updateSpeed(speed.intValue());
        }
        SensorData.setLat(Double.toString(loc.getLatitude()));
        SensorData.setLon(Double.toString(loc.getLongitude()));
    }

    public static void updateSpeed(int speed){
        obd2speed.setText(speed+" km/h");
//        obd2rpm.setText("RPM: "+ rpm);
        ObjectAnimator animSpeed = ObjectAnimator.ofInt(speedProgressBar, "progress", speedProgressBar.getProgress(), speed*10000);
        animSpeed.setDuration(900);
        animSpeed.setInterpolator(new DecelerateInterpolator());
        animSpeed.start();
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
//                updateOBD2Data(i1,i2);
                handler.postDelayed(handlerTask, 1000);
            }
        };
        handlerTask.run();
    }

    public static  void updateOBD2Data(int speed,int rpm){
        obdDataAvailable = true;
        bConnectBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        spinnerObd.setVisibility(View.GONE);
        updateSpeed(speed);
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

    public static void startSaving(){
        spinnerSave.setVisibility(View.VISIBLE);
        saveBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorIconBlack));
        saveBtn.setEnabled(false);
    }

    public static void stopSaving(){
        spinnerSave.setVisibility(View.GONE);
        saveBtn.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        saveBtn.setEnabled(true);
    }

    private void showAlertPhPermission() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Enable Phone")
                .setMessage("Please Enable Phone to " +
                        "use this app")
                .setPositiveButton("Application Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        startActivity(myIntent);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mainActivity.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }


    public static boolean isAutoSaveON() {
        return autoSaveON;
    }

    public static void setAutoSaveON(boolean autoSaveON) {
        HomeFragment.autoSaveON = autoSaveON;
    }

    private void askJourneyName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Journey Name");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if(text != null && text.trim().length() == 0){
                    Toast.makeText( getContext(),"Journey Name can not be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    SensorData.setJourneyId(SensorData.getDeviceId()+ System.currentTimeMillis());
                    DatabaseHandler.saveJourneyName(text);
                    // change the btn icon to started state
                    startBtn.setImageResource(R.drawable.ic_pause_blue_outline);
                    GraphFragment.setStarted(true);
                    Toast.makeText( getContext(),"Journey Started", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
