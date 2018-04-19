package codemo.iroads_mobile.Database;

import android.content.Context;

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

/**
 * Created by uwin5 on 04/01/18.
 */

public class DatabaseHandler {

    private Manager manager;
    private static Database database;
    private String mSyncGatewayUrl = "http://167.99.195.237:4984/db/";


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

    public static void saveToDatabase(){
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();
//        Log.d("DATA====",SensorData.getMacceX());
        properties.put("lat", SensorData.getMlat());
        properties.put("lon", SensorData.getMlat());
        properties.put("obdSpeed", SensorData.getMobdSpeed());
        properties.put("obdRpm", SensorData.getMobdRpm());
        properties.put("acceX", SensorData.getMacceX());
        properties.put("acceY", SensorData.getMacceY());
        properties.put("acceZ", SensorData.getMacceZ());
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
            push.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }




    }



    public Manager getManager() {
        return manager;
    }

    public Database getDatabase() {
        return database;
    }
}
