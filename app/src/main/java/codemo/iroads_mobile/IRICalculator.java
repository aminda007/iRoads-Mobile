package codemo.iroads_mobile;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by aminda on 3/31/2018.
 */

public class IRICalculator {

    private static final String TAG = "IRICalculator";

    private static ArrayList<Double> dataArray;
    private static ArrayList<Double> timeArray;
    private static ArrayList<Location> locationArray;
    private static boolean previousDirection;
    private static Location cuurentLoc;
    private StringBuilder dataReport;

    public IRICalculator() {
        dataArray = new ArrayList<Double>(); // create array for storing acceleration data
        timeArray = new ArrayList<Double>(); // create array for storing time
        locationArray = new ArrayList<Location>(); // create array for storing locations
        previousDirection = true; // if positive
    }

    public static double processIRI(double z){
//        boolean directionChanged = false;
        double area = 0.0;
        boolean currentDirection = true;  // if positive
        double earthGravity = 9.82;
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
                    area = calculateArea();
                    Log.d(TAG,"++++++++++++++++ Calculated Area ++++++++++  "+area);
                }
                dataArray.clear();
                timeArray.clear();
                dataArray.add(accelerationDiff);
                timeArray.add(time);
                locationArray.add(cuurentLoc);
            }else{
                dataArray.add(accelerationDiff);
                timeArray.add(time);
            }
            previousDirection = currentDirection;
//            dataReport.append("\nAcceleration Difference: "+accelerationDiff);
//            dataReport.append(" TimeStamp: "+time);
//            dataReport.append(" Location: "+cuurentLoc.getLatitude() + " " + cuurentLoc.getLongitude());

        }else{
            // do nothing
        }
        if(Math.abs(area) < 1.0 ){
            return area;
        }else{
            return 0.0;
        }
    }

    public static double calculateArea(){
        double sum= 0.0;
        double totTime=0.0;
        double displacement=0.0;
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
                    displacement += velocityDiff * totTime / 2000;
//                    dataReport.append("\nDisplacement: " + displacement);
                    Log.d(TAG,"********************** Displacement ********************************************************" + displacement);
                    sum = 0.0;
                    totTime = 0.0;
                }
            }
        }
        if(sum != 0.0){
            double velocityDiff = sum / 2000;
            Log.d(TAG,"********************** Velocity Diff *************" + velocityDiff);
            displacement += velocityDiff * totTime / 2000;
//            dataReport.append("\nDisplacement: " + displacement);
            Log.d(TAG,"********************** Displacement ********************************************************" + displacement);
        }
        return displacement;
//        totTime = dataArray.get(dataArray.size()-1) - dataArray.get(0)


    }
}
