package codemo.iroads_mobile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.vatichub.obd2.OBD2CoreConfiguration;
import com.vatichub.obd2.OBD2CoreConstants;
import com.vatichub.obd2.OBD2EventManager;
import com.vatichub.obd2.api.OBD2EventListener;
import com.vatichub.obd2.bean.OBD2Event;
import com.vatichub.obd2.connect.BTServiceCallback;
import com.vatichub.obd2.connect.bt.BluetoothCommandService;
import com.vatichub.obd2.realtime.OBD2SiddhiAgentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import codemo.iroads_mobile.Fragments.GMapFragment;
import codemo.iroads_mobile.Fragments.GraphFragment;
import codemo.iroads_mobile.Fragments.HomeFragment;
import codemo.iroads_mobile.Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,OBD2EventListener {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MapFragment mapFragment;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;

    private static final int REQUEST_ENABLE_BT = 2;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_OK=200;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String CAR_CONNECTED = "car_connected";

    public static final String CAR_CONNECTED_STATUS = "car_connected";

    private static final int REQUEST_CONNECT_DEVICE = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private IroadsConfiguration gconfigs;
    private Timer btconnectAttemptScheduler;
    private boolean attemptScheduled = false;
    private int btConnectAttemptsRemaining = 5;
    private boolean proceedAttemptCycle = true;

    private BluetoothCommandService mCommandService;
    private static final int REQUEST_ENABLE_BT_PLUS_CONNECT_DEVICE = 5;
    private Menu mainMenu;
    private Context context;
    private BottomNavigationView navigation;
    private ImageButton homeBtn;
    private boolean inHome = true;
    private Icon homeIcon;
    private Thread fakethread;


//    BottomNavigationView.BaseSavedState(R.id.navigation_home);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        init fragment manager
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    transaction.replace(R.id.contentLayout, homeFragment).commit();
                    NavigationHandler.navigateTo("homeFragment");
                    homeBtn.setImageResource(R.mipmap.ic_iroads);
                    homeBtn.setScaleX(1.1f);
                    homeBtn.setScaleY(1.1f);
                    return true;
//                    -------- remove comments for public app *****
//                case R.id.navigation_dashboard:
//                    NavigationHandler.navigateTo("mapFragment");
//                    homeBtn.setImageResource(R.mipmap.ic_iroads_black);
//                    homeBtn.setScaleX(1);
//                    homeBtn.setScaleY(1);
//                    return true;
                case R.id.navigation_graph:
                    NavigationHandler.navigateTo("graphFragment");
                    homeBtn.setImageResource(R.mipmap.ic_iroads_black);
                    homeBtn.setScaleX(1);
                    homeBtn.setScaleY(1);
                    return true;
                case R.id.navigation_settings:
                    NavigationHandler.navigateTo("settingsFragment");
                    homeBtn.setImageResource(R.mipmap.ic_iroads_black);
                    homeBtn.setScaleX(1);
                    homeBtn.setScaleY(1);
                    return true;
            }
            return false;
        }
    };

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHandler.navigateTo("homeFragment");
                homeBtn.setImageResource(R.mipmap.ic_iroads);
                homeBtn.setScaleX(1.1f);
                homeBtn.setScaleY(1.1f);
//                homeBtn.setPadding(0,0,0,3);
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        });
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        navigation.setSelectedItemId(R.id.navigation_home);
        //        init fragment manager
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

//        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
//        initiate fragment objects

//        open home fragment
//        transaction.add(R.id.contentLayout, new GMapFragment(), "mapFragment");
        transaction.add(R.id.contentLayout, new GraphFragment(), "graphFragment");
        transaction.add(R.id.contentLayout, new HomeFragment(), "homeFragment");
        transaction.add(R.id.contentLayout, new SettingsFragment(), "settingsFragment");
        transaction.commit();
//
        GMapFragment.setActivity(this);
        GraphFragment.setActivity(this);
        NavigationHandler.setManager(manager);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        checkLocation();

        gconfigs = IroadsConfiguration.getInstance();
        gconfigs.initApplicationSettings(this,mHandler);
        context = this;

        OBD2CoreConfiguration.init(this);

        //Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        //Register Event Listeners
        OBD2EventManager obd2EventManager = OBD2EventManager.getInstance();
//      obd2EventManager.registerOBD2EventListener(GraphManager.getInstance());
        obd2EventManager.registerOBD2EventListener(OBD2SiddhiAgentManager.getInstance());
        obd2EventManager.registerOBD2EventListener(this);

        boolean autoconnect = Boolean.valueOf(gconfigs.getSetting( "bt_autoconnect", Constants.BT_AUTOCONNECT_DEFAULT+""));
        if(autoconnect){
            btconnectAttemptScheduler = new Timer();
            String lastSuccessfulConnectBTAddr = OBD2CoreConfiguration.getInstance().getSetting(OBD2CoreConstants.LAST_CONNECTED_BT_ADDR);
            if(lastSuccessfulConnectBTAddr != null){
                TimerTask attemptTask = new BTConnectAttemptTask(lastSuccessfulConnectBTAddr,btconnectAttemptScheduler);
                btconnectAttemptScheduler.schedule(attemptTask,7000);
            }
        }

        HomeController.setMainActivity(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigation.setSelectedItemId(R.id.navigation_home);
        new MobileSensors(this);

    }

    public MapFragment  initMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        return mapFragment;
    }

    public void onLocationChanged(Location location) {
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
        HomeController.updateLocation(location);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Please Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            HomeController.updateLocation(mLocation);

        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            boolean autoenable = Boolean.valueOf(gconfigs.getSetting("bt_auto_enable", Constants
                    .BT_AUTO_ENABLE_DEFAULT + ""));
            if (autoenable) {
                mBluetoothAdapter.enable();
                if (mCommandService == null)
                    setupCommand();
            } else {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
        // otherwise set up the command service
        else {
            if (mCommandService == null){
                setupCommand();
            }

        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }




    private void setupCommand() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mCommandService = new BluetoothCommandService(new BTServiceConnectionHandler());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ActivityResult=====","Result received======================================="+resultCode);
        switch (requestCode) {
            case  REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    boolean manualConnect = data.getExtras().getBoolean(DeviceListActivity.MANUAL_CONNECT);



                    if(manualConnect){ //user manually try to connect the app to a device
                        proceedAttemptCycle = false; //stop auto connecting
                    }


                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mCommandService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupCommand();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, context.getString(R.string.bluetooth_not_enabled), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_ENABLE_BT_PLUS_CONNECT_DEVICE:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupCommand();
                    // Launch the DeviceListActivity to see devices and do scan
                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, context.getString(R.string.bluetooth_not_enabled), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }





    @Override
    public void receiveOBD2Event(OBD2Event e) {

        try {
            JSONObject realTimedata=e.getEventData().getJSONObject("obd2_real_time_data");
            JSONObject speedObject=(JSONObject)realTimedata.get("obd2_speed");
            JSONObject rpmObject=(JSONObject)realTimedata.get("obd2_rpm");
            String speed=speedObject.getString("value");
            String rpm=rpmObject.getString("value");
            Log.d("OBD2DATA","SPEED===="+speed);
//            HomeController.updateOBD2Data(Integer.parseInt(speed),Integer.parseInt(rpm));
        } catch (JSONException e1) {
            Log.d("OBD2DATA",e1.getMessage());

        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        this.mainMenu = menu;
//        return true;
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//
//        switch (item.getItemId())
//        {
//            case R.id.btconnect:
//
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT_PLUS_CONNECT_DEVICE);
//                }else{
//                    // Launch the DeviceListActivity to see devices and do scan
//                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
//                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                }
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void onConnectBtn(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT_PLUS_CONNECT_DEVICE);
        }else{
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

        }

    }





    class BTConnectAttemptTask extends TimerTask {

        private String address;
        private Timer scheduler;

        public BTConnectAttemptTask(String address, Timer scheduler) {
            this.address = address;
            this.scheduler = scheduler;
        }

        @Override
        public void run() {
            attemptScheduled = false;
            scheduler.cancel();

            if(btConnectAttemptsRemaining>0){
                btConnectAttemptsRemaining--;
            }else{
                proceedAttemptCycle = false;
            }

            boolean autoconnect = Boolean.valueOf(gconfigs.getSetting("bt_autoconnect", Constants.BT_AUTOCONNECT_DEFAULT+""));

            if(mCommandService != null && !gconfigs.isExitting()){
                int mState = mCommandService.getState();
                if(autoconnect && proceedAttemptCycle && (mState== BluetoothCommandService.STATE_NONE || mState == BluetoothCommandService.STATE_LISTEN)){
                    Intent data = new Intent();
                    data.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, address);
                    data.putExtra(DeviceListActivity.MANUAL_CONNECT, false);
                    MainActivity.this.onActivityResult(REQUEST_CONNECT_DEVICE, Activity.RESULT_OK, data);
                }
            }

        }

    }






    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCommandService.STATE_CONNECTED_ELM:

//                            indicatorELM.setImageResource(R.drawable.elm_ok);
                            if (msg.getData()!= null && msg.getData().containsKey(DEVICE_NAME)) {
                                String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                                Toast.makeText(getApplicationContext(), "Connected to "
                                        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            }

                            //resetting bluetooth connection re-attempt tasks
                            String attempts = gconfigs.getSetting("max_bt_attempts", Constants.MAX_BT_CONNECT_ATTEMPTS_DEFAULT + "");
                            btConnectAttemptsRemaining = Integer.parseInt(attempts);
                            proceedAttemptCycle = true;

                            break;
                        case BluetoothCommandService.STATE_CONNECTED_VINLI:
//                            indicatorELM.setImageResource(R.drawable.vinli_ok);
                            break;

                        case BluetoothCommandService.STATE_CONNECTED_CAR:
                            int carConnectedStatus = msg.getData().getInt(CAR_CONNECTED_STATUS);
                            if(carConnectedStatus==MESSAGE_OK){
//                                indicatorCar.setImageResource(R.drawable.car_ok);
                                // tripLogCalculator.resetTrip();
                            }else{
//                                indicatorCar.setImageResource(R.drawable.car_no);
                            }
                            break;
                        case BluetoothCommandService.STATE_CONNECTING:
                            break;
                        case BluetoothCommandService.STATE_LISTEN:
                        case BluetoothCommandService.STATE_NONE:
                            //tripLogCalculator.endTrip();

                            boolean autoconnect = Boolean.valueOf(gconfigs.getSetting("bt_autoconnect", Constants.BT_AUTOCONNECT_DEFAULT+""));
                            if(autoconnect && proceedAttemptCycle && !attemptScheduled){
                                String lastSuccessfulConnectBTAddr = OBD2CoreConfiguration.getInstance().getSetting(OBD2CoreConstants.LAST_CONNECTED_BT_ADDR);
                                btconnectAttemptScheduler.cancel();
                                btconnectAttemptScheduler = new Timer();
                                TimerTask attemptTask = new BTConnectAttemptTask(lastSuccessfulConnectBTAddr,btconnectAttemptScheduler);
                                btconnectAttemptScheduler.schedule(attemptTask , 10000);
                                attemptScheduled = true;
                            }

                            //sendBulkData();

                            break;
                    }
                    break;

            }
        }
    };


    class BTServiceConnectionHandler implements BTServiceCallback {

        @Override
        public void onStateChanged(int oldState, int newState, BluetoothDevice device) {
            //mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, newState, -1).sendToTarget();
        }

        @Override
        public void onConnecting(BluetoothDevice device) {
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.TOAST, "Connecting to " + device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onConnectedDevice(BluetoothDevice device) {
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,
                    BluetoothCommandService.STATE_CONNECTED_ELM, -1);
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME, device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onConnectedCar() {
            Message msg = mHandler
                    .obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,
                            BluetoothCommandService.STATE_CONNECTED_CAR, -1);
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.CAR_CONNECTED, MainActivity.MESSAGE_OK);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDisconnected(BluetoothDevice device, Map<String, Object> args) {
            mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,
                    BluetoothCommandService.STATE_NONE, -1).sendToTarget();
            gconfigs.sendToastToUI("Disconnected! Reason  : " + args.get(BluetoothCommandService.ARGS_REASON));
        }

        @Override
        public void onConnectionFailed(BluetoothDevice device) {
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.TOAST, "Unable to connect device " + device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,
                    BluetoothCommandService.STATE_NONE, -1).sendToTarget();
        }

        @Override
        public void onConnectionLost(BluetoothDevice device) {
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.TOAST, "Connection was lost with device " + device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,
                    BluetoothCommandService.STATE_NONE, -1).sendToTarget();
        }

    }






}
