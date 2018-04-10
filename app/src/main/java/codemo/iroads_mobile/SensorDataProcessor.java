package codemo.iroads_mobile;

import android.util.Log;

import java.util.HashMap;

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
    /**
     * reorientation
     */
    private static Reorientation reorientation=null;
    private static ReorientationType reorientationType;

    private static double reorientedAx;
    private static double reorientedAy;
    private static double reorientedAz;

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

    public static double getReorientedAx() {
        return reorientedAx;
    }

    public static double getReorientedAy() {
        return reorientedAy;
    }

    public static double getReorientedAz() {
        return reorientedAz;
    }

    /**
     * filtering
     */
    static SignalProcessor signalProcessor=new SignalProcessor();

}
