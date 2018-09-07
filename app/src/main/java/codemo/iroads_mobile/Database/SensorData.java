package codemo.iroads_mobile.Database;

/**
 * Created by uwin5 on 04/01/18.
 */

public class SensorData {

    private static String obdSpeed = "0.0";
    private static String obdRpm = "0.0";

    private static String lat = "0.0";
    private static String lon = "0.0";

    private static String acceX = "";
    private static String acceY = "";
    private static String acceZ = "";

    private static String magnetX = "";
    private static String magnetY = "";
    private static String magnetZ = "";

    private static String gyroX = "";
    private static String gyroY = "";
    private static String gyroZ = "";

    private static String deviceId = "";
    private static String journeyId = "";

    public static String getJourneyId() {
        return journeyId;
    }

    public static void setJourneyId(String journeyId) {
        SensorData.journeyId = journeyId;
    }

    public static String getObdSpeed() {
        return obdSpeed;
    }

    public static void setObdSpeed(String obdSpeed) {
        SensorData.obdSpeed = obdSpeed;
    }

    public static String getObdRpm() {
        return obdRpm;
    }

    public static void setObdRpm(String obdRpm) {
        SensorData.obdRpm = obdRpm;
    }

    public static String getLat() {
        return lat;
    }

    public static void setLat(String lat) {
        SensorData.lat = lat;
    }

    public static String getLon() {
        return lon;
    }

    public static void setLon(String lon) {
        SensorData.lon = lon;
    }

    public static String getAcceX() {
        return acceX;
    }

    public static void setAcceX(String acceX) {
        SensorData.acceX = acceX;
    }

    public static String getAcceY() {
        return acceY;
    }

    public static void setAcceY(String acceY) {
        SensorData.acceY = acceY;
    }

    public static String getAcceZ() {
        return acceZ;
    }

    public static void setAcceZ(String acceZ) {
        SensorData.acceZ = acceZ;
    }

    public static String getMagnetX() {
        return magnetX;
    }

    public static void setMagnetX(String magnetX) {
        SensorData.magnetX = magnetX;
    }

    public static String getMagnetY() {
        return magnetY;
    }

    public static void setMagnetY(String magnetY) {
        SensorData.magnetY = magnetY;
    }

    public static String getMagnetZ() {
        return magnetZ;
    }

    public static void setMagnetZ(String magnetZ) {
        SensorData.magnetZ = magnetZ;
    }

    public static String getGyroX() {
        return gyroX;
    }

    public static void setGyroX(String gyroX) {
        SensorData.gyroX = gyroX;
    }

    public static String getGyroY() {
        return gyroY;
    }

    public static void setGyroY(String gyroY) {
        SensorData.gyroY = gyroY;
    }

    public static String getGyroZ() {
        return gyroZ;
    }

    public static void setGyroZ(String gyroZ) {
        SensorData.gyroZ = gyroZ;
    }

    public static String getDeviceId() {
        return deviceId;
    }

    public static void setDeviceId(String deviceId) {
        SensorData.deviceId = deviceId;
    }
}
