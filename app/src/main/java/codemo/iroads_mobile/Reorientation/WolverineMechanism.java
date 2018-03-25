package codemo.iroads_mobile.Reorientation;

import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by dushan on 3/18/18.
 */

public class WolverineMechanism implements Reorientation{


    @Override
    public void reorient() {

        //accelerometer xyx
        float xValueA, yValueA, zValueA;

        //magnetometer xyx
        float xValueM, yValueM, zValueM;


    }


    /**
     *
     * @param X
     * @param Y
     * @param Z
     * @param xAngle
     * @param yAngle
     * @param zAngle
     * @return
     */

    private HashMap<String,Double> rotate(double X, double Y, double Z, double xAngle, double yAngle, double zAngle){

        double rotatedX=
                X * ( Math.cos(zAngle) * Math.cos(yAngle) + Math.sin(zAngle) * Math.sin(xAngle) * Math.sin(yAngle) ) +
                Y * ( - Math.sin(zAngle) * Math.cos(yAngle) + Math.cos(zAngle) * Math.sin(xAngle) * Math.sin(yAngle) ) +
                Z * ( Math.cos(xAngle) * Math.sin(yAngle) );

        double rotatedY=
                X * ( Math.sin(zAngle) * Math.cos(xAngle) ) +
                Y * ( Math.cos(zAngle) * Math.cos(xAngle) ) +
                Z * ( -Math.sin(xAngle) );

        double rotatedZ=
                X * ( - Math.cos(zAngle) * Math.sin(yAngle)   +   Math.sin(zAngle) * Math.sin(xAngle) * Math.cos(yAngle) ) +
                Y * ( Math.sin(zAngle) * Math.sin(yAngle)   +   Math.cos(zAngle) * Math.sin(xAngle) * Math.cos(yAngle) ) +
                Z * ( Math.cos(xAngle) * Math.cos(yAngle) );

        HashMap rotatedValues=new HashMap<>();
        rotatedValues.put('X',rotatedX);
        rotatedValues.put('Y',rotatedY);
        rotatedValues.put('Z',rotatedZ);

        return rotatedValues;

    }
}
