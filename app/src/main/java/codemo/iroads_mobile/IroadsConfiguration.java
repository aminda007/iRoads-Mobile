package codemo.iroads_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.vatichub.obd2.OBD2CoreConfiguration;

import java.util.HashMap;

/**
 * Created by uwin5 on 03/24/18.
 */

public class IroadsConfiguration {

    public static final String PREFS_NAME = "Kampana-pref-file";
    public static final String PREFS_DEFAULT_VALUE = "Kampana-pref-file-default-value";
    private HashMap<String, String> settingsMap;
    private SharedPreferences settings;
    private boolean exitting;

    private Context context;
    private Handler mHandler;

    private int OBD2updatespeed=500;


    private static IroadsConfiguration instance;


    private IroadsConfiguration(){
        settingsMap = new HashMap<String, String>();
    }

    public static IroadsConfiguration getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public static void init() {
        instance = new IroadsConfiguration();
    }


    public String getSetting(String key){

        String value = settingsMap.get(key);

        if(value == null){
            value = settings.getString(key, PREFS_DEFAULT_VALUE);
        }

        if (value.equals(PREFS_DEFAULT_VALUE)){
            value=null;
        }else{
            settingsMap.put(key, value);
        }

        return value;

    }

    public String getSetting(String key, String defaultValue){

        String value = getSetting(key);
        if(value==null) value = defaultValue;
        return value;

    }

    public boolean isExitting() {
        return exitting;
    }

    public void setExitting(boolean exitting) {
        this.exitting = exitting;
        OBD2CoreConfiguration.getInstance().setExiting(exitting);
    }

    public void initApplicationSettings(Context context, Handler mhandler){
        // Restore preferences
        this.context = context;
        this.mHandler= mhandler;
        settings = context.getSharedPreferences(PREFS_NAME, 0);

//        USERNAME 			= context.getString(R.string.USERNAME);
//        USERNAME_DEFAULT 	= context.getString(R.string.USERNAME_DEFAULT);
//        PASSWORD 			= context.getString(R.string.PASSWORD);
//        PASSWORD_DEFAULT	= context.getString(R.string.PASSWORD_DEFAULT);
//        TOKEN				= context.getString(R.string.TOKEN);
//        TOKEN_DEFAULT		= context.getString(R.string.TOKEN_DEFAULT);

//        cepURL = settings.getString(CEP_URL, CEP_URL_DEFAULT);
//        bamURL = settings.getString(BAM_URL, BAM_URL_DEFAULT);
//        uesURL = settings.getString(UES_URL, UES_URL_DEFAULT);

//        appopencount = Integer.parseInt(getAndAddIfNotAvailable(Constants.APP_USED_COUNT, "0"));
        OBD2updatespeed = Integer.parseInt(settings.getString(Constants.OBD2_UPDATE_DELAY, Constants.OBD2_UPDATE_DELAY_DEFAULT + ""));
//        sendtoserver = Boolean.valueOf(settings.getString(Constants.SEND_TO_SERVER, Constants.SEND_TO_SERVER_DEFAULT+""));
//
//        username = settings.getString(USERNAME, USERNAME_DEFAULT);
//        password = settings.getString(PASSWORD, PASSWORD_DEFAULT);
//        token = settings.getString(TOKEN, TOKEN_DEFAULT);

//        addSetting(Constants.APP_USED_COUNT, (appopencount+1) +"");

//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        this.displaySize = size;
//
//        //init displays
//        addDefaultDisplaysSettings();

        //add default features
//        initFeatures();
//        initFeatureParams();
    }

    public void sendToastToUI(String message) {
        Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }



}
