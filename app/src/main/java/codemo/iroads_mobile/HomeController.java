package codemo.iroads_mobile;

import android.location.Location;

import codemo.iroads_mobile.Fragments.HomeFragment;

/**
 * Created by aminda on 3/9/2018.
 */


public class HomeController {
    public static void updateLocation(Location loc){
        HomeFragment.updateLocation(loc);
    }

    public static void updateOBD2Data(String speed,String rpm){
        HomeFragment.updateOBD2Data(speed, rpm);
    }

    public static void setMainActivity(MainActivity activity){
        HomeFragment.setMainActivity(activity);
    }

}
