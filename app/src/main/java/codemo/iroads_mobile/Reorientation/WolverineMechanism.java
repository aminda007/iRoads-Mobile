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
     * @return rotated vector
     */
    public HashMap<String,Double> rotateVector(double X, double Y, double Z, double xAngle, double yAngle, double zAngle){

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

        HashMap rotatedValues = new HashMap<>();
        rotatedValues.put('X',rotatedX);
        rotatedValues.put('Y',rotatedY);
        rotatedValues.put('Z',rotatedZ);

        System.out.println("original vectors - " +"X: "+X+", Y:"+Y+", Z:"+Z+"\n" +
                "rotation in radians- around X:"+xAngle+", around Y:"+ yAngle+", around Z:"+zAngle+"\n" +
                "rotated vectors - "+rotatedValues);

        return rotatedValues;

    }

    /**
     *
     * @param vector1 keys X,Y,Z
     * @param vector2 keys X,Y,Z
     * @return cross product
     */
    public HashMap<String,Double> getCrossProduct(HashMap<String,Double> vector1, HashMap<String,Double> vector2){

        double u1, u2, u3, v1, v2, v3;
        u1 = new Double(vector1.get("X"));
        u1 = (double)vector1.get("X");
        u2 = (double)vector1.get("Y");
        u3 = (double)vector1.get("Z");
        v1 = (double)vector2.get("X");
        v2 = (double)vector2.get("Y");
        v3 = (double)vector2.get("Z");

        double uvi, uvj, uvk;
        uvi = u2 * v3 - v2 * u3;
        uvj = v1 * u3 - u1 * v3;
        uvk = u1 * v2 - v1 * u2;

        System.out.println("vector1 = " + u1 + "X + " + u2 + "Y + " + u3 + "Z \n" +
                "vector2 = " + u1 + "X + " + u2 + "Y + " + u3 + "Z \n\n" +
                "vector1 X vector2 : " + uvi + "X +" + uvj + "Y+ " + uvk + "Z ");

        HashMap crossProduct = new HashMap<>();
        crossProduct.put('X',uvi);
        crossProduct.put('Y',uvj);
        crossProduct.put('Z',uvk);

        return crossProduct;

    }
}
