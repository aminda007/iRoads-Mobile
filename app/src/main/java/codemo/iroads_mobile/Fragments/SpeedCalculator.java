package codemo.iroads_mobile.Fragments;

import android.util.Log;

/**
 * Created by Pivithuru on 4/2/2018.
 */

public class SpeedCalculator {
    private boolean start;
    private double lon1;
    private double lat1;
    private double time1;
    public double getRadiant(double degree){
        double radiant = degree*(2*Math.PI/360);
        return radiant;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public double getDistance(double lon1, double lat1, double lon2, double lat2){
        double r = 6371000;
        double phi1 = getRadiant(lat1);
        double phi2 = getRadiant(lat2);
        double deltaPhi = getRadiant(lat2-lat1);
        double deltaLamda = getRadiant(lon2 - lon1);

        double a = Math.sin(deltaPhi/2.0)*Math.sin(deltaPhi/2.0) +
                Math.cos(phi1)*Math.cos(phi2)*Math.sin(deltaLamda/2.0)*Math.sin(deltaLamda/2.0);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double meters = r * c;
        double km = meters/1000.0;
        return km;
    }

    public double getSpeed (double lon, double lat){
        double lon2 = lon;
        double lat2 = lat;
        double time2 = System.currentTimeMillis();
        double speed;
        if(!start){
            this.lon1 = lon;
            this.lat1 = lat;
            this.time1 = time2;
            speed = 0;
            start = true;
        } else {
            double distance = this.getDistance(this.lon1, this.lat1, lon2, lat2);
            double timeDiff = (time2 - this.time1)/3600000;
            speed = distance/timeDiff;
            this.lon1 = lon2;
            this.lat1 = lat2;
            this.time1 = time2;
        }

        return speed;
    }
}
