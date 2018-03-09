package codemo.iroads_mobile;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import codemo.iroads_mobile.Fragments.GMapFragment;
import codemo.iroads_mobile.Fragments.HomeFragment;
import codemo.iroads_mobile.Fragments.SettingsFragment;

/**
 * Created by aminda on 3/6/2018.
 */

public class NavigationHandler {

    private static FragmentManager manager;

    public static void navigateTo(String fragment){

        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment == "homeFragment"){
            if(manager.findFragmentByTag("homeFragment") == null){
                transaction.add(R.id.contentLayout, new HomeFragment(), "homeFragment");
            }
            else{
                transaction.show(manager.findFragmentByTag("homeFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....resume home ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("mapFragment") != null){
                transaction.hide(manager.findFragmentByTag("mapFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide map ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("settingsFragment") != null){
                transaction.hide(manager.findFragmentByTag("settingsFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide setting ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }else if(fragment == "mapFragment"){
            if(manager.findFragmentByTag("mapFragment") == null){
                transaction.add(R.id.contentLayout, new GMapFragment(), "homeFragment");
            }else{
                transaction.show(manager.findFragmentByTag("mapFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....resume map ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("homeFragment") != null){
                transaction.hide(manager.findFragmentByTag("homeFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide home ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("settingsFragment") != null){
                transaction.hide(manager.findFragmentByTag("settingsFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide setting ....aaaaaaaaaaaaaaaaaaaaaa***");

            }
        }else{
            if(manager.findFragmentByTag("settingsFragment") == null){
                transaction.add(R.id.contentLayout, new SettingsFragment(), "settingsFragment");
            }else{
                transaction.show(manager.findFragmentByTag("settingsFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....resume setting ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("homeFragment") != null){
                transaction.hide(manager.findFragmentByTag("homeFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide home ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("mapFragment") != null){
                transaction.hide(manager.findFragmentByTag("mapFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide map ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }

        transaction.commit();
    }

    public static void setManager(FragmentManager manager) {
        NavigationHandler.manager = manager;
    }
}
