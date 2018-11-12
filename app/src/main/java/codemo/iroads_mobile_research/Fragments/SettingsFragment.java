package codemo.iroads_mobile_research.Fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import codemo.iroads_mobile_research.GraphController;
import codemo.iroads_mobile_research.MainActivity;
import codemo.iroads_mobile_research.NavigationHandler;
import codemo.iroads_mobile_research.R;
import codemo.iroads_mobile_research.Reorientation.ReorientationType;
import codemo.iroads_mobile_research.SensorDataProcessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private Switch saving;
    private SeekBar frequencyBar;
    private RadioButton nericelMechanism, wolverineMechanism;
    private static MainActivity mainActivity;
    private TextView sampleRate;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        saving = (Switch) view.findViewById(R.id.savingSwitch);
        sampleRate = (TextView) view.findViewById(R.id.sampleRate);
        frequencyBar = (SeekBar) view.findViewById(R.id.frequencyBar);
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

        frequencyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int time = 1000/((i+1)*10);
                sampleRate.setText((i+1)*10 +" Hz");
                GraphController.setSleepTime(time);
                Log.d(TAG, "current state: " + time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button syncDButton= (Button) view.findViewById(R.id.syncDbBtn);
        syncDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Sync DB",Toast.LENGTH_SHORT).show();
                mainActivity.syncDatabase();
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

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        SettingsFragment.mainActivity = mainActivity;
    }


}
