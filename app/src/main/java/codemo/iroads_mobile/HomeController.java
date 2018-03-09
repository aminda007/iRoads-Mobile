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
}
