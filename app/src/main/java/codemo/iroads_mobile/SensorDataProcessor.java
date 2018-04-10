package codemo.iroads_mobile;

import android.util.Log;

import codemo.iroads_mobile.Entity.Vector3D;
import codemo.iroads_mobile.Fragments.SignalProcessor;
import codemo.iroads_mobile.Reorientation.NericellMechanism;
import codemo.iroads_mobile.Reorientation.Reorientation;
import codemo.iroads_mobile.Reorientation.ReorientationType;
import codemo.iroads_mobile.Reorientation.WolverineMechanism;

/**
 * Created by dushan on 4/3/18.
 */

public class SensorDataProcessor {

    private static final String TAG = "SensorDataProcessor";

    private static Reorientation reorientation=null;
    private static ReorientationType reorientationType;

    private static SignalProcessor signalProcessorX=new SignalProcessor();
    private static SignalProcessor signalProcessorY=new SignalProcessor();
    private static SignalProcessor signalProcessorZ=new SignalProcessor();

    private static SignalProcessor highPassSignalProcessorZ =new SignalProcessor();

    private static IRICalculator iriCalculator=new IRICalculator();

    private static double reorientedAx;
    private static double reorientedAy;
    private static double reorientedAz;

    private static double avgFilteredAx;
    private static double avgFilteredAy;
    private static double avgFilteredAz;

    private static double highPassFilteredAz;

    private static double rms;

    private static double iri;


    /**
     * this method is triggering when sensor data changing from MobileSensor.java
     */
    public static void updateSensorDataProcessingValues(){

        updateCurrentReorientedAccelerations();

        updateAvgFilteredX();

        updateAvgFilteredY();

        updateAvgFilteredZ();

        updateHighPassFilteredZ();

        updateRms();

        //updating iri should be below updateAvgFilteredZ();
        updateIRI();
    }


    /*
    getters
     */
    public static double getReorientedAx() {
        return reorientedAx;
    }

    public static double getReorientedAy() {
        return reorientedAy;
    }

    public static double getReorientedAz() {
        return reorientedAz;
    }

    public static double getAvgFilteredAx() {
        return avgFilteredAx;
    }

    public static double getAvgFilteredAy() {
        return avgFilteredAy;
    }

    public static double getAvgFilteredAz() {
        return avgFilteredAz;
    }

    public static double getHighPassFilteredAz() {
        return highPassFilteredAz;
    }

    public static double getRms() {
        return rms;
    }

    public static double getIri() {
        return iri;
    }






    /*
    reorientation mechanism
     */
    public static ReorientationType getReorientationType() {
        return reorientationType;
    }

    public static void setReorientation(ReorientationType type){
        reorientationType=type;
        if (type==ReorientationType.Nericel){
            reorientation=new NericellMechanism();
            Log.d(TAG,"Reorientation set to Nericel");
        }else if (type==ReorientationType.Wolverine){
            reorientation=new WolverineMechanism();
            Log.d(TAG,"Reorientation set to Wolverine");
        }
    }





    /*
    value updating methods
     */

    /**
     * this should run always or once in a peroid to keep updating reorientation.
     */
    public static void updateCurrentReorientedAccelerations(){

        if (reorientation!=null) {
            Vector3D reoriented= reorientation.reorient(MobileSensors.getCurrentAccelerationX(), MobileSensors.getCurrentAccelerationY(),
                    MobileSensors.getCurrentAccelerationZ(),
                    MobileSensors.getCurrentMagneticX(), MobileSensors.getCurrentMagneticY(),
                    MobileSensors.getCurrentMagneticZ()
            );

            reorientedAx=reoriented.getX();
            reorientedAy=reoriented.getY();
            reorientedAz=reoriented.getZ();

        }else {
            Log.d(TAG,"set reorientation type first");
        }
    }


    public static void updateAvgFilteredX(){
       avgFilteredAx =signalProcessorX.averageFilter(MobileSensors.getCurrentAccelerationX());
    }

    public static void updateAvgFilteredY(){
        avgFilteredAy =signalProcessorY.averageFilter(MobileSensors.getCurrentAccelerationY());
    }

    public static void updateAvgFilteredZ(){
        avgFilteredAz =signalProcessorZ.averageFilter(MobileSensors.getCurrentAccelerationZ());
    }

    public static void updateHighPassFilteredZ(){
        highPassFilteredAz = highPassSignalProcessorZ.averageFilter(MobileSensors.getCurrentAccelerationZ());
    }



    public static void updateRms(){
        rms= Math.sqrt(Math.pow(MobileSensors.getCurrentAccelerationX(),2)
                +Math.pow(MobileSensors.getCurrentAccelerationY(),2)+
                Math.pow(MobileSensors.getCurrentAccelerationZ(),2));
    }



    public static void updateIRI(){

        /**
         * get avgFilteredZ for this method input.
         * if avgFilteredZ not updated in updateSensorDataProcessingValues() below line should be uncommented
         */
        //updateAvgFilteredZ();


        /**
         * only if avg filtering is updating,
         */
        iri=iriCalculator.processIRI(getAvgFilteredAz());

    }


}
