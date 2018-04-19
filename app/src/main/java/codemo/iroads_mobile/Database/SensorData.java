package codemo.iroads_mobile.Database;

/**
 * Created by uwin5 on 04/01/18.
 */

public class SensorData {

    private static String mobdSpeed="";
    private static String mobdRpm="";
    private static String mlat="";
    private static String mlon="";
    private static String macceX="";
    private static String macceY="";
    private static String macceZ="";


    public static String getMobdSpeed() {
        return mobdSpeed;
    }

    public static void setMobdSpeed(String mobdSpeed) {
        SensorData.mobdSpeed = mobdSpeed;
    }

    public static String getMobdRpm() {
        return mobdRpm;
    }

    public static void setMobdRpm(String mobdRpm) {
        SensorData.mobdRpm = mobdRpm;
    }

    public static String getMlat() {
        return mlat;
    }

    public static void setMlat(String mlat) {
        SensorData.mlat = mlat;
    }

    public static String getMlon() {
        return mlon;
    }

    public static void setMlon(String mlon) {
        SensorData.mlon = mlon;
    }

    public static String getMacceX() {
        return macceX;
    }

    public static void setMacceX(String macceX) {
        SensorData.macceX = macceX;
    }

    public static String getMacceY() {
        return macceY;
    }

    public static void setMacceY(String macceY) {
        SensorData.macceY = macceY;
    }

    public static String getMacceZ() {
        return macceZ;
    }

    public static void setMacceZ(String macceZ) {
        SensorData.macceZ = macceZ;
    }
}
