package codemo.iroads_mobile.Database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import codemo.iroads_mobile.MainActivity;
import codemo.iroads_mobile.MobileSensors;
import codemo.iroads_mobile.SensorDataProcessor;

/**
 * Created by uwin5 on 04/01/18.
 */

public class DatabaseHandler {

    private Manager manager;
    private static Database database;
    private String mSyncGatewayUrl = "http://iroads.projects.mrt.ac.lk:4984/iroads/";
    private static final String TAG = "DatabaseHandler";
    private static String jid;

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
        properties.put("obdSpeed", SensorDataProcessor.vehicleSpeed());
        properties.put("gpsSpeed", MobileSensors.getGpsSpeed());
        properties.put("obdRpm", SensorData.getMobdRpm());
        properties.put("acceX", SensorDataProcessor.getReorientedAx());
        properties.put("acceY", SensorDataProcessor.getReorientedAy());
        properties.put("acceZ", SensorDataProcessor.getReorientedAz());
        properties.put("acceX_raw", SensorData.getMacceX());
        properties.put("acceY_raw", SensorData.getMacceY());
        properties.put("acceZ_raw", SensorData.getMacceZ());
        properties.put("time",System.currentTimeMillis());
        properties.put("type", "data_item");

        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static String getJid() {
        return jid;
    }

    public static void setJid(String jid) {
        DatabaseHandler.jid = jid;
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

    public static void saveJourneyName(String name){
        // The properties that will be saved on the document
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put("journeyID", SensorData.getJourneyId());
        properties.put("journeyName", name);
        properties.put("type", "trip_names");
        setJid(name);
        // Create a new document
        Document document = database.createDocument();
        // Save the document to the database
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public String createString(){
//        jid=null;
// Let's find the documents that have conflicts so we can resolve them:
        StringBuilder text = new StringBuilder();
        Query query = database.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        QueryEnumerator result = null;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            if(row !=null){
                if(row.getDocument().getProperty("acceX")!=null){
//                    jid=row.getDocument().getProperty("journeyID").toString();
//                    text.append(row.getDocument().getProperty("acceX").toString());
//                    text.append(row.getDocument().getProperty("acceX").toString());
                    text.append(row.getDocument().getProperties().toString());
                    text.append(",");
//                    Log.d("ROW",row.getDocument().getProperties().toString());
                }
//
            }

//            if (row.getConflictingRevisions().size() > 0) {
////                Log.w("MYAPP", "Conflict in document: %s", row.getDocumentId());
//                beginConflictResolution(row.getDocument());
//            }
        }
        Log.d("Dta=========",text.toString());
        try {
            database.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public static void writeToFile(String text)
    {
        Log.d("DJourneyID=====",getJid());

        File logFile = new File("sdcard/log"+getJid()+".txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


//    public void writeToFile11(String data,Context context) {
//        try {
//            File logFile =new File("sdcard/log.txt");
////            if(){
////
////            }
////            FileWriter out = new FileWriter(new File(context.getFilesDir(), "myFile.txt"));
////            out.write(data);
////            out.close();
////            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("myfile.txt", Context.MODE_PRIVATE));
////            outputStreamWriter.write(data);
////            outputStreamWriter.close();
//
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }

}
