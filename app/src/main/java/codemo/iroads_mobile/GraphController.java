package codemo.iroads_mobile;

import android.hardware.SensorEvent;
import android.location.Location;

import com.github.mikephil.charting.charts.LineChart;

import codemo.iroads_mobile.Fragments.GraphFragment;
import codemo.iroads_mobile.Fragments.HomeFragment;

/**
 * Created by aminda on 3/9/2018.
 */


public class GraphController {

    public static void setRmsChart(LineChart rmsChart) {
        GraphFragment.setRmsChart(rmsChart);
    }

    public static void setIRIChart(LineChart iriChart) {
        GraphFragment.setIRIChart(iriChart);
    }

    public static void drawGraph(SensorEvent sensorEvent) {
        GraphFragment.drawGraph(sensorEvent);
    }
}
