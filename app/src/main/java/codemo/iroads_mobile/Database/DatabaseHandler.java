package codemo.iroads_mobile.Database;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uwin5 on 04/01/18.
 */

public class DatabaseHandler {

    private Manager manager;
    private Database database;


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

    public void saveToDatabase(SensorData data){
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("lat", data.getLat());
        properties.put("lon", data.getLon());
        properties.put("obdSpeed", data.getObdSpeed());
        properties.put("obdRpm", data.getObdRpm());
        properties.put("acceX", data.getAcceX());
        properties.put("acceY", data.getAcceY());
        properties.put("acceZ", data.getAcceZ());
        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
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
