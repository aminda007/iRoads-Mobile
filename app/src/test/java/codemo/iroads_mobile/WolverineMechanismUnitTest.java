package codemo.iroads_mobile;

import org.junit.Test;

import java.util.HashMap;

import codemo.iroads_mobile.Reorientation.WolverineMechanism;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WolverineMechanismUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void rotateVector_test() throws Exception{
        WolverineMechanism wm=new WolverineMechanism();

        HashMap expectedVector = new HashMap<>();
        expectedVector.put('X',1.0);
        expectedVector.put('Y',-0.3660254037844385);
        expectedVector.put('Z',1.3660254037844388);

        assertEquals(expectedVector,wm.rotateVector(1,1,1,Math.toRadians(60),0, 0));
    }

    @Test
    public void vectorCross_test() throws Exception{
        WolverineMechanism wm=new WolverineMechanism();

        HashMap<String,Double> vector1=new HashMap();
        vector1.put("X",new Double(3));
        vector1.put("Y",new Double(-3));
        vector1.put("Z",new Double(1));

        HashMap vector2=new HashMap();
        vector2.put("X",new Double(4));
        vector2.put("Y",new Double(9));
        vector2.put("Z",new Double(2));


        HashMap expectedVector = new HashMap<>();
        expectedVector.put('X',-15.0);
        expectedVector.put('Y',-2.0);
        expectedVector.put('Z',39.0);

        assertEquals(expectedVector,wm.getCrossProduct(vector1,vector2));
    }
}