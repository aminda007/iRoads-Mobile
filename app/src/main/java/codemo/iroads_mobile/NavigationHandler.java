package codemo.iroads_mobile;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import codemo.iroads_mobile.Fragments.GMapFragment;
import codemo.iroads_mobile.Fragments.GraphFragment;
import codemo.iroads_mobile.Fragments.HomeFragment;
import codemo.iroads_mobile.Fragments.SettingsFragment;
import codemo.iroads_mobile.Fragments.TaggerFragment;

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
            if(manager.findFragmentByTag("graphFragment") != null){
                transaction.hide(manager.findFragmentByTag("graphFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide graph ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("taggerFragment") != null){
                transaction.hide(manager.findFragmentByTag("taggerFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide tagger ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }
        else if(fragment == "mapFragment"){
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
            if(manager.findFragmentByTag("graphFragment") != null){
                transaction.hide(manager.findFragmentByTag("graphFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide graph ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("taggerFragment") != null){
                transaction.hide(manager.findFragmentByTag("taggerFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide tagger ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }
        else if(fragment == "graphFragment"){
            if(manager.findFragmentByTag("graphFragment") == null){
                transaction.add(R.id.contentLayout, new GraphFragment(), "graphFragment");
            }else{
                transaction.show(manager.findFragmentByTag("graphFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....resume graph ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("homeFragment") != null){
                transaction.hide(manager.findFragmentByTag("homeFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide home ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("settingsFragment") != null){
                transaction.hide(manager.findFragmentByTag("settingsFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide setting ....aaaaaaaaaaaaaaaaaaaaaa***");

            }
            if(manager.findFragmentByTag("mapFragment") != null){
                transaction.hide(manager.findFragmentByTag("mapFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide map ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("taggerFragment") != null){
                transaction.hide(manager.findFragmentByTag("taggerFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide tagger ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }
        else if(fragment == "taggerFragment"){
            if(manager.findFragmentByTag("taggerFragment") == null){
                transaction.add(R.id.contentLayout, new TaggerFragment(), "taggerFragment");
            }else{
                transaction.show(manager.findFragmentByTag("taggerFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....resume tagger ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("homeFragment") != null){
                transaction.hide(manager.findFragmentByTag("homeFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide home ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("settingsFragment") != null){
                transaction.hide(manager.findFragmentByTag("settingsFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide setting ....aaaaaaaaaaaaaaaaaaaaaa***");

            }
            if(manager.findFragmentByTag("mapFragment") != null){
                transaction.hide(manager.findFragmentByTag("mapFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide map ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("graphFragment") != null){
                transaction.hide(manager.findFragmentByTag("graphFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide graph ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }
        else{
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
            if(manager.findFragmentByTag("graphFragment") != null){
                transaction.hide(manager.findFragmentByTag("graphFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide graph ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
            if(manager.findFragmentByTag("taggerFragment") != null){
                transaction.hide(manager.findFragmentByTag("taggerFragment"));
                Log.d("rht","aaaaaaaaaaaaaaaaaaaa.....hide tagger ....aaaaaaaaaaaaaaaaaaaaaa***");
            }
        }

        transaction.commit();
    }

    public static void setManager(FragmentManager manager) {
        NavigationHandler.manager = manager;
    }
}
