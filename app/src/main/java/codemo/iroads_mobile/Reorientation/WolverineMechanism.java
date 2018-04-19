package codemo.iroads_mobile.Reorientation;

import java.util.ArrayList;

import codemo.iroads_mobile.Entity.Vector3D;

/**
 * Created by dushan on 3/18/18.
 */

public class WolverineMechanism implements Reorientation{

    private Vector3D gravityVector;

    public Vector3D getGravityVector() {
        return gravityVector;
    }

    public void setGravityVector(Vector3D gravityVector) {
        if (this.gravityVector==null){
            this.gravityVector = gravityVector;
        }

    }


    @Override
    public Vector3D reorient( double xValueA, double yValueA, double zValueA, float xValueM, float yValueM, float zValueM) {


        //accelerometer xyx
        Vector3D accelerationVector = new Vector3D();
        accelerationVector.setX(xValueA);
        accelerationVector.setY(yValueA);
        accelerationVector.setZ(zValueA);

        //setting gravity vector from the first acceleration vector
        this.setGravityVector(accelerationVector);

        //magnetometer xyx
        Vector3D magneticVector = new Vector3D();
        magneticVector.setX(xValueM);
        magneticVector.setY(yValueM);
        magneticVector.setZ(zValueM);


        ArrayList<Vector3D> matrixForRotatingToGeometricAxes=new ArrayList<>();
        //getting east vector
        //this is done by getting the cross product of magnetic north vector and gravity vector and .
        Vector3D westEastVector=this.getCrossProduct(magneticVector,getGravityVector());
        matrixForRotatingToGeometricAxes.add(westEastVector);
        //getting north vector
        //this is done by getting the cross product of gravity vector and westEast vector.
        Vector3D northSouthVector=this.getCrossProduct(getGravityVector(),westEastVector);
        matrixForRotatingToGeometricAxes.add(northSouthVector);
        //gravityVector
        //getGravityVector()
        matrixForRotatingToGeometricAxes.add(getGravityVector());


        Vector3D gravityClearedAccelerationVectors=clearGravityFromAcclerations(matrixForRotatingToGeometricAxes,accelerationVector);
        Vector3D geometricallyRotatedAcceerations = getMatrixVectorMultiplication(matrixForRotatingToGeometricAxes, gravityClearedAccelerationVectors);

        return geometricallyRotatedAcceerations;
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
    public Vector3D rotateVector(double X, double Y, double Z, double xAngle, double yAngle, double zAngle){

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

        Vector3D rotatedValues = new Vector3D();
        rotatedValues.setX(rotatedX);
        rotatedValues.setY(rotatedY);
        rotatedValues.setZ(rotatedZ);

        return rotatedValues;

    }

    /**
     *
     * @param vector1
     * @param vector2
     * @return cross product
     */
    public Vector3D getCrossProduct(Vector3D vector1, Vector3D vector2){

        double u1, u2, u3, v1, v2, v3;
        u1 = vector1.getX();
        u2 = vector1.getY();
        u3 = vector1.getZ();

        v1 = vector2.getX();
        v2 = vector2.getY();
        v3 = vector2.getZ();

        double uvi, uvj, uvk;
        uvi = u2 * v3 - v2 * u3;
        uvj = v1 * u3 - u1 * v3;
        uvk = u1 * v2 - v1 * u2;

        Vector3D crossProduct = new Vector3D();
        crossProduct.setX(uvi);
        crossProduct.setY(uvj);
        crossProduct.setZ(uvk);

        return crossProduct;
    }

    /**
     *
     * @param matrix arraylist of vectors
     * @param vectors
     * @return
     */
    public Vector3D getMatrixVectorMultiplication(ArrayList<Vector3D> matrix, Vector3D vectors){
        double x=matrix.get(0).getX()*vectors.getX() +  matrix.get(0).getY()*vectors.getY() + matrix.get(0).getZ()*vectors.getZ();
        double y=matrix.get(1).getX()*vectors.getX() +  matrix.get(1).getY()*vectors.getY() + matrix.get(1).getZ()*vectors.getZ();
        double z=matrix.get(2).getX()*vectors.getX() +  matrix.get(2).getY()*vectors.getY() + matrix.get(2).getZ()*vectors.getZ();

        Vector3D value=new Vector3D();
        value.setX(x);
        value.setY(y);
        value.setZ(z);

        return value;

    }

    /**
     *
     * @param matrixForRotatingToGeometricAxes
     * @param accelerationVector
     * @return
     */
    private Vector3D clearGravityFromAcclerations(ArrayList<Vector3D> matrixForRotatingToGeometricAxes, Vector3D accelerationVector) {

        double xAngle;
        double yAngle;
        double zAngle;

        //removing gravity vector from accelerations
        // TODO: 3/31/18 implement this

        return accelerationVector;

    }

}
