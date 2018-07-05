package codemo.iroads_mobile.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import codemo.iroads_mobile.Database.SensorData;
import codemo.iroads_mobile.MainActivity;
import codemo.iroads_mobile.NavigationHandler;
import codemo.iroads_mobile.R;
import codemo.iroads_mobile.Reorientation.ReorientationType;
import codemo.iroads_mobile.SensorDataProcessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private Switch saving, filtering, orienting;
    private SeekBar filterBar;
    private RadioButton nericelMechanism, wolverineMechanism;
    private static MainActivity mainActivity;
    private TextView filterText;
    private String journeyId = "";
    private ArrayList<String> timeArray = new ArrayList<>();
    private boolean clicked = false;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        saving = (Switch) view.findViewById(R.id.savingSwitch);
        filterText = (TextView) view.findViewById(R.id.filterText);
        filtering = (Switch) view.findViewById(R.id.filteringSwitch);
        filterBar = (SeekBar) view.findViewById(R.id.filterSeekbar);
        nericelMechanism = (RadioButton) view.findViewById(R.id.nericel);
        wolverineMechanism = (RadioButton) view.findViewById(R.id.wolverine);

        RadioGroup reorientaationTypeGroup=(RadioGroup) view.findViewById(R.id.reorientationType);

        reorientaationTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId==R.id.nericel){
                    SensorDataProcessor.setReorientation(ReorientationType.Nericel);
                }

                else if (checkedId==R.id.wolverine){
                    SensorDataProcessor.setReorientation(ReorientationType.Wolverine);
                }

            }
        });

        saving.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.d(TAG, "settings auto save changed");
                if(b){
                    Log.d(TAG, "settings auto save enabled");
                    HomeFragment.setAutoSaveON(true);
                    saving.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                }else{
                    Log.d(TAG, "settings auto save disabled");
                    HomeFragment.setAutoSaveON(false);
                    saving.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorDisabledThumb), PorterDuff.Mode.MULTIPLY);
                }
            }
        });

        filterBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        disableFiltering();

        filtering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d(TAG, "filtering enabled");
                    enableFiltering();
                }else{
                    Log.d(TAG, "filtering disabled");
                    disableFiltering();
                }
            }
        });

        Button idButton = (Button) view.findViewById(R.id.idButton);
        idButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                askJourneyName();
            }
        });
        final Button addButton = (Button) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getJourneyId() == ""){
                    askJourneyName();
                }else{
                    getTimeArray().add(String.valueOf(System.currentTimeMillis())+","+ SensorData.getMlat()+","+ SensorData.getMlon());
                    if(isClicked()){
                        addButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        setClicked(false);
                    }else{
                        addButton.setBackgroundColor(Color.GRAY);
                        setClicked(true);
                    }
                    Toast.makeText( getContext(),"Add time", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getTimeArray().isEmpty()){
                    Toast.makeText( getContext(),"Nothing to write", Toast.LENGTH_SHORT).show();
                }else{
                    StringBuilder string = new StringBuilder();
                    for (String str : getTimeArray() ) {
                        string.append(str.toString()+"\n");
                    }
                    writeToFile(String.valueOf(string));
                }
            }
        });



//        saving.setColorFilter(ContextCompat.getColor(mainActivity.getApplicationContext(), R.color.colorPrimary));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationHandler.navigateTo("homeFragment");
    }

    public void enableFiltering(){
        filterBar.setEnabled(true);
        filterText.setEnabled(true);
        filtering.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    public void disableFiltering(){
        filterBar.setEnabled(false);
        filterText.setEnabled(false);
        filtering.getThumbDrawable().setColorFilter(ContextCompat.getColor(getMainActivity().getApplicationContext(), R.color.colorDisabledThumb), PorterDuff.Mode.MULTIPLY);

    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        SettingsFragment.mainActivity = mainActivity;
    }

    public void writeToFile(String text){
        Log.d("DJourneyID=====", getJourneyId());

        File logFile = new File("sdcard/log_"+getJourneyId()+".txt");
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
            setJourneyId("");
            getTimeArray().clear();
            Toast.makeText( getContext(),"Write successful!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void askJourneyName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Journey Name");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if(text != null && text.trim().length() == 0){
                    Toast.makeText( getContext(),"Journey Name can not be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    setJourneyId(text);
                    Toast.makeText( getContext(),"JourneyId Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public ArrayList<String> getTimeArray() {
        return timeArray;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
