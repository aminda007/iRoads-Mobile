package codemo.iroads_mobile;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class Home extends AppCompatActivity implements SensorEventListener{

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private static final String TAG = "Home";
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView xValue, yValue, zValue;
    TextView xMagValue, yMagValue, zMagValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.d(TAG, "Init sensor services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null){
            sensorManager.registerListener(Home.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            xValue = (TextView) findViewById(R.id.xValue);
            yValue = (TextView) findViewById(R.id.yValue);
            zValue = (TextView) findViewById(R.id.zValue);
        }else{
            Log.d(TAG, "Accelorometer not available");
        }


        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null){
            sensorManager.registerListener(Home.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            xMagValue = (TextView) findViewById(R.id.xMagValue);
            yMagValue = (TextView) findViewById(R.id.yMagValue);
            zMagValue = (TextView) findViewById(R.id.zMagValue);
        }else{
            Log.d(TAG, "Magnetometer not available");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        int sensorType = sensor.getType();

//        Log.d(TAG,
//                "Sensor changed X: "+sensorEvent.values[0]+
//                "Sensor changed Y: "+sensorEvent.values[1]+
//                "Sensor changed Z: "+sensorEvent.values[2]);

        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            xValue.setText("X Value: "+sensorEvent.values[0]);
            yValue.setText("Y Value: "+sensorEvent.values[1]);
            zValue.setText("Z Value: "+sensorEvent.values[2]);
        }else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            xMagValue.setText("X Value: "+sensorEvent.values[0]);
            yMagValue.setText("Y Value: "+sensorEvent.values[1]);
            zMagValue.setText("Z Value: "+sensorEvent.values[2]);
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
