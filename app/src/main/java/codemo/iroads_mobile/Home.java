package codemo.iroads_mobile;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import codemo.iroads_mobile.Fragments.CompassFragment;
import codemo.iroads_mobile.Fragments.HomeFragment;
import codemo.iroads_mobile.Fragments.SettingsFragment;

public class Home extends AppCompatActivity{


    private FragmentManager manager;
    private FragmentTransaction transaction;
    private CompassFragment compassFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        init fragment manager
            manager = getFragmentManager();
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.contentLayout, homeFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.contentLayout, compassFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.contentLayout, settingsFragment).commit();
                    return true;
            }
            return false;
        }
    };

    private static final String TAG = "Home";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        init fragment manager
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
//        initiate fragment objects
        homeFragment = new HomeFragment();
        compassFragment = new CompassFragment();
        settingsFragment = new SettingsFragment();
//        open home fragment
        transaction.replace(R.id.contentLayout, homeFragment).commit();
    }
}
