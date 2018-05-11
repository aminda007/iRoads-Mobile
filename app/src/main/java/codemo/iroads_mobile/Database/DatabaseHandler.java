package codemo.iroads_mobile.Database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import codemo.iroads_mobile.MainActivity;
import codemo.iroads_mobile.SensorDataProcessor;

/**
 * Created by uwin5 on 04/01/18.
 */

public class DatabaseHandler {

    private Manager manager;
    private static Database database;
    private String mSyncGatewayUrl = "http://167.99.195.237:4984/db/";
    private static final String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context){
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            database = getManager().getDatabase("iroads");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
//    private static int count = 0;
    public static void saveToDatabase(){
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();

//        Log.d("DATA====",SensorData.getMacceX());
        properties.put("journeyID", SensorData.getJourneyId());
        properties.put("imei", SensorData.getDeviceId());
        properties.put("lat", SensorData.getMlat());
        properties.put("lon", SensorData.getMlon());
        properties.put("obdSpeed", SensorData.getMobdSpeed());
        properties.put("obdRpm", SensorData.getMobdRpm());
        properties.put("acceX", SensorDataProcessor.getReorientedAx());
        properties.put("acceY", SensorDataProcessor.getReorientedAy());
        properties.put("acceZ", SensorDataProcessor.getReorientedAz());
        properties.put("time",System.currentTimeMillis());

        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    // Replication
    public void startReplication() {
        URL url = null;
        try {
            url = new URL(mSyncGatewayUrl);
            Replication push=database.createPushReplication(url);
            push.addChangeListener(new Replication.ChangeListener()  {
                @Override
                public void changed(Replication.ChangeEvent event) {
                    if (event.getStatus() == Replication.ReplicationStatus.REPLICATION_STOPPED){
                        Log.i(TAG, "Replication stopped");
                        MainActivity.setReplicationStopped(true);
                    }
                }
            });
//            push.setContinuous(true);
            push.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }




    }

    public static void printDocCount(){
        Log.d("DOC___COUNT",database.getDocumentCount()+"");

    }



    public Manager getManager() {
        return manager;
    }

    public Database getDatabase() {
        return database;
    }
}
