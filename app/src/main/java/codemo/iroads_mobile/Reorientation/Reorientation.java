package codemo.iroads_mobile.Reorientation;

import java.util.HashMap;

import codemo.iroads_mobile.Vector3D;

/**
 * Created by dushan on 3/18/18.
 */

public interface Reorientation {

    public Vector3D reorient(float xValueA, float yValueA, float zValueA, float xValueM, float yValueM, float zValueM);
}
